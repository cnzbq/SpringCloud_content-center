package cn.zbq.springcloud.contentcenter.feignclient;

import cn.zbq.springcloud.contentcenter.domain.dto.user.UserDTO;
import cn.zbq.springcloud.contentcenter.feignclient.fallback.UserCenterFeignClientFallback;
import cn.zbq.springcloud.contentcenter.feignclient.fallbackfactory.UserCenterFeignClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户中心feign客户端
 * <p>
 * FeignClient(name = "user-center")中的name指的是要请求的微服务的名称
 *
 * @author Zbq
 * @since 2019/11/3 21:33
 */
//@FeignClient(name = "user-center",configuration = UserCenterFeignConfiguration.class)
@FeignClient(name = "user-center",
        //    fallback = UserCenterFeignClientFallback.class,
        fallbackFactory = UserCenterFeignClientFallbackFactory.class
)
public interface UserCenterFeignClient {

    /**
     * 根据id查询用户
     * feign自动构建 http://user-center/user/{id}的请求连接
     *
     * @param id 用户id
     * @return 用户dto
     */
    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);
}
