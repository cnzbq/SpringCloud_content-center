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
                                                   ClientHttpRequestExecution execution, BlockException e) {
        log.warn("限流或者降级了", e);
        return new SentinelClientHttpResponse("限流或者降级了");
    }

    public static SentinelClientHttpResponse fallback(HttpRequest request, byte[] body,
                                                      ClientHttpRequestExecution execution, BlockException e) {
        log.warn("降级了", e);
        return new SentinelClientHttpResponse("降级了");
    }
}
