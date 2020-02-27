/**********************************
 @author - aswini.kumarparida@bt.com
 version-1.0
 ************************************/
package com.bt.metb.aggregator.messageprocessor;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dao.DAOInterface;
import com.bt.metb.aggregator.dataobjects.AggregationMainDBSchema;
import com.bt.metb.aggregator.mqConfig.MetbAggregatorMqConfigReader;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.provider.Aggregator;
import com.bt.metb.aggregator.requestProcessor.EmppalRequestProcessorImpl;
import com.bt.metb.aggregator.requestProcessor.MnplRequestProcessorImpl;
import com.bt.metb.aggregator.util.DBLoading;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import com.bt.metb.aggregator.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component("aggregatorCoreProcessor")
@RefreshScope
public class AggregatorCoreProcessor {

    private static final Logger log = LoggerFactory.getLogger(AggregatorCoreProcessor.class);

    private Map requestMap;
    private String aggregationName;
    private String interfaceProtocol;
    private String aggBusinessReq;
    private String aggTechnicalReq;
    private String actionType;
    private Map innerAggregation;

    @Autowired
    @Qualifier("resourceManager")
    private METResourceManagerInterface resourceManager;

    @Autowired
    @Qualifier("dao")
    private DAOInterface dao;

    @Autowired
    private MessagePublisher messagePublisher;

    @Autowired
    private MetbAggregatorMqConfigReader metbAggregatorMqConfigReader;

    @Autowired
    @Qualifier("simpleMessageConverter")
    private SimpleMessageConverter simpleMessageConverter;

    @Autowired
    private ApplicationContext applicationContext;

    public void doAggregation(Map outerAggregator) throws METBException {
        log.debug("AggregatorCoreProcessor.doAggregation():: Start ");
        String strStatus = null;

        if (outerAggregator.containsKey("aggregationStatus")) {
            HashMap status = (HashMap) outerAggregator.get("aggregationStatus");

            if (status.containsKey(WMGConstant.AGG_STATUS)) {
                strStatus = (String) status.get(WMGConstant.AGG_STATUS);
            }
        }
        requestMap = (HashMap) outerAggregator.get(WMGConstant.REQUEST_MAP);
        actionType = (String) requestMap.get(WMGConstant.ACTION_TYPE);
        String productName = (String) requestMap.get(WMGConstant.PRODUCT_TYPE);
        String lob = (String) requestMap.get(WMGConstant.TASK_LOB);
        String taskCategory = (String) requestMap.get(WMGConstant.TASK_CATEGORY);
        String metRefNumber = (String) requestMap.get(WMGConstant.MET_REF_NUM);
        String validatedXml = (String) requestMap.get("ValidatedXML");

        Map aggregationMap = deriveAggregationdata(productName, taskCategory, lob, metRefNumber);
        log.debug("AggregatorCoreProcessor.doAggregation() :: metRefNum ::{} ", metRefNumber);

        boolean sendResponseToMetb = true;
        for (int i = 1; i <= aggregationMap.size(); i++) {
            try {

                HashMap inner = (HashMap) aggregationMap.get(String.valueOf(i));
                aggregationName = (String) inner.get(WMGConstant.AGGREGATIONTYPE);
                interfaceProtocol = inner.get(WMGConstant.INTERFACE_PROTOCOL).toString();
                aggBusinessReq = (String) inner.get(WMGConstant.REQUIREMENT_BUSINESS);
                aggTechnicalReq = (String) inner.get(WMGConstant.REQUIREMENT_TECHNICAL);

                if (outerAggregator.containsKey(aggregationName)) {
                    if (WMGConstant.AGGREGATOR_REQCONT.equalsIgnoreCase(strStatus)) {
                        HashMap innerHm = (HashMap) outerAggregator.get(aggregationName);
                        if (innerHm.containsKey("AGG_STATUS")) {
                            String aggStatus = innerHm.get("AGG_STATUS").toString();
                            log.debug("AggregatorCoreProcessor:doAggregation() Aggregation of type {} is skipped as {} for task with metReferenceNumber :: {}",aggregationName, aggStatus, metRefNumber);
                        }
                    } else if(WMGConstant.AGG_FAILURE.equalsIgnoreCase(strStatus)){
                        break;
                    }
                    continue;
                }
                storeAggregationDetails(outerAggregator, metRefNumber);
                innerAggregation = new HashMap();
                Aggregator aggregator = getAggregatorInstance(aggregationName);

                Map<String, String> aggName = new HashMap();
                aggName.put(WMGConstant.AGG_NAME, aggregationName);
                aggName.put(WMGConstant.REQUIREMENT_TECHNICAL, aggTechnicalReq);
                aggName.put(WMGConstant.REQUIREMENT_BUSINESS, aggBusinessReq);

                outerAggregator.put(WMGConstant.AGG_NAME, aggName);

                if (!StringUtil.isNullOrEmpty(validatedXml) && aggregator != null) {
                    innerAggregation = aggregator.executeDelegate(outerAggregator, actionType, metRefNumber, validatedXml);
                }else{
                    log.error("AggregatorCoreProcessor:doAggregation() :: Validated Req XML is not received from METBCore");
                    break;
                }

                if (innerAggregation != null && !innerAggregation.isEmpty()) {
                    if (null != innerAggregation.get(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP) && WMGConstant.STRING_TRUE.equalsIgnoreCase(innerAggregation.get(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP).toString())) {
                        if (WMGConstant.MANDATORY.equals(aggBusinessReq)) {
                            throw new METBException(WMGConstant.AGGREGATION_BUSINESSERROR, WMGConstant.BUSINESS_EXCEPTION,
                                    WMGConstant.AGGREGATION_BUSINESSERROR, " AggregatorCoreProcessor.doAggregation() :: aggregation ::Null values Received from Input Request for the tag- ProjectId ");
                        } else {
                            continue;
                        }
                    } else if (null != innerAggregation.get(WMGConstant.AGGREGATION_IGNORE_SKIP) && WMGConstant.STRING_TRUE.equalsIgnoreCase(innerAggregation.get(WMGConstant.AGGREGATION_IGNORE_SKIP).toString())) {
                        continue;
                    }
                }

                if (innerAggregation != null && !innerAggregation.isEmpty()) {

                    if (WMGConstant.STRING_TRUE_CAPS.equals(innerAggregation.get(WMGConstant.DELIBERATE_SKIP))) {
                        outerAggregator.put(aggregationName, innerAggregation);
                        log.debug("AggregatorCoreProcessor.doAggregation() :: metRefNumber {} :: DeliberateSkip-TRUE and after outeraggregator.put() ", metRefNumber, innerAggregation);
                        continue;
                    }else {
                        outerAggregator.put(aggregationName, innerAggregation);
                        log.debug("AggregatorCoreProcessor.doAggregation() :: metRefNumber :: {} ::aggregationName :: {}  innerAggregation :: {} added to Outermap {}", metRefNumber, aggregationName,
                                innerAggregation,outerAggregator);
                    }
                }

                if (StringUtils.equalsAny(interfaceProtocol, WMGConstant.INTERFACE_PROTOCOL_MQ, WMGConstant.INTERFACE_PROTOCOL_JMS, WMGConstant.INTERFACE_PROTOCOL_WS)) {
                    String metdata = "METDATA";
                    if (StringUtils.equalsAny(interfaceProtocol, WMGConstant.INTERFACE_PROTOCOL_MQ, WMGConstant.INTERFACE_PROTOCOL_JMS) && outerAggregator.containsKey(metdata)) {
                        HashMap innerHM = (HashMap) outerAggregator.get(metdata);
                        innerHM.put(WMGConstant.AGGREGATION_ASYNCH_FLAG, "TRUE");
                        outerAggregator.put(metdata, innerHM);
                    }
                    sendResponseToMetb = false;
                    log.trace("AggregatorCoreProcessor.doAggregation() :: Breaking the main Thread for aggregation as it is Asynchronous::for MET_REF_NUMBER::{} sendResponseToMetb flag {}", metRefNumber, sendResponseToMetb);
                    break;
                }
            } catch (METBException me) {
                handleExceptionScenarios(outerAggregator, metRefNumber, me);

            } catch (Exception e) {
                log.error("AggCoreprocessor.doAggregation():: Exception occurred during aggregation for MET_REF_NUMBER:{}", metRefNumber, e);
            }
        }
        try {
            if (sendResponseToMetb) {
                sendTheResponseMapToMetbCore(outerAggregator,metRefNumber);
            }
        } catch (Exception e) {
            log.error("AggCoreprocessor.doAggregation():: Exception occurred during aggregation response sending for MET_REF_NUMBER:{}", metRefNumber, e);
        }
    }

    private void handleExceptionScenarios(Map outerAggregator, String metRefNumber, METBException me) throws METBException {
        log.debug("AggregatorCoreProcessor.handleExceptionScenarios() :: METBException occurred during aggregation for MET_REF_NUMBER:: {}", metRefNumber, me);
        if (WMGConstant.TECHNICAL_EXCEPTION.equalsIgnoreCase(me.getErrorType())) {
            handleTechnicalException(outerAggregator, metRefNumber, me);
        } else if (WMGConstant.BUSINESS_EXCEPTION.equalsIgnoreCase(me.getErrorType())) {
            handleBusinessException(outerAggregator, metRefNumber, me);
        } else if (WMGConstant.IGNORE_EXCEPTION.equalsIgnoreCase(me.getErrorType())) {
            handleIgnoreException(outerAggregator, metRefNumber, me);
        }
        else {
            log.error("AggCoreprocessor.doAggregation():: METBException occurred during aggregation for MET_REF_NUMBER:{}", metRefNumber, me);
        }
    }

    private void handleIgnoreException(Map outerAggregator, String metRefNumber, METBException me) {
        log.error("AggregatorCoreProcessor.handleIgnoreException() :: Exception occurred but ignored during aggregation {} for MET_REF_NUMBER:{}:: as", aggregationName, metRefNumber);
        if ((null == innerAggregation)) {
            innerAggregation = new HashMap();
        }
        if (!StringUtil.isNullOrEmpty(aggregationName)) {
            innerAggregation.put(aggregationName + "_" + WMGConstant.IGNORE_MESSAGE, me.getErrorCode() + ":" + me.getErrorDescription()
                    + ":" + me.getErrorText());
            outerAggregator.put(aggregationName, innerAggregation);
        }
    }

    private void handleBusinessException(Map outerAggregator, String metRefNumber, METBException me) {
        if (aggBusinessReq.equals(WMGConstant.MANDATORY)) {
            log.error("AggregatorCoreProcessor.handleBusinessException():: Business Exception occurred during mandatory aggregation ::{} for MET_REF_NUMBER:{} outerAggregator ::{}",
                    aggregationName,metRefNumber, outerAggregator,me);

            Map<String, Object> requestMapForBusinessException = (Map<String, Object>) outerAggregator.get(WMGConstant.REQUEST_MAP);
            requestMapForBusinessException.put("errorCode", me.getErrorCode());
            requestMapForBusinessException.put("errorDesc", me.getErrorDescription());
            requestMapForBusinessException.put("errorText", me.getErrorText());
        }else {
            log.error("AggregatorCoreProcessor.handleBusinessException():: Business Exception occurred during optional aggregation:: {} for MET_REF_NUMBER ::{} ", aggregationName,metRefNumber, me);
        }
    }

    private void handleTechnicalException(Map outerAggregator, String metRefNumber, METBException me) throws METBException {
        if (aggTechnicalReq.equals(WMGConstant.MANDATORY)) {
            log.error("AggregatorCoreProcessor.handleTechnicalException()::Technical Exception occurred during Mandatory aggregation {} for MET_REF_NUMBER:{}::", aggregationName,metRefNumber, me);
            try {
                dao.storeAggregationDetails(metRefNumber, outerAggregator, true);
            } catch (METBException meinsert) {
                throw meinsert;
            }
        } else {
            log.error("AggregatorCoreProcessor.handleTechnicalException():: Technical Exception occurred during optional aggregation:: {} for MET_REF_NUMBER ::{} ", aggregationName,metRefNumber, me);
        }
    }

    private void storeAggregationDetails(Map outerAggregator, String metRefNumber) throws METBException {
        try {
            dao.storeAggregationDetails(metRefNumber, outerAggregator, true);
        } catch (METBException e) {
            throw e;
        }
    }

    private Map deriveAggregationdata(String product, String taskCategory, String lob, String metRefNumber) throws METBException{
        Map aggregationRowMap;
        try {
            log.debug("AggCoreprocessor.deriveAggregationdata() :: fetching aggregation details for metRefNumber{}", metRefNumber);
            AggregationMainDBSchema aggMasterData = DBLoading.getAggregationMainData(product, taskCategory, lob);
            String aggId = aggMasterData.getAggregationId();
            aggregationRowMap = DBLoading.getAggregationData(aggId, deriveRequestType(actionType));
        } catch (METBException e) {
            log.error("AggCoreprocessor.deriveAggregationdata() :: Exception occurred during deriveAggregationdata :: metRefNumber{}", metRefNumber,e);
           throw e;
        }
        return aggregationRowMap;
    }

    private String deriveRequestType(String actionType) {
        RequestType reqType = RequestType.evaluate(actionType);
        String reqName = reqType.name();
        if(!StringUtil.isNullOrEmpty(reqName)) {
            switch (reqName) {
                case "CREATE":
                    return WMGConstant.CREATE_REQUEST_OPERATION;
                case "AMEND":
                    return WMGConstant.AMEND_REQUEST_OPERATION;
                case "CANCEL":
                    return WMGConstant.CANCEL_REQUEST_OPERATION;
                case "UPDATE_TASK_STATUS":
                    return WMGConstant.UPDATE_TASK_REQUEST_OPERATION;
            }
        }
        return null;
    }

    public void sendTheResponseMapToMetbCore(Map outerAggregator,String metRefNum)  {
        Message aggResponseString = null;
        String aggResponseToCore = null;
        try {
            aggResponseToCore =  new ObjectMapper().writeValueAsString(outerAggregator);
            aggResponseString = simpleMessageConverter.toMessage(aggResponseToCore, new MessageProperties());
            messagePublisher.publishMessage(metbAggregatorMqConfigReader.getAggregatorToMetbOutboundQueueExchange(),
                    metbAggregatorMqConfigReader.getRoutingKeyAggregatorToMetbOutbound(), aggResponseString);
            log.debug("AggregatorCoreProcessor.sendTheResponseMapToMetbCore() :: Successfully produced to AggCoreToMETB Queue for MetRefId {}:: Message :: {}", metRefNum,aggResponseString);

        } catch (Exception notSentToRabbitMqException) {
            try {
                MessageProperties messageProperties = new MessageProperties();
                String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                messageProperties.setHeader("timestamp", currentDate);
                aggResponseString = simpleMessageConverter.toMessage(aggResponseToCore, messageProperties);
                                messagePublisher.publishMessage(metbAggregatorMqConfigReader.getAggregatorToMetbOutboundHositalQExchange(), metbAggregatorMqConfigReader.getRoutingKeyAggregatorToMetbCoreHOutbound(), aggResponseString);
                log.debug("AggregatorCoreProcessor.sendTheResponseMapToMetbCore() ::Successfully produced to AggCoreToMETB Hospital Queue forMetRefId {}:: Message :: {}", metRefNum,aggResponseString);

            } catch (Exception notSentToRabbitMqHQException) {
                log.error("AggregatorCoreProcessor.sendTheResponseMapToMetbCore() :: Failed to send to Hospital queue for MetRefId {}:: MessageNotSentToRabbitMqException ::{}", metRefNum,
                        notSentToRabbitMqHQException);
            }
        }
    }

    private Aggregator getAggregatorInstance(String aggType) {
        Aggregator aggregator = null;
        switch (aggType) {
            case WMGConstant.MNPL:
                aggregator = applicationContext.getBean(MnplRequestProcessorImpl.class);
                break;
            case WMGConstant.EMPPAL_AGGREGATOR_NAME:
                aggregator = applicationContext.getBean(EmppalRequestProcessorImpl.class);
                break;
            default:
                log.debug("AggregatorCoreProcessor.getAggregatorInstance() :: Aggregation Type not found {}", aggType);
                break;
        }
        return aggregator;
    }
}
