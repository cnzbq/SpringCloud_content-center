package ribbonconfiguration;

import cn.zbq.springcloud.contentcenter.configuration.NacosWeightedRule;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ribbon配置类
 *
 * @author Zbq
 * @since 2019/9/18 22:39
 */
@Configuration
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        return new NacosWeightedRule();
    }
}
