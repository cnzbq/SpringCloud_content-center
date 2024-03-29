package cn.zbq.springcloud.contentcenter;

import cn.zbq.springcloud.contentcenter.exception.RestTemplateException;
import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.Collections;

/**
 * @author zbq
 */
// 扫描哪些包里面的接口
@MapperScan("cn.zbq.springcloud.contentcenter.dao.content")
@EnableFeignClients//(defaultConfiguration = GlobalFeignConfiguration.class)
@EnableBinding({Source.class})
@SpringBootApplication
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);
    }


    /**
     * 在spring容器中创建一个对象，类型为RestTemplate，名称/id为restTemplate
     * <bean id="restTemplate" class="org.springframework.web.client.RestTemplate"/>
     *
     * @return RestTemplate
     * @LoadBalanced 整合ribbon必须添加该注解
     */
    @Bean
    @SentinelRestTemplate(blockHandlerClass = RestTemplateException.class,
            blockHandler = "block",
            fallbackClass = RestTemplateException.class,
            fallback = "fallback")
    @LoadBalanced
    public RestTemplate restTemplate() {

        RestTemplate template = new RestTemplate();
        // 设置自定义的拦截器
        template.setInterceptors(
                // 单例list，节省内存开销
                Collections.singletonList(
                        new TestRestTemplateTokenRelayIntercept()
                )
        );

        return template;
    }

}
