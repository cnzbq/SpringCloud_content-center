package cn.zbq.springcloud.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.RequestOriginParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 实现获取请求来源
 *
 * @author Zbq
 * @since 2019/11/18 23:44
 */
//@Component
public class MyRequestOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest httpServletRequest) {
        // 从请求参数/请求头中获取origin参数
        // 如果获取不到就抛出异常

        // 从请求参数中获取origin的值
        String origin = httpServletRequest.getParameter("origin");
        // 从请求头header中获取origin的值
        //String origin = httpServletRequest.getHeader("origin");

        if (StringUtils.isBlank(origin)) {
            throw new IllegalArgumentException("origin must be specified");
        }

        return origin;
    }
}
