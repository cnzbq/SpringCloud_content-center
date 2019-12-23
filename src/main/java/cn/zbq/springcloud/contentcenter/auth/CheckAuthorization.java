package cn.zbq.springcloud.contentcenter.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 权限验证注解
 *
 * @author Zbq
 * @since 2019/12/23 9:33
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckAuthorization {
    /**
     * 需要的角色
     *
     * @return
     */
    String value();
}
