package cn.zbq.springcloud.contentcenter.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * feign 配置类
 * 这个类别加@Configuration注解，否则产生父子上下文的问题。
 * 如果一定要加，需要将其挪到@ComponentScan扫描不到的包以外（默认为启动类所在包以外）
 *
 * @author Zbq
 * @since 2019/11/4 20:34
 */
public class GlobalFeignConfiguration {
    @Bean
    public Logger.Level logConfig() {
        // 打印feign所有请求的细节
        return Logger.Level.FULL;
    }
}
