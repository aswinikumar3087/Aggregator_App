
package com.bt.metb.aggregator.responseProcessor;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dao.DAOInterface;
import com.bt.metb.aggregator.dataobjects.AggregationResponseDO;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.messageprocessor.AggregatorCoreProcessor;
import com.bt.metb.aggregator.model.MnplRequestAttributeReader;
import com.bt.metb.aggregator.mqConfig.MnplAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.RmqConfigReader;
import com.bt.metb.aggregator.parser.interfaces.ParserServiceInterface;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.util.*;
import com.bt.metb.mnp.stub.Error;
import com.bt.metb.mnp.stub.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Service("mnplResponseProcessorImpl")
@RefreshScope
public class MnplResponseProcessorImpl implements ResponseProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MnplResponseProcessorImpl.class);

    @Autowired
    @Qualifier("resourceManager")
    private METResourceManagerInterface resourceManager;

    @Autowired
    @Qualifier("parserServiceImpl")
    private ParserServiceInterface parserServiceInterface;

    @Autowired
    private MessagePublisher producerHandler;

    @Autowired
    @Qualifier("dao")
    private DAOInterface dao;

    @Autowired
    @Qualifier("simpleMessageConverter")
    private SimpleMessageConverter simpleMessageConverter;

    @Autowired
    private MnplRequestAttributeReader mnplRequestAttributeReader;

    @Autowired
    @Qualifier("aggregatorCoreProcessor")
    private AggregatorCoreProcessor aggregatorCoreProcessor;

    @Autowired
    @Qualifier("rmqConfigReader")
    private RmqConfigReader rmqConfigReader;


    @Autowired
    private MnplAggregatorMqConfigReader mnplAggregatorMqConfigReader;

    private String aggregationTechnical = null;
    private String aggregationBusiness = null;

    @Override
    public void processMessage(Message inputData) throws METBException {

        AggregationResponseDO mnplAggregationResponse = null;
        String requestStatus = null;
        String metReferenceNumber = null;
        String aggregationName = null;

        boolean messageIdNotFound = false;
        String requestXml = null;

        String strMessageID = null;
        String errorCode = null;
        String errorText = null;
        String errorDesc = null;
        String replyToInstanceId = null;
        String replyToAddress = null;
        String actionType = null;
        String taskReferenceNumber = null;
        String messageId = null;

        HashMap hashMapResponse = new HashMap();

        GetPlanDetailsResponse planDetailsResponse = (GetPlanDetailsResponse) simpleMessageConverter.fromMessage(inputData);
        logger.debug("MnplResponseProcessorImpl.processMessage() :  GetPlanDetailsResponse: {} ", planDetailsResponse);

        strMessageID = inputData.getMessageProperties().getHeaders().get("msgId").toString();

        if (StringUtil.isNullOrEmpty(strMessageID)) {
            logger.error("MnplResponseProcessorImpl ::processMessage:: ErrorCode : {} :: Error Message:Recieved NULL messageId in the MNPL response", WMGConstant.MET_ERRCODE);

        } else {
            logger.debug("MnplResponseProcessorImpl ::processMessage:: Received MNPL response for correlationId : {} ", strMessageID);
            mnplAggregationResponse = dao.getAggregationResponseDetailsObject(strMessageID);
            messageIdNotFound = false;
            if (null != mnplAggregationResponse) {

                requestStatus = mnplAggregationResponse.getReqStatus();
                aggregationName = mnplAggregationResponse.getAggregationName();
                aggregationTechnical = mnplAggregationResponse.getTechnicalExceptionRequirement();
                aggregationBusiness = mnplAggregationResponse.getBusinessExceptionRequirement();
                messageIdNotFound = mnplAggregationResponse.isMessageIdNotFound();
                metReferenceNumber = mnplAggregationResponse.getMetRefNumber();

                requestXml = dao.getValidatedXMLForMetrefId(metReferenceNumber);
                actionType = parserServiceInterface.getRequiredTagValue(requestXml, mnplRequestAttributeReader.getActionType());
                mnplAggregationResponse.setReqXML(requestXml);
            }
            logger.debug("MnplResponseProcessorImpl ::processMessage:: values from DB requestStatus ::{}metReferenceNumber ::{}" +
                    "aggregationName ::{} strMessageID ::{} ", requestStatus, metReferenceNumber, aggregationName, strMessageID);

            if (messageIdNotFound) {
                logger.error("MnplResponseProcessorImpl:: processMessage ::METRefrenceNumber : {} ::Error Code : {} ::Error Description :{} :: Message ID:{} ", metReferenceNumber, WMGConstant.MNPL_BUSINESS_ERROR_CODE, WMGConstant.MNPL_BUSINESS_ERROR_DESC, strMessageID);

            } else {
                if (WMGConstant.AGG_IGR_STATUS.equals(requestStatus) || WMGConstant.AGG_RES_STATUS.equals(requestStatus) || WMGConstant.AGG_BE_STATUS.equals(requestStatus) || WMGConstant.AGG_ITR_STATUS.equals(requestStatus)) {
                    logger.debug("MnplResponseProcessorImpl:: processMessage ::  response has either been processed or in progress or needs to be ignorged and hence the response received now is not processed further for MessageID : {} ", strMessageID);
                } else {
                    boolean flagPrepareInternalResponsePojo = false;

                    String serverName = resourceManager.getManagedServer();
                    String status = null;
                    try {
                        logger.debug("MnplResponseProcessorImpl.extractResponse() METRefrenceNumber: {} PlanDetailsResponse.PlanResponseList(): {} PlanDetailsResponse.StandardHeader(): {} planDetailsResponse {}",
                                metReferenceNumber, planDetailsResponse.getPlanResponseList(), planDetailsResponse.getStandardHeader(), planDetailsResponse);

                        StandardHeaderBlock resSHB = planDetailsResponse.getStandardHeader();

                        ServiceState resSS = resSHB != null ? resSHB.getServiceState() : null;
                        String resStateCode = null;

                        if (resSS != null) {
                            resStateCode = resSS.getStateCode();
                        }
                        String strSuccessStateCode = mnplRequestAttributeReader.getMNPLSuccessStateCode();

                        logger.debug("MnplResponseProcessorImpl.extractResponse() MetReferenceNumber: {} AuditCode: {} resStateCode: {} strSuccessStateCode: {}",
                                metReferenceNumber, WMGConstant.AUDITLOG_PROGRESSMSG_CODE, resStateCode, strSuccessStateCode);

                        if (strSuccessStateCode.equalsIgnoreCase(resStateCode)) {

                            flagPrepareInternalResponsePojo = true;
                            dao.updateRequestStatus(WMGConstant.AGG_ITR_STATUS, new Timestamp(System.currentTimeMillis()), metReferenceNumber);

                            PlanResponseList listPlan = planDetailsResponse.getPlanResponseList();
                            PlanResponseListPlan[] planDetail = null;
                            String projectId = null;
                            NetworkOrderEquipmentData[] eqpData = null;
                            NetworkOrderEquipmentData networkOrderEquipmentData = null;

                            if (listPlan != null) {
                                planDetail = listPlan.getPlan();
                                NetworkOrder[] data = null;

                                if (planDetail != null) {
                                    if (planDetail.length > 0) {
                                        projectId = planDetail[0].getProjectID();
                                        planDetail[0].getAction();
                                        messageId = planDetail[0].getCorrelationId();
                                        data = planDetail[0].getOrderData();
                                    }
                                }
                                if (data != null) {
                                    if (data.length > 0){
                                    eqpData = data[0].getEquipmentData();
                                    }
                                }
                                if (eqpData != null) {
                                    networkOrderEquipmentData = eqpData[0];
                                }
                            }

                            logger.debug("MnplResponseProcessorImpl.extractResponse() prepareMNPLOutputResponse projectId:: {}", projectId);
                            hashMapResponse = prepareMNPLOutputAndResponse(metReferenceNumber, networkOrderEquipmentData, hashMapResponse, projectId);

                            logger.debug("MnplResponseProcessorImpl.extractResponse() MetReferenceNumber: {} MNPLResponse: {}", metReferenceNumber, hashMapResponse.get(WMGConstant.MNPL_RESPONSE));
                            Map outerMap = new HashMap();
                            flagPrepareInternalResponsePojo = storeAggregationDetails(metReferenceNumber, flagPrepareInternalResponsePojo, hashMapResponse, outerMap);
                            status = WMGConstant.SUCCESS;
                        } else {
                            PlanResponseList planResponseList = planDetailsResponse.getPlanResponseList();
                            String strErrCode = null;
                            String strErrDesc = null;
                            String strErrText = null;

                            logger.debug("Business or Technical Exception PlanDetails Response: {}", planDetailsResponse);
                            logger.debug("Business or Technical Exception MetReferenceNumber: {} MNPLResponse: {} ", metReferenceNumber, hashMapResponse.get("MNPLResponse"));

                            if (planDetailsResponse.getPlanResponseList() != null) {
                                logger.debug("Business Exception occurred");

                                Error[] errorList = planResponseList.getPlanErrorList().getPlanError();
                                if (resSS != null) {
                                    strErrCode = resSS.getErrorCode();
                                    strErrDesc = resSS.getErrorDesc();
                                    strErrText = errorList[0].getErrorDescription();
                                }
                                logger.debug("Business Exception occurred strErrCode {} strErrDesc {} strErrText {}", strErrCode, strErrDesc, strErrText);

                                throw new METBException(WMGConstant.ORMNPL_BUSINESS_ERROR_CODE, WMGConstant.BUSINESS_EXCEPTION, WMGConstant.ORMNPL_BUSINESS_ERROR_DESC,
                                        "MET Received failure Response from MNPL with Error Code  ::" + strErrCode + " Error Text:: " + strErrText
                                                + " Error description:: " + strErrDesc);
                            } else if (planDetailsResponse.getPlanResponseList() == null) {
                                logger.debug("Technical Exception occurred");
                                throw new METBException(WMGConstant.ORMNPL_TECHNICAL_ERROR_CODE, WMGConstant.TECHNICAL_EXCEPTION, WMGConstant.ORMNPL_TECHNICAL_ERROR_DESC,
                                        "MET Received failure Response from MNPL with Error Code  ::" + strErrCode + " Error Text:: " + strErrText
                                                + " Error description:: " + strErrDesc);
                            }

                            logger.error("MnplResponseProcessorImpl.extractResponse() MetReferenceNumber: {}, AuditCode: {}, FailureCode: {}, ErrorDesc: {} ErrorText: {}",
                                    metReferenceNumber, WMGConstant.AUDITLOG_PROGRESSMSG_CODE, strErrCode, strErrDesc, strErrText);
                        }

                        logger.debug("MnplResponseProcessorImpl.extractResponse() MetReferenceNumber: {} RETURN: {}", metReferenceNumber, hashMapResponse);

                    } catch (METBException metEx) {
                        dao.storeAggregationLog(metReferenceNumber, metEx.getErrorCode(), metEx.getErrorDescription(), metEx.getErrorText());
                        if (WMGConstant.TECHNICAL_EXCEPTION.equalsIgnoreCase(metEx.getErrorType())) {
                            Map<String, Object> flagWithStatus = prepareTERelatedStatus(inputData, metReferenceNumber, aggregationTechnical, serverName, metEx);
                            flagPrepareInternalResponsePojo = (boolean) flagWithStatus.get("flag");
                            status = (String) flagWithStatus.get(WMGConstant.STATUS);

                        } else if (WMGConstant.BUSINESS_EXCEPTION.equalsIgnoreCase(metEx.getErrorType())) {
                            flagPrepareInternalResponsePojo = true;
                            errorCode = metEx.getErrorCode();
                            errorDesc = metEx.getErrorDescription();
                            errorText = metEx.getErrorText();
                            status = prepareBEStatus(metReferenceNumber, aggregationBusiness, metEx);
                        }
                    }

                    if (flagPrepareInternalResponsePojo) {
                        flagPrepareInternalResponsePojo(mnplAggregationResponse, metReferenceNumber, aggregationName, requestXml, errorCode, errorDesc, errorText, replyToInstanceId, replyToAddress, actionType, taskReferenceNumber, messageId, status);
                    }
                }
            }

        }

    }

    private HashMap prepareMNPLOutputAndResponse(String metReferenceNumber, NetworkOrderEquipmentData eqpDatum, HashMap hashMapResponse, String projectId) throws METBException {
        String mnplResponse;
        String mnplOutput = null;
        logger.debug("MnplResponseProcessorImpl.prepareMNPLOutputAndResponse() projectId:: {}", projectId);
        if (null != eqpDatum && (null != eqpDatum.getEquipment())) {

            Equipment[] eqp = eqpDatum.getEquipment();
            for (int eqpcount = 0; eqpcount < eqp.length; eqpcount++) {

                NetworkSite nt = eqp[eqpcount].getLogicalLocation();
                mnplOutput = mnplOutput + "<equipment><id>"
                        + eqp[eqpcount].getId()
                        + "</id><logicalLocation><siteId>"
                        + nt.getSiteId()
                        + "</siteId></logicalLocation><eqType>"
                        + eqp[eqpcount].getEqType()
                        + "</eqType></equipment>";
            }
            mnplOutput = "<getPlanDetailsResponse><PlanResponseList><plan><projectID>"
                    + projectId
                    + "</projectID><orderData><equipmentData>"
                    + mnplOutput
                    + "</equipmentData></orderData></plan>"
                    + "</PlanResponseList></getPlanDetailsResponse>";
        }


        logger.debug("MnplResponseProcessorImpl.extractResponse() MetReferenceNumber: {}  AuditLogCode: {}  MNPLResponse: {}",
                metReferenceNumber, WMGConstant.AUDITLOG_PROGRESSMSG_CODE, mnplOutput);

        String xslName = WMGConstant.MNPL_RESPONSE_XSL;

        mnplResponse = parserServiceInterface.transform(mnplOutput, xslName, true);

        logger.debug("MnplResponseProcessorImpl.extractResponse() MetReferenceNumber: {} TRANSFORMED  MNPLResponse: {}", metReferenceNumber, mnplResponse);

        int indxCnt = mnplResponse.indexOf("mnploutput");

        logger.debug("MnplResponseProcessorImpl.extractResponse() MetReferenceNumber: {} indxCnt: {}", metReferenceNumber, indxCnt);

        if (indxCnt >= 0) {

            mnplResponse = mnplResponse.substring(indxCnt - 1);
        }

        if (!StringUtil.isNullOrEmpty(mnplResponse)) {
            hashMapResponse.put("MNPLResponse", mnplResponse);
        }

        return hashMapResponse;

    }

    private String prepareBEStatus(String metReferenceNumber, String aggregationBusiness, METBException metEx) throws METBException {
        String status;
        dao.updateRequestStatus(WMGConstant.AGG_BE_STATUS, new Timestamp(System.currentTimeMillis()), metReferenceNumber);

        if (WMGConstant.MANDATORY.equalsIgnoreCase(aggregationBusiness)) {
            status = WMGConstant.FAILURE;
        } else {
            logger.error("MnplResponseProcessorImpl :: processMessage::METRefrenceNumber : {} Business Exception Code:{} :: Exception Text :{} ", metReferenceNumber, metEx.getErrorCode(), metEx.getErrorText());
            status = WMGConstant.CONTINUE;
        }
        return status;
    }

    private Map<String, Object> prepareTERelatedStatus(Message message, String metReferenceNumber, String aggregationTechnical, String serverName, METBException metEx) throws METBException {
        Map<String, Object> flagPrepareInternalResponsePojoWithStatus = new HashMap<>();
        logger.error("MnplResponseProcessorImpl :: processMessage::METRefrenceNumber : {} Technical Exception Code:{} :: Exception Text :{} ", metReferenceNumber, metEx.getErrorCode(), metEx.getErrorText());

        if (WMGConstant.MANDATORY.equalsIgnoreCase(aggregationTechnical)) {
            flagPrepareInternalResponsePojoWithStatus.put(WMGConstant.STATUS, WMGConstant.RETRY);
            flagPrepareInternalResponsePojoWithStatus.put("flag", false);
            Timestamp lastupdated = new Timestamp(System.currentTimeMillis());
            logger.debug("MnplResponseProcessorImpl :: processMessage::METRefrenceNumber : {}  ::Updating request status in Taskdetails", metReferenceNumber);

            dao.updateRequestStatus(WMGConstant.AGG_TE_STATUS, WMGConstant.RETRY, serverName, lastupdated, metReferenceNumber);

            Map<String, Object> headerMap = message.getMessageProperties().getHeaders();
            int retryCount = Integer.parseInt(headerMap.get(WMGConstant.RETRY_COUNT_IN_HEADER).toString());
            logger.debug(" MnplResponseProcessorImpl :: Retry Count of Message Headers {}", retryCount);

            if (retryCount < Integer.parseInt(mnplRequestAttributeReader.getMnplRetryCount()))
                retryHandler(message);
        } else {
            flagPrepareInternalResponsePojoWithStatus.put(WMGConstant.STATUS, WMGConstant.CONTINUE);
            flagPrepareInternalResponsePojoWithStatus.put("flag", true);
            Timestamp lastupdated = new Timestamp(System.currentTimeMillis());
            dao.updateRequestStatus(WMGConstant.AGG_TE_STATUS, WMGConstant.REQCONT, serverName, lastupdated, metReferenceNumber);
        }
        return flagPrepareInternalResponsePojoWithStatus;
    }

    private void flagPrepareInternalResponsePojo(AggregationResponseDO mnplAggregationResponse, String... strings) throws METBException {

        String metReferenceNumber = strings[0];
        String aggregationName = strings[1];
        String errorCode = strings[3];
        String errorDesc = strings[4];
        String errorText = strings[5];
        String actionType = strings[8];
        String messageId = strings[10];
        String status = strings[11];

        Map<String, Object> internalMnplResponseMap;
        String replyToInstanceId = mnplAggregationResponse.getSourceInstanceId();
        String replyToAddress = mnplAggregationResponse.getSourceAddress();
        String taskReferenceNumber = mnplAggregationResponse.getTaskRefNumber();
        try {
            internalMnplResponseMap = new HashMap();

            Map statusMap = new HashMap();
            if (WMGConstant.SUCCESS.equals(status)) {
                statusMap.put(WMGConstant.AGG_STATUS, WMGConstant.AGG_RES_STATUS);
            } else if (WMGConstant.AGG_CONTINUE.equals(status)) {
                statusMap.put(WMGConstant.AGG_STATUS, WMGConstant.AGGREGATOR_REQCONT);
                HashMap innerMap = new HashMap();
                if (StringUtil.isNullOrEmpty(errorCode))
                    innerMap.put(WMGConstant.AGGREGATION_STATUS, WMGConstant.AGG_STATUS_TECHNICAL_MSG);
                else
                    innerMap.put(WMGConstant.AGGREGATION_STATUS, WMGConstant.AGG_STATUS_BUSINESS_MSG);

                internalMnplResponseMap.put(aggregationName, innerMap);
            } else if (WMGConstant.AGG_FAILURE.equals(status)) {

                logger.error("Message ID : {} :: MnplResponseProcessorImpl :: processMessage :: Exception occured :: {} : {} : {}", messageId, errorCode, errorDesc, errorText);
                Map<String, String> hashTable = new HashMap<>();

                if ((WMGConstant.CREATE_ACTION).equals(actionType) || (WMGConstant.AMEND_ACTION).equals(actionType) || (WMGConstant.CANCEL_ACTION).equals(actionType)) {
                    messageId = parserServiceInterface.getRequiredTagValue(messageId);
                }
                if (!StringUtil.isNullOrEmpty(messageId)) {
                    hashTable.put("messageId", messageId);
                }
                postErrorResponseToAggregatorInbound(metReferenceNumber, actionType);
            }
            internalMnplResponseMap.put("aggregationStatus", statusMap);

            logger.debug("Inside MnplResponseProcessorImpl :: processMessage :: include Resume Flow in HashMap ");
            HashMap metData = new HashMap();
            metData.put(WMGConstant.IS_RESUME_FLOW, "true");
            internalMnplResponseMap.put("METDATA", metData);
            dao.getExistingHashData(taskReferenceNumber, replyToInstanceId, replyToAddress, internalMnplResponseMap);

            Map<String, Object> requestMap = (Map<String, Object>) internalMnplResponseMap.get("RequestMap");

            Timestamp lastupdated = new Timestamp(System.currentTimeMillis());
            if (WMGConstant.SUCCESS.equalsIgnoreCase(status)) {
                logger.debug(" MnplResponseProcessorImpl :: processMessage::METRefrenceNumber : {} ::updating staus to AGG_RES in taskdetails table", metReferenceNumber);
                dao.updateRequestStatusInITR(WMGConstant.AGG_RES_STATUS, lastupdated, metReferenceNumber);
                aggregatorCoreProcessor.doAggregation(internalMnplResponseMap);
            } else {
                if (StringUtil.isNotNull(errorCode) && WMGConstant.MANDATORY.equals(aggregationBusiness)) {
                    requestMap.put("errorCode", errorCode);
                    requestMap.put("errorDesc", errorDesc);
                    requestMap.put("errorText", errorText);
                }
                aggregatorCoreProcessor.doAggregation(internalMnplResponseMap);
            }
        } catch (Exception exception) {
            logger.error("MnplResponseProcessorImpl :: processMessage::METRefrenceNumber : {}::METBException: {} ",
                    metReferenceNumber, WMGUtil.getStackTrace(exception));
            Timestamp lastupdated = new Timestamp(System.currentTimeMillis());
            dao.updateRequestStatus(WMGConstant.AGG_REQ_STATUS, lastupdated, metReferenceNumber);
            dao.deleteAggregationSpecificData(metReferenceNumber, aggregationName);
        }
    }

    private void postErrorResponseToAggregatorInbound(String metReferenceNumber, String actionType) throws METBException {
        if (WMGConstant.CREATE_ACTION.equals(actionType)) {
            String responseStatus = WMGConstant.AGG_FAILURE;
            dao.updateResponse(metReferenceNumber, responseStatus);

        }
    }

    private boolean storeAggregationDetails(String metReferenceNumber, boolean flagPrepareInternalResponsePojo, HashMap hashMapResponse, Map outerMap) {
        if (null != hashMapResponse) {
            try {
                outerMap.put(WMGConstant.MNPL, hashMapResponse);

                dao.storeAggregationDetails(metReferenceNumber, outerMap, false);

            } catch (METBException e) {
                flagPrepareInternalResponsePojo = false;
                if (WMGConstant.ERROR_CODE_TO_REJECT_AGGREGATION_RESPONSE.equals(e.getErrorCode())) {
                    logger.error(" MnplResponseProcessorImpl :: processMessage::METRefrenceNumber : {}::METBException: Desc: {}  ErrorText: {}  ",
                            metReferenceNumber, e.getErrorDescription(), e.getErrorText());
                } else {
                    logger.error("MnplResponseProcessorImpl :: processMessage::METRefrenceNumber : {} :: Mannual Intervention Code: {} ::Error occurred while updating the MNPL response in database :: Manual intervention is required ", metReferenceNumber, WMGConstant.MANUAL_INTERVENTION_ERROR_CODE);
                }
            }
        }
        return flagPrepareInternalResponsePojo;
    }


    @Override
    public void retryHandler(final Message data) {

        try {
            Map<String, Object> headerMap = data.getMessageProperties().getHeaders();
            if (!(headerMap.containsKey(WMGConstant.RETRY_COUNT_IN_HEADER))) {
                logger.debug("MnplResponseProcessorImpl.RetryHandler() :: before 1st Retry.");
                headerMap.put(WMGConstant.RETRY_COUNT_IN_HEADER, 1);
                producerHandler.publishMessage(mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueExchange(), mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueRoutingKey(),
                        data);
            } else {
                int count = Integer.parseInt(headerMap.get(WMGConstant.RETRY_COUNT_IN_HEADER).toString());
                headerMap.put(WMGConstant.RETRY_COUNT_IN_HEADER, count + 1);
                logger.debug("MnplResponseProcessorImpl.RetryHandler() retries:: before ::{} Retry. {}", (count + 1), count);
                producerHandler.publishMessage(mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueExchange(), mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueRoutingKey(),
                        data);
            }
        } catch (Exception e1) {
            logger.error("MnplResponseProcessorImpl.RetryHandler() :: Exception Occured  :: ", e1);
        }
    }


}

