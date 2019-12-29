package cn.zbq.springcloud.contentcenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户增加积分dto
 *
 * @author Zbq
 * @since 2019/12/29 13:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddBonusDTO implements Serializable {

    private static final long serialVersionUID = 5851895403103139237L;

    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 积分
     */
    private Integer bonus;
}
