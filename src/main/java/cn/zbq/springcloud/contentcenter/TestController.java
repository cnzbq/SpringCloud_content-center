package cn.zbq.springcloud.contentcenter;

import cn.zbq.springcloud.contentcenter.dao.content.ShareMapper;
import cn.zbq.springcloud.contentcenter.domain.dto.user.UserDTO;
import cn.zbq.springcloud.contentcenter.domain.entity.content.Share;
import cn.zbq.springcloud.contentcenter.feignclient.TestBaiduFeignClient;
import cn.zbq.springcloud.contentcenter.feignclient.TestUserCenterFeignClient;
import cn.zbq.springcloud.contentcenter.sentineltest.TestControllerBlockHandlerClass;
import cn.zbq.springcloud.contentcenter.sentineltest.TestControllerFallbackHandlerClass;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO
 *
 * @author Dingwq
 * @since 2019/9/4 22:38
 */
@Slf4j
@RestController
@RefreshScope
public class TestController {
    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/test")
    public List<Share> testInsert() {
        // 1. 做插入
        Share share = new Share();
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        share.setTitle("xxx");
        share.setCover("xxx");
        share.setAuthor("zbq");
        share.setBuyCount(1);
        this.shareMapper.insertSelective(share);

        // 2. 查询所欲 查询当前数据库所有share 执行 select * from share
        List<Share> shares = this.shareMapper.selectAll();
        return shares;
    }

    /**
     * 测试： 服务发现，证明内容中心总能找到用户中心
     *
     * @return 用户中心所有实例的地址信息
     */
    @GetMapping("/test2")
    public List<ServiceInstance> getInstances() {
        // 查询指定服务的所有实例的信息
        // DiscoveryClient 由spring提供
        // consul/eureka/zookeeper...
        return this.discoveryClient.getInstances("user-center");
    }

    @Autowired
    private TestUserCenterFeignClient testUserCenterFeignClient;

    @GetMapping("user-get")
    public UserDTO query(UserDTO userDTO) {
        return testUserCenterFeignClient.query(userDTO);
    }

    @Autowired
    private TestBaiduFeignClient testBaiduFeignClient;

    @GetMapping("baidu")
    public String getBaiduIndex() {
        return testBaiduFeignClient.getIndex();
    }

    @Autowired
    private TestService testService;

    @GetMapping("test-a")
    public String testA() {
        this.testService.common();
        return "test-a";
    }

    @GetMapping("test-b")
    public String testB() {
        this.testService.common();
        return "test-b";
    }

    @GetMapping("test-hot")
    @SentinelResource(value = "hot")
    public String testHot(@RequestParam(required = false) String a, @RequestParam(required = false) String b) {

        return a + " " + b;
    }

    @GetMapping("test-add-flow-rule")
    public String testHot() {
        this.initFlowQpsRule();
        return "aaaaaaaa";
    }

    private void initFlowQpsRule() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule("shares/1");
        // set limit qps to 20
        rule.setCount(20);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setLimitApp("default");
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }


    @RequestMapping("test-sentinel-api")
    public String testSentinelAPI(@RequestParam(required = false) String a) {
        String resourceName = "test-sentinel-api";
        ContextUtil.enter(resourceName, "test-micro-service");
        Entry entry = null;
        try {
            // 定义一个sentinel 保护的资源，资源名称是test-sentinel-api，要保证唯一
            entry = SphU.entry(resourceName);
            // 被保护的业务逻辑
            // todo ...
            if (StringUtils.isBlank(a)) {
                throw new IllegalArgumentException("a 不能为空");
            }
            return a;
        } catch (BlockException e) {
            // 如果被保护的资源被限流或者降级就会抛出BlockException
            log.warn("限流或者降级了", e);
            return "限流或者降级了";
        } catch (IllegalArgumentException e2) {
            // 统计IllegalArgumentException【发生的次数，发生的占比】
            Tracer.trace(e2);
            return "参数非法";
        } finally {
            if (entry != null) {
                // 退出entry
                entry.exit();
            }
            ContextUtil.exit();
        }
    }

    /**
     * blockHandler 指定异常处理的方法
     */
    @RequestMapping("test-sentinel-resource")
    @SentinelResource(value = "test-sentinel-api",
            blockHandler = "block",
            blockHandlerClass = TestControllerBlockHandlerClass.class,
            fallback = "fallback",
            fallbackClass = TestControllerFallbackHandlerClass.class
    )
    public String testSentinelResource(@RequestParam(required = false) String a) {
        if (StringUtils.isBlank(a)) {
            throw new IllegalArgumentException("a cannot be blank");
        }
        return a;
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test-rest-template-sentinel/{userId}")
    public UserDTO test(@PathVariable Integer userId) {
        return this.restTemplate.getForObject("http://user-center/users/{userId}", UserDTO.class, userId);
    }

    @GetMapping("/tokenRelay/{userId}")
    public ResponseEntity<UserDTO> tokenRelay(@PathVariable Integer userId) {
        // 静态方式获取request，然后从request请求中拿到token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        String token = attributes.getRequest().getHeader("X-Token");

        // 新建header，设置token参数
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Token", token);

        // 发送请求
        return this.restTemplate.exchange(
                // 请求url
                "http://user-center/users/{userId}",
                // 请求方法 get 、post 等
                HttpMethod.GET,
                // http 请求体
                new HttpEntity<>(headers),
                // 请求响应实体
                UserDTO.class,
                // 请求参数
                userId
        );
    }

    @Value("${your.configuration}")
    private String yourConfiguration;

    @GetMapping("/test-config")
    public String testConfiguration() {
        return this.yourConfiguration;
    }
}
























