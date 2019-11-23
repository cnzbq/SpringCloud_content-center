package cn.zbq.springcloud.contentcenter.service.content;

import cn.zbq.springcloud.contentcenter.dao.content.ShareMapper;
import cn.zbq.springcloud.contentcenter.domain.dto.content.ShareAuditDTO;
import cn.zbq.springcloud.contentcenter.domain.dto.content.ShareDTO;
import cn.zbq.springcloud.contentcenter.domain.dto.message.UserAddBonusMagDTO;
import cn.zbq.springcloud.contentcenter.domain.dto.user.UserDTO;
import cn.zbq.springcloud.contentcenter.domain.entity.content.Share;
import cn.zbq.springcloud.contentcenter.feignclient.UserCenterFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

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

        // 2.审核资源，将状态设为PASS/REJECT
        share.setAuditStatus(auditDTO.getAuditStatusEnum().toString());
        this.shareMapper.updateByPrimaryKey(share);

        // 3.如果是PASS，那么发送消息给rocketmq，让用户中心去消费，并为发布人添加积分
        // 目标（destination）：topic
        // 消息提（payload）：
        this.rocketMQTemplate.convertAndSend("add-bonus",
                UserAddBonusMagDTO.builder()
                        .userId(share.getUserId())
                        .bonus(50)
                        .build());
        return share;
    }
}
