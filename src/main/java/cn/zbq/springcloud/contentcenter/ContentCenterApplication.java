package cn.zbq.springcloud.contentcenter;

import cn.zbq.springcloud.contentcenter.configuration.GlobalFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author zbq
 */
// 扫描哪些包里面的接口
@MapperScan("cn.zbq.springcloud")
@EnableFeignClients//(defaultConfiguration = GlobalFeignConfiguration.class)
@SpringBootApplication
public class ContentCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentCenterApplication.class, args);
    }


    /**
     * 在spring容器中创建一个对象，类型为RestTemplate，名称/id为restTemplate
     * <bean id="restTemplate" class="org.springframework.web.client.RestTemplate"/>
     * @LoadBalanced 整合ribbon必须添加该注解
     * @return RestTemplate
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
