package com.bt.metb.aggregator.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * class to post the message back to RABBIT MQ.
 * scenarios.errorResponse,sucessResponse to RABBIT MQ, from there service bus
 * will post to the IBM MQ(source system queue)
 *
 * @author 612031539
 */
@Component
public class MessagePublisher {


    private static final Logger logger = LoggerFactory.getLogger(MessagePublisher.class);

    @Autowired
    RabbitTemplate rabbitTemplate;

    public MessagePublisher() {
    }


    public void publishMessage(String exchangeName, String routingKey, Message msg) throws AmqpException {

            rabbitTemplate.convertAndSend(exchangeName, routingKey, msg);
            logger.debug("MessagePublisher.publishMessage()-successfully published to inbound/hospital queue. routingKey: {}", routingKey);
    }

    public void processResponse(String exchangeName, String routingKey, String msg) throws AmqpException {

            rabbitTemplate.convertAndSend(exchangeName, routingKey, msg);
            logger.debug("MessagePublisher.processResponse()-successfully published to outbound queue. routingKey: {}", routingKey);

    }
}
