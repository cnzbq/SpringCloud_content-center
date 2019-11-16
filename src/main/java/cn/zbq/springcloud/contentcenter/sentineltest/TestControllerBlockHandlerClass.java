package cn.zbq.springcloud.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

/**
 * sentinel block
 *
 * @author Zbq
 * @since 2019/11/16 11:49
 */
@Slf4j
public class TestControllerBlockHandlerClass {
    /**
     * 处理限流或者降级
     * <p>
     * 该方法返回值和入参必须与testSentinelResource 保持一致
     * BlockException 产生的异常信息
     */
    public static String block(String a, BlockException e) {
        log.warn("限流或者降级了", e);
        return "限流或者降级了 block";
    }
}
