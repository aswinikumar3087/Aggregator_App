package com.bt.metb.aggregator.listeners;

import com.bt.metb.MetbAbstractBase;
import com.bt.metb.aggregator.model.MnplRequestAttributeReader;
import com.bt.metb.aggregator.mqConfig.MnplAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.RmqConfigReader;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.responseProcessor.ResponseProcessor;
import com.bt.metb.mnp.stub.*;
import com.bt.metb.mnp.stub.Error;
import com.rabbitmq.client.Channel;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MnplMessageSubscriberTest extends MetbAbstractBase {

    @Mock
    public MnplAggregatorMqConfigReader mnplAggregatorMqConfigReader;
    @Mock
    ResponseProcessor iMessageProcessor;
    @Mock
    private MessagePublisher producerHandler;
    @Mock
    private MessagePublisher rabbitMsgProducer;
    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Channel channel;

    @InjectMocks
    private MnplMessageSubscriber mnplMessageSubscriber;

    @Mock
    private SimpleMessageConverter simpleMessageConverter;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private MnplRequestAttributeReader mnplRequestAttributeReader;


    @Mock
    private RmqConfigReader rmqConfigReader;
    private int numberOfInvocations;


    @Test
    public void receiveMessageFromInboundToMetbSuccess() throws Exception {

        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);
        doThrow(new RuntimeException()).when(iMessageProcessor).processMessage(textMessage);
        mnplMessageSubscriber.getMessageFromInboundToAggregator(textMessage, channel, tag);
        verify(iMessageProcessor).processMessage(textMessage);

    }


/*
    @Test
    public void consumeFromRabbitExecuteRetry() throws Exception {

        numberOfInvocations = 3;
        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);

        Mockito.when(mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueExchange()).thenReturn("SourceExchange");
        Mockito.when(mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueRoutingKey()).thenReturn("ewocs_to_metb_inb_rkey");
        Mockito.when(mnplAggregatorMqConfigReader.getAggregatorMnplHospitalQueueExchange()).thenReturn("SourceExchange");
        Mockito.when(mnplAggregatorMqConfigReader.getAggregatorMnplHospitalQueueRoutingKey()).thenReturn("ewocs_to_metb_inb_rkey");
        Mockito.when(rmqConfigReader.getRabbitRetry()).thenReturn("RetryCount");
        Mockito.when(mnplRequestAttributeReader.getMnplRetryCount()).thenReturn("3");

        for (int i = 0; i < 3; i++) {
            mnplMessageSubscriber.retryHandler(textMessage, channel, tag);

        }
        verify(producerHandler, times(numberOfInvocations)).publishMessage(anyString(), anyString(), any());

    }*/

/*    @Test
    public void receiveMessageFromInboundToMetbMandatoryError() throws Exception {

        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);
        when(simpleMessageConverter.fromMessage(textMessage)).thenReturn(getValidTestResponseBusinessError());
        when(mnplRequestAttributeReader.getMnplTechnicalParam()).thenReturn("M");
        when(mnplRequestAttributeReader.getMnplBusinessParam()).thenReturn("M");
        when(mnplRequestAttributeReader.getMNPLSuccessStateCode()).thenReturn("NOTOK");
        mnplMessageSubscriber.getMessageFromInboundToAggregator(textMessage, channel, tag);
        verify(mnplRequestAttributeReader).getMNPLSuccessStateCode();

    }*/


/*    @Test
    public void consumeFromRabbitThrowExceptionFromCoreTest() throws Exception {
        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);
        when(simpleMessageConverter.fromMessage(textMessage)).thenReturn(getValidTestResponseBusinessError());
        when(mnplRequestAttributeReader.getMnplTechnicalParam()).thenReturn("O");
        when(mnplRequestAttributeReader.getMnplBusinessParam()).thenReturn("O");
        when(mnplRequestAttributeReader.getMNPLSuccessStateCode()).thenReturn("NOTOK");
        doThrow(new RuntimeException()).when(iMessageProcessor).processMessage(textMessage);

        mnplMessageSubscriber.getMessageFromInboundToAggregator(textMessage, channel, tag);
        verify(iMessageProcessor).processMessage(textMessage);
    }*/

    @Test
    public void consumeFromRabbitThrowIOExceptionWhileAckTest() throws IOException {
        long tag = 1L;
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);

        doThrow(new IOException()).when(channel).basicAck(tag, true);
        mnplMessageSubscriber.getMessageFromInboundToAggregator(textMessage, channel, tag);
        verify(channel).basicAck(tag, true);
    }


    @After
    public void tearDown() {
        rabbitTemplate = null;
        producerHandler = null;
        mnplMessageSubscriber = null;

    }

}



