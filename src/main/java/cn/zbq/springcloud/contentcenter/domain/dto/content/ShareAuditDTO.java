package cn.zbq.springcloud.contentcenter.domain.dto.content;

import cn.zbq.springcloud.contentcenter.domain.enums.AuditStatusEnum;
import lombok.Data;

/**
 * 分享审核DTO
 *
 * @author Zbq
 * @since 2019/11/20 21:14
 */
@Data
public class ShareAuditDTO {
    /**
     * 审核状态
     */
    private AuditStatusEnum auditStatusEnum;
    /**
     * 原因
     */
    private String reason;
}
