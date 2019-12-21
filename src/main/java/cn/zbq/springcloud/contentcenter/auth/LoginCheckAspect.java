package cn.zbq.springcloud.contentcenter.auth;

import cn.zbq.springcloud.contentcenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 校验登录
 *
 * @author Zbq
 * @since 2019/12/21 15:45
 */
@Aspect
@Component
public class LoginCheckAspect {
    @Autowired
    private JwtOperator jwtOperator;

    @Around("@annotation(cn.zbq.springcloud.contentcenter.auth.LoginCheck)")
    public Object checkLogin(ProceedingJoinPoint joinPoint){
        try {
            // 1. 从header里面获取token
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
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

            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throw new SecurityException("token不合法！");
        }
    }
}
