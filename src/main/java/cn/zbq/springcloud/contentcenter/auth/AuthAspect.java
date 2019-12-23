package cn.zbq.springcloud.contentcenter.auth;

import cn.zbq.springcloud.contentcenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 校验登录
 *
 * @author Zbq
 * @since 2019/12/21 15:45
 */
@Aspect
@Component
public class AuthAspect {
    @Autowired
    private JwtOperator jwtOperator;

    @Around("@annotation(cn.zbq.springcloud.contentcenter.auth.LoginCheck)")
    public Object checkLogin(ProceedingJoinPoint joinPoint) throws Throwable {

        checkToken();
        return joinPoint.proceed();
    }

    private void checkToken() {
        try {
            // 1. 从header里面获取token
            HttpServletRequest request = getHttpServletRequest();
            String token = request.getHeader("X-Token");

            // 2.校验token是否合法，不合法抛出异常，合法直接放行
            Boolean isValid = jwtOperator.validateToken(token);
            if (!isValid) {
                throw new SecurityException("token不合法！");
            }
            // 3. 如果校验成功，将用户信息设置到request的attributes里
            Claims claims = jwtOperator.getClaimsFromToken(token);
            request.setAttribute("id", claims.get("id"));
            request.setAttribute("wxNickname", claims.get("wxNickname"));
            request.setAttribute("role", claims.get("role"));
        } catch (Throwable throwable) {
            throw new SecurityException("token不合法！");
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        return attributes.getRequest();
    }

    @Around("@annotation(cn.zbq.springcloud.contentcenter.auth.CheckAuthorization)")
    public Object checkAuthorization(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 1. 验证token是否合法
            checkToken();
            // 2.验证用户角色是否匹配
            HttpServletRequest request = getHttpServletRequest();
            String role = (String) request.getAttribute("role");

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            // 拿到添加注解方法的定义
            Method method = methodSignature.getMethod();
            // 拿到方法上方的注解
            CheckAuthorization checkAuthorization = method.getAnnotation(CheckAuthorization.class);
            // 拿到注解的value
            String value = checkAuthorization.value();
            if (!Objects.equals(role, value)) {
                throw new SecurityException("用户无权访问");
            }
        } catch (Throwable throwable) {
            throw new SecurityException("用户无权访问", throwable);
        }
        return joinPoint.proceed();
    }
}
