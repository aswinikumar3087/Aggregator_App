package com.bt.metb.aggregator.parser.impl;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.util.WMGException;
import com.bt.metb.aggregator.util.WMGTechnicalException;
import com.bt.metb.aggregator.util.WMGUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.env.Environment;

import javax.validation.Path.Node;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ParserServiceImplTest {

	@InjectMocks
	@Spy
	private ParserServiceImpl parserService;
	@Mock
	Map allXSLs;
	@Mock
	private Environment environment;
	@Mock
	XPath xpath;
	@Mock
	Node node;
	@Mock
	WMGUtil util;
	@Mock
	TransformerFactory factory;

	HashMap<String, String> map = new HashMap<String, String>();

	@Before
	public void initialize() {
		parserService = new ParserServiceImpl("");
		MockitoAnnotations.initMocks(this);
		map.put("SourceSystem", "FLWE1");
		map.put(WMGConstant.CARELEVEL, "2");
		map.put(WMGConstant.PRODUCT_TYPE, "I");
		map.put(WMGConstant.REPLY_TO_ADDRESS, "http://ccm.intra.bt.com/EWOCS");
		map.put(WMGConstant.TASK_TYPE, "IFSC002");
		map.put("metTaskIdentifier", "itc123");

	}

	@Test
	public void stripNamespaceTest() throws METBException, TransformerConfigurationException {
		String reqXML = "<WM_Request>test</WM_Request>";
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.WMIG_DUMMY_XSL, true);
		String result = parserService.stripNamespace(reqXML);
		assertEquals("xml", result);
	}

	@Test(expected = WMGTechnicalException.class)
	public void transformTest() throws WMGException {
		String reqXML = "<WM_Request>test</WM_Request>";
		parserService.transform(reqXML, "", false);
	}



/*	@Test
	public void transformUpdateStatusRequestTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";

		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.STATUS_UPDATE_TASK_REQUEST_WMIG_XSL, map,
				false);
		ReflectionTestUtils.setField(parserService, "workStringType", "Job");
		ReflectionTestUtils.setField(parserService, "statusUpdateWrkstringSbType", "Create");
		String result = parserService.transformUpdateStatusRequest(reqXML, "", map);
		assertEquals("xml", result);
	}

	@Test
	public void transformAmendRequestTest() throws METBException {
		String reqXML = "<Envelope><WM_Request>test</WM_Request></Envelope>";
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.AMEND_TASK_REQUEST_WMIG_XSL, map, false);
		String result = parserService.transformAmendRequest(reqXML, "", map);
		assertEquals("xml", result);
	}

	@Test
	public void transformCancelRequestTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.CANCEL_TASK_REQUEST_WMIG_XSL, map, false);
		String result = parserService.transformCancelRequest(reqXML, "", map);
		assertEquals("xml", result);
	}*/

	@Test
	public void loadxmlTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		ParserServiceImpl result = parserService.loadXML(reqXML);
		assertNotNull(result);
	}

	@Test
	public void getRequiredTagValueExcTest() throws METBException {
		String result = parserService.getRequiredTagValue("/Test");
		assertNull(result);
	}

	@Test
	public void getRequiredTagValueNullTest() throws METBException, XPathExpressionException {
		String reqXML = "<WM_Request>test</WM_Request>";
		doNothing().when(parserService).loadXMLInternal();
		when(xpath.evaluate("/Test", node)).thenReturn("value");
		// parserService.
		String result = parserService.getRequiredTagValue("/Test");
		assertNull(result);
	}

	@Test(expected = METBException.class)
	public void transformTaskInitiateErrResTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(
				"<createTaskResponse>http://ccm.intra.bt.com/EWOCS</createTaskResponse>",
				WMGConstant.CREATE_RESPONSE_XSL, map, false);
		String result = parserService.transformErrorResponse(reqXML, map, WMGConstant.TF_CREATE_RESPONSE);
		assertEquals("xml", result);
	}

	@Test(expected = METBException.class)
	public void transformTaskModifErrResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(
				"<amendTaskResponse>http://ccm.intra.bt.com/EWOCS</amendTaskResponse>", WMGConstant.AMEND_RESPONSE_XSL,
				map, false);
		String result = parserService.transformErrorResponse(reqXML, map, WMGConstant.TF_AMEND_RESPONSE);
		assertEquals("xml", result);
	}

	@Test(expected = METBException.class)
	public void transformTaskTFCancelErrResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(
				"<cancelTaskResponse>http://ccm.intra.bt.com/EWOCS</cancelTaskResponse>",
				WMGConstant.CANCEL_RESPONSE_XSL, map, false);
		String result = parserService.transformErrorResponse(reqXML, map, WMGConstant.TF_CANCEL_RESPONSE);
		assertEquals("xml", result);
	}

	@Test(expected = METBException.class)
	public void transformTaskStsUpdteAmendErrResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(
				"<updateStatusTaskResponse>http://ccm.intra.bt.com/EWOCS</updateStatusTaskResponse>",
				WMGConstant.STATUS_UPDATE_RESPONSE_FOR_AMEND_REQUEST_XSL, map, false);
		String result = parserService.transformErrorResponse(reqXML, map, WMGConstant.TF_STATUS_UPDATE_RESPONSE);
		assertEquals("xml", result);
	}

	@Test(expected = METBException.class)
	public void transformTaskStsUpdteErrResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		map.put("latestOperation", "UpdateTask");
		doReturn("xml").when(parserService).transform(
				"<updateStatusTaskResponse>http://ccm.intra.bt.com/EWOCS</updateStatusTaskResponse>",
				WMGConstant.STATUS_UPDATE_RESPONSE_FOR_UPDATE_REQUEST_XSL, map, false);
		String result = parserService.transformErrorResponse(reqXML, map, WMGConstant.TF_STATUS_UPDATE_RESPONSE);
		assertEquals("xml", result);
	}

	@Test
	public void transformCreateErrorResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(
				"<createTaskResponse>http://ccm.intra.bt.com/EWOCS</createTaskResponse>",
				WMGConstant.CREATE_RESPONSE_XSL, map, false);
		String result = parserService.transformErrorResponse(reqXML, map, WMGConstant.CREATE_ACTION);
		assertEquals("xml", result);
	}

	@Test
	public void transformAmendErrorResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(
				"<amendTaskResponse>http://ccm.intra.bt.com/EWOCS</amendTaskResponse>", WMGConstant.AMEND_RESPONSE_XSL,
				map, false);
		String result = parserService.transformErrorResponse(reqXML, map, WMGConstant.AMEND_ACTION);
		assertEquals("xml", result);
	}

	@Test
	public void transformCancelErrorResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(
				"<cancelTaskResponse>http://ccm.intra.bt.com/EWOCS</cancelTaskResponse>",
				WMGConstant.CANCEL_RESPONSE_XSL, map, false);
		String result = parserService.transformErrorResponse(reqXML, map, WMGConstant.CANCEL_ACTION);
		assertEquals("xml", result);
	}

	@Test
	public void transformUpdateErrorResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(
				"<updateStatusTaskResponse>http://ccm.intra.bt.com/EWOCS</updateStatusTaskResponse>",
				WMGConstant.STATUS_UPDATE_RESPONSE_FOR_UPDATE_REQUEST_XSL, map, false);
		String result = parserService.transformErrorResponse(reqXML, map, WMGConstant.UPDATE_ACTION);
		assertEquals("xml", result);
	}

	/*@Test
	public void transformTFCreateWmResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		ReflectionTestUtils.setField(parserService, "wmigMessageId", "/WM_Response/EAI_Header/HI_WORKSTRING");
		doReturn("201810232359").when(parserService).getRequiredTagValue("/WM_Response/EAI_Header/HI_WORKSTRING");
		doReturn("xml").when(parserService).transform(
				"<createTaskResponse>http://ccm.intra.bt.com/EWOCS<WM_Response>null</WM_Response></createTaskResponse>",
				WMGConstant.CREATE_RESPONSE_XSL, map, false);
		String result = parserService.createResponse(reqXML, WMGConstant.TF_CREATE_RESPONSE, map);
		assertEquals("xml", result);
	}

	@Test
	public void transformTFAmendWmResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(
				"<amendTaskResponse>http://ccm.intra.bt.com/EWOCS<WM_Response>null</WM_Response></amendTaskResponse>",
				WMGConstant.AMEND_RESPONSE_XSL, map, false);
		String result = parserService.createResponse(reqXML, WMGConstant.TF_AMEND_RESPONSE, map);
		assertEquals("xml", result);
	}

	@Test
	public void transformTFCancelWmResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(
				"<cancelTaskResponse>http://ccm.intra.bt.com/EWOCS<WM_Response>null</WM_Response></cancelTaskResponse>",
				WMGConstant.CANCEL_RESPONSE_XSL, map, false);
		String result = parserService.createResponse(reqXML, WMGConstant.TF_CANCEL_RESPONSE, map);
		assertEquals("xml", result);
	}

	@Test
	public void transformTFupdateWmResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		map.put("latestOperation", "UpdateTask");
		doReturn("xml").when(parserService).transform(
				"<updateStatusTaskResponse>http://ccm.intra.bt.com/EWOCS<WM_Response>null</WM_Response></updateStatusTaskResponse>",
				WMGConstant.STATUS_UPDATE_RESPONSE_FOR_UPDATE_REQUEST_XSL, map, false);
		String result = parserService.createResponse(reqXML, WMGConstant.TF_STATUS_UPDATE_RESPONSE, map);
		assertEquals("xml", result);
	}

	@Test
	public void transformTFupdateAmendWmResponseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(
				"<updateStatusTaskResponse>http://ccm.intra.bt.com/EWOCS<WM_Response>null</WM_Response></updateStatusTaskResponse>",
				WMGConstant.STATUS_UPDATE_RESPONSE_FOR_AMEND_REQUEST_XSL, map, false);
		String result = parserService.createResponse(reqXML, WMGConstant.TF_STATUS_UPDATE_RESPONSE, map);
		assertEquals("xml", result);
	}

	@Test
	public void tranformUpdateTaskRequestTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		ReflectionTestUtils.setField(parserService, "workStringType", "H_Request");
		ReflectionTestUtils.setField(parserService, "statusUpdateWrkstringSbType", "StatusUpdate");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.UPDATE_TASK_REQUEST_WMIG, map, false);
		String result = parserService.tranformUpdateTaskRequest(reqXML, map);
		assertEquals("xml", result);
	}

	@Test
	public void transformUpdateStatusRequestForInternalMetTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.STATUS_UPDATE_REQUEST_FOR_INTERNAL_MET_XSL,
				false);
		String result = parserService.transformUpdateStatusRequestForInternalMet(reqXML);
		assertEquals("xml", result);
	}

	@Test
	public void transformUpdateNotificationPMMISCssTest() throws METBException {
		String reqXML = "<TaskProgressDetails>test</TaskProgressDetails>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "test");
		doReturn("xml").when(parserService).transform("test", WMGConstant.ESCAPING_FSINOTES_XSL_FILE_NAME, false);
		doReturn("xml").when(parserService).transform("<WMG_Root>testml</WMG_Root>",
				WMGConstant.CSS_NOTIFYFURCOM_FSIDATA_WMG2PMMIS_XSL_FILE_NAME, false);

		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_PROGRESS_DETAILS_TAG)).thenReturn("test");
		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_DETAILS_TAG)).thenReturn("test");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiRoutingData("test");
		String result = parserService.transformUpdateNotificationPMMIS(reqXML, reqXML, eaiheader);
		assertEquals("xml", result);
	}

	@Test
	public void transformUpdateNotificationPMMISpaceTest() throws METBException {
		String reqXML = "<TaskProgressDetails>test</TaskProgressDetails>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "test");
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "rest");
		doReturn("xml").when(parserService).transform("test", WMGConstant.ESCAPING_FSINOTES_XSL_FILE_NAME, false);
		doReturn("xml").when(parserService).transform("<WMG_Root>testml</WMG_Root>",
				WMGConstant.SPACE_NOTIFYFURCOM_FSIDATA_WMG2PMMIS_XSL_FILE_NAME, false);

		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_PROGRESS_DETAILS_TAG)).thenReturn("test");
		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_DETAILS_TAG)).thenReturn("test");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiRoutingData("test");
		String result = parserService.transformUpdateNotificationPMMIS(reqXML, reqXML, eaiheader);
		assertEquals("xml", result);
	}

	@Test
	public void transformRequestPMMISCssTest() throws METBException {
		String reqXML = "<TaskProgressDetails>test</TaskProgressDetails>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "test");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.CSS_FSIDATA_WMG2PMMIS_XSL_FILE_NAME, true);
		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_PROGRESS_DETAILS_TAG)).thenReturn("test");
		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_DETAILS_TAG)).thenReturn("test");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiRoutingData("test");
		String result = parserService.transformRequestPMMIS(reqXML, WMGConstant.WORKSTRING_TYPE_JM, "test",
				WMGConstant.WORKSTRING_SUB_TYPE_IT);
		assertEquals("xml", result);
	}

	@Test
	public void transformRequestPMMISSpaceTest() throws METBException {
		String reqXML = "<TaskProgressDetails>test</TaskProgressDetails>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "rest");
		ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "test");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.SPACE_FSIDATA_WMG2PMMIS_XSL_FILE_NAME, true);
		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_PROGRESS_DETAILS_TAG)).thenReturn("test");
		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_DETAILS_TAG)).thenReturn("test");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiRoutingData("test");
		String result = parserService.transformRequestPMMIS(reqXML, WMGConstant.WORKSTRING_TYPE_JM, "test",
				WMGConstant.WORKSTRING_SUB_TYPE_IT);
		assertEquals("xml", result);
	}

	@Test
	public void transformRequestPMMISNoJMTest() throws METBException {
		String reqXML = "<TaskProgressDetails>test</TaskProgressDetails>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "rest");
		ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "test");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.SPACE_FSIDATA_WMG2PMMIS_XSL_FILE_NAME, true);
		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_PROGRESS_DETAILS_TAG)).thenReturn("test");
		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_DETAILS_TAG)).thenReturn("test");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiRoutingData("test");
		String result = parserService.transformRequestPMMIS(reqXML, "test", "test", WMGConstant.WORKSTRING_SUB_TYPE_IT);
		assertNull(result);
	}

	@Test(expected = WMGTechnicalException.class)
	public void transformRequestPMMISExcTest() throws METBException {
		String reqXML = "<TaskProgressDetails>test</TaskProgressDetails>";
		map.put("replyTo", "http://ccm.intra.bt.com/EWOCS");
		map.put("errorCode", "TEST001");
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "rest");
		ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "test");
		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_PROGRESS_DETAILS_TAG)).thenReturn("test");
		when(parserUtil.extractValueWithTag(reqXML, WMGConstant.TASK_DETAILS_TAG)).thenReturn("test");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiRoutingData("test");
		String result = parserService.transformRequestPMMIS(reqXML, WMGConstant.WORKSTRING_TYPE_JM, "test",
				WMGConstant.WORKSTRING_SUB_TYPE_IT);
		assertNull(result);
	}

	@Test
	public void getPrefixTest() throws METBException {
		String reqXML = "<header>test</header>";
		ReflectionTestUtils.setField(parserService, "headerNameSpace", "test");
		String result = parserService.getPrefix(reqXML);
		assertEquals("<heade", result);
	}

	@Test(expected = WMGTechnicalException.class)
	public void transformXslTest() throws METBException {
		String reqXML = "<createtaskRequest>test</createtaskRequest>";
		String result = parserService.transform(reqXML, WMGConstant.SPACE_TI_REQ_WMG2TF_XSL_FILE_NAME, false);
		assertEquals("<heade", result);
	}

	@Test
	public void transformCreateWithScodeTest() throws METBException {
		String reqXML = "<Envelope>><WM_Request><orderItem>\r\n" + "<productInstance>\r\n"
				+ "							<productName>\r\n"
				+ "								<name>BTRC BBB Fibre</name>\r\n"
				+ "								<identifier>\r\n"
				+ "									<name>S6100031</name>\r\n"
				+ "									<productReferenceNumber>1-1BFDIXBF-1</productReferenceNumber>\r\n"
				+ "								</identifier>\r\n" + "							</productName>\r\n"
				+ "							<productRequestType>Order</productRequestType>\r\n"
				+ "							<productSupplyMethod>Engineer Carry</productSupplyMethod>\r\n"
				+ "						</productInstance>\r\n" + "					</orderItem></WM_Request></Envelope>";
		map.put(WMGConstant.PRODUCT_TYPE, WMGConstant.PRODUCT_TYPE_GEA_FTTP);
		ReflectionTestUtils.setField(parserService, "toBeRouteDetailsMembersep", "toBeRoute");
		HashMap productType = new HashMap();
		productType.put("S6100031:0", "S6100031:0");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.CREATE_TASK_REQUEST_WMIG_XSL, map, false);
		String result = parserService.transformCreateRequest(reqXML, "", map);
		assertEquals("xml", result);
	}

	@Test
	public void transformCreatetaskAssistTaskTest() throws METBException {
		String reqXML = "<Envelope>><WM_Request>test</WM_Request></Envelope>";
		ReflectionTestUtils.setField(parserService, "toBeRouteDetailsMembersep", "toBeRoute");
		ReflectionTestUtils.setField(parserService, "pctAssist", "60");
		ReflectionTestUtils.setField(parserService, "sctAssist", "60");
		map.put(WMGConstant.PRODUCT_TYPE, WMGConstant.PRODUCT_TYPE_ASSIST);
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.CREATE_TASK_REQUEST_WMIG_XSL, map, false);
		String result = parserService.transformCreateRequest(reqXML, "", map);
		assertEquals("xml", result);
	}

	@Test(expected = METBException.class)
	public void transformCreatetaskWLR3Test() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		ReflectionTestUtils.setField(parserService, "toBeRouteDetailsMembersep", "toBeRoute");
		ReflectionTestUtils.setField(parserService, "pctAssist", "60");
		ReflectionTestUtils.setField(parserService, "sctAssist", "60");
		map.put(WMGConstant.PRODUCT_TYPE, WMGConstant.WLR3_PSTN_PRODUCTTYPE);
		doReturn("201810232359").when(parserService).getRequiredTagValue("<WM_Request>test</WM_Request>",
				"/createTaskRequest/task/taskCharacteristic[name='CustomerGuaranteeSchemeDateTime']/value");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.CREATE_TASK_REQUEST_WMIG_XSL, map, false);
		String result = parserService.transformCreateRequest(reqXML, "", map);
	}

	@Test
	public void transformCreatetaskMSIResponseTest() throws METBException {
		String reqXML = "<Envelope>><WM_Request>test</WM_Request></Envelope>";
		ReflectionTestUtils.setField(parserService, "toBeRouteDetailsMembersep", "toBeRoute");
		map.put("msiResponse", "msiResponse");
		map.put("msiResponsesecond", "msiResponsesecond");
		map.put("MNSDResponse", "MNSDResponse");
		map.put("MNSDResponseForHR", "MNSDResponseForHR");
		map.put("MPCResponse", "MPCResponse");
		map.put("MNPLResponse", "MNPLResponse");
		map.put(WMGConstant.MSI_SS_KEY, WMGConstant.MSI_SS_KEY);
		map.put(WMGConstant.MLI_KEY, WMGConstant.MLI_KEY);
		map.put(WMGConstant.MLI_2_KEY, WMGConstant.MLI_2_KEY);
		map.put("MSIParallelResponse", "MSIParallelResponse");
		map.put("baseLineTestDataResponse", "baseLineTestDataResponse");
		map.put(WMGConstant.EMPPAL_KEY, WMGConstant.EMPPAL_KEY);
		doReturn("201810232359").when(parserService).getRequiredTagValue("<WM_Request>test</WM_Request>",
				"/createTaskRequest/task/taskCharacteristic[name='CustomerGuaranteeSchemeDateTime']/value");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.CREATE_TASK_REQUEST_WMIG_XSL, map, false);
		String result = parserService.transformCreateRequest(reqXML, "", map);
	}

	@Test(expected = METBException.class)
	public void transformAmendRequestWLR3Test() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		ReflectionTestUtils.setField(parserService, "toBeRouteDetailsMembersep", "toBeRoute");
		ReflectionTestUtils.setField(parserService, "pctAssist", "60");
		ReflectionTestUtils.setField(parserService, "sctAssist", "60");
		map.put(WMGConstant.PRODUCT_TYPE, WMGConstant.WLR3_PSTN_PRODUCTTYPE);
		doReturn("201810232359").when(parserService).getRequiredTagValue("<WM_Request>test</WM_Request>",
				"/amendTaskRequest/task/taskCharacteristic[name='CustomerGuaranteeSchemeDateTime']/value");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.AMEND_TASK_REQUEST_WMIG_XSL, map, false);
		String result = parserService.transformAmendRequest(reqXML, "", map);
		assertEquals("xml", result);
	}

	@Test
	public void transformAmendRequestAggTest() throws METBException {
		String reqXML = "<Envelope><WM_Request>test</WM_Request></Envelope>";
		ReflectionTestUtils.setField(parserService, "toBeRouteDetailsMembersep", "toBeRoute");
		map.put("msiResponse", "msiResponse");
		map.put("msiResponsesecond", "msiResponsesecond");
		map.put("MNSDResponse", "MNSDResponse");
		map.put("MNSDResponseForHR", "MNSDResponseForHR");
		map.put("MPCResponse", "MPCResponse");
		map.put("MNPLResponse", "MNPLResponse");
		map.put(WMGConstant.MSI_SS_KEY, WMGConstant.MSI_SS_KEY);
		map.put(WMGConstant.MLI_KEY, WMGConstant.MLI_KEY);
		map.put(WMGConstant.MLI_2_KEY, WMGConstant.MLI_2_KEY);
		map.put("MSIParallelResponse", "MSIParallelResponse");
		map.put("baseLineTestDataResponse", "baseLineTestDataResponse");
		map.put(WMGConstant.EMPPAL_KEY, WMGConstant.EMPPAL_KEY);
		doReturn("201810232359").when(parserService).getRequiredTagValue("<WM_Request>test</WM_Request>",
				"/amendTaskRequest/task/taskCharacteristic[name='CustomerGuaranteeSchemeDateTime']/value");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.AMEND_TASK_REQUEST_WMIG_XSL, map, false);
		String result = parserService.transformAmendRequest(reqXML, "", map);
		assertEquals("xml", result);
	}

	@Test
	public void transformAmendWithScodeTest() throws METBException {
		String reqXML = "<Envelope>><WM_Request><orderItem>\r\n" + "<productInstance>\r\n"
				+ "							<productName>\r\n"
				+ "								<name>BTRC BBB Fibre</name>\r\n"
				+ "								<identifier>\r\n"
				+ "									<name>S6100031</name>\r\n"
				+ "									<productReferenceNumber>1-1BFDIXBF-1</productReferenceNumber>\r\n"
				+ "								</identifier>\r\n" + "							</productName>\r\n"
				+ "							<productRequestType>Order</productRequestType>\r\n"
				+ "							<productSupplyMethod>Engineer Carry</productSupplyMethod>\r\n"
				+ "						</productInstance>\r\n" + "					</orderItem></WM_Request></Envelope>";
		map.put(WMGConstant.PRODUCT_TYPE, WMGConstant.PRODUCT_TYPE_GEA_FTTP);
		ReflectionTestUtils.setField(parserService, "toBeRouteDetailsMembersep", "toBeRoute");
		HashMap productType = new HashMap();
		productType.put("S6100031:0", "S6100031:0");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.AMEND_TASK_REQUEST_WMIG_XSL, map, false);
		String result = parserService.transformAmendRequest(reqXML, "", map);
		assertEquals("xml", result);
	}

	@Test(expected = WMGTechnicalException.class)
	public void transformUpdateNotificationPMMISExceTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		parserService.transformUpdateNotificationPMMIS("", "", eaiheader);
	}

	@Test(expected = WMGTechnicalException.class)
	public void transformRequestPMMISExceTest() throws METBException {
		parserService.transformRequestPMMIS("", "", "", "");
	}*/

	@Test(expected = WMGTechnicalException.class)
	public void transformRequestFalseTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		parserService.transform(reqXML, WMGConstant.CREATE_RESPONSE_XSL, null, false);
	}

	@Test(expected = WMGTechnicalException.class)
	public void transformRequestTrueTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		parserService.transform(reqXML, WMGConstant.CREATE_RESPONSE_XSL, null, true);
	}

/*	@Test(expected = METBException.class)
	public void getOrderItemListTest() throws METBException {
		String reqXML = "<WM_Request>test</WM_Request>";
		parserService.getOrderItemList("WM_Request", reqXML);
	}

	@Test
	public void getOrderItemListSuccessTest() throws METBException {
		String reqXML = "<task><orderItem>\\r\\n\" + \"<productInstance>\\r\\n\"\r\n"
				+ "				+ \"							<productName>\\r\\n\"\r\n"
				+ "				+ \"								<name>BTRC BBB Fibre</name>\\r\\n\"\r\n"
				+ "				+ \"								<identifier>\\r\\n\"\r\n"
				+ "				+ \"									<name>S6100031</name>\\r\\n\"\r\n"
				+ "				+ \"									<productReferenceNumber>1-1BFDIXBF-1</productReferenceNumber>\\r\\n\"\r\n"
				+ "				+ \"								</identifier>\\r\\n\" + \"							</productName>\\r\\n\"\r\n"
				+ "				+ \"							<productRequestType>Order</productRequestType>\\r\\n\"\r\n"
				+ "				+ \"							<productSupplyMethod>Engineer Carry</productSupplyMethod>\\r\\n\"\r\n"
				+ "				+ \"						</productInstance>\\r\\n\" + \"					</orderItem></task>";
		LinkedHashMap map = parserService.getOrderItemList("orderItem", reqXML);// S6100031:0=Order
		assertEquals("Order", map.get("S6100031:0"));
	}

	@Test
	public void extractTaskReferenceNumberTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		String result = parserService.extractTaskReferenceNumber(reqXML, "123");
		assertNull(result);
	}

	@Test(expected = WMGTechnicalException.class)
	public void getRequestParamsMapTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		parserService.getRequestParamsMap(reqXML);
	}

	@Test(expected = METBException.class)
	public void getRequestParamsMapEmptyTest() throws METBException {
		parserService.getRequestParamsMap("");
	}

	@Test(expected = METBException.class)
	public void getRequestParametersExceTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.REQUEST_PARAM_XSL, false);
		parserService.getRequestParamsMap(reqXML);
	}

	@Test
	public void getRequestParametersSuccessTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		String res = "<RequestData>\r\n" + "	<ProductType>test</ProductType>\r\n"
				+ "	<TaskCategory>test</TaskCategory>\r\n" + "	<Postcode>test</Postcode>\r\n"
				+ "	<CustomerContactPostcode>test</CustomerContactPostcode>\r\n"
				+ "	<TaskMessageId>test</TaskMessageId>\r\n" + "	<TaskRelatesTo>test</TaskRelatesTo>\r\n"
				+ "	<e2eData>test</e2eData>\r\n" + "	<TaskIdentifier>test</TaskIdentifier>\r\n"
				+ "	<ReplyToInstanceId>test</ReplyToInstanceId>\r\n" + "	<CareLevel>test</CareLevel>\r\n"
				+ "	<MNSDtestid>test</MNSDtestid>\r\n" + "	<MNSDtarrid>test</MNSDtarrid>\r\n"
				+ "	<ReqMainWorkLocation>test</ReqMainWorkLocation>\r\n"
				+ "	<ReplyToAddress>test</ReplyToAddress>\r\n" + "	<StateCode>test</StateCode>\r\n"
				+ "	<TaskTypeId>test</TaskTypeId>\r\n" + "	<ReqResponseCode>test</ReqResponseCode>\r\n"
				+ "	<ReqWorkLocationQualifier>test</ReqWorkLocationQualifier>\r\n"
				+ "	<ActionType>test</ActionType>\r\n" + "	<A1141Code>test</A1141Code>\r\n"
				+ "	<JOBTYPEID>test</JOBTYPEID>\r\n" + "	<TaskLOB>test</TaskLOB>\r\n"
				+ "	<TaskDuration>test</TaskDuration>\r\n" + "	<SecondPromiseflag>test</SecondPromiseflag>\r\n"
				+ "	<SerSpecId>test</SerSpecId>\r\n" + "	<TaskMFL>test</TaskMFL>\r\n"
				+ "	<TaskTelephoneNo>test</TaskTelephoneNo>\r\n" + "	<TaskAssociateTask>test</TaskAssociateTask>\r\n"
				+ "	<AppointmentData>test</AppointmentData>\r\n" + "	<StatusUpdateTo>test</StatusUpdateTo>\r\n"
				+ "	<StatusUpdateTaskRequest>test</StatusUpdateTaskRequest>\r\n"
				+ "	<secondaryCommLatestFinish>test</secondaryCommLatestFinish>\r\n"
				+ "	<StartDateTime>test</StartDateTime>\r\n" + "	<LatestStartdateTime>test</LatestStartdateTime>\r\n"
				+ "	<ReqReportedDateTime>test</ReqReportedDateTime>\r\n"
				+ "	<ReqAppointmentDate></ReqAppointmentDate>\r\n"
				+ "	<ReqLatestAccessTime>test</ReqLatestAccessTime>\r\n"
				+ "	<ReqEarliestNextAccessTime>test</ReqEarliestNextAccessTime>\r\n"
				+ "	<BusinessTargetDateTime>test</BusinessTargetDateTime>\r\n"
				+ "	<FaultReportedDate1>test</FaultReportedDate1>\r\n"
				+ "	<FaultReportedDate2>test</FaultReportedDate2>\r\n"
				+ "	<FaultReportedDate3>test</FaultReportedDate3>\r\n"
				+ "	<FaultHistoryClearDate1>test</FaultHistoryClearDate1>\r\n"
				+ "	<FaultHistoryClearDate2>test</FaultHistoryClearDate2>\r\n"
				+ "	<FaultHistoryClearDate3>test</FaultHistoryClearDate3>\r\n" + "	<GangDetails>test</GangDetails>\r\n"
				+ "	<FTTCToBeRoute>test</FTTCToBeRoute>\r\n"
				+ "	<CustomerRequestedDate>test</CustomerRequestedDate>\r\n"
				+ "	<AccessStartDate>test</AccessStartDate>\r\n" + "	<AccessFinishDate>test</AccessFinishDate>\r\n"
				+ "	<AccessStartTime>test</AccessStartTime>\r\n" + "	<AccessFinishTime>test</AccessFinishTime>\r\n"
				+ "	<PrimaryCommLatestStart>test</PrimaryCommLatestStart>\r\n" + "	<FaultType>test</FaultType>\r\n"
				+ "	<FaultHistoryNotes>test</FaultHistoryNotes>\r\n"
				+ "	<TargetCompletionDate>test</TargetCompletionDate>\r\n"
				+ "	<NonFaultEndPostcode>test</NonFaultEndPostcode>\r\n" + "	<NADKey>test</NADKey>\r\n"
				+ "	<NoOfAffectedONTs>test</NoOfAffectedONTs>\r\n" + "	<MIMODULEQUANTITY>test</MIMODULEQUANTITY>\r\n"
				+ "	<MLIBatteryBackupUnit>test</MLIBatteryBackupUnit>\r\n"
				+ "	<ExchangeGroupCode>test</ExchangeGroupCode>\r\n" + "	<CssDBId>test</CssDBId>\r\n"
				+ "	<ProjectId>test</ProjectId>\r\n" + "	<EstimateId>test</EstimateId>\r\n"
				+ "	<DonorDn>test</DonorDn>\r\n" + "	<DonorDnStatus>test</DonorDnStatus>\r\n"
				+ "	<DonorToBeRoute>test</DonorToBeRoute>\r\n"
				+ "	<PrimaryCommitmentLatestStrt>test</PrimaryCommitmentLatestStrt>\r\n"
				+ "	<ContractualDeliveryDate>test</ContractualDeliveryDate>\r\n"
				+ "	<MNSDassociatedTestId>test</MNSDassociatedTestId>\r\n"
				+ "	<MNSDassociatedTarrId>test</MNSDassociatedTarrId>\r\n"
				+ "	<MNSDassociatedServiceId>test</MNSDassociatedServiceId>\r\n"
				+ "	<MNSDassociatedServiceType>test</MNSDassociatedServiceType>\r\n" + "</RequestData>\r\n" + "\r\n"
				+ "\r\n" + "";
		doReturn(res).when(parserService).transform(reqXML, WMGConstant.REQUEST_PARAM_XSL, false);
		Map test = parserService.getRequestParamsMap(reqXML);
		assertNotNull(test);
	}

	@Test(expected = WMGTechnicalException.class)
	public void transformWMRequestExcTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		parserService.transformRequest("", eaiheader);
	}

	@Test(expected = WMGTechnicalException.class)
	public void transformWMRequestWMGExTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_IT);
		parserService.transformRequest(reqXML, eaiheader);
	}
	
	@Test(expected = WMGTechnicalException.class)
	public void transformWMRequestNoWMSIdTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "CSS,FLW");
		ReflectionTestUtils.setField(parserService, "metsdin", "test");
		ReflectionTestUtils.setField(parserService, "mhwWebserviceConfig", "true");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_IT);
		eaiheader.setHiPckLoc("CSS");
		parserService.transformRequest(reqXML, eaiheader);
	}
	
	@Test(expected = WMGTechnicalException.class)
	public void transformWMRequestL2CCssTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "CSS,FLW");
		ReflectionTestUtils.setField(parserService, "metsdin", "test");
		ReflectionTestUtils.setField(parserService, "mhwWebserviceConfig", "true");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "copyRouteData", "true");
		ReflectionTestUtils.setField(parserService, "ontTimeOutFieldClient", "test");
		ReflectionTestUtils.setField(parserService, "notCopyNtwkServIdSS", "CSS");
		ReflectionTestUtils.setField(parserService, "flagMoveFsiNotesToCore", "false");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_IT);
		eaiheader.setHiPckLoc("CSS");
		eaiheader.setHiRoutingData("WMC83");
		Map paramWMS=new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("COPY_ROUTEDATA_IND", "false");
		paramWMS.put("WMSINSTANCE", "WMC83");
		paramWMS.put("ONT_TIMEOUT_AT_FIELDCLIENT_SEC", "test");
		paramWMS.put("MET_Indicator", "false");
//		eaiheader.setHiWorkstringOriginator("test");/isMHWJobManager
		doReturn("xml").when(parserService).transform(reqXML, "CSS_TaskInitiateRequest_WMG2TF.xsl", paramWMS, true);
		parserService.transformRequest(reqXML, eaiheader);
	}
	
	@Test(expected = WMGTechnicalException.class)
	public void transformWMRequestL2CSpaceTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "FLW");
		ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "CSS,FLW");
		ReflectionTestUtils.setField(parserService, "metsdin", "test");
		ReflectionTestUtils.setField(parserService, "mhwWebserviceConfig", "true");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "copyRouteData", "true");
		ReflectionTestUtils.setField(parserService, "ontTimeOutFieldClient", "test");
		ReflectionTestUtils.setField(parserService, "notCopyNtwkServIdSS", "CSS");
		ReflectionTestUtils.setField(parserService, "flagMoveFsiNotesToCore", "false");
		ReflectionTestUtils.setField(parserService, "neoSourceSystem", "CSS");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_IT);
		eaiheader.setHiPckLoc("CSS");
		eaiheader.setHiRoutingData("WMC83");
		Map paramWMS=new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("COPY_ROUTEDATA_IND", "false");
		paramWMS.put("WMSINSTANCE", "WMC83");
		
//		eaiheader.setHiWorkstringOriginator("test");/isMHWJobManager
		doReturn("xml").when(parserService).transform(reqXML, "SPACE_TaskInitiateRequest_WMG2TF.xsl", paramWMS, true);
		parserService.transformRequest(reqXML, eaiheader);
	}

	@Test
	public void transformWMRequestModifTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "FLW");
		ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "CSS,FLW");
		ReflectionTestUtils.setField(parserService, "metsdin", "test");
		ReflectionTestUtils.setField(parserService, "mhwWebserviceConfig", "true");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "copyRouteData", "true");
		ReflectionTestUtils.setField(parserService, "ontTimeOutFieldClient", "test");
		ReflectionTestUtils.setField(parserService, "notCopyNtwkServIdSS", "CSS");
		ReflectionTestUtils.setField(parserService, "flagMoveFsiNotesToCore", "false");
		ReflectionTestUtils.setField(parserService, "neoSourceSystem", "CSS");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_MT);
		eaiheader.setHiPckLoc("CSS");
		eaiheader.setHiRoutingData("WMC83");
		Map paramWMS = new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("COPY_ROUTEDATA_IND", "false");
		paramWMS.put("WMSINSTANCE", "WMC83");

//		eaiheader.setHiWorkstringOriginator("test");/isMHWJobManager
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.CSS_SPACE_COREDATA_XSL_FILE_NAME, true);
		String result = parserService.transformRequest(reqXML, eaiheader);
		assertEquals("xml", result);
	}
	
	@Test
	public void transformWMRequestcancelTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "FLW");
		ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "CSS,FLW");
		ReflectionTestUtils.setField(parserService, "metsdin", "test");
		ReflectionTestUtils.setField(parserService, "mhwWebserviceConfig", "true");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "copyRouteData", "true");
		ReflectionTestUtils.setField(parserService, "ontTimeOutFieldClient", "test");
		ReflectionTestUtils.setField(parserService, "notCopyNtwkServIdSS", "CSS");
		ReflectionTestUtils.setField(parserService, "flagMoveFsiNotesToCore", "false");
		ReflectionTestUtils.setField(parserService, "neoSourceSystem", "CSS");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_CT);
		eaiheader.setHiPckLoc("CSS");
		eaiheader.setHiRoutingData("WMC83");
		Map paramWMS = new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("COPY_ROUTEDATA_IND", "false");
		paramWMS.put("WMSINSTANCE", "WMC83");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.SPACE_TC_REQ_WMG2TF_XSL_FILE_NAME, true);
		String result = parserService.transformRequest(reqXML, eaiheader);
		assertEquals("xml", result);
	}
	
	@Test
	public void transformWMRequestUpdateSpaceTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "FLW");
		ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "CSS,FLW");
		ReflectionTestUtils.setField(parserService, "metsdin", "test");
		ReflectionTestUtils.setField(parserService, "mhwWebserviceConfig", "true");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "copyRouteData", "true");
		ReflectionTestUtils.setField(parserService, "ontTimeOutFieldClient", "test");
		ReflectionTestUtils.setField(parserService, "notCopyNtwkServIdSS", "CSS");
		ReflectionTestUtils.setField(parserService, "flagMoveFsiNotesToCore", "false");
		ReflectionTestUtils.setField(parserService, "neoSourceSystem", "CSS");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_SUT);
		eaiheader.setHiPckLoc("CSS");
		eaiheader.setHiRoutingData("WMC83");
		Map paramWMS = new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("COPY_ROUTEDATA_IND", "false");
		paramWMS.put("WMSINSTANCE", "WMC83");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.SPACE_TSU_REQ_WMG2TF_XSL_FILE_NAME, true);
		String result = parserService.transformRequest(reqXML, eaiheader);
		assertEquals("xml", result);
	}
	
	@Test
	public void transformWMRequestUpdateCssTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "CSS,FLW");
		ReflectionTestUtils.setField(parserService, "metsdin", "test");
		ReflectionTestUtils.setField(parserService, "mhwWebserviceConfig", "true");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "copyRouteData", "true");
		ReflectionTestUtils.setField(parserService, "ontTimeOutFieldClient", "test");
		ReflectionTestUtils.setField(parserService, "notCopyNtwkServIdSS", "CSS");
		ReflectionTestUtils.setField(parserService, "flagMoveFsiNotesToCore", "false");
		ReflectionTestUtils.setField(parserService, "neoSourceSystem", "CSS");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_SUT);
		eaiheader.setHiPckLoc("CSS");
		eaiheader.setHiRoutingData("WMC83");
		Map paramWMS = new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("COPY_ROUTEDATA_IND", "false");
		paramWMS.put("WMSINSTANCE", "WMC83");
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.CSS_TSU_REQ_WMG2TF_XSL_FILE_NAME, true);
		String result = parserService.transformRequest(reqXML, eaiheader);
		assertEquals("xml", result);
	}
	
	@Test
	public void transformWMRequestcancelCssTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "CSS,FLW");
		ReflectionTestUtils.setField(parserService, "metsdin", "test");
		ReflectionTestUtils.setField(parserService, "mhwWebserviceConfig", "true");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "copyRouteData", "true");
		ReflectionTestUtils.setField(parserService, "ontTimeOutFieldClient", "test");
		ReflectionTestUtils.setField(parserService, "notCopyNtwkServIdSS", "CSS");
		ReflectionTestUtils.setField(parserService, "flagMoveFsiNotesToCore", "false");
		ReflectionTestUtils.setField(parserService, "neoSourceSystem", "CSS");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_CT);
		eaiheader.setHiPckLoc("CSS");
		eaiheader.setHiRoutingData("WMC83");
		Map paramWMS = new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("COPY_ROUTEDATA_IND", "false");
		paramWMS.put("WMSINSTANCE", "WMC83");
		doReturn("xml").when(parserService).transform(reqXML, "CSS_TaskCancelRequest_WMG2TF.xsl", true);
		String result = parserService.transformRequest(reqXML, eaiheader);
		assertEquals("xml", result);
	}
	
	@Test
	public void transformWMRequestModifCssTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "CSS,FLW");
		ReflectionTestUtils.setField(parserService, "metsdin", "test");
		ReflectionTestUtils.setField(parserService, "mhwWebserviceConfig", "true");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "copyRouteData", "true");
		ReflectionTestUtils.setField(parserService, "ontTimeOutFieldClient", "test");
		ReflectionTestUtils.setField(parserService, "notCopyNtwkServIdSS", "CSS");
		ReflectionTestUtils.setField(parserService, "flagMoveFsiNotesToCore", "false");
		ReflectionTestUtils.setField(parserService, "neoSourceSystem", "CSS");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_MT);
		eaiheader.setHiPckLoc("CSS");
		eaiheader.setHiRoutingData("WMC83");
		Map paramWMS = new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("COPY_ROUTEDATA_IND", "false");
		paramWMS.put("WMSINSTANCE", "WMC83");

//		eaiheader.setHiWorkstringOriginator("test");/isMHWJobManager
		doReturn("xml").when(parserService).transform(reqXML, WMGConstant.CSS_SPACE_COREDATA_XSL_FILE_NAME, true);
		String result = parserService.transformRequest(reqXML, eaiheader);
		assertEquals("xml", result);
	}
	
	@Test(expected = WMGTechnicalException.class)
	public void transformWMRequestL2CCoreNotesTest() throws METBException {
		String reqXML = "<WM_Request>123</WM_Request>";
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "FLW");
		ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "CSS,FLW");
		ReflectionTestUtils.setField(parserService, "metsdin", "test");
		ReflectionTestUtils.setField(parserService, "mhwWebserviceConfig", "true");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "copyRouteData", "true");
		ReflectionTestUtils.setField(parserService, "ontTimeOutFieldClient", "test");
		ReflectionTestUtils.setField(parserService, "notCopyNtwkServIdSS", "CSS");
		ReflectionTestUtils.setField(parserService, "flagMoveFsiNotesToCore", "true");
		ReflectionTestUtils.setField(parserService, "neoSourceSystem", "CSS");
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_IT);
		eaiheader.setHiPckLoc("CSS");
		eaiheader.setHiRoutingData("WMC83");
		Map paramWMS=new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("COPY_ROUTEDATA_IND", "false");
		paramWMS.put("WMSINSTANCE", "WMC83");
		
		doReturn("xml").when(parserService).transform(reqXML, "SPACE_TaskInitiateRequest_WMG2TF.xsl", paramWMS, true);
		parserService.transformRequest(reqXML, eaiheader);
	}
	
	@Test
	public void checkforBTnewSiteTagYTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><Test>Important - please follow the link to the INFORMe</Test></WM_Request>";
		Map paramWMS = new Hashtable();
		Map result = parserService.checkforBTnewSiteTag(reqXML, paramWMS);
		assertFalse(result.isEmpty());
	}

	@Test
	public void checkforBTnewSiteTagNTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		Map paramWMS = new Hashtable();
		Map result = parserService.checkforBTnewSiteTag(reqXML, paramWMS);
		assertFalse(result.isEmpty());
	}

	@Test(expected = WMGTechnicalException.class)
	public void checkforBTnewSiteExceTest() throws METBException {
		Map paramWMS = new Hashtable();
		parserService.checkforBTnewSiteTag("", paramWMS);
	}

	@Test(expected = WMGTechnicalException.class)
	public void addEAIHeaderWMgexceTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		Map paramWMS = new Hashtable();
		EAIHeader eaiheader = new EAIHeader();
		parserService.addEAIHeader(eaiheader, reqXML);
	}

	@Test(expected = WMGTechnicalException.class)
	public void addEAIHeaderReqNullTest() throws METBException {
		Map paramWMS = new Hashtable();
		EAIHeader eaiheader = new EAIHeader();
		parserService.addEAIHeader(eaiheader, "");
	}

	@Test
	public void addEAIHeaderExcTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		ReflectionTestUtils.setField(parserService, "svcName", "H_H_request");
		ReflectionTestUtils.setField(parserService, "svcVersion", "0.0.1");
		Map paramHT = new Hashtable();
		paramHT.put("HI_WORKSTRING_KEY", "");
		paramHT.put("HI_SVC_NAME", "H_H_request");
		paramHT.put("HI_WORKSTRING_SEQ", "");
		paramHT.put("HI_WORKSTRING_SUB_TYPE", "");
		EAIHeader eaiheader = new EAIHeader();
		String pck= eaiheader.getHiPck();
		paramHT.put("HI_PCK", pck);
		paramHT.put("HI_WORKSTRING_TYPE", "");
		paramHT.put("HI_ROUTING_DATA", "");
		paramHT.put("HI_SVC_VERSION", "0.0.1");
		paramHT.put("HI_CLIENT_CALLERID", "");
		paramHT.put("HI_WORKSTRING_TYPE_INSTANCE", "");
		paramHT.put("HI_WORKSTRING", "");
		paramHT.put("HI_WORKSTRING_ORIGINATOR", "WMG_DOMAIN_ID");
		
		doReturn("xml").when(parserService).transform("<root></root>", "CSS_SPACE_CreateEAIHeader.xsl", paramHT, true);
		String result =parserService.addEAIHeader(eaiheader, reqXML);
		assertNull(result);
	}
	
	@Test
	public void addEAIHeaderSucTPTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		ReflectionTestUtils.setField(parserService, "svcName", "H_H_request");
		ReflectionTestUtils.setField(parserService, "svcVersion", "0.0.1");
		Map paramHT = new Hashtable();
		paramHT.put("HI_WORKSTRING_KEY", "");
		paramHT.put("HI_SVC_NAME", "H_H_request");
		paramHT.put("HI_WORKSTRING_SEQ", "");
		paramHT.put("HI_WORKSTRING_SUB_TYPE", "taskProgress");
		EAIHeader eaiheader = new EAIHeader();
		String pck= eaiheader.getHiPck();
		paramHT.put("HI_PCK", pck);
		paramHT.put("HI_WORKSTRING_TYPE", "");
		paramHT.put("HI_ROUTING_DATA", "");
		paramHT.put("HI_SVC_VERSION", "0.0.1");
		paramHT.put("HI_CLIENT_CALLERID", "");
		paramHT.put("HI_WORKSTRING_TYPE_INSTANCE", "");
		paramHT.put("HI_WORKSTRING", "");
		paramHT.put("HI_WORKSTRING_ORIGINATOR", "WMG_DOMAIN_ID");
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_TP);
		doReturn("xml").when(parserService).transform("<root></root>", "CSS_SPACE_CreateEAIHeader.xsl", paramHT, true);
		String result =parserService.addEAIHeader(eaiheader, reqXML);
		assertEquals(reqXML, result);
	}
	
	@Test
	public void addEAIHeaderSucTPPmmisTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		ReflectionTestUtils.setField(parserService, "svcName", "H_H_request");
		ReflectionTestUtils.setField(parserService, "svcVersion", "0.0.1");
		Map paramHT = new Hashtable();
		paramHT.put("HI_WORKSTRING_KEY", "");
		paramHT.put("HI_SVC_NAME", "H_H_request");
		paramHT.put("HI_WORKSTRING_SEQ", "");
		paramHT.put("HI_WORKSTRING_SUB_TYPE", WMGConstant.WORKSTRING_SUB_TYPE_TP);
		EAIHeader eaiheader = new EAIHeader();
		String pck= eaiheader.getHiPck();
		paramHT.put("HI_PCK", pck);
		paramHT.put("HI_WORKSTRING_TYPE", "");
		paramHT.put("HI_ROUTING_DATA", WMGConstant.PMMIS);
		paramHT.put("HI_SVC_VERSION", "0.0.1");
		paramHT.put("HI_CLIENT_CALLERID", "");
		paramHT.put("HI_WORKSTRING_TYPE_INSTANCE", "");
		paramHT.put("HI_WORKSTRING", "");
		paramHT.put("HI_WORKSTRING_ORIGINATOR", "WMG_DOMAIN_ID");
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_TP);
		eaiheader.setHiRoutingData(WMGConstant.PMMIS);
		doReturn("xml").when(parserService).transform("<root></root>", "CSS_SPACE_CreateEAIHeader.xsl", paramHT, true);
		String result =parserService.addEAIHeader(eaiheader, reqXML);
		assertEquals(reqXML, result);
	}

	@Test
	public void addEAIHeaderSucLinkTestResTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		ReflectionTestUtils.setField(parserService, "svcName", "H_H_request");
		ReflectionTestUtils.setField(parserService, "svcVersion", "0.0.1");
		Map paramHT = new Hashtable();
		paramHT.put("HI_WORKSTRING_KEY", "");
		paramHT.put("HI_SVC_NAME", "H_H_request");
		paramHT.put("HI_WORKSTRING_SEQ", "");
		paramHT.put("HI_WORKSTRING_SUB_TYPE", WMGConstant.WORKSTRING_SUB_TYPE_LTR);
		EAIHeader eaiheader = new EAIHeader();
		String pck= eaiheader.getHiPck();
		paramHT.put("HI_PCK", pck);
		paramHT.put("HI_WORKSTRING_TYPE", "");
		paramHT.put("HI_ROUTING_DATA", "");
		paramHT.put("HI_SVC_VERSION", "0.0.1");
		paramHT.put("HI_CLIENT_CALLERID", "");
		paramHT.put("HI_WORKSTRING_TYPE_INSTANCE", "");
		paramHT.put("HI_WORKSTRING", "");
		paramHT.put("HI_WORKSTRING_ORIGINATOR", "WMG_DOMAIN_ID");
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_LTR);
		doReturn("xml").when(parserService).transform("<root></root>", "CSS_SPACE_CreateEAIHeader.xsl", paramHT, true);
		String result =parserService.addEAIHeader(eaiheader, reqXML);
		assertEquals(reqXML, result);
	}
	
	@Test
	public void addEAIHeaderSucLinkTestTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		ReflectionTestUtils.setField(parserService, "svcName", "H_H_request");
		ReflectionTestUtils.setField(parserService, "svcVersion", "0.0.1");
		Map paramHT = new Hashtable();
		paramHT.put("HI_WORKSTRING_KEY", "");
		paramHT.put("HI_SVC_NAME", "H_H_request");
		paramHT.put("HI_WORKSTRING_SEQ", "");
		paramHT.put("HI_WORKSTRING_SUB_TYPE", WMGConstant.WORKSTRING_SUB_TYPE_LT);
		EAIHeader eaiheader = new EAIHeader();
		String pck= eaiheader.getHiPck();
		paramHT.put("HI_PCK", pck);
		paramHT.put("HI_WORKSTRING_TYPE", "");
		paramHT.put("HI_ROUTING_DATA", "");
		paramHT.put("HI_SVC_VERSION", "0.0.1");
		paramHT.put("HI_CLIENT_CALLERID", "");
		paramHT.put("HI_WORKSTRING_TYPE_INSTANCE", "");
		paramHT.put("HI_WORKSTRING", "");
		paramHT.put("HI_WORKSTRING_ORIGINATOR", "WMG_DOMAIN_ID");
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_LT);
		doReturn("xml").when(parserService).transform("<root></root>", "CSS_SPACE_CreateEAIHeader.xsl", paramHT, true);
		String result =parserService.addEAIHeader(eaiheader, reqXML);
		assertEquals(reqXML, result);
	}
	
	@Test
	public void addEAIHeaderCanceltaskTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		ReflectionTestUtils.setField(parserService, "svcName", "H_H_request");
		ReflectionTestUtils.setField(parserService, "svcVersion", "0.0.1");
		Map paramHT = new Hashtable();
		paramHT.put("HI_WORKSTRING_KEY", "");
		paramHT.put("HI_SVC_NAME", "H_H_request");
		paramHT.put("HI_WORKSTRING_SEQ", "");
		paramHT.put("HI_WORKSTRING_SUB_TYPE", WMGConstant.WORKSTRING_SUB_TYPE_CT);
		EAIHeader eaiheader = new EAIHeader();
		String pck= eaiheader.getHiPck();
		paramHT.put("HI_PCK", pck);
		paramHT.put("HI_WORKSTRING_TYPE", "");
		paramHT.put("HI_ROUTING_DATA", "");
		paramHT.put("HI_SVC_VERSION", "0.0.1");
		paramHT.put("HI_CLIENT_CALLERID", "");
		paramHT.put("HI_WORKSTRING_TYPE_INSTANCE", "");
		paramHT.put("HI_WORKSTRING", "");
		paramHT.put("HI_WORKSTRING_ORIGINATOR", "WMG_DOMAIN_ID");
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_CT);
		doReturn("xml").when(parserService).transform("<root></root>", "CSS_SPACE_CreateEAIHeader.xsl", paramHT, true);
		String result =parserService.addEAIHeader(eaiheader, reqXML);
		assertEquals(reqXML, result);
	}
	
	@Test
	public void addEAIHeaderCanceltaskPmmisTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		ReflectionTestUtils.setField(parserService, "svcName", "H_H_request");
		ReflectionTestUtils.setField(parserService, "svcVersion", "0.0.1");
		Map paramHT = new Hashtable();
		paramHT.put("HI_WORKSTRING_KEY", "");
		paramHT.put("HI_SVC_NAME", "H_H_request");
		paramHT.put("HI_WORKSTRING_SEQ", "");
		paramHT.put("HI_WORKSTRING_SUB_TYPE", WMGConstant.WORKSTRING_SUB_TYPE_CT);
		EAIHeader eaiheader = new EAIHeader();
		String pck= eaiheader.getHiPck();
		paramHT.put("HI_PCK", pck);
		paramHT.put("HI_WORKSTRING_TYPE", "");
		paramHT.put("HI_ROUTING_DATA", WMGConstant.PMMIS);
		paramHT.put("HI_SVC_VERSION", "0.0.1");
		paramHT.put("HI_CLIENT_CALLERID", "");
		paramHT.put("HI_WORKSTRING_TYPE_INSTANCE", "");
		paramHT.put("HI_WORKSTRING", "");
		paramHT.put("HI_WORKSTRING_ORIGINATOR", "WMG_DOMAIN_ID");
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_CT);
		eaiheader.setHiRoutingData(WMGConstant.PMMIS);
		doReturn("xml").when(parserService).transform("<root></root>", "CSS_SPACE_CreateEAIHeader.xsl", paramHT, true);
		String result =parserService.addEAIHeader(eaiheader, reqXML);
		assertEquals(reqXML, result);
	}

	@Test(expected = WMGTechnicalException.class)
	public void createErrorResponseXMLExceTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		
		//doReturn("xml").when(parserService).transform("<root></root>", "CSS_SPACE_CreateEAIHeader.xsl", paramWMS, true);
		parserService.createErrorResponseXML("", "", "", "", "");
	}

	@Test
	public void createErrorResponseXMLTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		Map paramHT = new Hashtable();
		paramHT.put("tag", "test");
		paramHT.put("FailureReason", "test");
		paramHT.put("FailureCode", "test");
		paramHT.put("FailureLocation", "test");
		paramHT.put("TASK_VERSION_NUMBER", "test");
		doReturn("xml").when(parserService).transform("<root></root>", "CSS_SPACE_CreateErrorResponse.xsl",paramHT, true);
		String result = parserService.createErrorResponseXML("test", "test", "test", "test", "test");
		assertEquals("xml", result);
	}
	
	@Test(expected = WMGTechnicalException.class)
	public void alterSourceSystemEmptyTest() throws METBException {
		parserService.alterSourceSystem("");
	}
	
	@Test
	public void alterSourceSystemNullTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		doReturn("xml").when(parserService).extractValue(reqXML, WMGConstant.SOURCESYSTEM);
		String result = parserService.alterSourceSystem(reqXML);
		assertNull(result);
	}
	
	
	@Test
	public void alterSourceSystemNoSSTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType></WM_Request>";
		doReturn("xml").when(parserService).extractValue(reqXML, WMGConstant.SOURCESYSTEM);
		doReturn("xml").when(environment).getProperty("xml");
		String result = parserService.alterSourceSystem(reqXML);
		assertNull(result);
	}
	
	@Test
	public void alterSourceSystemSSTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem></WM_Request>";
		doReturn("<SourceSystem>test</SourceSystem>").when(parserService).extractValue(reqXML, WMGConstant.SOURCESYSTEM);
		doReturn("FLW").when(environment).getProperty("<SourceSystem>test</SourceSystem>");
		String result = parserService.alterSourceSystem(reqXML);
		assertEquals(reqXML, result);
	}
	
	@Test(expected = WMGTechnicalException.class)
	public void transformResponseExcTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem></WM_Request>";
		String result = parserService.transformResponse("", eaiheader);
		assertEquals(reqXML, result);
	}
	
	@Test
	public void transformResponseSuccTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_IT);
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem></WM_Request>";
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).transform(reqXML, "CSS_SPACE_TaskInitiateResponse_WMG2HUB.xsl", false);
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).addEAIHeader(eaiheader, "<TaskInitiateResponseDs>test</TaskInitiateResponseDs>");
		String result = parserService.transformResponse(reqXML, eaiheader);
		assertEquals("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>", result);
	}

	@Test
	public void transformResponseMTTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_MT);
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem></WM_Request>";
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).transform(reqXML, WMGConstant.CSS_SPACE_TM_RES_WMG2HUB_XSL_FILE_NAME, false);
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).addEAIHeader(eaiheader,
				"<TaskInitiateResponseDs>test</TaskInitiateResponseDs>");
		String result = parserService.transformResponse(reqXML, eaiheader);
		assertEquals("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>", result);
	}
	
	@Test
	public void transformResponseCanclTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_CT);
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem></WM_Request>";
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).transform(reqXML, WMGConstant.CSS_SPACE_TC_RES_WMG2HUB_XSL_FILE_NAME, false);
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).addEAIHeader(eaiheader,
				"<TaskInitiateResponseDs>test</TaskInitiateResponseDs>");
		String result = parserService.transformResponse(reqXML, eaiheader);
		assertEquals("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>", result);
	}
	
	@Test
	public void transformResponseTaskStatusTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_SUT);
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem></WM_Request>";
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).transform(reqXML, WMGConstant.CSS_SPACE_TSU_RES_WMG2HUB_XSL_FILE_NAME, false);
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).addEAIHeader(eaiheader,
				"<TaskInitiateResponseDs>test</TaskInitiateResponseDs>");
		String result = parserService.transformResponse(reqXML, eaiheader);
		assertEquals("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>", result);
	}
	
	@Test(expected = WMGTechnicalException.class)
	public void alterDBIDExceTest() throws METBException {
		parserService.alterDBID("");
	}

	@Test
	public void alterDBIDNullTest() throws METBException {
	
		String result = parserService.alterDBID("test");
		assertNull(result);
	}

	@Test
	public void alterDBIDTest() throws METBException {
		doReturn("FLW").when(environment).getProperty("te");
		String result = parserService.alterDBID("test");
		assertEquals("FLWst", result);
	}

	@Test(expected = WMGTechnicalException.class)
	public void transformModifyAllExcTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		String result = parserService.transformModifyAll("", eaiheader);
		assertEquals("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>", result);
	}

	@Test
	public void transformModifyAllNullTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_SUT);
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem></WM_Request>";
		String result = parserService.transformModifyAll(reqXML, eaiheader);
		assertNull(result);
	}
	

	@Test(expected = WMGTechnicalException.class)
	public void transformModifyAllThrwTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_MT);
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem></WM_Request>";
		parserService.transformModifyAll(reqXML, eaiheader);
	}
	

	@Test(expected = WMGTechnicalException.class)
	public void transformModifyAllWmgexTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_MT);
		eaiheader.setHiPckLoc("test");
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "test");
		ReflectionTestUtils.setField(parserService, "otherJobMangerMETISDN", "METISDN");
		//ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "rest");
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem></WM_Request>";
		parserService.transformModifyAll(reqXML, eaiheader);
	}

	@Test(expected = WMGTechnicalException.class)
	public void transformModifyAllCssTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_MT);
		eaiheader.setHiPckLoc("test");
		eaiheader.setHiRoutingData("WMC82");
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "test");
		ReflectionTestUtils.setField(parserService, "otherJobMangerMETISDN", "METISDN");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "otherJobMangerCOPYROUTEDATA", WMGConstant.COPY_ROUTEDATA);
		//ReflectionTestUtils.setField(parserService, "flagMoveFSINotesToCore", "false");
		Map paramWMS = new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("BTNewsiteHandWflag", "N");
		paramWMS.put("COPY_ROUTEDATA_IND", "true");
		paramWMS.put("WMSINSTANCE", "WMC82");
		paramWMS.put("MET_Indicator", "true");
		paramWMS.put("ValidProductFlag", "V");
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem><ModifiedIndicator>false</ModifiedIndicator></WM_Request>";
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).transform(reqXML,
				WMGConstant.CSS_TM_ALL_REQ_WMG2TF_XSL_FILE_NAME, paramWMS, true);
		parserService.transformModifyAll(reqXML, eaiheader);
	}

	@Test(expected = WMGTechnicalException.class)
	public void transformModifyAllSpaceTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_MT);
		eaiheader.setHiPckLoc("test");
		eaiheader.setHiRoutingData("WMC82");
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "nill");
		ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "test");
		ReflectionTestUtils.setField(parserService, "otherJobMangerMETISDN", "METISDN");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "otherJobMangerCOPYROUTEDATA", WMGConstant.COPY_ROUTEDATA);
		//ReflectionTestUtils.setField(parserService, "flagMoveFSINotesToCore", "false");
		Map paramWMS = new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("BTNewsiteHandWflag", "N");
		paramWMS.put("COPY_ROUTEDATA_IND", "true");
		paramWMS.put("WMSINSTANCE", "WMC82");
		paramWMS.put("MET_Indicator", "true");
		paramWMS.put("ValidProductFlag", "V");
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem><ModifiedIndicator>false</ModifiedIndicator></WM_Request>";
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).transform(reqXML,
				WMGConstant.SPACE_TM_ALL_REQ_WMG2TF_XSL_FILE_NAME, paramWMS, true);
		parserService.transformModifyAll(reqXML, eaiheader);
	}
	

	@Test(expected = WMGTechnicalException.class)
	public void transformPinRemoveExcTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem><ModifiedIndicator>false</ModifiedIndicator></WM_Request>";
		parserService.transformPinRemove(reqXML, eaiheader, "");
	}
	
	@Test(expected = WMGTechnicalException.class)
	public void transformPinRemoveWmgExcTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_MT);
		eaiheader.setHiPckLoc("test");
		eaiheader.setHiRoutingData("WMC82");
		ReflectionTestUtils.setField(parserService, "jobManagerCSS", "nill");
		ReflectionTestUtils.setField(parserService, "jobManagerSPACE", "test");
		ReflectionTestUtils.setField(parserService, "otherJobMangerMETISDN", "METISDN");
		ReflectionTestUtils.setField(parserService, "journeyL2C", "WMG_DOMAIN__null");
		ReflectionTestUtils.setField(parserService, "otherJobMangerCOPYROUTEDATA", WMGConstant.COPY_ROUTEDATA);
		//ReflectionTestUtils.setField(parserService, "flagMoveFSINotesToCore", "false");
		Map paramWMS = new Hashtable();
		paramWMS.put("BTSHAWSWITCHINDICATOR", "false");
		paramWMS.put("BTNewsiteHandWflag", "N");
		paramWMS.put("COPY_ROUTEDATA_IND", "true");
		paramWMS.put("WMSINSTANCE", "WMC82");
		paramWMS.put("MET_Indicator", "true");
		paramWMS.put("ValidProductFlag", "V");
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem><ModifiedIndicator>false</ModifiedIndicator></WM_Request>";
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).transform(reqXML, WMGConstant.SWDT_PINREMOVE_REQ_WMG2TF_XSL_FILE_NAME, paramWMS, true);
		parserService.transformPinRemove(reqXML, eaiheader, "test");
	}

	@Test
	public void transformPinRemoveSuccessTest() throws METBException {
		EAIHeader eaiheader = new EAIHeader();
		eaiheader.setHiWorkstringType(WMGConstant.WORKSTRING_TYPE_JM);
		eaiheader.setHiWorkstringSubType(WMGConstant.WORKSTRING_SUB_TYPE_MT);
		eaiheader.setHiPckLoc("test");
		eaiheader.setHiRoutingData("WMC82");
		Map paramWMS = new Hashtable();
		paramWMS.put("strParamJIN", "test");
		
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem><ModifiedIndicator>false</ModifiedIndicator></WM_Request>";
		doReturn("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>").when(parserService).transform(reqXML, WMGConstant.SWDT_PINREMOVE_REQ_WMG2TF_XSL_FILE_NAME, paramWMS, true);
		String result = parserService.transformPinRemove(reqXML, eaiheader, "test");
		assertEquals("<TaskInitiateResponseDs>test</TaskInitiateResponseDs>", result);
	}

	@Test(expected = WMGTechnicalException.class)
	public void alterJINExcTest() throws METBException {
		String result = parserService.alterJIN("");
	}

	@Test
	public void alterJINNullTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem><ModifiedIndicator>false</ModifiedIndicator></WM_Request>";
		String result = parserService.alterJIN(reqXML);
		assertNull(result);
	}

	@Test
	public void alterJINTest() throws METBException {
		String reqXML = "<WM_Request><EMPProductType>FTTP</EMPProductType><SourceSystem>FLW</SourceSystem><ModifiedIndicator>false</ModifiedIndicator><JIN>Test</JIN></WM_Request>";
		// environment.getProperty(strOldJIN)
		when(environment.getProperty("Te")).thenReturn("Te");
		String result = parserService.alterJIN(reqXML);
		assertEquals(reqXML, result);
	}*/
}