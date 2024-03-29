package cn.zbq.springcloud.contentcenter.domain.entity.content;

import javax.persistence.*;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rocketmq_transaction_log")
public class RocketmqTransactionLog {
    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 事务id
     */
    @Column(name = "transaction_Id")
    private String transactionId;

    /**
     * 日志
     */
    private String log;
}