package cn.zbq.springcloud.contentcenter.rocketmq;

import cn.zbq.springcloud.contentcenter.dao.content.RocketmqTransactionLogMapper;
import cn.zbq.springcloud.contentcenter.domain.dto.content.ShareAuditDTO;
import cn.zbq.springcloud.contentcenter.domain.entity.content.RocketmqTransactionLog;
import cn.zbq.springcloud.contentcenter.service.content.ShareService;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

/**
 * 增加积分事务消息监听接口
 *
 * @author Zbq
 * @since 2019/11/23 20:47
 */
// txProducerGroup 要与发送事物消息的group保持一致
@RocketMQTransactionListener(txProducerGroup = "tx-add-bonus-group")
public class AddTransactionListener implements RocketMQLocalTransactionListener {

    @Autowired
    private ShareService shareService;
    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    /**
     * 执行本地事物(图中第3步，执行本地事物)
     *
     * @param message sendMessageInTransaction 中的Message
     * @param arg     sendMessageInTransaction中的arg
     * @return 执行状态（提交/回滚...）
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {

        MessageHeaders headers = message.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);
        Integer shareId = Integer.valueOf((String) headers.get("share_id"));
        String dtoStr = (String) headers.get("dto");
        ShareAuditDTO auditDTO = JSONObject.parseObject(dtoStr, ShareAuditDTO.class);

        try {
            this.shareService.auditByIdWithRocketMqLog(shareId, auditDTO, transactionId);
            // 本地事物执行成功，mq执行提交，投递消费者
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            // 本地事物执行失败，mq消息回滚，丢弃消息
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 检查本地事物（图中第5步，即未收到生产者提交/回滚的确认所执行的方法
     *
     * @param message sendMessageInTransaction 中的Message
     * @return 执行状态（提交/回滚...）
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        MessageHeaders headers = message.getHeaders();
        String transactionId = (String) headers.get(RocketMQHeaders.TRANSACTION_ID);

        // select * from XXX where transaction_id = xxx
        RocketmqTransactionLog rocketmqTransactionLog = rocketmqTransactionLogMapper.selectOne(
                RocketmqTransactionLog
                        .builder()
                        .transactionId(transactionId)
                        .build()
        );
        if (rocketmqTransactionLog != null) {
            return RocketMQLocalTransactionState.COMMIT;
        }
        return RocketMQLocalTransactionState.ROLLBACK;
    }
}
