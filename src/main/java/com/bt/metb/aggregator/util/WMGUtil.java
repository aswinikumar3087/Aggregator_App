package com.bt.metb.aggregator.util;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.mqConfig.MetbAggregatorMqConfigReader;
import com.bt.metb.aggregator.producer.MessagePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class WMGUtil {

    @Autowired
    private METResourceManagerInterface resourceManagerInterface;

    private static final Logger SLF4J_LOGGER = LoggerFactory.getLogger(WMGUtil.class);

    @Autowired
    private MetbAggregatorMqConfigReader metbAggregatorMqConfigReader;
    @Autowired
    private MessagePublisher messagePublisher;

    @Autowired
    @Qualifier("simpleMessageConverter")
    private SimpleMessageConverter simpleMessageConverter;

    public void setMETResourceManagerInterface(
            METResourceManagerInterface resourceManager) {
        resourceManagerInterface = resourceManager;
    }

    public static String getStackTrace(Throwable throwable) {
        String trace = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            throwable.printStackTrace(ps);
            ps.flush();
            trace = baos.toString();
            ps.close();
            baos.close();
        } catch (IOException ex) {
            SLF4J_LOGGER.error("getStackTrace :: Inside catch", ex);
        }
        return trace;
    }


    public static String prepareHiPck(String strSourceSystem,
                                      String metTaskIdentifier) throws METBException {
        String HI_PCK;
        String HI_PCK_EYE = "PCK0";
        String HI_PCK_VER = "02";
        String HI_PCK_LEN = "069";
        String HI_PCK_SYS = "TFSR            ";
        String HI_PCK_USR = getHiPck(strSourceSystem, 12);
        String HI_PCK_SEQ = getHiPck(metTaskIdentifier, 9) + ":"
                + new SimpleDateFormat("HHmmss").format(new Date());
        String HI_PCK_LOC = getHiPck(strSourceSystem, 16);
        HI_PCK = HI_PCK_EYE + HI_PCK_VER + HI_PCK_LEN + HI_PCK_SYS + HI_PCK_USR
                + HI_PCK_SEQ + HI_PCK_LOC;
        return HI_PCK;
    }

    public static String getHiPck(String pStrHiPck, int pLength)
            throws METBException {
        String strHiPck = pStrHiPck;
        int length = pLength;
        try {
            if (strHiPck != null) {
                StringBuilder sb = new StringBuilder(strHiPck);
                sb.append(StringUtil.getPaddedString(length));
                // truncate to required length
                sb.setLength(length);
                strHiPck = sb.toString();
            } else {
                strHiPck = "";
            }
        } catch (Exception e) {
            throw new METBException(
                    WMGConstant.UNKNOWN_EXCEPTION_ERRCODE,
                    WMGConstant.TECHNICAL_EXCEPTION, "while getting HI_PCK",
                    WMGUtil.getStackTrace(e));
        }
        return strHiPck;
    }

    public static String convertIntoTFAppointmentFormat(String pIndate,
                                                        String dateFormat) throws METBException {
        String indate = pIndate;
        if (StringUtil.isNullOrEmpty(indate)) {
            throw new METBException(WMGConstant.MET_NULL_KEY_EXC_ESMF,
                    WMGConstant.TECHNICAL_EXCEPTION,
                    "METUtil :: convertIntoTFStartDateFormat :: ",
                    "Input Parameter NULL");
        }
        SimpleDateFormat sdfInput = null;
        SimpleDateFormat sdfOutput = null;
        String outdate = null;
        String strRestString1 = null;
        String strRestString2 = null;
        String strYear = null;
        int hypenIndex;

        try {
            if (indate.indexOf('T') > -1) {

                hypenIndex = indate.indexOf('-', 1);
                strRestString1 = indate.substring(hypenIndex + 1, indate.length());

                if (strRestString1.indexOf('.') > -1) {
                    strRestString2 = strRestString1.substring(0, strRestString1.indexOf('.'));

                } else {
                    strRestString2 = strRestString1;
                }

                if (strRestString2.length() != 14) {

                    throw new METBException(
                            WMGConstant.XSD_VALIDATION_FAILED_CODE,
                            WMGConstant.BUSINESS_EXCEPTION,
                            WMGConstant.XSD_VALIDATION_FAILED_DESC,
                            "METUtil :: convertIntoTFAppointmentFormat :: Date : "
                                    + indate
                                    + " :: received in invalid dateformat from Input Request");
                }

                strYear = indate.substring(0, indate.indexOf('-', 1) + 1);
                indate = strYear + strRestString2;

                if (indate.startsWith("-")) {
                    indate = indate.substring(1);
                }
                sdfInput = new SimpleDateFormat(WMGConstant.XML_DATE_TIME_FORMAT);

            } else {
                sdfInput = new SimpleDateFormat(WMGConstant.PAL_DATE_FORMAT);
            }

            sdfInput.setLenient(false);
            sdfOutput = new SimpleDateFormat(dateFormat);
            Date date1 = sdfInput.parse(indate);
            outdate = sdfOutput.format(date1);

        } catch (METBException me) {
            throw me;
        } catch (ParseException parseException) {
            throw new METBException(
                    WMGConstant.XSD_VALIDATION_FAILED_CODE,
                    WMGConstant.BUSINESS_EXCEPTION,
                    WMGConstant.XSD_VALIDATION_FAILED_DESC,
                    "METUtil :: convertIntoTFAppointmentFormat :: Date : "
                            + indate
                            + " :: received in invalid dateformat from Input Request");
        } catch (Exception e) {
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE,
                    WMGConstant.TECHNICAL_EXCEPTION,
                    "METUtil :: convertIntoTFAppointmentFormat :: ",
                    WMGUtil.getStackTrace(e));
        }

        return outdate;
    }

    // Created a method to convert AccessDays Start and Finish dates to format
    // YYYYMMDD for R1306. 16-Jan-10
    public static String convertIntoWMIGAccessDsDateFormat(String indate,
                                                           String dateFormat) throws METBException {
        if (StringUtil.isNullOrEmpty(indate)) {
            throw new METBException(WMGConstant.MET_NULL_KEY_EXC_ESMF,
                    WMGConstant.TECHNICAL_EXCEPTION,
                    "METUtil :: convertIntoWMIGAccessDsDateFormat :: ",
                    "Input Parameters NULL");
        }
        SimpleDateFormat sdfInput = null;
        SimpleDateFormat sdfOutput = null;
        String outdate = null;
        String strRestString1 = null;
        String strRestString2 = null;
        String strYear = null;
        String newIndate = indate;
        int hypenIndex;

        try {
            if ((newIndate.indexOf('-') > -1)) {

                hypenIndex = newIndate.indexOf('-', 1);
                strRestString1 = newIndate.substring(hypenIndex + 1, newIndate.length());

                if (strRestString1.indexOf('Z') > -1) {
                    strRestString2 = strRestString1.substring(0, strRestString1.indexOf('Z'));
                } else if (strRestString1.indexOf('+') > -1) {
                    strRestString2 = strRestString1.substring(0, strRestString1.indexOf('+'));
                } else if (strRestString1.indexOf('-', 3) > -1) {
                    strRestString2 = strRestString1.substring(0, strRestString1.lastIndexOf('-'));
                } else {
                    strRestString2 = strRestString1;
                }

                if (strRestString2.length() != 5) {

                    throw new METBException(
                            WMGConstant.XSD_VALIDATION_FAILED_CODE,
                            WMGConstant.BUSINESS_EXCEPTION,
                            WMGConstant.XSD_VALIDATION_FAILED_DESC,
                            "METUtil :: convertIntoWMIGAccessDsDateFormat :: Date : "
                                    + newIndate
                                    + " : received invalid dateformat from Input Request");
                }
                strYear = newIndate.substring(0, newIndate.indexOf('-', 1) + 1);
                newIndate = strYear + strRestString2;

                newIndate = newIndate.substring(newIndate.indexOf('-'), newIndate.length());

                sdfInput = new SimpleDateFormat(WMGConstant.XML_DATE_FORMAT);

            } else if ((indate.indexOf('/') > -1)) {
                sdfInput = new SimpleDateFormat(WMGConstant.FH_DATE_FORMAT);
            } else {
                //added to accept date in yyyyMMdd format
                sdfInput = new SimpleDateFormat(WMGConstant.WMIG_DATE_FORMAT_APPDATE);
            }
            sdfInput.setLenient(false);
            sdfOutput = new SimpleDateFormat(dateFormat);
            Date accessDate = sdfInput.parse(newIndate);

            outdate = sdfOutput.format(accessDate);

        } catch (METBException me) {
            throw me;
        } catch (ParseException parseException) {

            throw new METBException(
                    WMGConstant.XSD_VALIDATION_FAILED_CODE,
                    WMGConstant.BUSINESS_EXCEPTION,
                    WMGConstant.XSD_VALIDATION_FAILED_DESC,
                    "METUtil :: convertIntoWMIGAccessDsDateFormat :: Date : "
                            + newIndate
                            + " : received invalid dateformat from Input Request");
        } catch (Exception e) {
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE,
                    WMGConstant.TECHNICAL_EXCEPTION,
                    "METUtil :: convertIntoWMIGAccessDsDateFormat :: ",
                    WMGUtil.getStackTrace(e));
        }

        return outdate;
    }

    // Created a method to convert AccessStartTime and AccessFinishTime to
    // format HHMMSS for R1306
    public static String convertIntoWMIGAccessDsTimeFormat(String indate,
                                                           String dateFormat) throws METBException {
        if (StringUtil.isNullOrEmpty(indate)) {
            throw new METBException(WMGConstant.MET_NULL_KEY_EXC_ESMF,
                    WMGConstant.TECHNICAL_EXCEPTION,
                    "METUtil :: convertIntoWMIGAccessDsTimeFormat :: ",
                    "Input Parameters NULL");
        }
        SimpleDateFormat sdfInput = null;
        SimpleDateFormat sdfOutput = null;
        String outdate = null;
        String strRestString1 = null;
        String newIndate = indate;
        int commomIndex = 0;

        try {

            commomIndex = StringUtil.getCommomIndex(newIndate);

            strRestString1 = newIndate.substring(0, commomIndex);

            if (commomIndex > 0) {
                if (strRestString1.length() < 8) {

                    throw new METBException(
                            WMGConstant.XSD_VALIDATION_FAILED_CODE,
                            WMGConstant.BUSINESS_EXCEPTION,
                            WMGConstant.XSD_VALIDATION_FAILED_DESC,
                            "METUtil :: convertIntoWMIGAccessDsTimeFormat :: Date : "
                                    + newIndate
                                    + " : received in invalid dateformat from Input Request");
                }
                newIndate = strRestString1;
                sdfInput = new SimpleDateFormat(WMGConstant.XML_TIME_FORMAT);
            } else if (indate.indexOf(':') > -1) {
                sdfInput = new SimpleDateFormat(WMGConstant.PAL_MET_ACCESSD_TIME_FORMAT);
            } else {
                //Added to accept time in HHmm or HHmmSS format
                if (indate.length() == 4) {
                    sdfInput = new SimpleDateFormat(WMGConstant.WMIG_DATE_FORMAT_AUTHTIME);
                } else {
                    sdfInput = new SimpleDateFormat(WMGConstant.WMIG_ACCESSD_TIME_FORMAT);
                }
            }

            sdfInput.setLenient(false);
            sdfOutput = new SimpleDateFormat(dateFormat);
            Date accessTime = sdfInput.parse(newIndate);

            outdate = sdfOutput.format(accessTime);
        } catch (METBException me) {
            throw me;
        } catch (ParseException parseException) {
            throw new METBException(
                    WMGConstant.XSD_VALIDATION_FAILED_CODE,
                    WMGConstant.BUSINESS_EXCEPTION,
                    WMGConstant.XSD_VALIDATION_FAILED_DESC,
                    "METUtil :: convertIntoWMIGAccessDsTimeFormat :: Date : "
                            + newIndate
                            + " : received in invalid dateformat from Input Request");
        } catch (Exception e) {
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE,
                    WMGConstant.TECHNICAL_EXCEPTION,
                    "METUtil :: convertIntoWMIGAccessDsTimeFormat :: ",
                    WMGUtil.getStackTrace(e));
        }

        return outdate;
    }

    public void postAggregationErrorResponse(String response, String strSMNTSystem) throws METBException {

        try {
            if (WMGConstant.MET_EWOCS_DESTINATION_TYPE.equalsIgnoreCase(strSMNTSystem)) {
                Message textMessage = simpleMessageConverter.toMessage(response, new MessageProperties());
                messagePublisher.publishMessage(metbAggregatorMqConfigReader.getAggregatorToMetbOutboundQueueExchange(), metbAggregatorMqConfigReader.getRoutingKeyAggregatorToMetbOutbound(), textMessage);
            } else {
                SLF4J_LOGGER.error("METException Occured..Invalid SMNT system configured in database");
                throw new METBException(
                        WMGConstant.INVALID_OR_NULL_SMNT_SYSTEM_ERROR_CODE,
                        WMGConstant.BUSINESS_EXCEPTION,
                        WMGConstant.INVALID_OR_NULL_SMNT_SYSTEM_FAILED_DESC,
                        "METUtil :: postAggregationErrorResponse :: Invalid SMNT system configured in database");
            }

        } catch (METBException me) {
            throw me;
        }

    }

    public static Hashtable mapToTable(Map m) {

        if (!(m instanceof Hashtable)) {
            throw new IllegalArgumentException("argument is not a hashtable instance");
        }
        return (Hashtable) m;
    }

    public String sqlFormatedList(List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (String i : list) {
            sb.append("'" + i + "'" + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }
}
    
