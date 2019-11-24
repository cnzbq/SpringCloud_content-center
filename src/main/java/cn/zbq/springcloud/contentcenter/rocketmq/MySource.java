package cn.zbq.springcloud.contentcenter.rocketmq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * spring cloud stream 自定义接口
 *
 * @author Zbq
 * @since 2019/11/24 21:17
 */
public interface MySource {

    String MY_OUTPUT = "my-output";

    @Output(MY_OUTPUT)
    MessageChannel output();
}
