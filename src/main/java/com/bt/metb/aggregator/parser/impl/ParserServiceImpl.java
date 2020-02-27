package com.bt.metb.aggregator.parser.impl;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dataobjects.EmppalRequestAttributeReader;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.model.ParserConfigReader;
import com.bt.metb.aggregator.parser.interfaces.ParserServiceInterface;
import com.bt.metb.aggregator.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;


@Component("parserServiceImpl")
@RefreshScope
public class ParserServiceImpl implements ParserServiceInterface {

    private static final Logger SLF4J_LOGGER = LoggerFactory.getLogger(ParserServiceImpl.class);

    private static final String SCHEMA_NAMESPACE = " xsi:schemaLocation=\"http://capabilities.nat.bt.com/xsd/ManageEngineeringTask/2008/06/30 ManageEngineeringTask.20080630.xsd\" xmlns:m=\"http://capabilities.nat.bt.com/xsd/ManageEngineeringTask/2008/06/30\" >";

    private static final String EMPTY_XML = "";

    private XPath cachedXPath = XPathFactory.newInstance().newXPath();

    private Node cachedXmlRootNode;

    private String cachedRawInput;

    @Autowired
    private Environment environment;

    @Autowired
    private ParserConfigReader parserConfigReader;
    @Autowired
    private EmppalRequestAttributeReader emppalRequestAttributeReader;

    Map allXSLs = new HashMap();

    public ParserServiceImpl() {
    }

    public ParserServiceImpl(String strXML) {
        loadXML(strXML);
    }

    public String stripNamespace(String reqXML) throws WMGException {

        String strXMLforstripping = reqXML.substring(reqXML.indexOf("<WM_Request"));
        String strToBeReplaced = strXMLforstripping.substring(strXMLforstripping.indexOf("<WM_Request"), strXMLforstripping.indexOf('>'));
        String strReplacedWith = "<WM_Request";
        String strTransformedXML = reqXML.replaceAll(strToBeReplaced, strReplacedWith);
        strTransformedXML = transform(strTransformedXML, WMGConstant.WMIG_DUMMY_XSL, true);
        return strTransformedXML;
    }

    @Override
    public ParserServiceImpl loadXML(String strXML) {
        cachedRawInput = strXML;
        cachedXmlRootNode = null;
        return this;
    }

    @Override
    public String getRequiredTagValue(String path) throws METBException {

        String value = null;
        try {
            if (cachedXmlRootNode == null) {
                loadXMLInternal();
            }
            value = cachedXPath.evaluate(path, cachedXmlRootNode);
            if (!StringUtil.isNullOrEmpty(value)) {
                value = value.trim();
            }
        } catch (XPathExpressionException xe) {
            throw new METBException(WMGConstant.XSD_VALIDATION_FAILED_CODE, WMGConstant.BUSINESS_EXCEPTION, WMGConstant.XSD_VALIDATION_FAILED_DESC, "ParserServiceImpl :: _getRequiredTagValue ::" + WMGUtil.getStackTrace(xe));
        } catch (Exception e) {
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION, "ParserServiceImpl :: _getRequiredTagValue :: Unknown Exception ", WMGUtil.getStackTrace(e));
        }
        return value;
    }

    @Override
    public String transformErrorResponse(String validatedXML, Map map, String actiontype) throws METBException {

        String strXMLToRet = null;
        String strInputXSLFileName = null;
        String respTypeXML = null;
        String messageId = null;
        String action = null;
        String mainTagName = WMGConstant.EMPTYSTRING;
        String mainReturnTagName = WMGConstant.EMPTYSTRING;
        try {
            LocalUtil.save2Map(map, "stateCode", WMGConstant.FAILED_STATUS, LocalUtil.CV.BLIND, null);
            LocalUtil.save2Map(map, "serviceName", WMGConstant.MET_SERVICE_NAME, LocalUtil.CV.BLIND, null);
            LocalUtil.save2Map(map, "from", WMGConstant.FROM_ADDRESS, LocalUtil.CV.BLIND, null);
            LocalUtil.save2Map(map, "address", WMGConstant.PAL_TO_ADDRESS, LocalUtil.CV.BLIND, null);

            if (!map.containsKey("errorTrace")) {
                LocalUtil.save2Map(map, "errorTrace", LocalUtil.getErrorTrace((String) map.get("errorCode")), LocalUtil.CV.BLIND, null);
            }
            String replyTo = (String) map.get("replyTo");
            loadXML(validatedXML);
            if (WMGConstant.TF_CREATE_RESPONSE.equalsIgnoreCase(actiontype)) {
                action = WMGConstant.RESPONSE_TO_PAL;
                messageId = getRequiredTagValue(parserConfigReader.getWmigMessageId());
                strInputXSLFileName = WMGConstant.CREATE_RESPONSE_XSL;
                mainTagName = "createTaskResponse";
                mainReturnTagName = "m:createTaskResponse";
            } else if (WMGConstant.TF_AMEND_RESPONSE.equalsIgnoreCase(actiontype)) {
                action = WMGConstant.AMEND_RESPONSE_TO_PAL;
                messageId = getRequiredTagValue(parserConfigReader.getWmigMessageId());
                strInputXSLFileName = WMGConstant.AMEND_RESPONSE_XSL;
                mainTagName = "amendTaskResponse";
                mainReturnTagName = "m:amendTaskResponse";
            } else if (WMGConstant.TF_CANCEL_RESPONSE.equalsIgnoreCase(actiontype)) {
                action = WMGConstant.CANCEL_RESPONSE_TO_PAL;
                messageId = getRequiredTagValue(parserConfigReader.getWmigMessageId());
                strInputXSLFileName = WMGConstant.CANCEL_RESPONSE_XSL;
                mainTagName = "cancelTaskResponse";
                mainReturnTagName = "m:cancelTaskResponse";
            } else if (WMGConstant.TF_STATUS_UPDATE_RESPONSE.equalsIgnoreCase(actiontype)) {

                String latestOperation = (String) map.get("latestOperation");
                if (!StringUtil.isNullOrEmpty(latestOperation)
                        && WMGConstant.UPDATE_TASK_REQUEST_OPERATION.equals(latestOperation)) {
                    mainReturnTagName = "m:updateTaskResponse";
                    action = WMGConstant.UPDATE_RESPONSE_TO_PAL;
                    messageId = getRequiredTagValue(parserConfigReader.getWmigMessageId());
                    strInputXSLFileName = WMGConstant.STATUS_UPDATE_RESPONSE_FOR_UPDATE_REQUEST_XSL;
                } else {
                    mainReturnTagName = "m:amendTaskResponse";
                    action = WMGConstant.AMEND_RESPONSE_TO_PAL;
                    messageId = getRequiredTagValue(parserConfigReader.getWmigMessageId());
                    strInputXSLFileName = WMGConstant.STATUS_UPDATE_RESPONSE_FOR_AMEND_REQUEST_XSL;
                }

                mainTagName = "updateStatusTaskResponse";
            } else if (WMGConstant.CREATE_ACTION.equalsIgnoreCase(actiontype)) {
                action = WMGConstant.RESPONSE_TO_PAL;
                strInputXSLFileName = WMGConstant.CREATE_RESPONSE_XSL;
                mainTagName = "createTaskResponse";
                mainReturnTagName = "m:createTaskResponse";
                messageId = (String) map.get("messageId");
            } else if (WMGConstant.AMEND_ACTION.equalsIgnoreCase(actiontype)) {
                action = WMGConstant.AMEND_RESPONSE_TO_PAL;
                strInputXSLFileName = WMGConstant.AMEND_RESPONSE_XSL;
                mainTagName = "amendTaskResponse";
                mainReturnTagName = "m:amendTaskResponse";
                messageId = (String) map.get("messageId");
            } else if (WMGConstant.CANCEL_ACTION.equalsIgnoreCase(actiontype)) {
                action = WMGConstant.CANCEL_RESPONSE_TO_PAL;
                strInputXSLFileName = WMGConstant.CANCEL_RESPONSE_XSL;
                mainTagName = "cancelTaskResponse";
                mainReturnTagName = "m:cancelTaskResponse";
                messageId = (String) map.get("messageId");
            } else if (WMGConstant.UPDATE_ACTION.equalsIgnoreCase(actiontype)) {
                action = WMGConstant.UPDATE_RESPONSE_TO_PAL;
                strInputXSLFileName = WMGConstant.STATUS_UPDATE_RESPONSE_FOR_UPDATE_REQUEST_XSL;
                mainTagName = "updateStatusTaskResponse";
                mainReturnTagName = "m:updateTaskResponse";
            }
            LocalUtil.save2Map(map, "action", action, LocalUtil.CV.BLIND, null);
            LocalUtil.save2Map(map, "messageId", messageId, LocalUtil.CV.NOCONVERT, null);

            respTypeXML = (StringUtil.isNullOrEmpty(replyTo)) ? XmlUtil.emptyTag(mainTagName) : (XmlUtil.startTag(mainTagName) + replyTo + XmlUtil.endTag(mainTagName));
            String strResponse = transform(respTypeXML, strInputXSLFileName, map, false);
            strXMLToRet = strResponse.replaceAll("&gt;", ">").replaceAll("&lt;", "<").replaceAll("&amp;", "&");
            strXMLToRet = strXMLToRet.replaceAll(XmlUtil.startTag(mainReturnTagName), "<" + mainReturnTagName + SCHEMA_NAMESPACE);
        } catch (METBException me) {
            throw me;
        } catch (Exception exception) {
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION, "ParserServiceImpl.transformErrorResponse() : ", WMGUtil.getStackTrace(exception));
        }
        return strXMLToRet;
    }

    @Override
    public String transform(String strInputXML, String strInputXSLFile, Boolean validateTFDTDFlag) throws WMGException {
        SLF4J_LOGGER.debug("ParserServiceImpl.transform() inside transform method");
        SLF4J_LOGGER.debug("XmlFiles  is {}:: ", strInputXML);
        SLF4J_LOGGER.debug("XslFiles  is {}:: ", strInputXSLFile);
        SLF4J_LOGGER.debug("ParserServiceImpl  {}:: ", validateTFDTDFlag);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Transformer transformer;
            if (validateTFDTDFlag) {
                transformer = ((Templates) getXSLs(strInputXSLFile)).newTransformer();
                transformer.transform(new StreamSource(new ByteArrayInputStream(strInputXML.getBytes("UTF-8")), parserConfigReader.getDtdPath()), new StreamResult(baos));
                return baos.toString("UTF-8");
            } else {
                SLF4J_LOGGER.debug("transformer  inside else block:: ");
                transformer = ((Templates) getXSLs(strInputXSLFile)).newTransformer();
                SLF4J_LOGGER.debug("transformer  is {}:: ", transformer);
                transformer.transform(new StreamSource(new ByteArrayInputStream(strInputXML.getBytes("UTF-8"))), new StreamResult(baos));
                String strippedxml = baos.toString("UTF-8");
                SLF4J_LOGGER.debug("transformed xml after stripping {}:: ", strippedxml);
                return baos.toString("UTF-8");
            }
        } catch (TransformerException e) {
            throw new WMGTechnicalException(WMGConstant.TRANSFORMER_EXC_JM, WMGConstant.TRANSFORMER_EXC_ESMF, "ParserServiceImpl.transform() :: " + WMGUtil.getStackTrace(e));
        } catch (Exception e) {
            throw new WMGTechnicalException(WMGConstant.OTHER_EXC_JM, WMGConstant.OTHER_EXC_ESMF, "ParserServiceImpl.transform() :: " + WMGUtil.getStackTrace(e));
        }
    }

    public String getRequiredTagValue(String xml, String path) throws METBException {
        ParserServiceImpl mt = new ParserServiceImpl(xml);
        return mt.getRequiredTagValue(path);
    }

    public String transform(String strInputXML, String strInputXSLFileName, Map map, Boolean validateTFDTDFlag) throws WMGException {
        try {
            SLF4J_LOGGER.debug("ParserServiceImpl.transform() strInputXSLFileName:{} ", strInputXSLFileName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Transformer transformer;
            if (validateTFDTDFlag) {
                transformer = (getXSLs(strInputXSLFileName)).newTransformer();
                for (Object entry : map.entrySet()) {
                    Map.Entry mapEntry = (Map.Entry) entry;
                    transformer.setParameter((String) mapEntry.getKey(), mapEntry.getValue());
                }
                SLF4J_LOGGER.debug("ParserServiceImpl.transform() strInputXML:{} ", strInputXML);
                transformer.transform(new StreamSource(new ByteArrayInputStream(strInputXML.getBytes("UTF-8")), parserConfigReader.getDtdPath(

                )), new StreamResult(baos));
                SLF4J_LOGGER.debug("ParserServiceImpl.transform() Return:{} ", baos.toString("UTF-8"));
                return baos.toString("UTF-8");
            } else {
                transformer = (getXSLs(strInputXSLFileName)).newTransformer();
                for (Object entry : map.entrySet()) {
                    Map.Entry mapEntry = (Map.Entry) entry;
                    transformer.setParameter((String) mapEntry.getKey(), mapEntry.getValue());
                }
                SLF4J_LOGGER.debug("ParserServiceImpl.transform() strInputXML:{} ", strInputXML);
                SLF4J_LOGGER.debug("ParserServiceImpl.transform() strInputXSLFileName:{} ", strInputXSLFileName);
                transformer.transform(new StreamSource(new ByteArrayInputStream(strInputXML.getBytes("UTF-8"))), new StreamResult(baos));
                SLF4J_LOGGER.debug("ParserServiceImpl.transform() Return:{} ", baos.toString("UTF-8"));
                return baos.toString("UTF-8");
            }

        } catch (TransformerException e) {
            SLF4J_LOGGER.debug("Transformation exception error, input XML is : {}, XSL is : {}", strInputXML, strInputXSLFileName);
            SLF4J_LOGGER.error("Error {} ", WMGUtil.getStackTrace(e), e);
            throw new WMGTechnicalException(WMGConstant.TRANSFORMER_EXC_JM, WMGConstant.TRANSFORMER_EXC_ESMF, "ParserServiceImpl.transform() :: " + WMGUtil.getStackTrace(e));
        } catch (Exception e) {
            SLF4J_LOGGER.debug("Other exception error, input XML is : {}, XSL is : {}", strInputXML, strInputXSLFileName);
            SLF4J_LOGGER.error("Error {} :: ", e.getStackTrace());
            throw new WMGTechnicalException(WMGConstant.OTHER_EXC_JM, WMGConstant.OTHER_EXC_ESMF, "ParserServiceImpl.transform() :: " + e.getStackTrace());

        }
    }

    void loadXMLInternal() throws XPathExpressionException {
        InputSource xmlSource = new InputSource(new StringReader(cachedRawInput));
        cachedXmlRootNode = (Node) cachedXPath.evaluate("/", xmlSource, XPathConstants.NODE);
        cachedRawInput = EMPTY_XML;
    }

    private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        String tagValue = null;
        if (nlList.item(0) != null) {
            Node nValue = nlList.item(0);
            tagValue = nValue.getNodeValue();
            if (!StringUtil.isNullOrEmpty(tagValue)) {
                tagValue = tagValue.trim();
            }
        }
        return tagValue;
    }


    public String extractValue(String strXML, final String strXMLTag) throws WMGException {

        if (StringUtil.isNullOrEmpty(strXML) || StringUtil.isNullOrEmpty(strXMLTag)) {
            throw new WMGTechnicalException(WMGConstant.NULL_KEY_EXC_JM, WMGConstant.NULL_KEY_EXC_ESMF, "ParserServiceImpl.extractValue() :: " + "Input Parameters NULL");
        }
        try {
            int iStartLocation = strXML.indexOf(XmlUtil.startTag(strXMLTag));
            if (iStartLocation == -1) {
                return null;
            }
            int iEndLocation = strXML.indexOf(XmlUtil.endTag(strXMLTag));
            if (iEndLocation == -1) {
                return null;
            }
            return strXML.substring(iStartLocation + strXMLTag.length() + 2, iEndLocation);
        } catch (Exception e) {
            throw new WMGTechnicalException(WMGConstant.OTHER_EXC_JM, WMGConstant.OTHER_EXC_ESMF, "ParserServiceImpl.extractValue() :: " + WMGUtil.getStackTrace(e));
        }

    }

    private Templates getXSLs(String strInputXSLFileName) {
        SLF4J_LOGGER.debug("transformer  inside getXSLs ");
        if (allXSLs.isEmpty()) {
            SLF4J_LOGGER.debug("transformer  inside empty ");
            FileUtil.loadAllXSLs(WMGConstant.XSL_FOLDER_PATH, allXSLs);
        }
        return (Templates) allXSLs.get(strInputXSLFileName);
    }

    public String aggregationResponseNamespaceStrip(String inputXML) throws WMGException {
        return transform(inputXML, WMGConstant.STRIP_XSD_NAMESPACE, false);
    }

    public String transformResponse(String emppalOutput, String actionValue, String productType, String xslFileName) throws WMGException{
        String xsl = null;
        HashMap map = new HashMap();
        map.put("productType", productType);
        if (WMGConstant.CREATE_ACTION.equalsIgnoreCase(actionValue) || (WMGConstant.AMEND_ACTION.equalsIgnoreCase(actionValue)))
        {
            xsl = xslFileName;
        }
        return transform(emppalOutput, xsl, map, false);
    }
}
