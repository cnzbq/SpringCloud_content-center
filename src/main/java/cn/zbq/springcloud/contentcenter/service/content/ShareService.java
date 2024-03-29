package cn.zbq.springcloud.contentcenter.service.content;

import cn.zbq.springcloud.contentcenter.dao.content.MidUserShareMapper;
import cn.zbq.springcloud.contentcenter.dao.content.RocketmqTransactionLogMapper;
import cn.zbq.springcloud.contentcenter.dao.content.ShareMapper;
import cn.zbq.springcloud.contentcenter.domain.dto.content.ShareAuditDTO;
import cn.zbq.springcloud.contentcenter.domain.dto.content.ShareDTO;
import cn.zbq.springcloud.contentcenter.domain.dto.message.UserAddBonusMagDTO;
import cn.zbq.springcloud.contentcenter.domain.dto.user.UserAddBonusDTO;
import cn.zbq.springcloud.contentcenter.domain.dto.user.UserDTO;
import cn.zbq.springcloud.contentcenter.domain.entity.content.MidUserShare;
import cn.zbq.springcloud.contentcenter.domain.entity.content.RocketmqTransactionLog;
import cn.zbq.springcloud.contentcenter.domain.entity.content.Share;
import cn.zbq.springcloud.contentcenter.domain.enums.AuditStatusEnum;
import cn.zbq.springcloud.contentcenter.feignclient.UserCenterFeignClient;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.sound.midi.MidiUnavailableException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 分享服务
 *
 * @author Zbq
 * @since 2019/9/8 17:57
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareService {
    private final ShareMapper shareMapper;
    private final UserCenterFeignClient userCenterFeignClient;
    private final RocketMQTemplate rocketMQTemplate;
    private final RocketmqTransactionLogMapper rocketmqTransactionLogMapper;
    private final Source source;
    private final MidUserShareMapper midUserShareMapper;


    /**
     * 通过id查询分享详情
     *
     * @param id id
     * @return share
     */
    public ShareDTO findById(Integer id) {
        // 获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        // 发布人id
        Integer userId = share.getUserId();
        // 调用用户微服务的 /users/{userId}
        UserDTO userDTO = this.userCenterFeignClient.findById(userId);
        // 消息的装配
        ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        // 用http get 方法请求，并且返回一个对象
        String forObject = restTemplate.getForObject(
                "http://localhost:8080/users/{id}", String.class, 1
        );
        System.out.println(forObject);
    }

    public Share auditById(Integer id, ShareAuditDTO auditDTO) {

        // 1.查询share是否存在，不存在或者当前的audit_status != NOT_YET,那么抛异常
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("参数非法！该分享不存在！");
        }
        if (!Objects.equals("NOT_YET", share.getAuditStatus())) {
            throw new IllegalArgumentException("参数非法！该分享已审核通过或不通过");
        }

        // 3.如果是PASS，那么发送消息给rocketmq，让用户中心去消费，并为发布人添加积分
        if (AuditStatusEnum.PASS.equals(auditDTO.getAuditStatusEnum())) {
            // 发送半消息
            String transactionId = UUID.randomUUID().toString();
            this.source.output().send(
                    MessageBuilder
                            .withPayload(
                                    UserAddBonusMagDTO.builder()
                                            .userId(share.getUserId())
                                            .bonus(50)
                                            .build()
                            )
                            // header 也有妙用  AddTransactionListener#executeLocalTransaction
                            .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("share_id", id)
                            .setHeader("dto", JSONObject.toJSONString(auditDTO))
                            .build()
            );
        } else {
            auditByIdInDB(id, auditDTO);
        }
        return share;
    }

    /**
     * 审核资源，将状态设为PASS/REJECT
     *
     * @param id       分享id
     * @param auditDTO 分享审核DTO
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id, ShareAuditDTO auditDTO) {

        Share share = Share.builder()
                .id(id)
                .auditStatus(auditDTO.getAuditStatusEnum().toString())
                .reason(auditDTO.getReason())
                .build();

        this.shareMapper.updateByPrimaryKeySelective(share);
    }

    /**
     * 记录本地事物执行结果
     *
     * @param id            内容分享id
     * @param auditDTO      分享审核DTO
     * @param transactionId 事物消息id
     */
    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDTO auditDTO, String transactionId) {

        this.auditByIdInDB(id, auditDTO);
        // save log
        this.rocketmqTransactionLogMapper.insertSelective(
                RocketmqTransactionLog
                        .builder()
                        .transactionId(transactionId)
                        .log("审核分享...")
                        .build()
        );
    }

    public PageInfo<Share> q(String title, Integer pageNo, Integer pageSize, Integer userId) {

        // 切入下面不分页的sql，自动拼接成分页的sql
        PageHelper.startPage(pageNo, pageSize);
        // 不分页的sql
        List<Share> shares = this.shareMapper.selectByParam(title);
        List<Share> shareList;
        // 1.如果用户未登录，那么downloadUrl全部设为null
        if (userId == null) {
            shareList = shares.stream().peek(share -> share.setDownloadUrl(null)).collect(Collectors.toList());
        }
        // 2. 如果用户登录了，那么查询一下mid_user_share,如果没有数据，那么这条share的downloadUrl设为null
        else {
            shareList = shares.stream().peek(share -> {
                MidUserShare midUserShare = this.midUserShareMapper.selectOne(MidUserShare.builder()
                        .shareId(share.getId())
                        .userId(userId)
                        .build());
                if (midUserShare == null) {
                    share.setDownloadUrl(null);
                }
            }).collect(Collectors.toList());
        }
        return new PageInfo<>(shareList);
    }

    public Share exchangeById(Integer id, HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("id");

        // 1.根据id查询share，校验是否存在
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (Objects.isNull(share)) {
            throw new IllegalArgumentException("该分享不存在");
        }

        // 如果当前用户已经兑换过该分享，直接返回
        MidUserShare midUserShare = midUserShareMapper.selectOne(MidUserShare.builder()
                .userId(userId)
                .shareId(id)
                .build());
        if (Objects.nonNull(midUserShare)) {
            return share;
        }

        // 2. 根据当前登陆的用户id，查询积分是否够
        UserDTO userDTO = this.userCenterFeignClient.findById(userId);
        int price = share.getPrice();
        if (price > userDTO.getBonus()) {
            throw new IllegalArgumentException("用户积分不够用!");
        }

        // 3. 扣减积分 & 往mid_user_share里插入一条数据
        this.userCenterFeignClient.addBonus(
                UserAddBonusDTO.builder()
                        .userId(userId)
                        .bonus(-price)
                        .build()
        );
        midUserShareMapper.insert(
                MidUserShare.builder()
                        .userId(userId)
                        .shareId(id)
                        .build()
        );
        return share;
    }
}
