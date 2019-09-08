package cn.zbq.springcloud.contentcenter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author zbq
 */
// 扫描哪些包里面的接口
@MapperScan("cn.zbq.springcloud")
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
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
