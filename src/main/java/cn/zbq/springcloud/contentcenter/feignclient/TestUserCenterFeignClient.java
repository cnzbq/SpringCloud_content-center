package cn.zbq.springcloud.contentcenter.feignclient;

import cn.zbq.springcloud.contentcenter.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 用户中心feign client
 *
 * @author Zbq
 * @since 2019/11/4 21:51
 */
@FeignClient(name = "user-center")
public interface TestUserCenterFeignClient {
    @GetMapping("/q")
    UserDTO query(@SpringQueryMap UserDTO userDTO);
}
