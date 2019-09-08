package cn.zbq.springcloud.contentcenter.controller.content;

import cn.zbq.springcloud.contentcenter.domain.dto.content.ShareDTO;
import cn.zbq.springcloud.contentcenter.service.content.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分享控制器
 *
 * @author Zbq
 * @since 2019/9/8 18:33
 */
@RestController
@RequestMapping("/shares")
public class ShareController {
    @Autowired
    private ShareService shareService;

    @RequestMapping("/{id}")
    public ShareDTO findById(@PathVariable Integer id) {
        return this.shareService.findById(id);
    }
}
