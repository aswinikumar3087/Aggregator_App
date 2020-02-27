package com.bt.metb.aggregator.parser.interfaces;

import com.bt.metb.aggregator.parser.impl.ParserServiceImpl;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.util.WMGException;

import java.util.Map;

public interface ParserServiceInterface {

    String stripNamespace(String reqXML) throws METBException;

    ParserServiceImpl loadXML(String strXML);

    String getRequiredTagValue(String path) throws METBException;

    String transformErrorResponse(String validatedXML, Map map, String actiontype) throws METBException;

    String transform(String strInputXML, String strInputXSLFile, Boolean validateTFDTDFlag) throws WMGException;

    String extractValue(String strXML, final String strXMLTag) throws WMGException;

    String getRequiredTagValue(String xml, String path) throws METBException;

    String transform(String strInputXML, String strInputXSLFileName, Map map, Boolean validateTFDTDFlag) throws WMGException;

    String aggregationResponseNamespaceStrip(String xml) throws WMGException;

    String transformResponse(String emppalOutput, String actionValue, String product, String xsl) throws WMGException;

}

