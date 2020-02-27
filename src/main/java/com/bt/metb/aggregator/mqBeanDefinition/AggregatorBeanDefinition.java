package com.bt.metb.aggregator.mqBeanDefinition;

import com.bt.metb.aggregator.mqConfig.EmppalAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.MetbAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.MnplAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.RmqConfigReader;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.DirectRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class AggregatorBeanDefinition {

    @Autowired
    public RmqConfigReader rmqConfigReader;

    @Autowired
    private EmppalAggregatorMqConfigReader emppalAggregatorMqConfigReader;

    @Autowired
    private MnplAggregatorMqConfigReader mnplAggregatorMqConfigReader;

    @Autowired
    private MetbAggregatorMqConfigReader metbAggregatorMqConfigReader;

    @Value("${rabbitmq.x-message-ttl}")
    private String ttl;

    @Value("${rabbitmq.x-message-ttl.value}")
    private long ttlValue;

    @Value("${rabbitmq.x-max-length}")
    private String maxlength;

    @Value("${rabbitmq.x-max-length.value}")
    private long maxLengthValue;

    @Value("${rabbitmq.x-dead-letter-exchange}")
    private String routingExchange;

    @Value("${rabbitmq.x-dead-letter-routing-key}")
    private String routingKey;

    /***********************EMPPAL -Aggregator***********************************/

    /* Creating a bean for EMPPAL TO Aggregator Inbound Message queue Exchange */
    @Bean
    public DirectExchange getEmppalAggregatorInboundQueueExchange() {
        return new DirectExchange(emppalAggregatorMqConfigReader.getEmppalResToAggregatorInboundQueueExchange());
    }

    /* Creating a bean for EMPPAL TO Aggregator Inbound  Message queue */
    @Bean
    public Queue getEmppalAggregatorInboundQueue() {

        return QueueBuilder.durable(emppalAggregatorMqConfigReader.getEmppalResToAggregatorInboundQueueName()).withArgument(ttl, ttlValue).
                withArgument(maxlength, maxLengthValue).withArgument(routingExchange, emppalAggregatorMqConfigReader.getEmppalResToAggregatorInboundQueueExchange()).
                withArgument(routingKey, emppalAggregatorMqConfigReader
                        .getEmppalResToAggregatorInboundQueueRoutingKey()).build();
    }

    /* Binding between Exchange and EMPPAL TO Aggregator Inbound Queue using routing key */
    @Bean
    public Binding declareBindingForEmppalAggregatorInboundqueue() {
        return BindingBuilder.bind(getEmppalAggregatorInboundQueue()).to(getEmppalAggregatorInboundQueueExchange())
                .with(emppalAggregatorMqConfigReader
                        .getEmppalResToAggregatorInboundQueueRoutingKey());
    }

    /* Creating a bean for EMPPAL TO Aggregator Hospital Message queue  Exchange */
    @Bean
    public DirectExchange getEmppalAggregatorHospitalqueueExchange() {
        return new DirectExchange(emppalAggregatorMqConfigReader
                .getEmppalResToAggregatorHospitalQueueExchange());
    }

    //* Creating a bean for the EMPPAL TO Aggregator Hospital Message queue *//
    @Bean
    public Queue getEmppalAggregatorHospitalqueue() {
        return QueueBuilder.durable(emppalAggregatorMqConfigReader
                .getEmppalResToAggregatorHospitalQueueName()).build();
    }

    //* Binding between Exchange and EMPPAL TO Aggregator Hospital Queue using routing key *//
    @Bean
    public Binding declareBindingForEmppalAggregatorHospitalqueue() {
        return BindingBuilder.bind(getEmppalAggregatorHospitalqueue()).to(getEmppalAggregatorHospitalqueueExchange())
                .with(emppalAggregatorMqConfigReader
                        .getEmppalResToAggregatorHospitalQueueRoutingKey());
    }

    /***********************MNPL -Aggregator***********************************/

    /* Creating a bean for MNPL TO Aggregator Inbound Message queue Exchange */
    @Bean
    public DirectExchange getMnplAggregatorInboundQueueExchange() {
        return new DirectExchange(mnplAggregatorMqConfigReader.getMnplAggregatorInboundQueueExchange());
    }

    /* Creating a bean for MNPL TO Aggregator Inbound  Message queue */
    @Bean
    public Queue getMnplAggregatorInboundQueue() {

        return QueueBuilder.durable(mnplAggregatorMqConfigReader.getMnplAggregatorInboundQueueName()).withArgument(ttl, ttlValue).
                withArgument(maxlength, maxLengthValue).withArgument(routingExchange, mnplAggregatorMqConfigReader.getMnplAggregatorHospitalQueueExchange()).
                withArgument(routingKey, mnplAggregatorMqConfigReader.getMnplAggregatorHospitalQueueRoutingKey()).build();
    }

    /* Binding between Exchange and MNPL TO Aggregator Inbound Queue using routing key */
    @Bean
    public Binding declareBindingForMnplAggregatorInboundqueue() {
        return BindingBuilder.bind(getMnplAggregatorInboundQueue()).to(getMnplAggregatorInboundQueueExchange())
                .with(mnplAggregatorMqConfigReader.getMnplAggregatorInboundQueueRoutingKey());
    }

        /* Creating a bean for MNPL TO Aggregator Hospital Message queue  Exchange */
    @Bean
    public DirectExchange getMnplAggregatorHospitalqueueExchange() {
        return new DirectExchange(mnplAggregatorMqConfigReader.getMnplAggregatorHospitalQueueExchange());
    }

    //* Creating a bean for the MNPL TO Aggregator Hospital Message queue *//
    @Bean
    public Queue getMnplAggregatorHospitalqueue() {
        return QueueBuilder.durable(mnplAggregatorMqConfigReader.getMnplAggregatorHospitalQueueName()).build();
    }

    //* Binding between Exchange and MNPL TO Aggregator Hospital Queue using routing key *//
    @Bean
    public Binding declareBindingForMnplMetbHospitalqueue() {
        return BindingBuilder.bind(getMnplAggregatorHospitalqueue()).to(getMnplAggregatorHospitalqueueExchange())
                .with(mnplAggregatorMqConfigReader.getMnplAggregatorHospitalQueueRoutingKey());
    }

    @Bean(name = "simpleMessageConverter")
    public SimpleMessageConverter simpleMessageConverter() {
        return new SimpleMessageConverter();
    }


    /***********************METB -AGGREGATOR***********************************/

    /* Creating a bean for METB TO AGGREGATOR Inbound Message queue Exchange */
    @Bean
    public DirectExchange getMetbToAggregatorInboundQueueExchange() {
        return new DirectExchange(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueExchange());
    }

    /* Creating a bean for METB TO AGGREGATOR Inbound  Message queue */
    @Bean
    public Queue getMetbToAggregatorInboundQueue() {

        return QueueBuilder.durable(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueName()).withArgument(ttl, ttlValue).
                withArgument(maxlength, maxLengthValue).withArgument(routingExchange, metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueExchange()).
                withArgument(routingKey, metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueRoutingKey()).build();
    }

    /* Binding between Exchange and METB TO AGGREGATOR  Inbound Queue using routing key */
    @Bean
    public Binding declareBindingForMetbAggregatorInboundQueueExchange() {
        return BindingBuilder.bind(getMetbToAggregatorInboundQueue()).to(getMetbToAggregatorInboundQueueExchange())
                .with(metbAggregatorMqConfigReader.getMetbToAggregatorInboundQueueRoutingKey());
    }

        /* Creating a bean for METB TO AGGREGATOR Hospital Message queue  Exchange */
    @Bean
    public DirectExchange getMetbAggregatorHospitalqueueExchange() {
        return new DirectExchange(metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueExchange());
    }

    //* Creating a bean for the METB TO AGGREGATOR Hospital Message queue *//
    @Bean
    public Queue getMetbAggregatorHospitalqueue() {
        return QueueBuilder.durable(metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueName()).build();
    }

    //* Binding between Exchange and METB TO AGGREGATOR Hospital Queue using routing key *//
    @Bean
    public Binding declareBindingForMetbAggregatorHospitalqueue() {
        return BindingBuilder.bind(getMetbAggregatorHospitalqueue()).to(getMetbAggregatorHospitalqueueExchange())
                .with(metbAggregatorMqConfigReader.getMetbToAggregatorHospitalQueueRoutingKey());
    }

    @Bean
    public RabbitTemplate initRabbitTemplate(CachingConnectionFactory cf) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(cf);
        rabbitTemplate.setMessageConverter(producerString2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageConverter producerString2MessageConverter() {
        return new SimpleMessageConverter();
    }

    @Bean
    public StringMessageConverter consumerStringMessageConverter() {
        return new StringMessageConverter();
    }

    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(consumerStringMessageConverter());
        return factory;
    }

    @Bean
    public RabbitListenerContainerFactory<?> prefetchRabbitListenerContainerFactory(CachingConnectionFactory cachingConnectionFactory) {
        DirectRabbitListenerContainerFactory factory = new DirectRabbitListenerContainerFactory();
        factory.setConnectionFactory(cachingConnectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

}
