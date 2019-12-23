package cn.zbq.springcloud.contentcenter.controller.content;

import cn.zbq.springcloud.contentcenter.auth.CheckAuthorization;
import cn.zbq.springcloud.contentcenter.domain.dto.content.ShareAuditDTO;
import cn.zbq.springcloud.contentcenter.domain.entity.content.Share;
import cn.zbq.springcloud.contentcenter.service.content.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * TODO
 *
 * @author Zbq
 * @since 2019/11/20 20:47
 */
@RestController
@RequestMapping("admin/shares")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShareAdminController {

    private final ShareService shareService;

    @PutMapping("/audit/{id}")
    @CheckAuthorization("admin")
    public Share auditById(@PathVariable("id") Integer id, @RequestBody ShareAuditDTO auditDTO) {

        // TODO 认证和授权

        return this.shareService.auditById(id, auditDTO);
    }
}
