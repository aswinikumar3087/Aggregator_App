package com.bt.metb.aggregator.listeners;

import com.bt.metb.aggregator.responseProcessor.ResponseProcessor;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Class acting as a subscriber of messages from Rabbit Message Queues for Aggregator application.
 * @author Ananya Banerjee
 * @version 1.0
 */
@Component
@RefreshScope
public class MnplMessageSubscriber implements AggregatorMessageSubscriber {

    private static final Logger log = LoggerFactory.getLogger(MnplMessageSubscriber.class);
    @Autowired
    @Qualifier("mnplResponseProcessorImpl")
    private ResponseProcessor iMessageProcessor;

    @RabbitListener(queues = "${inbound.mnpl.to.aggregator}", containerFactory = "prefetchRabbitListenerContainerFactory")
    public void getMessageFromInboundToAggregator(final Message data, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            byte[] requestXmlFromBytes = data.getBody();
            String requestXml = new String(requestXmlFromBytes);
            log.debug("MnplMessageSubscriber.getMessageFromInboundToAggregator() :: Payload  xml from MNPL TO METB inbound queue :: {} ", requestXml);
            iMessageProcessor.processMessage(data);
        } catch (Exception e) {
            log.error("MnplMessageSubscriber.getMessageFromInboundToAggregator() :: Exception Occured :: ", e);
        } finally {
            try {
                channel.basicAck(tag, true);
            } catch (IOException e1) {
                log.error("MnplMessageSubscriber.getMessageFromInboundToAggregator() :: Exception Occured :: ", e1);
            }
        }
    }


}
