/*
* @author aswini.kumarparida@bt.com
* */
package com.bt.metb.aggregator.requestProcessor;

import com.bt.metb.MetbAbstractBase;
import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dao.DAO;
import com.bt.metb.aggregator.dataobjects.EmppalRequestAttributeReader;
import com.bt.metb.aggregator.parser.interfaces.ParserServiceInterface;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EMPPALRequestProcessorImplTest extends MetbAbstractBase {

    @InjectMocks
    private EmppalRequestProcessorImpl emppalRequestProcessor;

    @Mock
    private EmppalRequestAttributeReader emppalRequestAttributeReader;

    @Mock
    private DAO dao;

    @Mock
    private METResourceManagerInterface resourceManager;

    @Mock
    private SimpleMessageConverter simpleMessageConverter;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private ParserServiceInterface parserServiceInterface;

    @Mock
    private Message message;
    @Mock
    private MessageProperties messageProperties;

    @Test
    public void executeDelegateForSkipAggregationForInvalidTaskId() throws Exception {
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");

        when(emppalRequestAttributeReader.getTaskTypeApplicable()).thenReturn("COMPBDUK");

        Map requestMap = new HashMap<>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE,"dummytaskid");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");

        Map aggName = new HashMap();
        aggName.put(WMGConstant.REQUIREMENT_TECHNICAL,"O");
        aggName.put(WMGConstant.REQUIREMENT_BUSINESS,"O");

        final Map aggregationValues = new HashMap();
        aggregationValues.put("RequestMap", requestMap);
        aggregationValues.put(WMGConstant.AGG_NAME, aggName);

        Map map = emppalRequestProcessor.executeDelegate(aggregationValues, "CREATE", "dummyMetref", request);
        String skipTrue = (String) map.get(WMGConstant.AGGREGATION_IGNORE_SKIP);
        System.out.println("skipTrue"+skipTrue);
        Assert.assertEquals("true",skipTrue);


    }
    @Test
    public void executeDelegateForSuccessfulResponse() throws Exception{

        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");
        when(emppalRequestAttributeReader.getTaskTypeApplicable()).thenReturn("COMPBDUK");
        when(resourceManager.getManagedServer()).thenReturn("managed1_ormetbba01");

        Map requestMap = new HashMap<>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"dummyEstimateId");
        requestMap.put(WMGConstant.TASK_TYPE_ID,"COMPBDUK");
        requestMap.put(WMGConstant.GANG_DETAILS,"000000000+000000000+0000000+true");

        Map aggName = new HashMap();
        aggName.put(WMGConstant.REQUIREMENT_TECHNICAL,"O");
        aggName.put(WMGConstant.REQUIREMENT_BUSINESS,"O");

        final Map aggregationValues = new HashMap();
        aggregationValues.put("RequestMap", requestMap);
        aggregationValues.put(WMGConstant.AGG_NAME, aggName);

        doNothing().when(dao).insertAggReqXML(anyString(), anyString(), anyString(), anyString());
        doNothing().when(dao).updateAggStatusWithReqXML(any(Timestamp.class), anyString(), anyString(), anyString(), anyString());

        when(parserServiceInterface.transform(anyString(), anyString(), anyMap(), anyBoolean())).thenReturn("anytransformedxml");

        System.out.println("Posting xml to EMPPAL");
        byte[] body = request.getBytes();
        Message message =new Message(body, mp);
        Map map = emppalRequestProcessor.executeDelegate(aggregationValues, "CREATE", "dummyMetref", request);

    }
    @Test
    public void skipAggregationIfReqParamsAreAbsent() throws Exception{

        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");

        when(emppalRequestAttributeReader.getTaskTypeApplicable()).thenReturn("COMPBDUK");

        Map requestMap = new HashMap<>();
        requestMap.put(WMGConstant.ESTIMATE_ID,"");
        requestMap.put(WMGConstant.TASK_TYPE,"anytasktype");
        requestMap.put(WMGConstant.GANG_DETAILS,"");

        Map aggName = new HashMap();
        aggName.put(WMGConstant.REQUIREMENT_TECHNICAL,"O");
        aggName.put(WMGConstant.REQUIREMENT_BUSINESS,"O");

        final Map aggregationValues = new HashMap();
        aggregationValues.put("RequestMap", requestMap);
        aggregationValues.put(WMGConstant.AGG_NAME, aggName);

        byte[] body = request.getBytes();
        Message message =new Message(body, mp);
        Map map = emppalRequestProcessor.executeDelegate(aggregationValues, "CREATE", "dummyMetref", request);
        String skipAgg = (String)map.get(WMGConstant.AGGREGATION_IGNORE_SKIP);
        Assert.assertEquals("true",skipAgg);
        //verify(emppalRequestAttributeReader).getTaskTypeApplicable();
    }

}
