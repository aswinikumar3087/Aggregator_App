package com.bt.metb.aggregator.listeners;

import com.bt.metb.aggregator.messageprocessor.AggregatorCoreProcessor;
import com.bt.metb.aggregator.mqConfig.MetbAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.RmqConfigReader;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


/**
 * Class acting as a subscriber of messages from Rabbit Message Queues for Aggregator application.
 *
 * @author Ananya Banerjee
 * @version 1.0
 */

@Component
@RefreshScope
public class MetbMessageSubscriber implements AggregatorMessageSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(MetbMessageSubscriber.class);
    @Autowired
    @Qualifier("aggregatorCoreProcessor")
    private AggregatorCoreProcessor aggregatorCoreProcessor;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RmqConfigReader rmqConfigReader;
    @Autowired
    private MessagePublisher producerHandler;
    @Autowired
    private MetbAggregatorMqConfigReader metbAggregatorMqConfigReader;


    @RabbitListener(queues = "${inbound.metb.to.aggregator}", containerFactory = "prefetchRabbitListenerContainerFactory")
    public void getMessageFromInboundToAggregator(final Message data, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        try {
            byte[] requestXmlFromBytes = data.getBody();
            String requestXml = new String (requestXmlFromBytes);
            logger.debug("MetbMessageSubscriber.getMessageFromInboundToMetb() :: Payload  map from METB TO AGGREGATOR inbound queue :: {} ",requestXml);

            Map<Object,Object> map = objectMapper.readValue(requestXml, new TypeReference<Map<String ,Object>>() {});
                aggregatorCoreProcessor.doAggregation(map);
                logger.debug("MetbMessageSubscriber.getMessageFromInboundToMetb() :: Payload  map from METB TO AGGREGATOR inbound queue :: {} ",map);
        } catch (Exception e) {
            logger.error("MetbMessageSubscriber.getMessageFromInboundToMetb() :: Exception Occured :: ", e);
            retryHandler(data, channel, tag);
        } finally {
            try {
                channel.basicAck(tag, true);
            } catch (IOException e1) {
                logger.error("MetbMessageSubscriber.getMessageFromInboundToMetb() :: Exception Occured :: ", e1);
            }

        }
    }


    public void retryHandler(final Message data, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        try {
            Map<String, Object> headerMap = data.getMessageProperties().getHeaders();

            if (!(headerMap.containsKey(rmqConfigReader.getRabbitRetry()))) {
                logger.debug("MetbMessageSubscriber.RetryHandler() :: before 1st Retry.");

                headerMap.put(rmqConfigReader.getRabbitRetry(), 1);
                producerHandler.publishMessage(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueExchange(), metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueRoutingKey(), data);
                channel.basicAck(tag, true);
            } else {
                int count = Integer.parseInt(headerMap.get(rmqConfigReader.getRabbitRetry()).toString());

                if (count == rmqConfigReader.getRabbitRetryCount() - 1) {
                    logger.debug("MetbMessageSubscriber.RetryHandler():: before 3rd Retry.:: {}", count);
                    String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                    headerMap.put(rmqConfigReader.getRabbitRetry(), count + 1);
                    headerMap.put("timestamp", currentDate);
                    producerHandler.publishMessage(metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueExchange(), metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueRoutingKey(), data);
                    channel.basicAck(tag, true);
                } else {
                    headerMap.put(rmqConfigReader.getRabbitRetry(), count + 1);
                    logger.debug("MetbMessageSubscriber.RetryHandler() retries:: before ::{} Retry. {}", (count + 1), count);
                    producerHandler.publishMessage(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueExchange(), metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueRoutingKey(), data);
                    channel.basicAck(tag, true);
                }
            }
        } catch (IOException e1) {
            logger.error("MetbMessageSubscriber.RetryHandler() :: Exception Occured while sending ACK :: ", e1);
        } catch (Exception e1) {
            logger.error("MetbMessageSubscriber.RetryHandler() :: Exception Occured  :: ", e1);
        }
    }



}
