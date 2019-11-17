package cn.zbq.springcloud.contentcenter.exception;

import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

/**
 * sentinel restTemplate fallback blockHandler
 *
 * @author Zbq
 * @since 2019/11/16 16:14
 */
@Slf4j
public class RestTemplateException {
    public static SentinelClientHttpResponse block(HttpRequest request, byte[] body,
                                                   ClientHttpRequestExecution execution, BlockException e){
        log.warn("限流或者降级了", e);
        // SentinelClientHttpResponse 中的内容A会自动转化为restTemplate请求的对象
        // eg：SentinelClientHttpResponse内容为{"wxNickname":"zbq"},restTemplate请求返回类型为UserDTO，则自动将{"wxNickname":"zbq"}转为UserDTO对象

        // 抛出友好提示可以使用自定义的异常，然后做异常统一处理
        //throw new XXXException("限流或者降级了");
        // 当服务调用失败时可以指定一个默认值（必须为json格式），否则请求对象参数为空
       return new SentinelClientHttpResponse("\"message\":\"限流或者降级了\"");
    }

    public static SentinelClientHttpResponse fallback(HttpRequest request, byte[] body,
                                                      ClientHttpRequestExecution execution, BlockException e) {
        log.warn("降级了", e);
        return new SentinelClientHttpResponse("\"message\":\"降级了\"");
    }
}
