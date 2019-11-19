package cn.zbq.springcloud.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlCleaner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 支持RESTful url
 *
 * @author Zbq
 * @since 2019/11/19 21:00
 */
@Slf4j
@Component
public class MyUrlCleaner implements UrlCleaner {
    @Override
    public String clean(String s) {
        log.info("originUrl={}", s);
        String[] strings = s.split("/");
        return Arrays.stream(strings)
                .map(str -> {
                    if (NumberUtils.isCreatable(str)) {
                        return "{number}";
                    }
                    return str;
                })
                .reduce((a, b) -> a + "/" + b)
                .orElse("");
    }
}
