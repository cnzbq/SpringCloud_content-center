package cn.zbq.springcloud.contentcenter.sentineltest;

import lombok.extern.slf4j.Slf4j;

/**
 * sentinel fallback
 *
 * @author Zbq
 * @since 2019/11/16 11:51
 */
@Slf4j
public class TestControllerFallbackHandlerClass {
    /**
     * 处理降级
     * <p>
     * 该方法返回值和入参必须与testSentinelResource 保持一致
     * BlockException 产生的异常信息
     */
    public static String fallback(String a, Throwable e) {
        log.warn("降级了", e);
        return "降级了 fallback";
    }
}
