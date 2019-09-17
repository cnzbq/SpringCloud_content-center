package cn.zbq.springcloud.contentcenter.service.content;

import cn.zbq.springcloud.contentcenter.dao.content.ShareMapper;
import cn.zbq.springcloud.contentcenter.domain.dto.content.ShareDTO;
import cn.zbq.springcloud.contentcenter.domain.dto.user.UserDTO;
import cn.zbq.springcloud.contentcenter.domain.entity.content.Share;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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
    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

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
        UserDTO userDTO = this.restTemplate.getForObject(
                "http://user-center/users/{userId}",
                UserDTO.class, userId
        );
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
}
