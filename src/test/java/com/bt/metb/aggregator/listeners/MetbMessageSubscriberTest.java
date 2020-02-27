package com.bt.metb.aggregator.listeners;

import com.bt.metb.MetbAbstractBase;
import com.bt.metb.aggregator.messageprocessor.AggregatorCoreProcessor;
import com.bt.metb.aggregator.mqConfig.MetbAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.RmqConfigReader;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


public class MetbMessageSubscriberTest extends MetbAbstractBase {

    @Mock
    public MetbAggregatorMqConfigReader metbAggregatorMqConfigReader;
    @Mock
    private MessagePublisher producerHandler;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Channel channel;

    @InjectMocks
    private MetbMessageSubscriber metbMessageSubscriber;
    @Mock
    AggregatorCoreProcessor aggregatorCoreProcessor;
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RmqConfigReader rmqConfigReader;
    private int numberOfInvocations;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void receiveMessageFromInboundToMetb() throws Exception {

        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);

        Mockito.when(objectMapper.readValue(anyString(),any(TypeReference.class))).thenReturn(new HashMap<>());
        metbMessageSubscriber.getMessageFromInboundToAggregator(textMessage, channel, tag);
        Mockito.verify(aggregatorCoreProcessor).doAggregation(any(Map.class));

    }


    @Test
    public void consumeFromRabbitThrowExceptionFromCoreTest() throws Exception {
        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);

        doThrow(new RuntimeException()).when(aggregatorCoreProcessor)
                .doAggregation(any(Map.class));
        Mockito.when(objectMapper.readValue(anyString(),any(TypeReference.class))).thenReturn(new HashMap<>());
        metbMessageSubscriber.getMessageFromInboundToAggregator(textMessage, channel, tag);
        Mockito.verify(aggregatorCoreProcessor).doAggregation(any(Map.class));
    }

    @Test
    public void consumeFromRabbitThrowIOExceptionWhileAckTest() throws IOException {
        long tag = 1L;
        numberOfInvocations = 1;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);

        doThrow(new IOException()).when(channel).basicAck(tag, true);
        metbMessageSubscriber.getMessageFromInboundToAggregator(textMessage, channel, tag);
        verify(channel, times(numberOfInvocations)).basicAck(tag, true);
    }


    @After
    public void tearDown() {
        rabbitTemplate = null;
        producerHandler = null;
        metbMessageSubscriber = null;

    }


    @Test
    public void consumeFromRabbitThrowIOExceptionInExecuteRetry() throws IOException {
        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);

        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueExchange()).thenReturn("SourceExchange");
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueRoutingKey()).thenReturn("ewocs_to_metb_inb_rkey");
        doThrow(new IOException()).when(channel).basicAck(tag, true);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
        metbMessageSubscriber.retryHandler(textMessage, channel, tag);
        verify(producerHandler).publishMessage(anyString(), anyString(), any());

    }


    @Test
    public void consumeFromRabbitExecuteRetry() throws Exception {

        numberOfInvocations = 2;
        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);

        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueExchange()).thenReturn("SourceExchange");
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueRoutingKey()).thenReturn("ewocs_to_metb_inb_rkey");
        Mockito.when(rmqConfigReader.getRabbitRetry()).thenReturn("RetryCount");
        Mockito.when(rmqConfigReader.getRabbitRetryCount()).thenReturn(3);


        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
        for (int i = 0; i < 3; i++) {

            metbMessageSubscriber.retryHandler(textMessage, channel, tag);

        }
        verify(producerHandler, times(numberOfInvocations)).publishMessage(anyString(), anyString(), any());

    }


}


