package cn.zbq.springcloud.contentcenter;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @author Zbq
 * @since 2019/11/7 21:34
 */
@Slf4j
@Service
public class TestService {

    @SentinelResource("common")
    public String common() {
        log.info("common....");
        return "common";
    }
}
