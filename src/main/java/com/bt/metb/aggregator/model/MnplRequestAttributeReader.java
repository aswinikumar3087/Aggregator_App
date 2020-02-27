package com.bt.metb.aggregator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@RefreshScope
public class MnplRequestAttributeReader implements Serializable {

    @Value("${MNPLServiceAddressingAction}")
    private String mNPLServiceAddressingAction;

    @Value("${MNPLServiceAddressingAddress}")
    private String mNPLServiceAddressingAddress;

    @Value("${MNPLServiceAddressingFrom}")
    private String mNPLServiceAddressingFrom;

    @Value("${MNPLServiceAddressingServiceName}")
    private String mNPLServiceAddressingServiceName;

    @Value("${MNPLServiceAddressingToAddress}")
    private String mNPLServiceAddressingToAddress;

    @Value("${MNPLServiceStateStateCode}")
    private String mNPLServiceStateStateCode;

    @Value("${MNPLServiceStateResendIndicator}")
    private String mNPLServiceStateResendIndicator;

    @Value("${MNPLServiceStateRetriesRemaining}")
    private String mNPLServiceStateRetriesRemaining;

    @Value("${MNPLServiceStateRetryInterval}")
    private String mNPLServiceStateRetryInterval;

    @Value("${MNPLServiceSpecificationPayloadFormat}")
    private String mNPLServiceSpecificationPayloadFormat;

    @Value("${MNPLServiceSpecificationRevision}")
    private String mNPLServiceSpecificationRevision;

    @Value("${MNPLServiceSpecificationVersion}")
    private String mNPLServiceSpecificationVersion;

    @Value("${MNPLSuccessStateCode}")
    private String mNPLSuccessStateCode;

    @Value("${MNPLMessageTimeOut}")
    private String mNPLMessageTimeOut;

    @Value("${mnpl.technical.param}")
    private String mnplTechnicalParam;

    @Value("${mnpl.business.param}")
    private String mnplBusinessParam;

    @Value("${mnpl.retry.count}")
    private String mnplRetryCount;

    @Value("${ActionType}")
    private String actionType;
}
