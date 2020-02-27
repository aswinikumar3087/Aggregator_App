package com.bt.metb.aggregator.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class StringUtil
{

    public static final String COMMA_STRING = ",";
    public static final String EMPTY_STRING = "";
    
    /**
     * This is a class with static Utility method. Constructor is made private to suppress initialisation and creation of unwanted objects
     */
    private StringUtil(){}
    
	/**
     * Converts , separated string into a string array
     * @param stringNodeCategoryList
     * @return
     */
    public static String[] getStringArrayFromCsv(String stringNodeCategoryList)
    {
        return stringNodeCategoryList.split(COMMA_STRING);
    }


   /**
     * Converts , separated string to a string ArrayList
     * @param stringNodeCategoryList
     * @return
     */
    public static List<String> getStringArrayListFromCsv(String stringNodeCategoryList)
    {
        return Arrays.asList(stringNodeCategoryList.split(COMMA_STRING));
    }
    
    /**
     * Returns true for a null or empty string
     * @param obj
     * @return
     */
    public static boolean isNullOrEmpty(String str)
    {
    	return (str==null || str.trim().length()==0);
    }

    public static boolean isNullOrEmptyArrayElements(String... elements){
        if (elements!=null){
            for (String element:elements) {
                if(isNullOrEmpty(element)){
                    return true;
                }
            }
        }else{
            return true;
        }
        return false;
    }
    
    /**
     * Returns empty string is the parameter is null, otherwise the same string is returned
     * 
     * @param str
     * @return
     */
    public static String returnEmptyIfNull(String str) {
    	return (str==null)?EMPTY_STRING:str;
    }
    
    /**
     * Returns true if the comma separated string contains the searchStr
     * @param csvString
     * @param searchStr
     * @return
     */
    public static boolean isValuePresentInCSVString(String csvString, String searchStr)
    {
		if (!StringUtil.isNullOrEmpty(csvString)) 
		{
			StringTokenizer strTokens = new StringTokenizer(csvString, COMMA_STRING);
			while (strTokens.hasMoreTokens()) {
		        if(strTokens.nextToken().equals(searchStr)) 
		        {		                
		        	return true;
		        }
		    }
		}
		return false;
    }
    
    /**
     * This method creates a padded String.
     * 
     * @param noOfPads
     *            - Number of pads("") required
     * @return The paddedString
     */
    public static String getPaddedString(int noOfPads) {
        StringBuilder paddedString = new StringBuilder();
        for (int i = 1; i <= noOfPads; i++) {
            paddedString.append(" ");
        }
        return paddedString.toString();
    }
    
    public static String getCurrentDateTime(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String removeSingleQuotes(String valueWithQuote) {
        String valueWithOutQuote = null;

        if (valueWithQuote != null) {
            valueWithOutQuote = valueWithQuote.substring(1,valueWithQuote.length() - 1);
        }
        return valueWithOutQuote;
    }
    
   public static int getCommomIndex(String indate) {        
        int commomIndex = 0;
        
        if (indate.indexOf('.') > -1) {
            commomIndex = indate.indexOf('.');
        }
        if (indate.indexOf('-') > -1) {
            commomIndex = indate.indexOf('-');
        }

        if (indate.indexOf('+') > -1) {
            commomIndex = indate.indexOf('+');
        }
        if (indate.indexOf('Z') > -1) {
            commomIndex = indate.indexOf('Z');
        }
        
        return commomIndex;
    }
   /**
    * This method is used to remove the characters like ":"," ","-" and "."
    * from the strTime.
    * 
    * @param strTime
    *            String value to be used for transformation
    * @return formated date
    * @author Neha Jain
    */

   public static String dateFormation(String strTime) {
       String newStrTime = strTime;
       if (newStrTime.length() > 0) {
           newStrTime = newStrTime.replaceAll(":", "");
           newStrTime = newStrTime.replaceAll(" ", "");
           newStrTime = newStrTime.replaceAll("-", "");
           newStrTime = newStrTime.replaceAll("\\.", "");
           newStrTime = newStrTime.substring(3);
       }
       return newStrTime;
   }

    public static boolean isNotNull(Object param) {

        if(param == null){
            return false;
        }else{
            if (param instanceof String) {
                return ((String) param).length() != 0 && !((String) param).trim().equals("");
            }
        }

        return true;
    }

}
