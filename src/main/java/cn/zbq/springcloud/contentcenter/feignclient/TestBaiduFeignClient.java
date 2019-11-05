package cn.zbq.springcloud.contentcenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * feign 脱离Ribbon的使用
 * FeignClient 中的name或value必须有一项不为空
 * @author Zbq
 * @since 2019/11/5 21:21
 */
@FeignClient(name = "baidu", url = "www.baidu.com")
public interface TestBaiduFeignClient {

    @GetMapping("")
    String getIndex();
}
