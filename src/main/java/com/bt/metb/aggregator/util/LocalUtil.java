package com.bt.metb.aggregator.util;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.exception.METBException;

import java.util.HashMap;
import java.util.Map;

public class LocalUtil {
    private static final Map<String, String> ERROR_TRACE_MAP = new HashMap<>();

    static {
        ERROR_TRACE_MAP.put(WMGConstant.BIP_BUSINESS_ERROR_CODE, "BIP");
        ERROR_TRACE_MAP.put(WMGConstant.MAC_BUSINESS_ERROR_CODE, "MAC");
        ERROR_TRACE_MAP.put(WMGConstant.MCP_BUSINESS_ERROR_CODE, "MCP");
        ERROR_TRACE_MAP.put(WMGConstant.MLI_BUSINESS_ERROR_CODE, "MLI");
        ERROR_TRACE_MAP.put(WMGConstant.MNSD_BUSINESS_ERROR_CODE, "MNSD");
        ERROR_TRACE_MAP.put(WMGConstant.MPIC_BUSINESS_ERROR_CODE, "MPlC");
        ERROR_TRACE_MAP.put(WMGConstant.PMMIS_BUSINESS_ERROR_CODE, "PMMIS");
        ERROR_TRACE_MAP.put(WMGConstant.STAA_DISPLAYFRAMETERMINATIONS_BUSINESS_ERROR_CODE, "STAA");
        ERROR_TRACE_MAP.put(WMGConstant.STAA_GETEXCHANGEDETAILS_BUSINESS_ERROR_CODE, "STAA");
        ERROR_TRACE_MAP.put(WMGConstant.STAA_GETREPAIRDETAILS_BUSINESS_ERROR_CODE, "STAA");
        ERROR_TRACE_MAP.put(WMGConstant.MSI_BUSINESS_ERROR_CODE, "MSI");
        ERROR_TRACE_MAP.put(WMGConstant.ORMNSD_BUSINESS_ERROR_CODE, "MNSD");
        ERROR_TRACE_MAP.put(WMGConstant.MPC_BUSINESS_ERROR_CODE, "MPC");

    }

    public static String getErrorTrace(String strErrorCode) {
        String strErrorTrace = ERROR_TRACE_MAP.get(strErrorCode.toUpperCase());

        if (StringUtil.isNullOrEmpty(strErrorTrace)) {
            strErrorTrace = WMGConstant.SOURCE_FOR_DIPLOMAT;
        }

        return strErrorTrace;
    }

    public enum CV {
        BLIND, NOCONVERT, POSTCODE, WMIGACCESSDSTIME, TFAPPOINTMENT, WMIGACCESSDSDATE, CUSTOMERGRANTEEDATETIME
    }

    public static void save2Map(Map map, String key, String value, CV conversionType, String optFormat) throws METBException {
        if (conversionType == CV.BLIND) {
            map.put(key, value);

        } else if (!StringUtil.isNullOrEmpty(value)) {
            if (conversionType == CV.NOCONVERT) {
                map.put(key, value);
            } else if (conversionType == CV.POSTCODE) {
                map.put(key, formatPostCode(value));
            } else if (conversionType == CV.WMIGACCESSDSTIME) {
                map.put(key, WMGUtil.convertIntoWMIGAccessDsTimeFormat(value, optFormat));
            } else if (conversionType == CV.WMIGACCESSDSDATE) {
                map.put(key, WMGUtil.convertIntoWMIGAccessDsDateFormat(value, optFormat));
            } else if (conversionType == CV.TFAPPOINTMENT) {
                map.put(key, WMGUtil.convertIntoTFAppointmentFormat(value, optFormat));
            } else if (conversionType == CV.CUSTOMERGRANTEEDATETIME) {
                map.put(key, WMGUtil.convertIntoTFAppointmentFormat(value, optFormat));
            } else {
                throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION, "MessageTransformer.LocalUtil : save2Map : ", "Unsupported conversion type="
                        + conversionType.name());
            }
        }
    }

    private static String formatPostCode(String strPostCode) throws METBException {
        try {

            String newStrPostCode = strPostCode.replaceAll(" ", "");

            int postCodeLength = newStrPostCode.length();

            if (postCodeLength < 5 || postCodeLength > 7) {
                throw new METBException(WMGConstant.XSD_VALIDATION_FAILED_CODE, WMGConstant.BUSINESS_EXCEPTION, WMGConstant.XSD_VALIDATION_FAILED_DESC,
                        "MessageTransformer :: formatPostCode :: PostCode : \"" + newStrPostCode
                                + "\" : is not in valid format (Expected value, Outward Code should be of length 2 to 4 characters and " + "Inward Code should be of length 3 characters)");
            }
            String strInwardCode = newStrPostCode.substring(postCodeLength - 3, postCodeLength).trim();

            String strOutwardCode = newStrPostCode.substring(0, postCodeLength - 3).trim();


            String strFormattedPostCode = strOutwardCode + StringUtil.getPaddedString(4 - strOutwardCode.length()) + strInwardCode;

            return strFormattedPostCode;
        } catch (METBException me) {
            throw me;
        } catch (Exception e) {
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION, "MessageTransformer :: formatPostCode :: ", WMGUtil.getStackTrace(e));
        }
    }
}