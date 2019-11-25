package cn.zbq.springcloud.contentcenter.domain.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户增加积分消息dto
 *
 * @author Zbq
 * @since 2019/11/22 21:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddBonusMagDTO implements Serializable {

    private static final long serialVersionUID = 791605758435025722L;
    /**
     * 为谁增加积分
     */
    private Integer userId;
    /**
     * 加多少积分
     */
    private Integer bonus;
}
