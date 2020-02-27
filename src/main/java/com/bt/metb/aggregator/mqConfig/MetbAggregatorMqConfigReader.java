package com.bt.metb.aggregator.mqConfig;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@RefreshScope
public class MetbAggregatorMqConfigReader {

    //METB to Aggregator Inbound Queue
    @Value("${inbound.metb.to.aggregator}")
    private String metbToAggregatorInboundQueueName;

    @Value("${inbound.metb.to.aggregator.routing.key}")
    private String metbToAggregatorInboundQueueRoutingKey;

    @Value("${inbound.metb.to.aggregator.exchange.name}")
    private String metbToAggregatorInboundQueueExchange;


    //METB to Aggregator Hospital Queue

    @Value("${inbound.metb.to.aggregatorHospital}")
    private String metbToAggregatorHospitalQueueName;

    @Value("${inbound.metb.to.aggregatorHospital.routing.key}")
    private String metbToAggregatorHospitalQueueRoutingKey;

    @Value("${inbound.metb.to.aggregatorHospital.exchange.name}")
    private String metbToAggregatorHospitalQueueExchange;


    //Aggregator to METB Outbound Queue
    @Value("${inbound.aggregator.to.metb}")
    private String aggregatorToMetbOutboundQueueName;

    @Value("${inbound.aggregator.to.metb.routing.key}")
    private String routingKeyAggregatorToMetbOutbound;

    @Value("${inbound.aggregator.to.metb.exchange.name}")
    private String aggregatorToMetbOutboundQueueExchange;

    //Aggregator to METB Outbound Hospital
    @Value("${outbound.aggregator.to.metbcoreHospital.routing.key}")
    private String routingKeyAggregatorToMetbCoreHOutbound;

    @Value("${outbound.aggregator.to.metbcoreHospital}")
    private String aggreagtorToMetbCoreOutboundHQueue;

    @Value("${outbound.aggregator.to.metbcoreHospital.exchange.name}")
    private String aggregatorToMetbOutboundHositalQExchange;
}
