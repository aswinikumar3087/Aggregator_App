package com.bt.metb.aggregator.mqConfig;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@RefreshScope
public class EmppalAggregatorMqConfigReader {

    //EMPPALResponse to AGGREGATOR Inbound ResponseQueue
    @Value("${inbound.emppal.to.aggregator}")
    private String emppalResToAggregatorInboundQueueName;

    @Value("${inbound.emppal.to.aggregator.routing.key}")
    private String emppalResToAggregatorInboundQueueRoutingKey;

    @Value("${inbound.emppal.to.aggregator.exchange.name}")
    private String emppalResToAggregatorInboundQueueExchange;

    // AGGREGATOR TO EMPPAL OUTBOUND Queue
    @Value("${outbound.aggregator.to.emppal}")
    private String aggReqToEmppalOutboundQueueName;

    @Value("${outbound.aggregator.to.emppal.routing.key}")
    private String aggReqToEmppalOutboundQueueRoutingKey;

    @Value("${outbound.aggregator.to.emppal.exchange.name}")
    private String aggReqToEmppalOutboundQueueExchange;


    //EMPPAL to AGGREGATOR Hospital Queue

    @Value("${inbound.emppal.to.aggregatorHospital}")
    private String emppalResToAggregatorHospitalQueueName;

    @Value("${inbound.emppal.to.aggregatorHospital.routing.key}")
    private String emppalResToAggregatorHospitalQueueRoutingKey;

    @Value("${inbound.emppal.to.aggregatorHospital.exchange.name}")
    private String emppalResToAggregatorHospitalQueueExchange;

    // AGGREGATOR TO EMPPAL HOSPITAL  Queue
    @Value("${outbound.aggregator.to.emppalHospital}")
    private String aggrReqToEmppalHospitalQueueName;

    @Value("${outbound.aggregator.to.emppalHospital.routing.key}")
    private String aggReqToEmppalHospitalQueueRoutingKey;

    @Value("${outbound.aggregator.to.emppalHospital.exchange.name}")
    private String aggrReqToEmppalHospitalQueueExchange;

}
