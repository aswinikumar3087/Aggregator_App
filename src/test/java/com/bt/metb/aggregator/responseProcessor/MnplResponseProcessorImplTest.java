package com.bt.metb.aggregator.responseProcessor;

import com.bt.metb.MetbAbstractBase;
import com.bt.metb.aggregator.dao.DAOInterface;
import com.bt.metb.aggregator.dataobjects.AggregationResponseDO;
import com.bt.metb.aggregator.model.MnplRequestAttributeReader;
import com.bt.metb.aggregator.mqConfig.MnplAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.RmqConfigReader;
import com.bt.metb.aggregator.parser.interfaces.ParserServiceInterface;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import com.bt.metb.mnp.stub.Error;
import com.bt.metb.mnp.stub.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
 * @author Tanuj.Tripathi@bt.com
 */
@RunWith(MockitoJUnitRunner.class)
public class MnplResponseProcessorImplTest extends MetbAbstractBase {

    @InjectMocks
    private MnplResponseProcessorImpl mnplResponseProcessor;

    @Mock
    private METResourceManagerInterface resourceManager;

    @Mock
    private DAOInterface dao;

    @Mock
    private SimpleMessageConverter simpleMessageConverter;

    @Mock
    private MnplRequestAttributeReader mnplRequestAttributeReader;

    @Mock
    private ParserServiceInterface parserServiceInterface;

    @Mock
    private RmqConfigReader rmqConfigReader;

    @Mock
    private MessagePublisher producerHandler;

    @Mock
    private MnplAggregatorMqConfigReader mnplAggregatorMqConfigReader;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    public void processMessageTest() throws IOException, METBException {
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");

        Message textMessage = new Message(request.getBytes(), mp);
        textMessage.getMessageProperties().setHeader("msgId", "msgId");

        AggregationResponseDO aggregationResponseDO = getAggregationResponseDO();
        GetPlanDetailsResponse getPlanDetailsResponse = getValidTestResponse();

        when(simpleMessageConverter.fromMessage(any())).thenReturn(getPlanDetailsResponse);
        when(dao.getAggregationResponseDetailsObject(anyString())).thenReturn(aggregationResponseDO);
        when(dao.getValidatedXMLForMetrefId(anyString())).thenReturn("");
        when(mnplRequestAttributeReader.getMNPLSuccessStateCode()).thenReturn("OK");
        when(resourceManager.getManagedServer()).thenReturn("servername");
        when(parserServiceInterface.transform(anyString(), anyString(), anyBoolean())).thenReturn("mmnploutput");
        //doNothing().when(dao).storeAggregationDetails(anyString(), anyMap(), anyBoolean());

        mnplResponseProcessor.processMessage(textMessage);
        verify(parserServiceInterface).transform(anyString(), anyString(), anyBoolean());
    }

    @Test
    public void processMessageIfMNPLSuccessStateCodeIsNotOKTest() throws IOException, METBException {
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");

        Message textMessage = new Message(request.getBytes(), mp);
        textMessage.getMessageProperties().setHeader("msgId", "msgId");

        AggregationResponseDO aggregationResponseDO = getAggregationResponseDO();
        GetPlanDetailsResponse getPlanDetailsResponse = getValidTestResponse();

        when(simpleMessageConverter.fromMessage(any())).thenReturn(getPlanDetailsResponse);
        when(dao.getAggregationResponseDetailsObject(anyString())).thenReturn(aggregationResponseDO);
        when(dao.getValidatedXMLForMetrefId(anyString())).thenReturn("");
        when(mnplRequestAttributeReader.getMNPLSuccessStateCode()).thenReturn("Not OK");
        when(resourceManager.getManagedServer()).thenReturn("servername");
        doNothing().when(dao).deleteAggregationSpecificData(anyString(), anyString());
        doNothing().when(dao).storeAggregationLog(anyString(), anyString(), anyString(), anyString());

        mnplResponseProcessor.processMessage(textMessage);

        verify(dao, times(2)).updateRequestStatus(anyString(), any(), anyString());
    }

    @Test
    public void retryTest() throws IOException {
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);

        //when(rmqConfigReader.getRabbitRetry()).thenReturn("1");
        when(mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueExchange()).thenReturn("SourceExchange");
        when(mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueRoutingKey()).thenReturn("ewocs_to_metb_inb_rkey");

        mnplResponseProcessor.retryHandler(textMessage);
        textMessage.getMessageProperties().setHeader("1", "1");
        mnplResponseProcessor.retryHandler(textMessage);
        verify(producerHandler, times(2)).publishMessage(anyString(), anyString(), any());
    }

    @Test
    public void retryIfNotCountTest() throws IOException {
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        Message textMessage = new Message(request.getBytes(), mp);

        //when(rmqConfigReader.getRabbitRetry()).thenReturn("1");
        when(mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueExchange()).thenReturn("SourceExchange");
        when(mnplAggregatorMqConfigReader.getAggregatorMnplOutboundQueueRoutingKey()).thenReturn("ewocs_to_metb_inb_rkey");

        textMessage.getMessageProperties().setHeader("1", "1");
        mnplResponseProcessor.retryHandler(textMessage);
        verify(producerHandler).publishMessage(anyString(), anyString(), any());

    }

    private AggregationResponseDO getAggregationResponseDO() {
        AggregationResponseDO aggregationResponseDO = new AggregationResponseDO();
        aggregationResponseDO.setCorrelationId("CORRELATION_ID");
        aggregationResponseDO.setReqXML("REQUEST_XML");
        aggregationResponseDO.setReqStatus("status");
        aggregationResponseDO.setAggregationName("MNPL");
        aggregationResponseDO.setTechnicalExceptionRequirement("M");
        aggregationResponseDO.setBusinessExceptionRequirement("M");
        aggregationResponseDO.setMessageIdNotFound(false);
        aggregationResponseDO.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
        aggregationResponseDO.setLatestTaskOperation("Task Operation");
        aggregationResponseDO.setMetRefNumber("1234567890");
        return aggregationResponseDO;
    }

    public GetPlanDetailsResponse getValidTestResponse() {

        GetPlanDetailsResponse response = new GetPlanDetailsResponse();
        ServiceState resSS = new ServiceState();
        resSS.setStateCode("OK");
        StandardHeaderBlock resHB = new StandardHeaderBlock();
        resHB.setServiceState(resSS);
        response.setStandardHeader(resHB);

        ResponseMetaData responseMetaData = new ResponseMetaData("1");
        PlanResponseListPlan planResponseListPlan = new PlanResponseListPlan();
        planResponseListPlan.setResponseMetaData(responseMetaData);
        planResponseListPlan.setProjectID("projectid");

        NetworkSite networkSite = new NetworkSite();
        networkSite.setSiteId("1223");

        Equipment equipment = new Equipment();
        equipment.setId("eid");
        equipment.setEqType("eqtype");
        equipment.setLogicalLocation(networkSite);

        Equipment[] equipments = new Equipment[1];
        equipments[0] = equipment;

        NetworkOrderEquipmentData networkOrderEquipmentData = new NetworkOrderEquipmentData();
        networkOrderEquipmentData.setEquipment(equipments);
        NetworkOrderEquipmentData[] eqp_data = new NetworkOrderEquipmentData[1];
        eqp_data[0] = networkOrderEquipmentData;

        NetworkOrder orderdata = new NetworkOrder();
        orderdata.setEquipmentData(eqp_data);

        NetworkOrder[] orders = new NetworkOrder[1];
        orders[0] = orderdata;

        PlanResponseList planResponseList = new PlanResponseList();

        PlanResponseListPlan[] plan = new PlanResponseListPlan[1];
        planResponseListPlan.setOrderData(orders);
        plan[0] = planResponseListPlan;

        Error error = new Error();
        error.setErrorDescription("unable to get project id related details");
        Error[] errors = new Error[]{error};

        PlanErrorList planErrorList = new PlanErrorList();
        planErrorList.setPlanError(errors);

        planResponseList.setPlan(plan);
        planResponseList.setPlanErrorList(planErrorList);
        response.setPlanResponseList(planResponseList);

        planResponseList.setPlan(plan);
        response.setPlanResponseList(planResponseList);

        return response;
    }
}