package cn.zbq.springcloud.contentcenter.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import ribbonconfiguration.RibbonConfiguration;

/**
 * ribbon config
 *
 * @author Zbq
 * @since 2019/9/18 22:37
 */
@Configuration
@RibbonClients(defaultConfiguration =  RibbonConfiguration.class)
public class UserCenterRibbonConfiguration {

}
