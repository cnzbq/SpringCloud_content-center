package cn.zbq.springcloud.contentcenter.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 同一集群有优先调用
 *
 * @author Zbq
 * @since 2019/11/1 21:15
 */
@Slf4j
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
    }

    @Override
    public Server choose(Object o) {
        try {
            // 拿到配置文件中的集群名称 BJ
            String clusterName = nacosDiscoveryProperties.getClusterName();
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();
            // 想要请求的微服务名称
            String name = loadBalancer.getName();
            // 服务发现的API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            // 1.找到指定服务的所有实例 A
            List<Instance> instanceList = namingService.selectInstances(name, true);
            // 2.过滤出同集群下的所有实例B
            List<Instance> clusterInstanceList = instanceList.stream()
                    .filter(instance -> clusterName.equals(instance.getClusterName())).collect(Collectors.toList());
            // 3.如果B为空，则使用A
            if (CollectionUtils.isEmpty(clusterInstanceList)) {
                log.warn("发生跨服务调用");
                clusterInstanceList = instanceList;
            }
            // 4.基于权重的负载均衡算法，返回一个实例
            Instance instance = ExtendBalancer.getHostByRandomWeight2(clusterInstanceList);
            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("查找服务实例发生异常", e);
            return null;
        }
    }
}

/**
 * 由于nacos中Balancer实现的getHostByRandomWeight方法受办法，
 * 因此继承该类，通过新的方法调用其受保护的方法
 */
class ExtendBalancer extends Balancer {
    static Instance getHostByRandomWeight2(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}
