package cn.zbq.springcloud.contentcenter.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 扩展ribbon，支持nacos权重
 *
 * @author Zbq
 * @since 2019/10/31 21:24
 */
@Slf4j
public class NacosWeightedRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    /**
     * 读取配置文件，并初始化NacosWeightedRule
     *
     * @param iClientConfig config
     */
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        try {
            // 因为需要使用到一个getName方法，但是ILoadBalancer中并没有，所以需要进行一下强转
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            if (log.isDebugEnabled()) {
                log.debug("lb ={}", loadBalancer);
            }
            // 想要请求的微服务的名称
            String name = loadBalancer.getName();

            // 实现负载均衡算法
            // 使用nacos已经实现好的基于权重的负载均衡算法
            // 拿到服务发现的相关API
            NamingService namingMaintainService = nacosDiscoveryProperties.namingServiceInstance();
            // nacos client 自动通过基于权重的负载均衡算法，选择一个实例
            Instance instance = namingMaintainService.selectOneHealthyInstance(name);
            if (log.isDebugEnabled()) {
                log.debug("选择的实例是，port:{},instance:{}", instance.getPort(), instance);
            }
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("获取实例发生异常",e);
            return null;
        }
    }
}
