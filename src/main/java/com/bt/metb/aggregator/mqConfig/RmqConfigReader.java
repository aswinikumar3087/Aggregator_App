package com.bt.metb.aggregator.mqConfig;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@RefreshScope
public class RmqConfigReader {


    @Value("${spring.rabbitmq.username}")
    private String rabbitUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitPassword;

    @Value("${spring.rabbitmq.virtual-host}")
    private String rabbitVhost;

    @Value("${spring.rabbitmq.addresses}")
    private String rabbitHostAddresses;

    @Value("${spring.rabbitmq.listener.simple.retry.max-attempts}")
    private int rabbitRetryCount;

    @Value("${retry}")
    private String rabbitRetry;

    @Value("${outbound.mnpl.aggcore.to.metbsb.routingkey}")
    private String routingKeyForMNPL;

    @Value("${aggregator.exchange.name}")
    private String aggregatorExchnage;


}