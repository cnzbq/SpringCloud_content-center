package cn.zbq.springcloud.contentcenter.domain.dto.user;

import lombok.Data;

import java.util.Date;

/**
 * 用户DTO
 *
 * @author Zbq
 * @since 2019/9/8 18:15
 */
@Data
public class UserDTO {
    /**
     * id
     */
    private Integer id;

    /**
     * 微信id
     */
    private String wxId;

    /**
     * 微信昵称
     */
    private String wxNickname;

    /**
     * 角色
     */
    private String roles;

    /**
     * 头像地址
     */
    private String avatarUrl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 积分
     */
    private Integer bonus;
}
