package com.bt.metb.aggregator.mqBeanDefinition;

import com.bt.metb.aggregator.mqConfig.EmppalAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.MetbAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.MnplAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.RmqConfigReader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class AggregatorBeanDefinitionTest {

    @Mock
    public RmqConfigReader rmqConfigReader;
    @Mock
    public EmppalAggregatorMqConfigReader emppalAggregatorMqConfigReader;
    @Mock
    private MetbAggregatorMqConfigReader metbAggregatorMqConfigReader;
    @Mock
    private MnplAggregatorMqConfigReader mnplToAggregatorConfigReader;
    @InjectMocks
    private AggregatorBeanDefinition aggregatorBeanDefinition;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

    }


    @Test
    public void getQueueForEmppalToAggregatorInbound() {
        Mockito.when(emppalAggregatorMqConfigReader.getEmppalResToAggregatorInboundQueueName()).thenReturn("EMPPAL_TO_AGGCORE_INB");
        aggregatorBeanDefinition.getEmppalAggregatorInboundQueue();
        verify(emppalAggregatorMqConfigReader).getEmppalResToAggregatorInboundQueueName();
    }

    @Test
    public void getQueueForEmppalToAggregatorHospital() {
        Mockito.when(emppalAggregatorMqConfigReader.getEmppalResToAggregatorHospitalQueueName()).thenReturn("EMPPAL_TO_AGGCORE_INB_H");
        aggregatorBeanDefinition.getEmppalAggregatorHospitalqueue();
        verify(emppalAggregatorMqConfigReader).getEmppalResToAggregatorHospitalQueueName();
    }

    @Test
    public void getExchangeForEmppalToAggregatorInboundQueue() {
        Mockito.when(emppalAggregatorMqConfigReader.getEmppalResToAggregatorInboundQueueExchange()).thenReturn("AggregatorExchange");
        aggregatorBeanDefinition.getEmppalAggregatorInboundQueueExchange();
        verify(emppalAggregatorMqConfigReader).getEmppalResToAggregatorInboundQueueExchange();
    }

    @Test
    public void getExchangeForEmppalToAggregatorHospitalQueue() {
        Mockito.when(emppalAggregatorMqConfigReader.getEmppalResToAggregatorHospitalQueueExchange()).thenReturn("AggregatorExchange");
        aggregatorBeanDefinition.getEmppalAggregatorHospitalqueueExchange();
        verify(emppalAggregatorMqConfigReader).getEmppalResToAggregatorHospitalQueueExchange();
    }

    @Test
    public void getBindingForEmppalToAggregatorInboundqueue() {
        Mockito.when(emppalAggregatorMqConfigReader.getEmppalResToAggregatorInboundQueueExchange()).thenReturn("AggregatorExchange");
        Mockito.when(emppalAggregatorMqConfigReader.getEmppalResToAggregatorInboundQueueName()).thenReturn("EMPPAL_TO_AGGCORE_INB");
        Mockito.when(emppalAggregatorMqConfigReader.getEmppalResToAggregatorInboundQueueRoutingKey()).thenReturn("emppal_to_aggcore_inb_rkey");
        aggregatorBeanDefinition.declareBindingForEmppalAggregatorInboundqueue();
        verify(emppalAggregatorMqConfigReader, times(2)).getEmppalResToAggregatorInboundQueueExchange();
        verify(emppalAggregatorMqConfigReader).getEmppalResToAggregatorInboundQueueName();
        verify(emppalAggregatorMqConfigReader, times(2)).getEmppalResToAggregatorInboundQueueRoutingKey();
    }


    @Test
    public void getBindingForEmppalToAggregatorHospitalqueue() {
        Mockito.when(emppalAggregatorMqConfigReader.getEmppalResToAggregatorHospitalQueueExchange()).thenReturn("AggregatorExchange");
        Mockito.when(emppalAggregatorMqConfigReader.getEmppalResToAggregatorHospitalQueueName()).thenReturn("EMPPAL_TO_AGGCORE_INB_H");
        Mockito.when(emppalAggregatorMqConfigReader.getEmppalResToAggregatorHospitalQueueRoutingKey()).thenReturn("emppal_to_aggcore_inb_h_rkey");
        aggregatorBeanDefinition.declareBindingForEmppalAggregatorHospitalqueue();
        verify(emppalAggregatorMqConfigReader).getEmppalResToAggregatorHospitalQueueExchange();
        verify(emppalAggregatorMqConfigReader).getEmppalResToAggregatorHospitalQueueName();
        verify(emppalAggregatorMqConfigReader).getEmppalResToAggregatorHospitalQueueRoutingKey();
    }


    ////


    @Test
    public void getQueueForMnplToAggregatorInbound() {
        Mockito.when(mnplToAggregatorConfigReader.getMnplAggregatorInboundQueueName()).thenReturn("MNPL_TO_AGGCORE_INB");
        aggregatorBeanDefinition.getMnplAggregatorInboundQueue();
        verify(mnplToAggregatorConfigReader).getMnplAggregatorInboundQueueName();
    }

    @Test
    public void getQueueForMnplToAggregatorHospital() {
        Mockito.when(mnplToAggregatorConfigReader.getMnplAggregatorHospitalQueueName()).thenReturn("MNPL_TO_AGGCORE_INB_H");
        aggregatorBeanDefinition.getMnplAggregatorHospitalqueue();
        verify(mnplToAggregatorConfigReader).getMnplAggregatorHospitalQueueName();
    }

    @Test
    public void getExchangeForMnplToAggregatorInboundQueue() {
        Mockito.when(mnplToAggregatorConfigReader.getMnplAggregatorInboundQueueExchange()).thenReturn("AggregatorExchange");
        aggregatorBeanDefinition.getMnplAggregatorInboundQueueExchange();
        verify(mnplToAggregatorConfigReader).getMnplAggregatorInboundQueueExchange();
    }

    @Test
    public void getExchangeForMnplToAggregatorHospitalQueue() {
        Mockito.when(mnplToAggregatorConfigReader.getMnplAggregatorHospitalQueueExchange()).thenReturn("AggregatorExchange");
        aggregatorBeanDefinition.getMnplAggregatorHospitalqueueExchange();
        verify(mnplToAggregatorConfigReader).getMnplAggregatorHospitalQueueExchange();
    }

    @Test
    public void getBindingForMnplToAggregatorInboundqueue() {
        Mockito.when(mnplToAggregatorConfigReader.getMnplAggregatorInboundQueueExchange()).thenReturn("AggregatorExchange");
        Mockito.when(mnplToAggregatorConfigReader.getMnplAggregatorInboundQueueName()).thenReturn("MNPL_TO_AGGCORE_INB");
        Mockito.when(mnplToAggregatorConfigReader.getMnplAggregatorInboundQueueRoutingKey()).thenReturn("mnpl_to_aggcore_inb_rkey");
        aggregatorBeanDefinition.declareBindingForMnplAggregatorInboundqueue();
        verify(mnplToAggregatorConfigReader).getMnplAggregatorInboundQueueExchange();
        verify(mnplToAggregatorConfigReader).getMnplAggregatorInboundQueueName();
        verify(mnplToAggregatorConfigReader).getMnplAggregatorInboundQueueRoutingKey();
    }


    @Test
    public void getBindingForMnplToAggregatorHospitalqueue() {
        Mockito.when(mnplToAggregatorConfigReader.getMnplAggregatorHospitalQueueExchange()).thenReturn("AggregatorExchange");
        Mockito.when(mnplToAggregatorConfigReader.getMnplAggregatorHospitalQueueName()).thenReturn("MNPL_TO_AGGCORE_INB_H");
        Mockito.when(mnplToAggregatorConfigReader.getMnplAggregatorHospitalQueueRoutingKey()).thenReturn("mnpl_to_aggcore_inb_h_rkey");
        aggregatorBeanDefinition.declareBindingForMnplMetbHospitalqueue();
        verify(mnplToAggregatorConfigReader).getMnplAggregatorHospitalQueueExchange();
        verify(mnplToAggregatorConfigReader).getMnplAggregatorHospitalQueueName();
        verify(mnplToAggregatorConfigReader).getMnplAggregatorHospitalQueueRoutingKey();
    }

    ////


    @Test
    public void getQueueForMetbToAggregatorInbound() {
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueName()).thenReturn("METBCORE_TO_AGGCORE");
        aggregatorBeanDefinition.getMetbToAggregatorInboundQueue();
        verify(metbAggregatorMqConfigReader).getMetbToAggregatorInboundQueueName();
    }

    @Test
    public void getQueueForMetbToAggregatorHospital() {
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueName()).thenReturn("METBCORE_TO_AGGCORE_H");
        aggregatorBeanDefinition.getMetbAggregatorHospitalqueue();
        verify(metbAggregatorMqConfigReader).getMetbToAggregatorHospitalQueueName();
    }

    @Test
    public void getExchangeForMetbToAggregatorInboundQueue() {
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueExchange()).thenReturn("AggregatorExchange");
        aggregatorBeanDefinition.getMetbToAggregatorInboundQueueExchange();
        verify(metbAggregatorMqConfigReader).getMetbToAggregatorInboundQueueExchange();
    }

    @Test
    public void getExchangeForMetbToAggregatorHospitalQueue() {
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueExchange()).thenReturn("AggregatorExchange");
        aggregatorBeanDefinition.getMetbAggregatorHospitalqueueExchange();
        verify(metbAggregatorMqConfigReader).getMetbToAggregatorHospitalQueueExchange();
    }

    @Test
    public void getBindingForMetbToAggregatorInboundqueue() {
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueExchange()).thenReturn("AggregatorExchange");
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueName()).thenReturn("METBCORE_TO_AGGCORE");
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueRoutingKey()).thenReturn("metbcore_to_aggcore_rkey");
        aggregatorBeanDefinition.getMetbToAggregatorInboundQueue();
        verify(metbAggregatorMqConfigReader).getMetbToAggregatorHospitalQueueExchange();
        verify(metbAggregatorMqConfigReader).getMetbToAggregatorInboundQueueName();
        verify(metbAggregatorMqConfigReader).getMetbToAggregatorHospitalQueueRoutingKey();
    }


    @Test
    public void getBindingForMetbToAggregatorHospitalqueue() {
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueExchange()).thenReturn("AggregatorExchange");
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueName()).thenReturn("METBCORE_TO_AGGCORE_H");
        Mockito.when(metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueRoutingKey()).thenReturn("metbcore_to_aggcore_h_rkey");
        aggregatorBeanDefinition.declareBindingForMetbAggregatorHospitalqueue();
        verify(metbAggregatorMqConfigReader).getMetbToAggregatorHospitalQueueExchange();
        verify(metbAggregatorMqConfigReader).getMetbToAggregatorHospitalQueueName();
        verify(metbAggregatorMqConfigReader).getMetbToAggregatorHospitalQueueRoutingKey();
    }

    ////


/*    @Test
    public void initCachingConnectionFactory() {

        Mockito.when(rmqConfigReader.getRabbitUsername()).thenReturn("USERNAME");
        Mockito.when(rmqConfigReader.getRabbitPassword()).thenReturn("PASSWORD");
        Mockito.when(rmqConfigReader.getRabbitHostAddresses()).thenReturn("ADDRESS");
        Mockito.when(rmqConfigReader.getRabbitVhost()).thenReturn("METBVHOST");
        aggregatorBeanDefinition.initCachingConnectionFactory();
        verify(rmqConfigReader).getRabbitUsername();
        verify(rmqConfigReader).getRabbitPassword();
        verify(rmqConfigReader).getRabbitHostAddresses();
        verify(rmqConfigReader).getRabbitVhost();


    }*/


    @Test
    public void prefetchRabbitListenerContainerFactory() {
        CachingConnectionFactory cf = new CachingConnectionFactory();
        RabbitListenerContainerFactory<?> factory = aggregatorBeanDefinition.prefetchRabbitListenerContainerFactory(cf);
        Assert.assertNotNull(factory);
    }

}
