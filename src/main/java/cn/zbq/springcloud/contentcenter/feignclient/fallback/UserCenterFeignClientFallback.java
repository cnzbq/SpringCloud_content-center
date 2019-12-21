package cn.zbq.springcloud.contentcenter.feignclient.fallback;
/*

import cn.zbq.springcloud.contentcenter.domain.dto.user.UserDTO;
import cn.zbq.springcloud.contentcenter.feignclient.UserCenterFeignClient;
import org.springframework.stereotype.Component;

*/
/**
 * 当被限流或者降级时可以返回这里的默认值(无法获取异常信息)
 *
 * @author Zbq
 * @since 2019/11/16 23:12
 *//*

@Component
public class UserCenterFeignClientFallback implements UserCenterFeignClient {

    @Override
    public UserDTO findById(Integer id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setWxNickname("这是一个默认用户");
        return userDTO;
    }
}
*/
