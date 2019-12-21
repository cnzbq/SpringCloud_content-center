package cn.zbq.springcloud.contentcenter.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * token 拦截器
 *
 * @author Zbq
 * @since 2019/12/21 23:59
 */
public class TokenRelayRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 1. 获取到token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        String token = attributes.getRequest().getHeader("X-Token");

        // 2. 将token传递
        if (StringUtils.isNotBlank(token)) {
            requestTemplate.header("X-Token", token);
        }
    }
}
