package com.bt.metb.aggregator.listeners;

import com.bt.metb.MetbAbstractBase;
import com.bt.metb.aggregator.mqConfig.EmppalAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.RmqConfigReader;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.responseProcessor.ResponseProcessor;
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

import java.io.IOException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;


public class EmppalMessageSubscriberTest extends MetbAbstractBase {


    @Mock
    private MessagePublisher producerHandler;

    @Mock
    ResponseProcessor emppalResponseProcessorImpl;

    @Mock
    private Channel channel;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private EmppalMessageSubscriber emppalMessageSubscriber;

    @Mock
    private EmppalAggregatorMqConfigReader emppalAggregatorMqConfigReader;

    @Mock
    private RmqConfigReader rmqConfigReader;
    private int numberOfInvocations;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /*@Test
    public void receiveMessageFromEmppalToMetb() throws Exception {

        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);
        emppalMessageSubscriber.getMessageFromInboundToAggregator(textMessage, channel, tag);
        verify(emppalResponseProcessorImpl).processMessage(textMessage);


    }
*/

   /* @Test
    public void consumeFromRabbitThrowExceptionFromCoreTest() throws Exception {
        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);

        doThrow(new RuntimeException()).when(emppalResponseProcessorImpl).processMessage(textMessage);
        emppalMessageSubscriber.getMessageFromInboundToAggregator(textMessage, channel, tag);
        verify(emppalResponseProcessorImpl).processMessage(textMessage);
    }*/

    @Test
    public void consumeFromRabbitThrowIOExceptionWhileAckTest() throws IOException {
        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);

        doThrow(new IOException()).when(channel).basicAck(tag, true);
        emppalMessageSubscriber.getMessageFromInboundToAggregator(textMessage, channel, tag);
        verify(channel).basicAck(tag, true);
    }


    @After
    public void tearDown() {
        rabbitTemplate = null;
        producerHandler = null;
        emppalMessageSubscriber = null;

    }

}


