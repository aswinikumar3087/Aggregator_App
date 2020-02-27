package com.bt.metb.aggregator.requestProcessor;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dao.DAOInterface;
import com.bt.metb.aggregator.model.MnplRequestAttributeReader;
import com.bt.metb.aggregator.mqConfig.MnplAggregatorMqConfigReader;
import com.bt.metb.aggregator.parser.interfaces.ParserServiceInterface;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.provider.Aggregator;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import com.bt.metb.aggregator.util.StringUtil;
import com.bt.metb.aggregator.util.WMGUtil;
import com.bt.metb.mnp.stub.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.axis.types.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Component("mnplRequestProcessorImpl")
@RefreshScope
public class MnplRequestProcessorImpl implements Aggregator {

    private static final Logger logger = LoggerFactory.getLogger(MnplRequestProcessorImpl.class);

    @Value("${PlotId}")
    private String plotId;

    @Value("${CSPId}")
    private String strCSPId;

    @Value("${SplitterId}")
    private String strSplitterId;

    @Autowired
    private MnplAggregatorMqConfigReader mnplAggregatorMqConfigReader;

    @Autowired
    private MessagePublisher messagePublisher;

    @Autowired
    private MnplRequestAttributeReader mnplRequestAttributeReader;

    @Autowired
    @Qualifier("simpleMessageConverter")
    private SimpleMessageConverter simpleMessageConverter;

    @Autowired
    @Qualifier("resourceManager")
    private METResourceManagerInterface resourceManager;

    @Autowired
    @Qualifier("dao")
    private DAOInterface dao;

    @Autowired
    @Qualifier("parserServiceImpl")
    private ParserServiceInterface parserServiceInterface;


    public MnplRequestProcessorImpl(METResourceManagerInterface resourceManagerInterface) {
        resourceManager = resourceManagerInterface;
    }

    public Map executeDelegate(Map outerAggregator, String actionType, String metRefNumber, String validatedXML) throws METBException {
        logger.debug("MnplRequestProcessorImpl.executeDelegate() METRefrenceNumber: {}  actionType: {} "
                , metRefNumber, actionType);

        HashMap<String, Object> hashMapResponse = new HashMap();
        HashMap hashMapRequestValues = null;
        boolean mnplcallApplicable = false;
        String aggTechnicalReq = null;
        String aggBusinessReq = null;

        try {

            logger.debug("MnplRequestProcessorImpl.executeDelegate() parserServiceIntertace {} dao :: {}"
                    , parserServiceInterface, dao);

            plotId = parserServiceInterface.getRequiredTagValue(validatedXML, WMGConstant.PLOT_ID);
            strCSPId = parserServiceInterface.getRequiredTagValue(validatedXML, WMGConstant.CSP_ID);
            strSplitterId = parserServiceInterface.getRequiredTagValue(validatedXML, WMGConstant.SPLITTER_ID);
            Map requestMap = (HashMap) outerAggregator.get("RequestMap");
            String strProjectId = (String) requestMap.get(WMGConstant.PROJECT_ID);
            String strEstimateId = (String) requestMap.get(WMGConstant.ESTIMATE_ID);
            String strMessageId = (String) requestMap.get(WMGConstant.TASK_MESSAGEID);
            aggTechnicalReq = ((Map<String, String>) outerAggregator.get(WMGConstant.AGG_NAME)).get(WMGConstant.REQUIREMENT_TECHNICAL);
            aggBusinessReq = ((Map<String, String>) outerAggregator.get(WMGConstant.AGG_NAME)).get(WMGConstant.REQUIREMENT_BUSINESS);

            GetPlanDetailsRequest getPlanDetailsRequest = null;

            if (StringUtil.isNullOrEmpty(strProjectId) || !StringUtil.isNullOrEmpty(plotId) || !StringUtil.isNullOrEmpty(strCSPId) || !StringUtil.isNullOrEmpty(strSplitterId)) {
                mnplcallApplicable = false;
                logger.debug("MnplRequestProcessorImpl.executeDelegate():MNPL Call was not made as PlotDetails are present in requestXML:: METReferenceNumber: {}", metRefNumber);
            } else {
                mnplcallApplicable = true;
                getPlanDetailsRequest = populateRequestObject(strProjectId, strEstimateId, strMessageId, metRefNumber); //static things
            }

            if (mnplcallApplicable) {

                //adding bussiness exception to map and returning back from this point
                if (null == getPlanDetailsRequest) {
                    logger.debug("MnplRequestProcessorImpl.executeDelegate(): getPlanDetailsRequest is null  ::{}", getPlanDetailsRequest);
                    hashMapResponse.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);
                    return hashMapResponse;
                }
                String strUpdateTaskStatusRequest = (String) requestMap.get(WMGConstant.STATUS_UPDATE_TASK_REQUEST);
                if ((!StringUtil.isNullOrEmpty(strUpdateTaskStatusRequest)) && WMGConstant.TRUE.equalsIgnoreCase(strUpdateTaskStatusRequest)) {
                    logger.debug("MnplRequestProcessorImpl.executeDelegate(): call is not made as amend request is for UpdateTask for MET Ref Number ::{}", metRefNumber);
                    hashMapResponse.put(WMGConstant.AGGREGATION_IGNORE_SKIP, WMGConstant.STRING_TRUE);
                    return hashMapResponse;
                }
                postRequestToMnplOutBoundForProcessing(getPlanDetailsRequest, metRefNumber, aggTechnicalReq, aggBusinessReq, strMessageId);
            } else {
                return mnplCallNotApplicable(metRefNumber, hashMapResponse, aggTechnicalReq, aggBusinessReq);
            }
        } catch (Exception ex) {
            logger.error("MnplRequestProcessorImpl.executeDelegate() METRefrenceNumber: {} STACK: {} ", metRefNumber, WMGUtil.getStackTrace(ex));
            hashMapResponse.put(WMGConstant.AGGREGATION_IGNORE_SKIP, WMGConstant.STRING_TRUE);
            if (WMGConstant.OPTIONAL.equalsIgnoreCase(aggBusinessReq) || WMGConstant.OPTIONAL.equalsIgnoreCase(aggTechnicalReq)) {
                logger.debug("EmppalRequestProcessorImpl.executeDelegate() :: Unknown Exception occured :: EMPPAL call is SKIPPED for MET Ref Number :: {} error:: {}", metRefNumber, WMGUtil.getStackTrace(ex));
                hashMapResponse.put(WMGConstant.AGGREGATION_IGNORE_SKIP, WMGConstant.STRING_TRUE);
            } else {
                hashMapResponse.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);
            }
        }
        logger.debug("MnplRequestProcessorImpl.executeDelegate()  METRefrenceNumber: {} RETURN: {} ", metRefNumber, hashMapRequestValues);
        return hashMapResponse;
    }

    private Map mnplCallNotApplicable(String metRefNumber, HashMap<String, Object> hashMapResponse, String aggTechnicalReq, String aggBusinessReq) {
        if (WMGConstant.OPTIONAL.equalsIgnoreCase(aggBusinessReq) || WMGConstant.OPTIONAL.equalsIgnoreCase(aggTechnicalReq)) {
            logger.debug("MnplRequestProcessorImpl.executeDelegate() :: MNPL call is SKIPPED for MET Ref Number :: {}", metRefNumber);
            hashMapResponse.put(WMGConstant.AGGREGATION_IGNORE_SKIP, WMGConstant.STRING_TRUE);
        } else {
            hashMapResponse.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);
        }
        return hashMapResponse;
    }


    /**
     * @param strProjectId
     * @param strEstimateId
     * @param strMessageId
     * @param metReferenceNumber
     * @return
     * @throws METBException DOCUMENT ME!
     */
    @SuppressWarnings("rawtypes")
    protected GetPlanDetailsRequest populateRequestObject(String strProjectId, String strEstimateId, String strMessageId, String metReferenceNumber) throws METBException {

        logger.debug("MnplRequestProcessorImpl.populateRequestObject() METRefrenceNumber: {} ", metReferenceNumber);

        String strAction;
        String strAddress;
        String strServiceName;
        String strToAddress;
        String strStateCode;
        String strPayloadFormat;
        String strRevision;
        String strVersion;
        String strIndicator;
        String strInterval;
        String strRetry;
        GetPlanDetailsRequest getPlanDetailsRequest = null;

        try {
            String strFromAddress;

            logger.debug("MnplRequestProcessorImpl.populateRequestObject() METRefrenceNumber: {}  RequestProjectId: {} RequestEstimateId: {} MessageId: {}"
                    , metReferenceNumber, strProjectId, strEstimateId, strMessageId);

            if (StringUtil.isNullOrEmpty(strProjectId)) {
                return getPlanDetailsRequest;
            } else {

                getPlanDetailsRequest = new GetPlanDetailsRequest();
                StandardHeaderBlock standardHeader = new StandardHeaderBlock();
                E2E e2e = new E2E();
                e2e.setE2EDATA("");

                ServiceState state = new ServiceState();
                strStateCode = mnplRequestAttributeReader.getMNPLServiceStateStateCode();
                state.setStateCode(strStateCode);

                strIndicator = mnplRequestAttributeReader.getMNPLServiceStateResendIndicator();
                state.setResendIndicator(Boolean.valueOf(strIndicator));

                strInterval = mnplRequestAttributeReader.getMNPLServiceStateRetryInterval();
                state.setRetryInterval(BigInteger.valueOf(Integer.parseInt(strInterval)));

                strRetry = mnplRequestAttributeReader.getMNPLServiceStateRetriesRemaining();
                state.setRetriesRemaining(BigInteger.valueOf(Integer.parseInt(strRetry)));

                logger.debug("MnplRequestProcessorImpl.populateRequestObject() METRefrenceNumber: {} StateCode : {}, ResendIndicator : {}, RetryInterval : {}, RetriesRemaining : {} ",
                        metReferenceNumber, state.getStateCode(), state.getResendIndicator(), state.getRetryInterval(), state.getRetriesRemaining());


                standardHeader.setServiceState(state);

                ServiceAddressing serviceAddressing = new ServiceAddressing();

                strAction = mnplRequestAttributeReader.getMNPLServiceAddressingAction();
                serviceAddressing.setAction(new URI(strAction));

                logger.debug("MnplRequestProcessorImpl.populateRequestObject() METRefrenceNumber: {} ServiceAddressing.Action: {}", metReferenceNumber, serviceAddressing.getAction());

                AddressReference addrRef = new AddressReference();
                strAddress = mnplRequestAttributeReader.getMNPLServiceAddressingAddress();
                addrRef.setAddress(new URI(strAddress));


                strFromAddress = mnplRequestAttributeReader.getMNPLServiceAddressingFrom();
                serviceAddressing.setFrom(new URI(strFromAddress));
                serviceAddressing.setMessageId(strMessageId);
                serviceAddressing.setReplyTo(addrRef);
                strServiceName = mnplRequestAttributeReader.getMNPLServiceAddressingServiceName();
                serviceAddressing.setServiceName(new URI(strServiceName));

                AddressReference toAddrRef = new AddressReference();

                strToAddress = mnplRequestAttributeReader.getMNPLServiceAddressingToAddress();
                toAddrRef.setAddress(new URI(strToAddress));


                serviceAddressing.setTo(toAddrRef);

                serviceAddressing.setFaultTo(toAddrRef);

                //Set ServiceAddressing
                standardHeader.setServiceAddressing(serviceAddressing);

                ServiceSpecification spec = new ServiceSpecification();
                strPayloadFormat = mnplRequestAttributeReader.getMNPLServiceSpecificationPayloadFormat();
                spec.setPayloadFormat(strPayloadFormat);

                strRevision = mnplRequestAttributeReader.getMNPLServiceSpecificationRevision();
                spec.setRevision(strRevision);

                strVersion = mnplRequestAttributeReader.getMNPLServiceSpecificationVersion();
                spec.setVersion(strVersion);
//	          	Set ServiceSpecification
                standardHeader.setServiceSpecification(spec);

                //Set StandardHeader
                getPlanDetailsRequest.setStandardHeader(standardHeader);

                // Set the PlanrequestList elements
                PlanRequestListPlan listPlan = new PlanRequestListPlan();

                NetworkPlanningActivity activity = new NetworkPlanningActivity();

                activity.setActivityId(strEstimateId);

                NetworkPlanningActivity[] activityArr = {activity};
                listPlan.setProjectID(strProjectId);
                listPlan.setAction(WMGConstant.ACTION_ID);
                listPlan.setPlanningActivity(activityArr);
                PlanRequestListPlan[] listPlansArray = {listPlan};
                PlanRequestList planRequestList = new PlanRequestList();
                planRequestList.setPlan(listPlansArray);
                getPlanDetailsRequest.setPlanRequestList(planRequestList);

                logger.debug("MnplRequestProcessorImpl.populateRequestObject() MetReferenceNumber: {}  ProjectId: {} EstimateID : {}", metReferenceNumber, strProjectId, strEstimateId);
            }
        } catch (Exception ex) {
            throw new METBException(WMGConstant.ORMNPL_TECHNICAL_ERROR_CODE, WMGConstant.TECHNICAL_EXCEPTION,
                    WMGConstant.ORMNPL_TECHNICAL_ERROR_DESC, WMGUtil.getStackTrace(ex));
        }

        logger.debug("MnplRequestProcessorImpl.populateRequestObject() METRefrenceNumber: {}  RETURN: {}", metReferenceNumber, getPlanDetailsRequest);
        return getPlanDetailsRequest;
    }

    private void postRequestToMnplOutBoundForProcessing(GetPlanDetailsRequest getPlanDetailsRequest, String metRefNumber, String technicalExceptionParam, String businessExceptionParam, String messageId) throws METBException, JsonProcessingException {

        logger.debug("MnplRequestProcessorImpl.postRequestToMnplOutBoundForProcessing() METRefrenceNumber: {}  REQUEST-VALUEST: {} MessageId {}", metRefNumber, getPlanDetailsRequest, messageId);
        String managedServer = resourceManager.getManagedServer();
        String strTime = new Timestamp(System.currentTimeMillis()).toString();
        String msgId = managedServer + "-" + StringUtil.dateFormation(strTime) + "-" + metRefNumber;
        Timestamp strTimeStamp = new Timestamp(System.currentTimeMillis());
        ObjectMapper message = new ObjectMapper();
        dao.insertAggReqXML(message.writerWithDefaultPrettyPrinter().writeValueAsString(getPlanDetailsRequest), WMGConstant.MNPL, metRefNumber, WMGConstant.AGG_REQ_STATUS);
        dao.updateAggStatusWithReqXML(strTimeStamp, WMGConstant.MNPL, WMGConstant.AGG_REQ_STATUS, metRefNumber, msgId);

        try {
            MessageProperties mp = prepareMessageHeaders(technicalExceptionParam, businessExceptionParam, msgId);
            Message textMessage = simpleMessageConverter.toMessage(getPlanDetailsRequest, mp);
            logger.debug("MnplRequestProcessorImpl.postRequestToMnplOutBoundForProcessing() TextMessage {}", textMessage);

            messagePublisher.publishMessage(mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueExchange(), mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueRoutingKey(),
                    textMessage);
        } catch (Exception ex) {
            try {
                MessageProperties mp = prepareMessageHeaders(technicalExceptionParam, businessExceptionParam, msgId);
                Message textMessage = simpleMessageConverter.toMessage(getPlanDetailsRequest, mp);
                messagePublisher.publishMessage(mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueExchange(), mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueRoutingKey(),
                        textMessage);
                logger.debug("MnplRequestProcessorImpl.postRequestToMnplOutBoundForProcessing() :: Successfully produced to Aggregator to Mnpl Outbound Queue :: Message :: {}", getPlanDetailsRequest);

            } catch (Exception er) {
                logger.error("MnplRequestProcessorImpl.postRequestToMnplOutBoundForProcessing() :: Exception occured :: {} ", er);
            }
        }
    }

    private MessageProperties prepareMessageHeaders(String technicalExceptionParam, String businessExceptionParam, String msgId) {
        MessageProperties mp = new MessageProperties();
        mp.getHeaders().put("msgId", msgId);
        mp.getHeaders().put("technicalExceptionParam", technicalExceptionParam);
        mp.getHeaders().put("businessExceptionParam", businessExceptionParam);
        mp.getHeaders().put("RetryCount", 0);
        return mp;
    }
}
