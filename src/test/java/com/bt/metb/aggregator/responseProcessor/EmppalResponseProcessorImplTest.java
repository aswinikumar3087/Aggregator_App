/*
* @author aswini.kumarparida@bt.com
* */
package com.bt.metb.aggregator.responseProcessor;

import com.bt.metb.MetbAbstractBase;
import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dao.DAO;
import com.bt.metb.aggregator.dataobjects.AggregationResponseDO;
import com.bt.metb.aggregator.dataobjects.EmppalRequestAttributeReader;
import com.bt.metb.aggregator.messageprocessor.AggregatorCoreProcessor;
import com.bt.metb.aggregator.parser.interfaces.ParserServiceInterface;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmppalResponseProcessorImplTest extends MetbAbstractBase {

    @InjectMocks
    private EmppalResponseProcessorImpl emppalResponseProcessor;

    @Mock
    private ParserServiceInterface parserServiceInterface;

    @Mock
    private EmppalRequestAttributeReader emppalRequestAttributeReader;

    @Mock
    private DAO dao;

    @Mock
    private METResourceManagerInterface resourceManager;

    @Mock
    AggregatorCoreProcessor aggregatorCoreProcessor;

    @Test
    public void testEmppalSuccessResponse() throws Exception {
        MessageProperties mp = new MessageProperties();
        String responseXml = getFileContentAsString("EmppalResponse.xml");
        Message textMessage = new Message(responseXml.getBytes(), mp);
        textMessage.getMessageProperties().setHeader(WMGConstant.MSG_ID, "msgId");

        String xpathForErrorCode = "/GetItemDetailsByEstimateNumberResponse/header/errorCode";
        String actionTypeXpath = "//standardHeader/serviceAddressing/action";
        String actionTypeValue = "http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskRequest";

        AggregationResponseDO aggregationResponseDO = getAggregationResponseDO();

        when(dao.getAggregationResponseDetailsObject(anyString())).thenReturn(aggregationResponseDO);
        when(parserServiceInterface.aggregationResponseNamespaceStrip(anyString())).thenReturn(responseXml);
        when(emppalRequestAttributeReader.getEmppalErrorCode()).thenReturn(xpathForErrorCode);
        when(emppalRequestAttributeReader.getActionType()).thenReturn(actionTypeXpath);
        when(dao.getValidatedXMLForMetrefId(anyString())).thenReturn("XML");
        when(parserServiceInterface.getRequiredTagValue(anyString(), anyString()))
                .thenReturn(actionTypeValue)
                .thenReturn(WMGConstant.SUCCESS_ERROR_CODE);

        when(resourceManager.getManagedServer()).thenReturn("managed1_ormetbba01");


        String resxmlAfterXslapply = getFileContentAsString("Emppalresafterxsl.xml");
        when(parserServiceInterface.transformResponse(anyString(), anyString(), anyString(), anyString())).thenReturn(resxmlAfterXslapply);

        doNothing().when(dao).updateRequestStatus(anyString(), any(), anyString());
        emppalResponseProcessor.processMessage(textMessage);

    }

    @Test
    public void testEmppalTEResponseOptional() throws Exception {
        MessageProperties mp = new MessageProperties();
        String responseXml = getFileContentAsString("RaaTEResponse.xml");
        System.out.println("Error resp" + responseXml);
        Message textMessage = new Message(responseXml.getBytes(), mp);
        textMessage.getMessageProperties().setHeader(WMGConstant.MSG_ID, "msgId");

        String xpathForErrorCode = "/GetItemDetailsByEstimateNumberResponse/header/errorCode";
        String actionTypeXpath = "//standardHeader/serviceAddressing/action";
        String actionTypeValue = "http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskRequest";
        final String errorDesc = "Not able to connect To RAA database";
        final String errorText = "Not able to connect To RAA database";
        AggregationResponseDO aggregationResponseDO = getAggregationResponseDO();

        when(dao.getAggregationResponseDetailsObject(anyString())).thenReturn(aggregationResponseDO);
        when(parserServiceInterface.aggregationResponseNamespaceStrip(anyString())).thenReturn(responseXml);
        when(emppalRequestAttributeReader.getActionType()).thenReturn(actionTypeXpath);
        when(emppalRequestAttributeReader.getEmppalErrorCode()).thenReturn(xpathForErrorCode);
       // when(emppalRequestAttributeReader.getEMPPALMessageTimeOut()).thenReturn("2");
        when(dao.getValidatedXMLForMetrefId(anyString())).thenReturn("XML");
        when(parserServiceInterface.getRequiredTagValue(anyString(), anyString()))
                .thenReturn(actionTypeValue)
                .thenReturn("1000")
                .thenReturn(errorDesc)
                .thenReturn(errorText);

        when(resourceManager.getManagedServer()).thenReturn("managed1_ormetbba01");

        when(emppalRequestAttributeReader.getEmppalTechnicalErrorCode()).thenReturn("1000|3000");
        doNothing().when(dao).updateRequestStatus(anyString(), anyString(), anyString(), any(), anyString());
        emppalResponseProcessor.processMessage(textMessage);
    }

    @Test
    public void testEmppalTEResponseMandatory() throws Exception {
        MessageProperties mp = new MessageProperties();
        String responseXml = getFileContentAsString("RaaTEResponse.xml");
        System.out.println("Error resp" + responseXml);
        Message textMessage = new Message(responseXml.getBytes(), mp);
        textMessage.getMessageProperties().setHeader(WMGConstant.MSG_ID, "msgId");

        String xpathForErrorCode = "/GetItemDetailsByEstimateNumberResponse/header/errorCode";
        String actionTypeXpath = "//standardHeader/serviceAddressing/action";
        String actionTypeValue = "http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskRequest";
        final String errorDesc = "Not able to connect To RAA database";
        final String errorText = "Not able to connect To RAA database";
        AggregationResponseDO aggregationResponseDO = getAggregationResponseDO();
        aggregationResponseDO.setTechnicalExceptionRequirement("M");

        when(dao.getAggregationResponseDetailsObject(anyString())).thenReturn(aggregationResponseDO);
        when(parserServiceInterface.aggregationResponseNamespaceStrip(anyString())).thenReturn(responseXml);
        when(emppalRequestAttributeReader.getActionType()).thenReturn(actionTypeXpath);
        when(emppalRequestAttributeReader.getEmppalErrorCode()).thenReturn(xpathForErrorCode);
       // when(emppalRequestAttributeReader.getEMPPALMessageTimeOut()).thenReturn("2");
        when(dao.getValidatedXMLForMetrefId(anyString())).thenReturn("XML");
        when(parserServiceInterface.getRequiredTagValue(anyString(), anyString()))
                .thenReturn(actionTypeValue)
                .thenReturn("1000")
                .thenReturn(errorDesc)
                .thenReturn(errorText);

        when(resourceManager.getManagedServer()).thenReturn("managed1_ormetbba01");

        when(emppalRequestAttributeReader.getEmppalTechnicalErrorCode()).thenReturn("1000|3000");
        doNothing().when(dao).updateRequestStatus(anyString(), anyString(), anyString(), any(), anyString());
        emppalResponseProcessor.processMessage(textMessage);
    }
    @Test
    public void testEmppalDefaultTEResponse() throws Exception {
        MessageProperties mp = new MessageProperties();
        String responseXml = getFileContentAsString("RaaTEResponse.xml");
        System.out.println("Error resp" + responseXml);
        Message textMessage = new Message(responseXml.getBytes(), mp);
        textMessage.getMessageProperties().setHeader(WMGConstant.MSG_ID, "msgId");

        String xpathForErrorCode = "/GetItemDetailsByEstimateNumberResponse/header/errorCode";
        String actionTypeXpath = "//standardHeader/serviceAddressing/action";
        String actionTypeValue = "http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskRequest";
        final String errorDesc = "Not able to connect To RAA database";
        final String errorText = "Not able to connect To RAA database";
        AggregationResponseDO aggregationResponseDO = getAggregationResponseDO();
        aggregationResponseDO.setTechnicalExceptionRequirement("O");

        when(dao.getAggregationResponseDetailsObject(anyString())).thenReturn(aggregationResponseDO);
        when(parserServiceInterface.aggregationResponseNamespaceStrip(anyString())).thenReturn(responseXml);
        when(emppalRequestAttributeReader.getActionType()).thenReturn(actionTypeXpath);
        when(emppalRequestAttributeReader.getEmppalErrorCode()).thenReturn(xpathForErrorCode);
        // when(emppalRequestAttributeReader.getEMPPALMessageTimeOut()).thenReturn("2");
        when(dao.getValidatedXMLForMetrefId(anyString())).thenReturn("XML");
        when(parserServiceInterface.getRequiredTagValue(anyString(), anyString()))
                .thenReturn(actionTypeValue)
                .thenReturn("50")
                .thenReturn(errorDesc)
                .thenReturn(errorText);

        when(resourceManager.getManagedServer()).thenReturn("managed1_ormetbba01");

        when(emppalRequestAttributeReader.getEmppalTechnicalErrorCode()).thenReturn("1000|3000");
        when(emppalRequestAttributeReader.getEmppalDefaultDataErrorCode()).thenReturn("DummyCode");

        emppalResponseProcessor.processMessage(textMessage);
    }
    @Test
    public void testEmppalBEResponseOptional() throws Exception {
        MessageProperties mp = new MessageProperties();
        String responseXml = getFileContentAsString("RaaTEResponse.xml");
        System.out.println("Error resp" + responseXml);
        Message textMessage = new Message(responseXml.getBytes(), mp);
        textMessage.getMessageProperties().setHeader(WMGConstant.MSG_ID, "msgId");

        String xpathForErrorCode = "/GetItemDetailsByEstimateNumberResponse/header/errorCode";
        String actionTypeXpath = "//standardHeader/serviceAddressing/action";
        String actionTypeValue = "http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskRequest";
        final String errorDesc = "Nodata found in RAA database for estimate id";
        final String errorText = "Nodata found in RAA database for estimate id";
        AggregationResponseDO aggregationResponseDO = getAggregationResponseDO();

        when(dao.getAggregationResponseDetailsObject(anyString())).thenReturn(aggregationResponseDO);
        when(parserServiceInterface.aggregationResponseNamespaceStrip(anyString())).thenReturn(responseXml);
        when(emppalRequestAttributeReader.getActionType()).thenReturn(actionTypeXpath);
        when(emppalRequestAttributeReader.getEmppalErrorCode()).thenReturn(xpathForErrorCode);
//        when(emppalRequestAttributeReader.getEMPPALMessageTimeOut()).thenReturn("2");
        when(dao.getValidatedXMLForMetrefId(anyString())).thenReturn("XML");
        when(parserServiceInterface.getRequiredTagValue(anyString(), anyString()))
                .thenReturn(actionTypeValue)
                .thenReturn("2000")
                .thenReturn(errorDesc)
                .thenReturn(errorText);

        when(resourceManager.getManagedServer()).thenReturn("managed1_ormetbba01");

        when(emppalRequestAttributeReader.getEmppalTechnicalErrorCode()).thenReturn("1000|3000");
        when(emppalRequestAttributeReader.getEmppalDefaultDataErrorCode()).thenReturn("1");

        emppalResponseProcessor.processMessage(textMessage);
    }

    @Test
    public void testEmppalBEResponseMandatory() throws Exception {
        MessageProperties mp = new MessageProperties();
        String responseXml = getFileContentAsString("RaaTEResponse.xml");
        System.out.println("Error resp" + responseXml);
        Message textMessage = new Message(responseXml.getBytes(), mp);
        textMessage.getMessageProperties().setHeader(WMGConstant.MSG_ID, "msgId");

        String xpathForErrorCode = "/GetItemDetailsByEstimateNumberResponse/header/errorCode";
        String actionTypeXpath = "//standardHeader/serviceAddressing/action";
        String actionTypeValue = "http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskRequest";
        final String errorDesc = "Nodata found in RAA database for estimate id";
        final String errorText = "Nodata found in RAA database for estimate id";
        AggregationResponseDO aggregationResponseDO = getAggregationResponseDO();
        aggregationResponseDO.setBusinessExceptionRequirement("M");

        when(dao.getAggregationResponseDetailsObject(anyString())).thenReturn(aggregationResponseDO);
        when(parserServiceInterface.aggregationResponseNamespaceStrip(anyString())).thenReturn(responseXml);
        when(emppalRequestAttributeReader.getActionType()).thenReturn(actionTypeXpath);
        when(emppalRequestAttributeReader.getEmppalErrorCode()).thenReturn(xpathForErrorCode);
//        when(emppalRequestAttributeReader.getEMPPALMessageTimeOut()).thenReturn("2");
        when(dao.getValidatedXMLForMetrefId(anyString())).thenReturn("XML");
        when(parserServiceInterface.getRequiredTagValue(anyString(), anyString()))
                .thenReturn(actionTypeValue)
                .thenReturn("2000")
                .thenReturn(errorDesc)
                .thenReturn(errorText);

        when(resourceManager.getManagedServer()).thenReturn("managed1_ormetbba01");

        when(emppalRequestAttributeReader.getEmppalTechnicalErrorCode()).thenReturn("1000|3000");
        when(emppalRequestAttributeReader.getEmppalDefaultDataErrorCode()).thenReturn("1");

        doNothing().when(dao).updateResponse(anyString(), anyString());

        emppalResponseProcessor.processMessage(textMessage);
    }

    private AggregationResponseDO getAggregationResponseDO() {
        AggregationResponseDO aggregationResponseDO = new AggregationResponseDO();
        aggregationResponseDO.setCorrelationId("CORRELATION_ID");
        aggregationResponseDO.setReqXML("REQUEST_XML");
        aggregationResponseDO.setReqStatus("status");
        aggregationResponseDO.setAggregationName("MNPL");
        aggregationResponseDO.setTechnicalExceptionRequirement("O");
        aggregationResponseDO.setBusinessExceptionRequirement("O");
        aggregationResponseDO.setMessageIdNotFound(false);
        aggregationResponseDO.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
        aggregationResponseDO.setLatestTaskOperation("Task Operation");
        aggregationResponseDO.setMetRefNumber("1234567890");
        aggregationResponseDO.setProduct("Complex");
        aggregationResponseDO.setTaskRefNumber("dummy_taskref");
        return aggregationResponseDO;
    }
}
