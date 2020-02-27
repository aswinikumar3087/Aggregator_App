package com.bt.metb.aggregator.processor;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dao.DAOInterface;
import com.bt.metb.aggregator.messageprocessor.AggregatorCoreProcessor;
import com.bt.metb.aggregator.model.StuckPollerAttributeReader;
import com.bt.metb.aggregator.mqConfig.EmppalAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.MetbAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.MnplAggregatorMqConfigReader;
import com.bt.metb.aggregator.parser.interfaces.GetPlanDetailsRequestIgnore;
import com.bt.metb.aggregator.parser.interfaces.ParserServiceInterface;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import com.bt.metb.aggregator.util.WMGUtil;
import com.bt.metb.mnp.stub.GetPlanDetailsRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.axis.types.Duration;
import org.apache.axis.types.Time;
import org.apache.axis.types.URI;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;


@SuppressWarnings({"rawtypes", "unchecked"})
@Component
@RefreshScope
public class StuckRequestPollerBeanProcessor {

    private static final Logger logger = LoggerFactory.getLogger(StuckRequestPollerBeanProcessor.class);

    private static final long serialVersionUID = -3765972619261167134L;

    @Autowired
    private StuckPollerAttributeReader stuckPollerAttributeReader;

    @Autowired
    private MessagePublisher messagePublisher;

    @Autowired
    @Qualifier("simpleMessageConverter")
    private SimpleMessageConverter simpleMessageConverter;

    @Autowired
    private MnplAggregatorMqConfigReader mnplAggregatorMqConfigReader;

    @Autowired
    private EmppalAggregatorMqConfigReader emppalAggregatorMqConfigReader;

    @Autowired
    private MetbAggregatorMqConfigReader metbAggregatorMqConfigReader;

    @Autowired
    @Qualifier("parserServiceImpl")
    private ParserServiceInterface parserServiceInterface;

    @Autowired
    @Qualifier("resourceManager")
    private METResourceManagerInterface resourceManager;

    @Autowired
    AggregatorCoreProcessor aggregatorCoreProcessor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("dao")
    private DAOInterface dao;

    @Autowired
    private Environment environment;

    /**
     * Default Constructor
     */
    public StuckRequestPollerBeanProcessor() {
    }

    /**
     * @param resourceManager : Object of resourceManager interface
     */

   /* @Autowired
    public StuckRequestPollerBeanProcessor(METResourceManagerInterface resourceManager) {
        this.resourceManager = resourceManager;
    }*/

    /**
     * This method is called by startNotificationRouter. This method fetches the requests from taskDetails that have
     * not yet been responded to by the aggregator and reposts them to the Aggregator MQ
     * and then updates the taskDetails table to reflect that the request has been posted.
     */
    /**
     * @throws METBException Whenever a METException is raised
     */
    public void routeStuckRequest() {
        String strManagedServer = null;


        try {
            logger.debug("StuckRequestPollerBeanProcessor:routeStuckRequest() called");
            strManagedServer = resourceManager.getManagedServer();

            dao.updateServerLookupLastProcessdate(strManagedServer);
            Object[] params = new Object[]{strManagedServer, stuckPollerAttributeReader.getBatchSizeAggregation()};
            int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};

            List<Map<String, Object>> selectTaskDetails = jdbcTemplate.queryForList(WMGConstant.SELECT_STUCK_REQUEST, params, types);
            logger.debug("StuckRequestPollerBeanProcessor:routeStuckRequest() selectTaskDetails.size() :{}", selectTaskDetails.size());

            int retryLimit = Integer.parseInt(stuckPollerAttributeReader.getAggregationRetryLimit());
            String metReferenceNumber = null;
            String aggregationName = null;
            int aggregationRetryCount = 0;
            String retryStatus = null;
            String aggregationRequestXML = null;
            String correlationId = null;

            for (Map<String, Object> mapDetails : selectTaskDetails) {
                for (Map.Entry<String, Object> detail : mapDetails.entrySet()) {
                    String key = detail.getKey();
                    Object value = detail.getValue();
                    if (key.equalsIgnoreCase(WMGConstant.MET_REF_NUMBER)) {
                        metReferenceNumber = String.valueOf(value);
                    } else if (key.equalsIgnoreCase(WMGConstant.AGG_NAME)) {
                        aggregationName = String.valueOf(value);
                    } else if (key.equalsIgnoreCase(WMGConstant.AGG_RETRY_COUNT)) {
                        aggregationRetryCount = Integer.parseInt(String.valueOf(value));
                    } else if (key.equalsIgnoreCase(WMGConstant.RETRY_STATUS)) {
                        retryStatus = String.valueOf(value);
                    } else if (key.equalsIgnoreCase(WMGConstant.AGG_XML)) {
                        aggregationRequestXML = String.valueOf(value);
                    } else if (key.equalsIgnoreCase(WMGConstant.CORRELATION_ID)) {
                        correlationId = String.valueOf(value);
                    } else {
                        logger.debug("StuckRequestPollerBeanProcessor:routeStuckRequest() INVALID KEY");
                    }
                }

                logger.debug("StuckRequestPollerBeanProcessor:routeStuckRequest() metref:{} ::aggregationname:{}  :: aggregationRetryCount:{}  ::  retryStatus:{}   :: aggregationRequestXML:{}  ::correlationId:{}", metReferenceNumber, aggregationName, aggregationRetryCount, retryStatus, aggregationRequestXML, correlationId);
                if (WMGConstant.AGGREGATOR_RETRY.equalsIgnoreCase(retryStatus)) {

                    logger.debug("StuckRequestPollerBeanProcessor:routeStuckRequest() AGGREGATOR_RETRY");
                    //Go for RETRY option
                    if (aggregationRetryCount >= retryLimit) {

                        logger.debug("StuckRequestPollerBeanProcessor:routeStuckRequest() ::: Retry Limit exhausted");
                        //Retry Limit exhausted  send the Requestmap to core with technical error codes in requestmap
                        String status = null;
                        Map requestMapDetails = dao.getRequestMapWithErrorData(metReferenceNumber, aggregationName);

                        aggregatorCoreProcessor.sendTheResponseMapToMetbCore(requestMapDetails, metReferenceNumber);

                        status = dao.updateStuckRequestStatus(metReferenceNumber);
                        if ("SUCCESS".equalsIgnoreCase(status)) {
                            logger.debug(" Selected {} : {} Reset the xml stored for aggregation call", metReferenceNumber, aggregationName);
                            dao.updateAggReqXML(aggregationName, metReferenceNumber);
                        }

                    } else {

                        logger.debug("StuckRequestPollerBeanProcessor:routeStuckRequest() ::: Retries Left");
                        //Below RETRY LIMIT
                        Timestamp strTimeStamp = new Timestamp(System.currentTimeMillis());
                        String status = null;
                        status = dao.updateStuckRetryRequest(strTimeStamp, aggregationRetryCount, metReferenceNumber);

                        if ("UPDATED_SUCCESS".equalsIgnoreCase(status)) {
                            logger.debug(" Selected {} : {} for posting to {} Queue", metReferenceNumber, aggregationName, aggregationName);

                            String exchange = null;
                            String routingKey = null;
                            Message textMessage = null;

                            String msgId = correlationId;
                            MessageProperties mp = new MessageProperties();
                            mp.getHeaders().put("msgId", msgId);

                            //AGGCORE_TO_EMPPAL_OUTB
                            if (WMGConstant.EMPPAL.equalsIgnoreCase(aggregationName)) {
                                exchange = emppalAggregatorMqConfigReader.getAggReqToEmppalOutboundQueueExchange();
                                routingKey = emppalAggregatorMqConfigReader.getAggReqToEmppalOutboundQueueRoutingKey();
                                if (StringUtils.isNotBlank(aggregationRequestXML))
                                    textMessage = simpleMessageConverter.toMessage(aggregationRequestXML, mp);
                                else
                                    logger.error("StuckRequestPollerBeanProcessor:routeStuckRequest() aggregationRequestXML is null");

                            } else if (WMGConstant.MNPL.equalsIgnoreCase(aggregationName)) {//AGGCORE_TO_MNPL_OUTB
                                exchange = mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueExchange();
                                routingKey = mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueRoutingKey();

                                ObjectMapper mapper = new ObjectMapper();
                                mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
                                mapper.addMixIn(Duration.class, GetPlanDetailsRequestIgnore.class);
                                mapper.addMixIn(Time.class, GetPlanDetailsRequestIgnore.class);
                                mapper.addMixIn(URI.class, GetPlanDetailsRequestIgnore.class);

                                GetPlanDetailsRequest getPlanDetailsRequest = mapper.readValue(aggregationRequestXML, GetPlanDetailsRequest.class);
                                textMessage = simpleMessageConverter.toMessage(getPlanDetailsRequest, mp);

                            }
                            postRequestToComponentOutBoundForProcessing(textMessage, metReferenceNumber, exchange, routingKey);

                        }

                        if ("UPDATED_SUCCESS".equalsIgnoreCase(status)) {
                            logger.debug("StuckRequestPollerBeanProcessor.routeStuckRequest : METReferenceNumber: {} :: AuditAGGREGATORLOGGER-Code: {} :: AggregationName: {} :: Aggregation Request XML posted on Inbound MQ", metReferenceNumber, WMGConstant.AUDITLOG_PROGRESSMSG_CODE, aggregationName);
                        }
                    }
                }
            }

        } catch (Exception ex) {
            logger.error("StuckRequestPollerBeanProcessor :: routeStuckRequest: Unknown Exception : Error Code: {} :: STACK: {}", WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGUtil.getStackTrace(ex));
        }

    }

    private void postRequestToComponentOutBoundForProcessing(Message textMessage, String metRefNumber, String exchange, String routingKey) {

        logger.debug("StuckRequestPollerBeanProcessor.postRequestToComponentOutBoundForProcessing() METRefrenceNumber: {}  REQUEST-VALUEST: {}", metRefNumber, textMessage.getBody());
        try {
            messagePublisher.publishMessage(exchange, routingKey, textMessage);
        } catch (Exception er) {

            logger.error("StuckRequestPollerBeanProcessor.postRequestToComponentOutBoundForProcessing() :: Exception occured :: {} ", er);
        }
    }
}

