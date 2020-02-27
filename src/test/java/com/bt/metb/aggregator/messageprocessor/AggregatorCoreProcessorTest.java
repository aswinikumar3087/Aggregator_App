/*
* @author-aswini.kumarparida@bt.com
* */
package com.bt.metb.aggregator.messageprocessor;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dao.DAO;
import com.bt.metb.aggregator.dataobjects.AggregationMainDBSchema;
import com.bt.metb.aggregator.mqConfig.MetbAggregatorMqConfigReader;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.provider.Aggregator;
import com.bt.metb.aggregator.requestProcessor.EmppalRequestProcessorImpl;
import com.bt.metb.aggregator.requestProcessor.MnplRequestProcessorImpl;
import com.bt.metb.aggregator.util.DBLoading;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.ApplicationContext;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DBLoading.class,RequestType.class})
public class AggregatorCoreProcessorTest {

    @InjectMocks
    private AggregatorCoreProcessor aggregatorCoreProcessor;
    @Mock
    private DAO dao;

    @Mock
    private METResourceManagerInterface resourceManager;

    @Mock
    private SimpleMessageConverter simpleMessageConverter;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    AggregationMainDBSchema aggregationMainDBSchema;

    @Mock
    Aggregator aggregator;

    @Mock
    ApplicationContext applicationContext;

    @Mock
    MnplRequestProcessorImpl mnplRequestProcessor;

    @Mock
    EmppalRequestProcessorImpl emppalRequestProcessor;

    @Mock
    MetbAggregatorMqConfigReader metbAggregatorMqConfigReader;

    @Mock
    Message aggResponseString;

    @Mock
    ObjectMapper objectMapper;




@Before
public void setUp(){
    MockitoAnnotations.initMocks(this);
}


    @Test
    public void doAggregationTestForMnplCreate() throws Exception {
        Map outerMap = new HashMap();
        Map<String,String> requestMap = new HashMap<String,String>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");
        requestMap.put(WMGConstant.PRODUCT_TYPE,"COMPLEX");
        requestMap.put(WMGConstant.TASK_LOB,"Openreach");
        requestMap.put(WMGConstant.TASK_CATEGORY,"Fulfillment");
        requestMap.put(WMGConstant.MET_REF_NUM,"146");
        requestMap.put("ActionType","CreateTaskRequest");
        requestMap.put("ValidatedXML","ValidatedXML");
        outerMap.put("RequestMap",requestMap);

        Map mnplInner = new HashMap();
        Map emppalinner = new HashMap();
        mnplInner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.MNPL);
        mnplInner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        mnplInner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        mnplInner.put(WMGConstant.INTERFACE_PROTOCOL, "WS");

       /* emppalinner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.EMPPAL);
        emppalinner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        emppalinner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        emppalinner.put(WMGConstant.INTERFACE_PROTOCOL, "WS");*/
        Map outer = new HashMap();
        outer.put("1",mnplInner);
       // outer.put("2",emppalinner);

        Map mnplResponseMap = new HashMap();
        mnplResponseMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);

        Map emppalResMap = new HashMap();
        emppalResMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);
       /* AggregationMainDBSchema ag= new AggregationMainDBSchema();
        ag.setLob("Openreach");
        ag.setProductName("COMPLEX");
        ag.setTaskCategoryName("ASSURANCE");
        ag.setAggregationId("006");
        ag.setWmsTaskCategory("N");
        ag.setMainWorkLocation("LN");
        ag.setTaskTypeId("COMPLEX");
        ag.setWmsCustomerType("BM");*/

       PowerMockito.mockStatic(DBLoading.class);
       AggregationMainDBSchema aggregationMainDBSchema= Mockito.mock(AggregationMainDBSchema.class);
       when(DBLoading.getAggregationMainData(any(),any(),any())).thenReturn(aggregationMainDBSchema);
       when(aggregationMainDBSchema. getAggregationId()).thenReturn("038");
       when(DBLoading.getAggregationData(anyString(),anyString())).thenReturn(outer);
       PowerMockito.mockStatic(RequestType.class);
       when(RequestType.evaluate(anyString())).thenReturn(RequestType.CREATE);

       doNothing().when(dao).storeAggregationDetails(anyString(), any(), anyBoolean());
       when(applicationContext.getBean(MnplRequestProcessorImpl.class)).thenReturn(mnplRequestProcessor);
       when(aggregator.executeDelegate(any(), anyString(), anyString(), anyString())).thenReturn(mnplResponseMap);
       aggregatorCoreProcessor.doAggregation(outerMap);
    }
    @Test
    public void doAggregationTestForMnplAmend() throws Exception {
        Map outerMap = new HashMap();

        Map<String,String> requestMap = new HashMap<String,String>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");
        requestMap.put(WMGConstant.PRODUCT_TYPE,"COMPLEX");
        requestMap.put(WMGConstant.TASK_LOB,"Openreach");
        requestMap.put(WMGConstant.TASK_CATEGORY,"Fulfillment");
        requestMap.put(WMGConstant.MET_REF_NUM,"146");
        requestMap.put("ActionType","CreateTaskRequest");
        requestMap.put("ValidatedXML","ValidatedXML");
        outerMap.put("RequestMap",requestMap);

        Map mnplInner = new HashMap();
        mnplInner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.MNPL);
        mnplInner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        mnplInner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        mnplInner.put(WMGConstant.INTERFACE_PROTOCOL, "WS");

        Map outer = new HashMap();
        outer.put("1",mnplInner);

        Map mnplResponseMap = new HashMap();
        mnplResponseMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);

        PowerMockito.mockStatic(DBLoading.class);
        AggregationMainDBSchema aggregationMainDBSchema= Mockito.mock(AggregationMainDBSchema.class);
        when(DBLoading.getAggregationMainData(any(),any(),any())).thenReturn(aggregationMainDBSchema);
        when(aggregationMainDBSchema. getAggregationId()).thenReturn("038");
        when(DBLoading.getAggregationData(anyString(),anyString())).thenReturn(outer);
        PowerMockito.mockStatic(RequestType.class);
        when(RequestType.evaluate(anyString())).thenReturn(RequestType.AMEND);

        doNothing().when(dao).storeAggregationDetails(anyString(), any(), anyBoolean());
        when(applicationContext.getBean(MnplRequestProcessorImpl.class)).thenReturn(mnplRequestProcessor);
        when(aggregator.executeDelegate(any(), anyString(), anyString(), anyString())).thenReturn(mnplResponseMap);
        aggregatorCoreProcessor.doAggregation(outerMap);
    }
    @Test
    public void doAggregationTestForEMPPALAmend() throws Exception {
        Map outerMap = new HashMap();

        Map<String,String> requestMap = new HashMap<String,String>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");
        requestMap.put(WMGConstant.PRODUCT_TYPE,"COMPLEX");
        requestMap.put(WMGConstant.TASK_LOB,"Openreach");
        requestMap.put(WMGConstant.TASK_CATEGORY,"Fulfillment");
        requestMap.put(WMGConstant.MET_REF_NUM,"146");
        requestMap.put("ActionType","CreateTaskRequest");
        requestMap.put("ValidatedXML","ValidatedXML");
        outerMap.put("RequestMap",requestMap);

        Map emppalinner = new HashMap();
        emppalinner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.EMPPAL);
        emppalinner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        emppalinner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        emppalinner.put(WMGConstant.INTERFACE_PROTOCOL, "WS");

        Map outer = new HashMap();
        outer.put("1",emppalinner);

        Map emppalResMap = new HashMap();
        emppalResMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);

        PowerMockito.mockStatic(DBLoading.class);
        AggregationMainDBSchema aggregationMainDBSchema= Mockito.mock(AggregationMainDBSchema.class);
        when(DBLoading.getAggregationMainData(any(),any(),any())).thenReturn(aggregationMainDBSchema);
        when(aggregationMainDBSchema. getAggregationId()).thenReturn("038");
        when(DBLoading.getAggregationData(anyString(),anyString())).thenReturn(outer);
        PowerMockito.mockStatic(RequestType.class);
        when(RequestType.evaluate(anyString())).thenReturn(RequestType.AMEND);

        doNothing().when(dao).storeAggregationDetails(anyString(), any(), anyBoolean());
        when(applicationContext.getBean(EmppalRequestProcessorImpl.class)).thenReturn(emppalRequestProcessor);
        when(aggregator.executeDelegate(any(), anyString(), anyString(), anyString())).thenReturn(emppalResMap);
        aggregatorCoreProcessor.doAggregation(outerMap);
    }

    @Test
    public void doAggregationTestForEMPPALCreate() throws Exception {
        Map outerMap = new HashMap();

        Map<String,String> requestMap = new HashMap<String,String>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");
        requestMap.put(WMGConstant.PRODUCT_TYPE,"COMPLEX");
        requestMap.put(WMGConstant.TASK_LOB,"Openreach");
        requestMap.put(WMGConstant.TASK_CATEGORY,"Fulfillment");
        requestMap.put(WMGConstant.MET_REF_NUM,"146");
        requestMap.put("ActionType","CreateTaskRequest");
        requestMap.put("ValidatedXML","ValidatedXML");
        outerMap.put("RequestMap",requestMap);

        Map emppalinner = new HashMap();
        emppalinner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.EMPPAL);
        emppalinner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        emppalinner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        emppalinner.put(WMGConstant.INTERFACE_PROTOCOL, "WS");

        Map outer = new HashMap();
        outer.put("1",emppalinner);

        Map emppalResMap = new HashMap();
        emppalResMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);

        PowerMockito.mockStatic(DBLoading.class);
        AggregationMainDBSchema aggregationMainDBSchema= Mockito.mock(AggregationMainDBSchema.class);
        when(DBLoading.getAggregationMainData(any(),any(),any())).thenReturn(aggregationMainDBSchema);
        when(aggregationMainDBSchema. getAggregationId()).thenReturn("038");
        when(DBLoading.getAggregationData(anyString(),anyString())).thenReturn(outer);
        PowerMockito.mockStatic(RequestType.class);
        when(RequestType.evaluate(anyString())).thenReturn(RequestType.CREATE);

        doNothing().when(dao).storeAggregationDetails(anyString(), any(), anyBoolean());
        when(applicationContext.getBean(EmppalRequestProcessorImpl.class)).thenReturn(emppalRequestProcessor);
        when(aggregator.executeDelegate(any(), anyString(), anyString(), anyString())).thenReturn(emppalResMap);
        aggregatorCoreProcessor.doAggregation(outerMap);
    }

    @Test
    public void doAggregationTestForCreateWithMnplReqCont() throws Exception {
        Map outerMap = new HashMap();

        Map<String,String> requestMap = new HashMap<String,String>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");
        requestMap.put(WMGConstant.PRODUCT_TYPE,"COMPLEX");
        requestMap.put(WMGConstant.TASK_LOB,"Openreach");
        requestMap.put(WMGConstant.TASK_CATEGORY,"Fulfillment");
        requestMap.put(WMGConstant.MET_REF_NUM,"146");
        requestMap.put("ActionType","CreateTaskRequest");
        requestMap.put("ValidatedXML","ValidatedXML");
        Map statusMap = new HashMap();
        statusMap.put(WMGConstant.AGG_STATUS, WMGConstant.AGGREGATOR_REQCONT);

        HashMap innerMap = new HashMap();
        innerMap.put(WMGConstant.AGGREGATION_STATUS, WMGConstant.AGG_STATUS_TECHNICAL_MSG);

        outerMap.put(WMGConstant.MNPL,innerMap);
        outerMap.put("aggregationStatus", statusMap);
        outerMap.put("RequestMap",requestMap);

        Map emppalinner = new HashMap();
        emppalinner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.EMPPAL);
        emppalinner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        emppalinner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        emppalinner.put(WMGConstant.INTERFACE_PROTOCOL, "dummy");

        Map mnplInner = new HashMap();
        mnplInner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.MNPL);
        mnplInner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        mnplInner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        mnplInner.put(WMGConstant.INTERFACE_PROTOCOL, "WS");

        Map outer = new HashMap();
        outer.put("1",mnplInner);
        outer.put("2",emppalinner);

        Map emppalResMap = new HashMap();
        emppalResMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);

        PowerMockito.mockStatic(DBLoading.class);
        AggregationMainDBSchema aggregationMainDBSchema= Mockito.mock(AggregationMainDBSchema.class);
        when(DBLoading.getAggregationMainData(any(),any(),any())).thenReturn(aggregationMainDBSchema);
        when(aggregationMainDBSchema. getAggregationId()).thenReturn("038");
        when(DBLoading.getAggregationData(anyString(),anyString())).thenReturn(outer);
        PowerMockito.mockStatic(RequestType.class);
        when(RequestType.evaluate(anyString())).thenReturn(RequestType.CREATE);

        doNothing().when(dao).storeAggregationDetails(anyString(), any(), anyBoolean());
        when(applicationContext.getBean(EmppalRequestProcessorImpl.class)).thenReturn(emppalRequestProcessor);
        when(emppalRequestProcessor.executeDelegate(anyMap(), anyString(), anyString(), anyString())).thenReturn(emppalResMap);
        aggregatorCoreProcessor.doAggregation(outerMap);
    }

    @Test
    public void doAggregationTestForCreateWithMnplFailure() throws Exception {
        Map outerMap = new HashMap();

        Map<String,String> requestMap = new HashMap<String,String>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");
        requestMap.put(WMGConstant.PRODUCT_TYPE,"COMPLEX");
        requestMap.put(WMGConstant.TASK_LOB,"Openreach");
        requestMap.put(WMGConstant.TASK_CATEGORY,"Fulfillment");
        requestMap.put(WMGConstant.MET_REF_NUM,"146");
        requestMap.put("ActionType","CreateTaskRequest");
        requestMap.put("ValidatedXML","ValidatedXML");
        requestMap.put("errorCode", "dummyerrorcode");
        requestMap.put("errorDesc", "dummyerrorDesc");
        requestMap.put("errorText", "dummyerrorText");
        Map statusMap = new HashMap();
        statusMap.put(WMGConstant.AGG_STATUS, WMGConstant.AGG_FAILURE);

        HashMap innerMap = new HashMap();
        innerMap.put(WMGConstant.AGGREGATION_STATUS, WMGConstant.AGG_STATUS_TECHNICAL_MSG);

        outerMap.put(WMGConstant.MNPL,innerMap);
        outerMap.put("aggregationStatus", statusMap);
        outerMap.put("RequestMap",requestMap);

        Map emppalinner = new HashMap();
        emppalinner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.EMPPAL);
        emppalinner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        emppalinner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        emppalinner.put(WMGConstant.INTERFACE_PROTOCOL, "dummy");

        Map mnplInner = new HashMap();
        mnplInner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.MNPL);
        mnplInner.put(WMGConstant.REQUIREMENT_BUSINESS, "M");
        mnplInner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        mnplInner.put(WMGConstant.INTERFACE_PROTOCOL, "WS");

        Map outer = new HashMap();
        outer.put("1",mnplInner);
        outer.put("2",emppalinner);

        Map emppalResMap = new HashMap();
        emppalResMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);

        PowerMockito.mockStatic(DBLoading.class);
        AggregationMainDBSchema aggregationMainDBSchema= Mockito.mock(AggregationMainDBSchema.class);
        when(DBLoading.getAggregationMainData(any(),any(),any())).thenReturn(aggregationMainDBSchema);
        when(aggregationMainDBSchema. getAggregationId()).thenReturn("038");
        when(DBLoading.getAggregationData(anyString(),anyString())).thenReturn(outer);
        PowerMockito.mockStatic(RequestType.class);
        when(RequestType.evaluate(anyString())).thenReturn(RequestType.CREATE);

        doNothing().when(dao).storeAggregationDetails(anyString(), any(), anyBoolean());
        when(applicationContext.getBean(EmppalRequestProcessorImpl.class)).thenReturn(emppalRequestProcessor);
        when(emppalRequestProcessor.executeDelegate(anyMap(), anyString(), anyString(), anyString())).thenReturn(emppalResMap);
        when(objectMapper.writeValueAsString(any())).thenReturn("responseStringmap");
       // when(simpleMessageConverter.toMessage(anyString(), any())).thenReturn(aggResponseString);
        when(metbAggregatorMqConfigReader.getAggregatorToMetbOutboundQueueExchange()).thenReturn("AggregatorExchange");
        when( metbAggregatorMqConfigReader.getRoutingKeyAggregatorToMetbOutbound()).thenReturn("aggcore_to_metbcore_rkey");
        doNothing().when(messagePublisher).publishMessage(anyString(),anyString(),any(Message.class));
        aggregatorCoreProcessor.doAggregation(outerMap);
    }
    @Test
    public void doAggregationTestForCreateWithEMPPALSkipBEMandatory() throws Exception {
        Map outerMap = new HashMap();

        Map<String,String> requestMap = new HashMap<String,String>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");
        requestMap.put(WMGConstant.PRODUCT_TYPE,"COMPLEX");
        requestMap.put(WMGConstant.TASK_LOB,"Openreach");
        requestMap.put(WMGConstant.TASK_CATEGORY,"Fulfillment");
        requestMap.put(WMGConstant.MET_REF_NUM,"146");
        requestMap.put("ActionType","CreateTaskRequest");
        requestMap.put("ValidatedXML","ValidatedXML");
        requestMap.put("errorCode", "dummyerrorcode");
        requestMap.put("errorDesc", "dummyerrorDesc");
        requestMap.put("errorText", "dummyerrorText");
        Map statusMap = new HashMap();
        statusMap.put(WMGConstant.AGG_STATUS, WMGConstant.REQCONT);

        HashMap innerMap = new HashMap();
        innerMap.put(WMGConstant.AGGREGATION_STATUS, WMGConstant.AGG_STATUS_TECHNICAL_MSG);

        outerMap.put(WMGConstant.MNPL,innerMap);
        outerMap.put("aggregationStatus", statusMap);
        outerMap.put("RequestMap",requestMap);

        Map emppalinner = new HashMap();
        emppalinner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.EMPPAL);
        emppalinner.put(WMGConstant.REQUIREMENT_BUSINESS, "M");
        emppalinner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        emppalinner.put(WMGConstant.INTERFACE_PROTOCOL, "dummy");

        Map mnplInner = new HashMap();
        mnplInner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.MNPL);
        mnplInner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        mnplInner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        mnplInner.put(WMGConstant.INTERFACE_PROTOCOL, "WS");

        Map outer = new HashMap();
        outer.put("1",mnplInner);
        outer.put("2",emppalinner);

        Map emppalResMap = new HashMap();
        emppalResMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);

        PowerMockito.mockStatic(DBLoading.class);
        AggregationMainDBSchema aggregationMainDBSchema= Mockito.mock(AggregationMainDBSchema.class);
        when(DBLoading.getAggregationMainData(any(),any(),any())).thenReturn(aggregationMainDBSchema);
        when(aggregationMainDBSchema. getAggregationId()).thenReturn("038");
        when(DBLoading.getAggregationData(anyString(),anyString())).thenReturn(outer);
        PowerMockito.mockStatic(RequestType.class);
        when(RequestType.evaluate(anyString())).thenReturn(RequestType.CREATE);

        doNothing().when(dao).storeAggregationDetails(anyString(), any(), anyBoolean());
        when(applicationContext.getBean(EmppalRequestProcessorImpl.class)).thenReturn(emppalRequestProcessor);
        when(emppalRequestProcessor.executeDelegate(anyMap(), anyString(), anyString(), anyString())).thenReturn(emppalResMap);
        when(objectMapper.writeValueAsString(any())).thenReturn("responseStringmap");
        // when(simpleMessageConverter.toMessage(anyString(), any())).thenReturn(aggResponseString);
        when(metbAggregatorMqConfigReader.getAggregatorToMetbOutboundQueueExchange()).thenReturn("AggregatorExchange");
        when( metbAggregatorMqConfigReader.getRoutingKeyAggregatorToMetbOutbound()).thenReturn("aggcore_to_metbcore_rkey");
        doNothing().when(messagePublisher).publishMessage(anyString(),anyString(),any(Message.class));
        aggregatorCoreProcessor.doAggregation(outerMap);
    }
    @Test
    public void doAggregationTestForCreateWithEMPPALSkipBEOptional() throws Exception {
        Map outerMap = new HashMap();

        Map<String,String> requestMap = new HashMap<String,String>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");
        requestMap.put(WMGConstant.PRODUCT_TYPE,"COMPLEX");
        requestMap.put(WMGConstant.TASK_LOB,"Openreach");
        requestMap.put(WMGConstant.TASK_CATEGORY,"Fulfillment");
        requestMap.put(WMGConstant.MET_REF_NUM,"146");
        requestMap.put("ActionType","CreateTaskRequest");
        requestMap.put("ValidatedXML","ValidatedXML");

        Map statusMap = new HashMap();
        statusMap.put(WMGConstant.AGG_STATUS, WMGConstant.REQCONT);

        HashMap innerMap = new HashMap();
        innerMap.put(WMGConstant.AGGREGATION_STATUS, WMGConstant.AGG_STATUS_TECHNICAL_MSG);

        outerMap.put(WMGConstant.MNPL,innerMap);
        outerMap.put("aggregationStatus", statusMap);
        outerMap.put("RequestMap",requestMap);

        Map emppalinner = new HashMap();
        emppalinner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.EMPPAL);
        emppalinner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        emppalinner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        emppalinner.put(WMGConstant.INTERFACE_PROTOCOL, "dummy");

        Map mnplInner = new HashMap();
        mnplInner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.MNPL);
        mnplInner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        mnplInner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        mnplInner.put(WMGConstant.INTERFACE_PROTOCOL, "WS");

        Map outer = new HashMap();
        outer.put("1",mnplInner);
        outer.put("2",emppalinner);

        Map emppalResMap = new HashMap();
        emppalResMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);

        PowerMockito.mockStatic(DBLoading.class);
        AggregationMainDBSchema aggregationMainDBSchema= Mockito.mock(AggregationMainDBSchema.class);
        when(DBLoading.getAggregationMainData(any(),any(),any())).thenReturn(aggregationMainDBSchema);
        when(aggregationMainDBSchema. getAggregationId()).thenReturn("038");
        when(DBLoading.getAggregationData(anyString(),anyString())).thenReturn(outer);
        PowerMockito.mockStatic(RequestType.class);
        when(RequestType.evaluate(anyString())).thenReturn(RequestType.CREATE);

        doNothing().when(dao).storeAggregationDetails(anyString(), any(), anyBoolean());
        when(applicationContext.getBean(EmppalRequestProcessorImpl.class)).thenReturn(emppalRequestProcessor);
        when(emppalRequestProcessor.executeDelegate(anyMap(), anyString(), anyString(), anyString())).thenReturn(emppalResMap);
        when(objectMapper.writeValueAsString(any())).thenReturn("responseStringmap");
        // when(simpleMessageConverter.toMessage(anyString(), any())).thenReturn(aggResponseString);
        when(metbAggregatorMqConfigReader.getAggregatorToMetbOutboundQueueExchange()).thenReturn("AggregatorExchange");
        when( metbAggregatorMqConfigReader.getRoutingKeyAggregatorToMetbOutbound()).thenReturn("aggcore_to_metbcore_rkey");
        doNothing().when(messagePublisher).publishMessage(anyString(),anyString(),any(Message.class));
        aggregatorCoreProcessor.doAggregation(outerMap);
    }
    @Test
    public void doAggregationTestForCreateWithEMPPALSkipTEMandatory() throws Exception {
        Map outerMap = new HashMap();

        Map<String,String> requestMap = new HashMap<String,String>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");
        requestMap.put(WMGConstant.PRODUCT_TYPE,"COMPLEX");
        requestMap.put(WMGConstant.TASK_LOB,"Openreach");
        requestMap.put(WMGConstant.TASK_CATEGORY,"Fulfillment");
        requestMap.put(WMGConstant.MET_REF_NUM,"146");
        requestMap.put("ActionType","CreateTaskRequest");
        requestMap.put("ValidatedXML","ValidatedXML");

        outerMap.put("RequestMap",requestMap);

        Map emppalinner = new HashMap();
        emppalinner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.EMPPAL);
        emppalinner.put(WMGConstant.REQUIREMENT_BUSINESS, "M");
        emppalinner.put(WMGConstant.REQUIREMENT_TECHNICAL, "M");
        emppalinner.put(WMGConstant.INTERFACE_PROTOCOL, "dummy");

        Map outer = new HashMap();
        //outer.put("1",mnplInner);
        outer.put("1",emppalinner);

        Map emppalResMap = new HashMap();
        emppalResMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);

        PowerMockito.mockStatic(DBLoading.class);
        AggregationMainDBSchema aggregationMainDBSchema= Mockito.mock(AggregationMainDBSchema.class);
        when(DBLoading.getAggregationMainData(any(),any(),any())).thenReturn(aggregationMainDBSchema);
        when(aggregationMainDBSchema. getAggregationId()).thenReturn("038");
        when(DBLoading.getAggregationData(anyString(),anyString())).thenReturn(outer);
        PowerMockito.mockStatic(RequestType.class);
        when(RequestType.evaluate(anyString())).thenReturn(RequestType.CREATE);

        doNothing().when(dao).storeAggregationDetails(anyString(), any(), anyBoolean());
        when(applicationContext.getBean(EmppalRequestProcessorImpl.class)).thenReturn(emppalRequestProcessor);
        doThrow(new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                "Exception executing doDelegate()","errortext"))
                .when(emppalRequestProcessor).executeDelegate(anyMap(), anyString(), anyString(), anyString());
        when(objectMapper.writeValueAsString(any())).thenReturn("responseStringmap");
        // when(simpleMessageConverter.toMessage(anyString(), any())).thenReturn(aggResponseString);
        when(metbAggregatorMqConfigReader.getAggregatorToMetbOutboundQueueExchange()).thenReturn("AggregatorExchange");
        when( metbAggregatorMqConfigReader.getRoutingKeyAggregatorToMetbOutbound()).thenReturn("aggcore_to_metbcore_rkey");
        doNothing().when(messagePublisher).publishMessage(anyString(),anyString(),any(Message.class));
        aggregatorCoreProcessor.doAggregation(outerMap);
    }
     @Test
    public void doAggregationTestForCreateWithEMPPALSkipTEOptional() throws Exception {
        Map outerMap = new HashMap();

        Map<String,String> requestMap = new HashMap<String,String>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");
        requestMap.put(WMGConstant.PRODUCT_TYPE,"COMPLEX");
        requestMap.put(WMGConstant.TASK_LOB,"Openreach");
        requestMap.put(WMGConstant.TASK_CATEGORY,"Fulfillment");
        requestMap.put(WMGConstant.MET_REF_NUM,"146");
        requestMap.put("ActionType","CreateTaskRequest");
        requestMap.put("ValidatedXML","ValidatedXML");
        outerMap.put("RequestMap",requestMap);

        Map emppalinner = new HashMap();
        emppalinner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.EMPPAL);
        emppalinner.put(WMGConstant.REQUIREMENT_BUSINESS, "M");
        emppalinner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        emppalinner.put(WMGConstant.INTERFACE_PROTOCOL, "dummy");

        Map outer = new HashMap();
        outer.put("1",emppalinner);

        Map emppalResMap = new HashMap();
        emppalResMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);

        PowerMockito.mockStatic(DBLoading.class);
        AggregationMainDBSchema aggregationMainDBSchema= Mockito.mock(AggregationMainDBSchema.class);
        when(DBLoading.getAggregationMainData(any(),any(),any())).thenReturn(aggregationMainDBSchema);
        when(aggregationMainDBSchema. getAggregationId()).thenReturn("038");
        when(DBLoading.getAggregationData(anyString(),anyString())).thenReturn(outer);
        PowerMockito.mockStatic(RequestType.class);
        when(RequestType.evaluate(anyString())).thenReturn(RequestType.CREATE);

        doNothing().when(dao).storeAggregationDetails(anyString(), any(), anyBoolean());
        when(applicationContext.getBean(EmppalRequestProcessorImpl.class)).thenReturn(emppalRequestProcessor);
        doThrow(new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                "Exception executing doDelegate()","errortext"))
                .when(emppalRequestProcessor).executeDelegate(anyMap(), anyString(), anyString(), anyString());
        when(objectMapper.writeValueAsString(any())).thenReturn("responseStringmap");
        when(metbAggregatorMqConfigReader.getAggregatorToMetbOutboundQueueExchange()).thenReturn("AggregatorExchange");
        when( metbAggregatorMqConfigReader.getRoutingKeyAggregatorToMetbOutbound()).thenReturn("aggcore_to_metbcore_rkey");
        doNothing().when(messagePublisher).publishMessage(anyString(),anyString(),any(Message.class));
        aggregatorCoreProcessor.doAggregation(outerMap);
    }

    @Test
    public void doAggregationTestForCreateWithEMPPALIgnoreExOptional() throws Exception {
        Map outerMap = new HashMap();

        Map<String,String> requestMap = new HashMap<String,String>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");
        requestMap.put(WMGConstant.PRODUCT_TYPE,"COMPLEX");
        requestMap.put(WMGConstant.TASK_LOB,"Openreach");
        requestMap.put(WMGConstant.TASK_CATEGORY,"Fulfillment");
        requestMap.put(WMGConstant.MET_REF_NUM,"146");
        requestMap.put("ActionType","CreateTaskRequest");
        requestMap.put("ValidatedXML","ValidatedXML");

        outerMap.put("RequestMap",requestMap);

        Map emppalinner = new HashMap();
        emppalinner.put(WMGConstant.AGGREGATIONTYPE, WMGConstant.EMPPAL);
        emppalinner.put(WMGConstant.REQUIREMENT_BUSINESS, "O");
        emppalinner.put(WMGConstant.REQUIREMENT_TECHNICAL, "O");
        emppalinner.put(WMGConstant.INTERFACE_PROTOCOL, "dummy");

        Map outer = new HashMap();
        outer.put("1",emppalinner);

        Map emppalResMap = new HashMap();
        emppalResMap.put(WMGConstant.AGGREGATION_BUSINESSERROR_SKIP, WMGConstant.STRING_TRUE);

        PowerMockito.mockStatic(DBLoading.class);
        AggregationMainDBSchema aggregationMainDBSchema= Mockito.mock(AggregationMainDBSchema.class);
        when(DBLoading.getAggregationMainData(any(),any(),any())).thenReturn(aggregationMainDBSchema);
        when(aggregationMainDBSchema. getAggregationId()).thenReturn("038");
        when(DBLoading.getAggregationData(anyString(),anyString())).thenReturn(outer);
        PowerMockito.mockStatic(RequestType.class);
        when(RequestType.evaluate(anyString())).thenReturn(RequestType.CREATE);

        doNothing().when(dao).storeAggregationDetails(anyString(), any(), anyBoolean());
        when(applicationContext.getBean(EmppalRequestProcessorImpl.class)).thenReturn(emppalRequestProcessor);
        doThrow(new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.IGNORE_EXCEPTION,
                "Exception executing doDelegate()","errortext"))
                .when(emppalRequestProcessor).executeDelegate(anyMap(), anyString(), anyString(), anyString());
        when(objectMapper.writeValueAsString(any())).thenReturn("responseStringmap");
        // when(simpleMessageConverter.toMessage(anyString(), any())).thenReturn(aggResponseString);
        when(metbAggregatorMqConfigReader.getAggregatorToMetbOutboundQueueExchange()).thenReturn("AggregatorExchange");
        when( metbAggregatorMqConfigReader.getRoutingKeyAggregatorToMetbOutbound()).thenReturn("aggcore_to_metbcore_rkey");
        doNothing().when(messagePublisher).publishMessage(anyString(),anyString(),any(Message.class));
        aggregatorCoreProcessor.doAggregation(outerMap);
    }
}

