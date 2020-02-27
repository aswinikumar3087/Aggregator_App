package com.bt.metb.aggregator.producer;

import com.bt.metb.MetbAbstractBase;
import com.bt.metb.aggregator.mqConfig.MetbAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.RmqConfigReader;
import com.rabbitmq.client.Channel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MessagePublisherTest extends MetbAbstractBase {

    @InjectMocks
    private MessagePublisher producerHandler;

    @Mock
    private Channel channel;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private MetbAggregatorMqConfigReader metbAggregatorMqConfigReader;

    @Mock
    private RmqConfigReader rmqConfigReader;
    private int numberOfInvocations;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        rabbitTemplate = null;
        producerHandler = null;
    }


    @Test
    public void publishMessage() throws Exception {


        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);
        producerHandler.publishMessage("AggregatorExchange", "metbcore_to_aggcore_rkey", textMessage);
        verify(rabbitTemplate).convertAndSend("AggregatorExchange", "metbcore_to_aggcore_rkey", textMessage);

    }

    @Test
    public void publishMessageWithException() throws Exception {


        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);
        producerHandler.publishMessage("AggregatorExchange", "metbcore_to_aggcore_rkey", textMessage);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Message.class));

    }

    @Test
    public void processResponse() throws Exception {


        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        producerHandler.processResponse("AggregatorExchange", "metbcore_to_aggcore_rkey", request);
        verify(rabbitTemplate).convertAndSend("AggregatorExchange", "metbcore_to_aggcore_rkey", request);

    }

    @Test
    public void processResponseWithException() throws Exception {


        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        producerHandler.processResponse("AggregatorExchange", "metbcore_to_aggcore_rkey", request);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(String.class));

    }


}