package cn.zbq.springcloud.contentcenter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

/**
 * RestTemplate Intercept拦截器
 * 需要将当前类在new restTemplate时设置进去，否则不生效
 *
 * @author Zbq
 * @since 2019/12/22 22:45
 */
public class TestRestTemplateTokenRelayIntercept implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 静态方式获取request，然后从request请求中拿到token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        String token = attributes.getRequest().getHeader("X-Token");

        // 从方法的request请求中拿到当前请求的header
        HttpHeaders headers = request.getHeaders();
        // 将token设置到当前的请求中
        headers.set("X-Token", token);

        // 让当前请求继续执行
        return execution.execute(request, body);
    }
}
