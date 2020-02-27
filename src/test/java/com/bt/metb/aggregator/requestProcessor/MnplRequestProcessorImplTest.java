package com.bt.metb.aggregator.requestProcessor;

import com.bt.metb.MetbAbstractBase;
import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dao.DAO;
import com.bt.metb.aggregator.model.MnplRequestAttributeReader;
import com.bt.metb.aggregator.parser.interfaces.ParserServiceInterface;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MnplRequestProcessorImplTest extends MetbAbstractBase {
    @InjectMocks
    private MnplRequestProcessorImpl mnplRequestProcessorImpl;

    @Mock
    private MnplRequestAttributeReader mnplRequestAttributeReader;

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

    @Test
    public void executeDelegate() throws Exception {
        MessageProperties mp = new MessageProperties();
        String request = getFileContentAsString("receivedcreateRequest.xml");

        when(mnplRequestAttributeReader.getMNPLServiceStateRetryInterval()).thenReturn("3");
        when(mnplRequestAttributeReader.getMNPLServiceStateRetriesRemaining()).thenReturn("3");
        when(mnplRequestAttributeReader.getMNPLServiceAddressingAction()).thenReturn("http://create");
        when(mnplRequestAttributeReader.getMNPLServiceAddressingAddress()).thenReturn("http://address");
        when(mnplRequestAttributeReader.getMNPLServiceAddressingFrom()).thenReturn("http://addressFrom");
        when(mnplRequestAttributeReader.getMNPLServiceAddressingServiceName()).thenReturn("http://servicename");
        when(mnplRequestAttributeReader.getMNPLServiceAddressingToAddress()).thenReturn("http://addressTo");
        when(resourceManager.getManagedServer()).thenReturn("http://addressTo");

        Map requestMap = new HashMap();
        Map aggMap = new HashMap();


        requestMap.put(WMGConstant.ESTIMATE_ID, "1234567");
        requestMap.put(WMGConstant.TASK_MESSAGEID, "1234567");
        requestMap.put(WMGConstant.PROJECT_ID, "1234");


        aggMap.put(WMGConstant.AGG_NAME, WMGConstant.OPTIONAL);
        aggMap.put(WMGConstant.AGG_NAME, WMGConstant.OPTIONAL);


        final Map aggregationValues = new HashMap();
        aggregationValues.put("RequestMap", requestMap);
        aggregationValues.put(WMGConstant.AGG_NAME, aggMap);
        aggregationValues.put(WMGConstant.PROJECT_ID, "1234");

        doNothing().when(dao).insertAggReqXML(anyString(), anyString(), anyString(), anyString());
        doNothing().when(dao).updateAggStatusWithReqXML(any(Timestamp.class), anyString(), anyString(), anyString(), anyString());

        mnplRequestProcessorImpl.executeDelegate(aggregationValues, "CREATE", "12345", request);
        verify(mnplRequestAttributeReader).getMNPLServiceAddressingFrom();

    }
}
