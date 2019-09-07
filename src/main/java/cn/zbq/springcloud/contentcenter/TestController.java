package cn.zbq.springcloud.contentcenter;

import cn.zbq.springcloud.contentcenter.dao.content.ShareMapper;
import cn.zbq.springcloud.contentcenter.domain.entity.content.Share;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * TODO
 *
 * @author Dingwq
 * @since 2019/9/4 22:38
 */
@RestController
public class TestController {
    @Autowired
    private ShareMapper shareMapper;

    @GetMapping("/test")
    public List<Share> testInsert(){
        // 1. 做插入
        Share share = new Share();
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        share.setTitle("xxx");
        share.setCover("xxx");
        share.setAuthor("zbq");
        share.setBuyCount(1);
        this.shareMapper.insertSelective(share);

        // 2. 查询所欲 查询当前数据库所有share 执行 select * from share
        List<Share> shares = this.shareMapper.selectAll();
        return shares;
    }
}
