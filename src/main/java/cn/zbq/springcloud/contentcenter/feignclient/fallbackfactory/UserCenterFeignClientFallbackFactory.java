package cn.zbq.springcloud.contentcenter.feignclient.fallbackfactory;
/*
import cn.zbq.springcloud.contentcenter.domain.dto.user.UserDTO;
import cn.zbq.springcloud.contentcenter.feignclient.UserCenterFeignClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

*//**
 * 当被限流或者降级时可以返回这里的默认值(可以获取异常信息)
 *
 * @author Zbq
 * @since 2019/11/16 23:29
 *//*
@Slf4j
@Component
public class UserCenterFeignClientFallbackFactory implements FallbackFactory<UserCenterFeignClient> {
    @Override
    public UserCenterFeignClient create(Throwable throwable) {
        // 匿名内部类实现方式
      *//*return new UserCenterFeignClient() {
          @Override
          public UserDTO findById(Integer id) {
              return null;
          }
      };*//*
        // lambda 实现
        return (id) -> {
            log.warn("远程调用被限流/降级了", throwable);
            UserDTO userDTO = new UserDTO();
            userDTO.setWxNickname("这是一个默认用户");
            return userDTO;
        };
    }
}*/
