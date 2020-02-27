package com.bt.metb.aggregator.mqConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@AllArgsConstructor
@NoArgsConstructor
@RefreshScope
public class MnplAggregatorMqConfigReader {

    //MNPL to AGGREGATOR Inbound Queue
    @Value("${inbound.mnpl.to.aggregator}")
    private String mnplAggregatorInboundQueueName;

    @Value("${inbound.mnpl.to.aggregator.routing.key}")
    private String mnplAggregatorInboundQueueRoutingKey;

    @Value("${inbound.mnpl.to.aggregator.exchange.name}")
    private String mnplAggregatorInboundQueueExchange;


    // AGGREGATOR TO MNPL to OUTBOUND Queue
    @Value("${outbound.aggregator.to.mnpl}")
    private String aggregatorMnplOutboundQueueName;

    @Value("${outbound.aggregator.to.mnpl.routing.key}")
    private String aggregatorMnplOutboundQueueRoutingKey;

    @Value("${outbound.aggregator.to.mnpl.exchange.name}")
    private String aggregatorMnplOutboundQueueExchange;



    //MNPL to AGGREGATOR Inbound Hospital Queue

    @Value("${inbound.mnpl.to.aggregatorHospital}")
    private String mnplAggregatorHospitalQueueName;

    @Value("${inbound.mnpl.to.aggregatorHospital.routing.key}")
    private String mnplAggregatorHospitalQueueRoutingKey;

    @Value("${inbound.mnpl.to.aggregatorHospital.exchange.name}")
    private String mnplAggregatorHospitalQueueExchange;



    // AGGREGATOR TO MNPL to OUTBOUND hospital Queue
    @Value("${outbound.aggregator.to.mnplHospital}")
    private String aggregatorMnplHospitalQueueName;

    @Value("${outbound.aggregator.to.mnplHospital.routing.key}")
    private String aggregatorMnplHospitalQueueRoutingKey;

    @Value("${outbound.aggregator.to.mnplHospital.exchange.name}")
    private String aggregatorMnplHospitalQueueExchange;


}
