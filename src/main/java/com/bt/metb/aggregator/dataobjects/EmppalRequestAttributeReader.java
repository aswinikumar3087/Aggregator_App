package com.bt.metb.aggregator.dataobjects;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import java.io.Serializable;
@Data
@Configuration
@RefreshScope
public class EmppalRequestAttributeReader implements Serializable {

    @Value("${EMPPALMessageId}")
    private String emppalMessageId;

    @Value("${EMPPALErrorCode}")
    private String emppalErrorCode;

    @Value("${ActionType}")
    private String actionType;

    @Value("${EMPPALErrorDesc}")
    private String EMPPALErrorDesc;

    @Value("${EMPPALErrorText}")
    private String EMPPALErrorText;

    @Value("${EMPPALTechnicalErrorCode}")
    private String emppalTechnicalErrorCode;

    @Value("${EMPPALDefaultDataErrorCode}")
    private String emppalDefaultDataErrorCode;

    @Value("${CreateTaskMessageId}")
    private String CreateTaskMessageId;

    @Value("${AmendTaskMessageId}")
    private String AmendTaskMessageId;

    @Value("${CancelTaskMessageId}")
    private String CancelTaskMessageId;

    @Value("${BTEngineerDetails}")
    private String btEngineerDetails;

    @Value("${EMPPALApplicableTasktype}")
    private String taskTypeApplicable;

    @Value("${TaskforceDTDPath}")
    private String TaskforceDTDPath;

    @Value("${EMPPALMessageTimeOut}")
    private String EMPPALMessageTimeOut;

    @Value("${emppal.retry.count}")
    private String emppalRetryCount;

}
