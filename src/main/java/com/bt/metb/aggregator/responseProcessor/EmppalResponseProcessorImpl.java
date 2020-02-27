package com.bt.metb.aggregator.responseProcessor;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dao.DAOInterface;
import com.bt.metb.aggregator.dataobjects.AggregationResponseDO;
import com.bt.metb.aggregator.dataobjects.EmppalRequestAttributeReader;
import com.bt.metb.aggregator.messageprocessor.AggregatorCoreProcessor;
import com.bt.metb.aggregator.mqConfig.EmppalAggregatorMqConfigReader;
import com.bt.metb.aggregator.parser.interfaces.ParserServiceInterface;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import com.bt.metb.aggregator.util.StringUtil;
import com.bt.metb.aggregator.util.WMGUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("emppalResponseProcessorImpl")
@RefreshScope
public class EmppalResponseProcessorImpl implements ResponseProcessor {

    private final static Logger logger = LoggerFactory.getLogger(EmppalResponseProcessorImpl.class);

    @Autowired
    @Qualifier("resourceManager")
    private METResourceManagerInterface resourceManager;

    @Autowired
    @Qualifier("aggregatorCoreProcessor")
    private AggregatorCoreProcessor aggregatorCoreProcessor;

    @Autowired
    private EmppalRequestAttributeReader emppalRequestAttributeReader;

    @Autowired
    @Qualifier("dao")
    private DAOInterface dao;

    @Autowired
    @Qualifier("parserServiceImpl")
    private ParserServiceInterface parserServiceInterface;

    @Autowired
    private EmppalAggregatorMqConfigReader emppalAggregatorMqConfigReader;

    @Autowired
    private MessagePublisher messagePublisher;

    private AggregationResponseDO emppalAggregationResponse;
    private String requestStatus;
    private String metReferenceNumber;
    private String aggregationName;
    private String aggregationTechnical;
    private String aggregationBusiness;
    private Timestamp lastUpdatedTime;
    private String requestXml;

    public void processMessage(Message inputData) throws METBException {

        String strMessageID;
        String errorCode;
        String errorDesc = null;
        String errorText = null;

        int timeOutInterval;
        String emppalResponse = null;
        Map<String, Object> internalEmppalResponseMap;
        String sourceInstanceId= null;
        String sourceAddress = null;
        String taskReferenceNumber =null;
        String product =null;

        try {
            byte[] requestXmlFromBytes = inputData.getBody();
            String receivedXML = new String(requestXmlFromBytes);
            logger.debug("EmppalResponseProcessorImpl ::processMessage()::  START Processing response :: AuditLogCode ::{} Received XML {}", WMGConstant.AUDITLOG_PROGRESSMSG_CODE, receivedXML);
            String stripedXML = parserServiceInterface.aggregationResponseNamespaceStrip(receivedXML);
            String emppalOp = stripedXML.substring(stripedXML.indexOf("<GetItemDetailsByEstimateNumberResponse>") + "<GetItemDetailsByEstimateNumberResponse>".length(), stripedXML.indexOf("</GetItemDetailsByEstimateNumberResponse>"));
            String emppalOutput = "<GetItemDetailsByEstimateNumberResponse>" + emppalOp + "</GetItemDetailsByEstimateNumberResponse>";
            strMessageID = inputData.getMessageProperties().getHeaders().get(WMGConstant.MSG_ID).toString();

            if (StringUtil.isNullOrEmpty(strMessageID)) {
                logger.error("EmppalResponseProcessorImpl:: processMessage :: ErrorCode {} :: Error Message:Recieved NULL messageId in the EMPPAL response From SB", WMGConstant.MET_ERRCODE);

            } else {
                boolean messageIdNotFound =false;

                logger.debug("EmppalResponseProcessorImpl ::processMessage:: Recieved EMPPAL response for correlationId : {} ", strMessageID);
                emppalAggregationResponse = fetchTaskDetailsFromDBForMsgId(strMessageID);

                String actionType = parserServiceInterface.getRequiredTagValue(requestXml, emppalRequestAttributeReader.getActionType());
                if(null != emppalAggregationResponse){
                    taskReferenceNumber = emppalAggregationResponse.getTaskRefNumber();
                    sourceInstanceId = emppalAggregationResponse.getSourceInstanceId();
                    sourceAddress = emppalAggregationResponse.getSourceAddress();
                    messageIdNotFound = emppalAggregationResponse.isMessageIdNotFound();
                    product =emppalAggregationResponse.getProduct();
                }
                logger.debug("EmppalResponseProcessorImpl ::processMessage:: values from DB requestStatus ::{} metReferenceNumber ::{}" +
                        "aggregationName ::{} strMessageID ::{} ", requestStatus, metReferenceNumber, aggregationName, strMessageID);

                if (messageIdNotFound) {
                    logger.error("EmppalResponseProcessorImpl:: processMessage ::METRefrenceNumber :: {} messageId :: {}  Not Present in TASKDETAILS table::Error Code : {} ::Error Description :{} :: Message ID:{} ",
                            metReferenceNumber, messageIdNotFound, WMGConstant.EMPPAL_BUSINESS_ERROR_CODE, WMGConstant.EMPPAL_BUSINESS_ERROR_DESC, strMessageID);

                } else {
                    if (WMGConstant.AGG_IGR_STATUS.equals(requestStatus) || WMGConstant.AGG_RES_STATUS.equals(requestStatus) || WMGConstant.AGG_BE_STATUS.equals(requestStatus) || WMGConstant.AGG_ITR_STATUS.equals(requestStatus)) {
                        logger.debug("EmppalResponseProcessorImpl:: processMessage :: response has either been processed or in progress or needs to be ignorged and hence the response received now is not processed further for MessageID : {} ", strMessageID);
                    } else {
                        boolean flagPrepareInternalresponseXml = true;

                            errorCode = parserServiceInterface.getRequiredTagValue(emppalOutput, emppalRequestAttributeReader.getEmppalErrorCode());
                            String serverName = resourceManager.getManagedServer();
                            String status = null;
                            logger.debug("EmppalResponseProcessorImpl::errorCode::{} ", errorCode);//1001
                            try {
                                if (WMGConstant.SUCCESS_ERROR_CODE.equals(errorCode)) {

                                    Timestamp lastupdated = new Timestamp(System.currentTimeMillis());

                                    dao.updateRequestStatus(WMGConstant.AGG_ITR_STATUS, lastupdated, metReferenceNumber);

                                    //String product = emppalAggregationResponse.getProduct();
                                    logger.debug("EmppalResponseProcessorImpl.processMessage() ::METRefrenceNumber:: {} ::product {} ::actionValue :{} :: Message ID:{} ",
                                            metReferenceNumber, product, actionType, strMessageID);

                                    emppalResponse = parserServiceInterface.transformResponse(emppalOutput, actionType, product, WMGConstant.EMPPAL_RESPONSE_XSL);

                                    logger.debug("EmppalResponseProcessorImpl :: processMessage:: METRefrenceNumber : {}:: AuditLogCode : {} ::received xml :: {}",
                                            metReferenceNumber, WMGConstant.AUDITLOG_PROGRESSMSG_CODE, emppalResponse);

                                    Map emppalResponseMap = populateEMPPALResponse(emppalResponse);

                                    if (null!=emppalResponseMap && emppalResponseMap.size() != 0 && emppalResponseMap.containsKey(WMGConstant.EMPPAL_KEY)) {

                                        flagPrepareInternalresponseXml = storeResToAggregationData(emppalResponseMap, flagPrepareInternalresponseXml);
                                    }

                                    status = WMGConstant.SUCCESS;
                                } else {
                                    errorDesc = parserServiceInterface.getRequiredTagValue(emppalOutput, emppalRequestAttributeReader.getEMPPALErrorDesc());
                                    errorText = parserServiceInterface.getRequiredTagValue(emppalOutput, emppalRequestAttributeReader.getEMPPALErrorText());

                                    String strTechErrCode = emppalRequestAttributeReader.getEmppalTechnicalErrorCode(); //1000 || 3000
                                    if (errorCode.matches(strTechErrCode)) {
                                        throw new METBException(WMGConstant.EMPPAL_TECHNICAL_ERROR_CODE, WMGConstant.TECHNICAL_EXCEPTION, WMGConstant.EMPPAL_TECHNICAL_ERROR_DESC,
                                                " Technical Error Code ::" + errorCode + " Error Text::" + errorText + " Error Description::" + errorDesc);
                                    } else {
                                        String strDefDataErrCode = emppalRequestAttributeReader.getEmppalDefaultDataErrorCode();

                                        if (errorCode.matches(strDefDataErrCode)) {
                                            throw new METBException(
                                                    WMGConstant.EMPPAL_MET_DEFAULT_DATA_EXCEPTION_ERROR_CODE, WMGConstant.MET_DEFAULT_DATA_EXCEPTION, WMGConstant.EMPPAL_MET_DEFAULT_DATA_EXCEPTION_ERROR_DESC,
                                                    "While calling EMPPAL ,Received error due to data defaulted at MET end :: Error Code " + errorCode + " Erroe Text: " + errorText + " Error description: " + errorDesc);
                                        } else {
                                            throw new METBException(WMGConstant.EMPPAL_BUSINESS_ERROR_CODE, WMGConstant.BUSINESS_EXCEPTION, WMGConstant.EMPPAL_BUSINESS_ERROR_DESC,
                                                    " Business Error Code  ::" + errorCode + " Error Text: " + errorText + " Error description: " + errorDesc);
                                        }
                                    }
                                }
                            } catch (METBException metEx) {

                                dao.storeAggregationLog(metReferenceNumber, metEx.getErrorCode(), metEx.getErrorDescription(), metEx.getErrorText());
                                if (WMGConstant.TECHNICAL_EXCEPTION.equalsIgnoreCase(metEx.getErrorType())) {
                                    logger.error("EmppalResponseProcessorImpl.processMessage()::METRefrenceNumber : {} Technical Exception Code:{} :: Exception Text :{} ", metReferenceNumber, metEx.getErrorCode(), metEx.getErrorText());
                                    String retryStatus;

                                    if (WMGConstant.MANDATORY.equalsIgnoreCase(aggregationTechnical)) {
                                        flagPrepareInternalresponseXml = false;
                                        retryStatus = WMGConstant.RETRY;
                                        Timestamp lastupdated = new Timestamp(System.currentTimeMillis());
                                        logger.error("EmppalResponseProcessorImpl.processMessage():: MANDATORY AGGREGATION {} FAILED :: " +
                                                        "Technical exception occured for METRefrenceNumber ::{} and updating TASKDETAILS.RETRY_STATUS as :: {}"
                                                , aggregationName, metReferenceNumber, retryStatus);
                                        dao.updateRequestStatus(WMGConstant.AGG_TE_STATUS, retryStatus, serverName, lastupdated, metReferenceNumber);
                                        Map<String, Object> headerMap = inputData.getMessageProperties().getHeaders();
                                        int count = Integer.parseInt(headerMap.get(WMGConstant.RETRY_COUNT_IN_HEADER).toString());
                                        if (count < Integer.parseInt(emppalRequestAttributeReader.getEmppalRetryCount())) {

                                            retryHandler(inputData);
                                        }
                                    } else {
                                        retryStatus = WMGConstant.REQCONT;
                                        flagPrepareInternalresponseXml = true;
                                        status = WMGConstant.CONTINUE;
                                        Timestamp lastupdated = new Timestamp(System.currentTimeMillis());
                                        dao.updateRequestStatus(WMGConstant.AGG_TE_STATUS, retryStatus, serverName, lastupdated, metReferenceNumber);
                                        logger.debug("EmppalResponseProcessorImpl.processMessage():: TE Response received for Optional AGGREGATION :: {} METRefrenceNumber :: {} " +
                                                "updating TASKDETAILS.RETRY_STATUS  as ::{}", aggregationName, metReferenceNumber, retryStatus);
                                    }
                                } else if (WMGConstant.BUSINESS_EXCEPTION.equalsIgnoreCase(metEx.getErrorType())) {
                                    flagPrepareInternalresponseXml = true;
                                    Timestamp lastupdated = new Timestamp(System.currentTimeMillis());
                                    dao.updateRequestStatus(WMGConstant.AGG_BE_STATUS, lastupdated, metReferenceNumber);
                                    errorCode = metEx.getErrorCode();
                                    errorDesc = metEx.getErrorDescription();
                                    errorText = metEx.getErrorText();
                                    if (WMGConstant.MANDATORY.equalsIgnoreCase(aggregationBusiness)) {
                                        status = WMGConstant.FAILURE;
                                        logger.error("EmppalResponseProcessorImpl.processMessage():: Mandatory AGGREGATION {} FAILED :: Business exception occured for METRefrenceNumber{} ::status {} " +
                                                "errorCode ::{} Errordesc:: {} ErrorText {}", aggregationName, metReferenceNumber, status, errorCode, errorDesc, errorText);
                                    } else {
                                        logger.debug("EmppalResponseProcessorImpl.processMessage():: BE Response received for Optional AGGREGATION {} , " +
                                                "METRefrenceNumber :: {},Business Exception Code:{} :: Error Desc :: {}, Error text ::{} ", aggregationName, metReferenceNumber, errorCode, errorDesc, errorText);
                                        status = WMGConstant.CONTINUE;
                                    }
                                }
                            }

                            if (flagPrepareInternalresponseXml) {
                                try {
                                    internalEmppalResponseMap = new HashMap();

                                    Map statusMap;
                                    if (WMGConstant.SUCCESS.equals(status)) {
                                        statusMap = new HashMap();
                                        statusMap.put(WMGConstant.AGG_STATUS, WMGConstant.AGG_RES_STATUS);
                                        internalEmppalResponseMap.put("aggregationStatus", statusMap);
                                    } else if (WMGConstant.AGG_CONTINUE.equals(status)) {
                                        statusMap = new HashMap();
                                        statusMap.put(WMGConstant.AGG_STATUS, WMGConstant.AGGREGATOR_REQCONT);
                                        internalEmppalResponseMap.put("aggregationStatus", statusMap);
                                        HashMap innerMap = new HashMap();
                                        logger.debug("EmppalResponseProcessorImpl.processMessage() :: Erororcode :: {} metrefId :: {}", errorCode, metReferenceNumber);
                                        if (!StringUtil.isNullOrEmpty(errorCode) && errorCode.matches(emppalRequestAttributeReader.getEmppalTechnicalErrorCode())) {
                                            innerMap.put(WMGConstant.AGGREGATION_STATUS, WMGConstant.AGG_STATUS_TECHNICAL_MSG);
                                        } else {
                                            innerMap.put(WMGConstant.AGGREGATION_STATUS, WMGConstant.AGG_STATUS_BUSINESS_MSG);
                                        }
                                        internalEmppalResponseMap.put(aggregationName, innerMap);
                                        logger.debug("EmppalResponseProcessorImpl.processMessage() ::Updating aggregationData with aggregation {} for metref {} ", aggregationName, metReferenceNumber);
                                    } else if (WMGConstant.AGG_FAILURE.equals(status)) {
                                        if (WMGConstant.CREATE_ACTION.equals(actionType) ||
                                                WMGConstant.AMEND_ACTION.equals(actionType)) {

                                            statusMap = new HashMap();
                                            statusMap.put(WMGConstant.AGG_STATUS, WMGConstant.AGG_FAILURE);

                                            internalEmppalResponseMap.put("aggregationStatus", statusMap);

                                            logger.debug("EmpppalResProcImpl.processMessage() ::errorCode {}, " +
                                                    "errorDesc :: {} errorText ::{}", errorCode, errorDesc + ":" + errorText);
                                            try {
                                                dao.updateResponse(metReferenceNumber, WMGConstant.AGG_FAILURE);
                                            } catch (METBException internal) {
                                                logger.debug("METBException ::Exception occured ::errorCode {}, " +
                                                        "errorDesc :: {} errorText ::{}", errorCode, errorDesc + ":" + errorText);
                                                throw new METBException(internal.getErrorCode(), "EMPPALResProcImpl" + WMGConstant.COLON + "procMessage()", internal.getErrorDescription(), internal.getErrorText());
                                            }
                                        } else {
                                            logger.debug("EmppalResponseProcessorImpl.processMessage() :: Error reposne receved is for Neither Create or Amend task");
                                        }
                                    }

                                    if ((null != internalEmppalResponseMap)) {
                                        HashMap metData = new HashMap();
                                        metData.put(WMGConstant.IS_RESUME_FLOW, "true");
                                        internalEmppalResponseMap.put("METDATA", metData);
                                        internalEmppalResponseMap = dao.getExistingHashData(taskReferenceNumber, sourceInstanceId, sourceAddress, internalEmppalResponseMap);
                                        logger.debug("EmppalResponseProcessorImpl.processMessage() :: metReferenceNumber :: {} ResponseMap from Aggregation_Data table:: {}", metReferenceNumber,internalEmppalResponseMap);
                                    }

                                    Timestamp lastupdated = new Timestamp(System.currentTimeMillis());

                                    if (WMGConstant.SUCCESS.equalsIgnoreCase(status)) {

                                        dao.updateRequestStatusInITR(WMGConstant.AGG_RES_STATUS, lastupdated, metReferenceNumber);
                                        logger.debug("EmppalResponseProcessorImpl.processMessage():: METRefrenceNumber ::{} Updated reqStatus to AGG_RES in taskdetails table For Emppal Aggregation", metReferenceNumber);
                                        aggregatorCoreProcessor.doAggregation(internalEmppalResponseMap);

                                    } else {
                                        logger.debug("EmppalResponseProcessorImpl.processMessage():: METRefrenceNumber ::{} In ELSE Block ", metReferenceNumber);
                                        if (!StringUtil.isNullOrEmpty(errorCode) && WMGConstant.MANDATORY.equals(aggregationBusiness)) {
                                            Map<String, Object> requestMap = (Map<String, Object>) internalEmppalResponseMap.get(WMGConstant.REQUEST_MAP);
                                            requestMap.put("errorCode", errorCode);
                                            requestMap.put("errorDesc", errorDesc);
                                            requestMap.put("errorText", errorText);
                                        }
                                        logger.debug("EmppalResponseProcessorImpl.processMessage():: METRefrenceNumber ::{}, Posting ResponseMAP to AggreagatorCoreProcessor", metReferenceNumber);
                                        aggregatorCoreProcessor.doAggregation(internalEmppalResponseMap);
                                    }
                                } catch (METBException metEx) {
                                    logger.error("EmppalResponseProcessorImpl.processMessage()::METRefrenceNumber : {} METBException:  ",
                                            metReferenceNumber,metEx);
                                    try {
                                        Timestamp lastupdated = new Timestamp(System.currentTimeMillis());
                                        dao.updateRequestStatus(WMGConstant.AGG_REQ_STATUS, lastupdated, metReferenceNumber);
                                        logger.debug("EmppalResponseProcessorImpl.processMessage() :: Updated Status to AGG_REQ for metRefId {}",metReferenceNumber);
                                        dao.deleteAggregationSpecificData(metReferenceNumber, aggregationName);
                                        logger.debug("EmppalResponseProcessorImpl.processMessage() :: AggSpecificData deleted from aggregation_data table for metRefId {}",metReferenceNumber);
                                    } catch (Exception e) {
                                        logger.error("EmppalResponseProcessorImpl.processMessage()::Exception occurred ::for METRefrenceNumber ::{}  Manual intervention is required to update status to AGG_REQ in taskdetails table and remove the aggregation" +
                                                "specific data from aggregation_data table",metReferenceNumber,e);
                                    }
                                } catch (Exception exception) {
                                    logger.error("EmppalResponseProcessorImpl.processMessage()::METRefrenceNumber : {}::METBException: {} ",
                                            metReferenceNumber, WMGUtil.getStackTrace(exception));
                                    try {
                                        Timestamp lastupdated = new Timestamp(System.currentTimeMillis());
                                        dao.updateRequestStatus(WMGConstant.AGG_REQ_STATUS, lastupdated, metReferenceNumber);
                                        logger.debug("EmppalResponseProcessorImpl.processMessage() :: Updated Status to AGG_REQ for metRefId {}",metReferenceNumber);
                                        dao.deleteAggregationSpecificData(metReferenceNumber, aggregationName);
                                        logger.debug("EmppalResponseProcessorImpl.processMessage() :: AggSpecificData deleted from aggregation_data table for metRefId {}",metReferenceNumber);
                                    } catch (Exception e) {
                                        logger.error("EmppalResponseProcessorImpl.processMessage()::Exception occurred ::for METRefrenceNumber ::{}  Manual intervention is required to update status to AGG_REQ in taskdetails table and remove the aggregation" +
                                                "specific data from aggregation_data table",metReferenceNumber,e);
                                    }
                                }
                            }
                    }
                }
            }
        } catch (METBException me) {
            logger.error("EmppalResponseProcessorImpl :: processMessage::METRefrenceNumber : {}::METBException: Error Code: {}  Desc: {}  ErrorText: {} ",
                    metReferenceNumber, me.getErrorCode(), me.getErrorDescription(), me.getErrorText());
        } catch (NullPointerException nullPointerException) {
            logger.error("EmppalResponseProcessorImpl :: processMessage::METRefrenceNumber : {}:: Null Request Received STACK :: {} ", metReferenceNumber, WMGUtil.getStackTrace(nullPointerException));
        } catch (Exception e) {
            logger.error("EmppalResponseProcessorImpl :: processMessage::METRefrenceNumber : {}:: STACK :: {} ", metReferenceNumber, WMGUtil.getStackTrace(e));
        }

    }

    private AggregationResponseDO fetchTaskDetailsFromDBForMsgId(String msgId) throws METBException {
        emppalAggregationResponse = dao.getAggregationResponseDetailsObject(msgId);
        if (null != emppalAggregationResponse) {

            requestStatus = emppalAggregationResponse.getReqStatus();
            metReferenceNumber = emppalAggregationResponse.getMetRefNumber();
            aggregationName = emppalAggregationResponse.getAggregationName();
            aggregationTechnical = emppalAggregationResponse.getTechnicalExceptionRequirement();
            aggregationBusiness = emppalAggregationResponse.getBusinessExceptionRequirement();

            lastUpdatedTime = emppalAggregationResponse.getLastUpdateDate();
            requestXml = dao.getValidatedXMLForMetrefId(metReferenceNumber);
            emppalAggregationResponse.setReqXML(requestXml);

        }
        return emppalAggregationResponse;
    }

    private Map populateEMPPALResponse(String emppalResponse) {
        logger.debug("EmppalResponseProcessorImpl.populateEMPPALResponse() :: Start:: METRefrenceNumber{}, emppalResponse received {} ", metReferenceNumber, emppalResponse);
        Map emppalResponseMap = null;
        if (!StringUtil.isNullOrEmpty(emppalResponse)) {
            int indxCntFibre = emppalResponse.indexOf("BDUKStoresItemsDetails");

            if (indxCntFibre >= 0) {
                emppalResponse = emppalResponse.substring(indxCntFibre - 1);
            }
            if (!StringUtil.isNullOrEmpty(emppalResponse)) {
                emppalResponseMap = new HashMap();
                emppalResponseMap.put(WMGConstant.EMPPAL_KEY, emppalResponse);
            }
            logger.debug("EmppalResponseProcessorImpl.populateEMPPALResponse() ::METRefrenceNumber {} emppalResponseMap {} ", metReferenceNumber, emppalResponseMap);
        } else {
            logger.debug("EmppalResponseProcessorImpl.processMessage() ::METRefrenceNumber :: {}  emppalResponse not received ::Mannual Intervention Code: {} ", metReferenceNumber, WMGConstant.MANUAL_INTERVENTION_ERROR_CODE);
        }
        logger.debug("EmppalResponseProcessorImpl.populateEMPPALResponse() ::METRefrenceNumber {} End ", metReferenceNumber);
        return emppalResponseMap;
    }

    private boolean storeResToAggregationData(Map emppalResponseMap, boolean flagPrepareInternalresponseXml) {

        logger.debug("EmppalResponsePrcImpl.storeResToAggregationData () Start :: metReferenceNumber{} flagPrepareInternalresponseXml {}", metReferenceNumber, flagPrepareInternalresponseXml);

        Map outerMap = new HashMap();
        if (!(emppalResponseMap == null) && emppalResponseMap.size() != 0) {
            try {

                outerMap.put(WMGConstant.EMPPAL, emppalResponseMap);
                dao.storeAggregationDetails(metReferenceNumber, outerMap, false);

            } catch (METBException e) {
                flagPrepareInternalresponseXml = false;
                if (WMGConstant.ERROR_CODE_TO_REJECT_AGGREGATION_RESPONSE.equals(e.getErrorCode())) {
                    logger.error("EmppalResponseProcessorImpl :: processMessage::METRefrenceNumber :: {}::METBException: Desc: {} ErrorText: {}",
                            metReferenceNumber, e.getErrorDescription(), e.getErrorText());
                } else {
                    logger.error("EmppalResponseProcessorImpl :: processMessage::METRefrenceNumber : {} :: Mannual Intervention Code: {} ::" +
                            "Error occurred while updating the EMPPAL response in database :: Manual intervention is required ", metReferenceNumber, WMGConstant.MANUAL_INTERVENTION_ERROR_CODE);
                }
            }
        }
        logger.debug("EmppalResponsePrcImpl.storeResToAggregationData () End ::flagPrepareInternalresponseXml {}", flagPrepareInternalresponseXml);
        return flagPrepareInternalresponseXml;
    }

    @Override
    public void retryHandler(Message data) {
        try {
            Map<String, Object> headerMap = data.getMessageProperties().getHeaders();
            if (!(headerMap.containsKey(WMGConstant.RETRY_COUNT_IN_HEADER))) {
                logger.debug("EmppalResponsePrcImpl.retryHandler() :: before 1st Retry.");

                headerMap.put(WMGConstant.RETRY_COUNT_IN_HEADER, 1);
                messagePublisher.publishMessage(emppalAggregatorMqConfigReader.getAggReqToEmppalOutboundQueueExchange(),
                        emppalAggregatorMqConfigReader.getAggReqToEmppalOutboundQueueRoutingKey(), data);

            } else {
                int count = Integer.parseInt(headerMap.get(WMGConstant.RETRY_COUNT_IN_HEADER).toString());

                headerMap.put(WMGConstant.RETRY_COUNT_IN_HEADER, count + 1);
                logger.debug("EmppalResponsePrcImpl.retryHandler() retries :: before :: {} Retry. {}", (count + 1), count);
                messagePublisher.publishMessage(emppalAggregatorMqConfigReader.getAggReqToEmppalOutboundQueueExchange(),
                        emppalAggregatorMqConfigReader.getAggReqToEmppalOutboundQueueRoutingKey(), data);
            }
        } catch (Exception ex) {
            logger.error("EmppalResponsePrcImpl.RetryHandler() :: Exception Occured :: Posting Message to Hospital Queue", ex);
            String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            data.getMessageProperties().setHeader("timestamp", currentDate);
            messagePublisher.publishMessage(emppalAggregatorMqConfigReader.getAggrReqToEmppalHospitalQueueExchange(),
                    emppalAggregatorMqConfigReader.getAggReqToEmppalHospitalQueueRoutingKey(), data);
        }
    }


}
