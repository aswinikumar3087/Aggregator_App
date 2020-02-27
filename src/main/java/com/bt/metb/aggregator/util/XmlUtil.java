package com.bt.metb.aggregator.util;

import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Utility class for handling XML data. Provides capability to serialise a DOM
 * tree to a string/XML file and the reverse & XML DOM document interfacing
 * methods.
 **/

@SuppressWarnings({ "rawtypes", "unchecked" })
public class XmlUtil
{

    private static DocumentBuilder dBuilder = null;
    
    private static DocumentBuilderFactory dbFactory = null;  

    private static final org.slf4j.Logger SLF4J_LOGGER = LoggerFactory.getLogger(XmlUtil.class);
    
    /**
     * This is a class with static Utility method. Constructor is made private to suppress initialisation and creation of unwanted objects
     */
    private XmlUtil(){}

    static
    {
        dbFactory = DocumentBuilderFactory.newInstance();

        try
        {
            dBuilder = dbFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException pce)
        {
        	SLF4J_LOGGER.error("XmlUtil::static block::", pce);
        }
        catch (Exception e)
        {
        	SLF4J_LOGGER.error("XmlUtil::static block::", e);
        }
    }

    public static String startTag(String strTagName)
    {
        if (StringUtil.isNullOrEmpty(strTagName))
            throw new IllegalArgumentException("Expecting a start tag for empty or null object");

        return String.format("<%s>", strTagName);

    }

    /**
     * This method creates an XML ending tag for the given tag name. This is a
     * Convenience method which obviates need for writing code like " </" +
     * WMGConstant.TASKINITIATEDS +">" and instead write
     * WMGUtil.endTag(WMGConstant.TASKINITIATEDS) which is more readable and
     * maintainable.
     * 
     * @param strTagName
     *            - - The name of the tag for which XML end tag is to be created
     * @return The XML tag corresponding to the tag name
     */
    public static String endTag(String strTagName)
    {

        if (StringUtil.isNullOrEmpty(strTagName))
            throw new IllegalArgumentException("Expecting a end tag for empty or null object");

        return "</" + strTagName + ">";

    }

    /**
     * Creates and empty element task
     * 
     * @param strTagName
     * @return
     */
    public static String emptyTag(String strTagName)
    {

        if (StringUtil.isNullOrEmpty(strTagName))
            throw new IllegalArgumentException("Expecting a end tag for empty or null object");

        return "<" + strTagName + "/>";

    }
    
}
