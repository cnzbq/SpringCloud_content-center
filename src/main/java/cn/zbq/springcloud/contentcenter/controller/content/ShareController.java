package cn.zbq.springcloud.contentcenter.controller.content;

import cn.zbq.springcloud.contentcenter.auth.LoginCheck;
import cn.zbq.springcloud.contentcenter.domain.dto.content.ShareDTO;
import cn.zbq.springcloud.contentcenter.domain.entity.content.Share;
import cn.zbq.springcloud.contentcenter.service.content.ShareService;
import cn.zbq.springcloud.contentcenter.util.JwtOperator;
import com.github.pagehelper.PageInfo;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
    @Autowired
    private JwtOperator jwtOperator;

    @LoginCheck
    @RequestMapping("/{id}")
    public ShareDTO findById(@PathVariable Integer id) {
        return this.shareService.findById(id);
    }

    @GetMapping("/q")
    public PageInfo<Share> q(@RequestParam(required = false) String title,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                             @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                             @RequestHeader(value = "X-Token", required = false) String token) {

        // 注意点： pageSize 务必做控制
        if (pageSize > 100) {
            pageSize = 100;
        }

        Integer userId = null;
        if (StringUtils.isNotBlank(token)) {
            Claims claims = jwtOperator.getClaimsFromToken(token);
            userId = (Integer) claims.get("id");
        }

        return this.shareService.q(title, pageNo, pageSize, userId);
    }

    @GetMapping("/exchange/{id}")
    @LoginCheck
    public Share exchangeById(@PathVariable Integer id, HttpServletRequest request) {
        return this.shareService.exchangeById(id, request);
    }
}
