package com.bt.metb.aggregator.requestProcessor;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dao.DAOInterface;
import com.bt.metb.aggregator.dataobjects.EmppalRequestAttributeReader;
import com.bt.metb.aggregator.mqConfig.EmppalAggregatorMqConfigReader;
import com.bt.metb.aggregator.parser.interfaces.ParserServiceInterface;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.provider.Aggregator;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import com.bt.metb.aggregator.util.StringUtil;
import com.bt.metb.aggregator.util.WMGUtil;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

@Component("emppalRequestProcessorImpl")
@RefreshScope
public class EmppalRequestProcessorImpl implements Aggregator {

    @Autowired
    private EmppalRequestAttributeReader emppalRequestAttributeReader;

    @Autowired
    @Qualifier("resourceManager")
    private METResourceManagerInterface resourceManager;

    @Autowired
    @Qualifier("dao")
    private DAOInterface dao;

    @Autowired
    private EmppalAggregatorMqConfigReader emppalAggregatorMqConfigReader;

    @Autowired
    private MessagePublisher messagePublisher;

    @Autowired
    @Qualifier("simpleMessageConverter")
    private SimpleMessageConverter simpleMessageConverter;

    @Autowired
    @Qualifier("parserServiceImpl")
    private ParserServiceInterface parserServiceInterface;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EmppalRequestProcessorImpl.class);
    public EmppalRequestProcessorImpl(METResourceManagerInterface resourceManagerInterface) {
        resourceManager = resourceManagerInterface;
    }
    public Map executeDelegate(Map outerAggregator, String actionType, String metRefNumber, String validatedXML) throws METBException{
        Map empPalResultMap = new HashMap();
        String aggBusinessReq= null;
        String aggTechnicalReq = null;
        try {
            logger.debug("EmppalRequestProcessorImpl.execute() :: Start");
            Map requestMap = (HashMap) outerAggregator.get("RequestMap");

            String estimateId = (String) requestMap.get(WMGConstant.ESTIMATE_ID);
            String taskTypeId  = (String) requestMap.get(WMGConstant.TASK_TYPE_ID);

            aggTechnicalReq = ((Map<String, String>) outerAggregator.get(WMGConstant.AGG_NAME)).get(WMGConstant.REQUIREMENT_TECHNICAL);
            aggBusinessReq = ((Map<String, String>) outerAggregator.get(WMGConstant.AGG_NAME)).get(WMGConstant.REQUIREMENT_BUSINESS);

            boolean empcallApplicableFlag = isEmppalCallApplicable(taskTypeId);
            logger.debug("EmppalRequestProcessorImpl.execute() :: Start :: metRefNumber :: {},estimateId ::{},taskTypeId ::{}," +
                                "empcallApplicableFlag:: {}", metRefNumber, estimateId, taskTypeId, empcallApplicableFlag);

            if (!StringUtil.isNullOrEmpty(estimateId) && empcallApplicableFlag) {

                    String strUpdateTaskStatusRequest = (String) requestMap.get(WMGConstant.STATUS_UPDATE_TASK_REQUEST);
                    if ((!StringUtil.isNullOrEmpty(strUpdateTaskStatusRequest)) && WMGConstant.TRUE.equalsIgnoreCase(strUpdateTaskStatusRequest)) {
                        empPalResultMap.put(WMGConstant.AGGREGATION_IGNORE_SKIP, WMGConstant.STRING_TRUE);
                        logger.debug("EmppalRequestProcessorImpl.execute() :: EMPPAL skipped as STATUS_UPDATE_TASK_REQUEST flag is true");
                        return empPalResultMap;
                    }
                    String msgId = deriveMessageId(metRefNumber);
                    String empPalInputXML = doTransformationAndPersistIntoDB(validatedXML, msgId, estimateId, metRefNumber);
                    logger.debug("EmppalRequestProcessorImpl.executeDelegate() :: metRefNumber :: {},msgId {} " +
                            "emppalInputXML generated ::{},", metRefNumber, msgId,empPalInputXML);
                    sendMessageToSB(empPalInputXML, msgId, metRefNumber);
            }
                else {
                if (WMGConstant.OPTIONAL.equalsIgnoreCase(aggBusinessReq) || WMGConstant.OPTIONAL.equalsIgnoreCase(aggTechnicalReq)) {
                    logger.debug("EmppalRequestProcessorImpl.executeDelegate() :: Estimate id {}  is null, EMPPAL call is SKIPPED for MET Ref Number :: {}", estimateId, metRefNumber);
                    empPalResultMap.put(WMGConstant.AGGREGATION_IGNORE_SKIP, WMGConstant.STRING_TRUE);
                } else {
                    empPalResultMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP,WMGConstant.STRING_TRUE);
                }
            }
        }catch (Exception e){
            if (WMGConstant.OPTIONAL.equalsIgnoreCase(aggBusinessReq) || WMGConstant.OPTIONAL.equalsIgnoreCase(aggTechnicalReq)) {
                logger.debug("EmppalRequestProcessorImpl.executeDelegate() :: Unknown Exception occured :: EMPPAL call is SKIPPED for MET Ref Number :: {} exception {}", metRefNumber, WMGUtil.getStackTrace(e));
                empPalResultMap.put(WMGConstant.AGGREGATION_IGNORE_SKIP, WMGConstant.STRING_TRUE);
            } else {
                empPalResultMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP,WMGConstant.STRING_TRUE);
            }
        }

        logger.debug("EmppalRequestProcessorImpl.executeDelegate() Returning emppalResultMap :: {} for METRefNumber :: {}",empPalResultMap, metRefNumber);
        return empPalResultMap;
    }

    private boolean isEmppalCallApplicable(String taskTypeId){

        boolean empcallApplicable=false;
        StringTokenizer tokenizer = new StringTokenizer(emppalRequestAttributeReader.getTaskTypeApplicable(), WMGConstant.COMMA_DELIMITER);

        if (!StringUtil.isNullOrEmpty(taskTypeId)) {
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.equalsIgnoreCase(taskTypeId)) {
                    empcallApplicable = true;
                }
            }
        }
        return empcallApplicable;
    }

    private String deriveMessageId(String metRefNumber) throws METBException{
        String managedServer = resourceManager.getManagedServer();
        String strTime = new Timestamp(System.currentTimeMillis()).toString();
        return managedServer + "-" + StringUtil.dateFormation(strTime) + metRefNumber;
    }
    private String doTransformationAndPersistIntoDB(String validatedXML, String msgId, String estimateId, String metRefNumber) throws METBException{
        String empPalInputXML =null;
        Map map = new ConcurrentHashMap();
        map.put(WMGConstant.MESSAGE_ID, msgId);

        if (!StringUtil.isNullOrEmpty(estimateId)) {
            map.put("estimateId", estimateId);
        }
        if(!StringUtil.isNullOrEmpty(validatedXML)){
           empPalInputXML = parserServiceInterface.transform(validatedXML, WMGConstant.EMPPAL_REQUEST_XSL, map, false);
           Timestamp strTimeStamp = new Timestamp(System.currentTimeMillis());
           dao.insertAggReqXML(empPalInputXML, WMGConstant.EMPPAL, metRefNumber, WMGConstant.AGG_REQ_STATUS);
           dao.updateAggStatusWithReqXML(strTimeStamp, WMGConstant.EMPPAL, WMGConstant.AGG_REQ_STATUS, metRefNumber, msgId);
        }
        return empPalInputXML;
    }
    private void sendMessageToSB(String emppalReqXML,String msgId,String metRefNumber){
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("msgId", msgId);
        messageProperties.getHeaders().put(WMGConstant.RETRY_COUNT_IN_HEADER, 0);
        Message textMessage =null;
        try {
            logger.debug("EmppalRequestProcessorImpl.sendMessageToSB() :: Start");
            textMessage = simpleMessageConverter.toMessage(emppalReqXML, messageProperties);
            messagePublisher.publishMessage(emppalAggregatorMqConfigReader.getAggReqToEmppalOutboundQueueExchange(),
                    emppalAggregatorMqConfigReader.getAggReqToEmppalOutboundQueueRoutingKey(), textMessage);
            logger.debug("EmppalRequestProcessorImpl.sendMessageToSB() :: MetRefId {}, Successfully posted to AggregatorToEMPPAL Outbound Queue ::  ,msgId {}",metRefNumber,msgId);
        }
        catch (AmqpException notSentToRabbitMqException) {
            try {
                String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                if(textMessage != null)
                    textMessage.getMessageProperties().setHeader("timestamp", currentDate);
                messagePublisher.publishMessage(emppalAggregatorMqConfigReader.getAggReqToEmppalOutboundQueueExchange(),emppalAggregatorMqConfigReader.getAggReqToEmppalHospitalQueueRoutingKey(),textMessage);

                logger.debug("EmppalRequestProcessorImpl.sendMessageToSB() :: " +
                        "Successfully posted to AggregatorToEMPPAL Outbound Hospital Queue :: metRefNumber {} Message :: {}", metRefNumber,emppalReqXML);

            } catch (AmqpException notSentToRabbitMqHQException) {
                logger.error("EmppalRequestProcessorImpl.sendMessageToSB() :: Failed to send to Hospital queue ::metRefNumber{}, MessageNotSentToRabbitMqException :: {} ", metRefNumber,notSentToRabbitMqHQException);
            }
        }
        catch (Exception ex){
            logger.error("EXCEPTION OCCURED :: While posting message to ServiceBus from EmppalReqProcImpl for metRefNumber {} exception {}",metRefNumber,WMGUtil.getStackTrace(ex));
        }
    }
}
