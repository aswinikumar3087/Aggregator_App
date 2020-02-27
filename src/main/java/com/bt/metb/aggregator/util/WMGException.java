/*
 * Project: Field Data Enhancement
 * Module: WMG Exception
 * Filename: WMGException.java
 * Prepared By: K.Dhanasekaran (dhanak@mahindrabt.com)
 * Description: WMGException is super class for all exceptions raised in WorkManager Gateway
 * Copyright (c) 2004 BT
 *
 * MODIFICATION HISTORY
 * Version 		Date			Author					Remarks
 * -------------------------------------------------------------------------
 * 1.0 			01/12/2004		K.Dhanasekaran			Initial Version
 *  			10/12/2004		Prajakta Mahajan 		-createErrorXML now gives call to createErrorResponseXML of MT.
 * 	 			14/12/2004		Raju Desai 				- throws NULL_KEY exception in case of null/"" input  parameters
 * 				15/12/2004		K.Dhanasekaran			- prtinStackTrace() method calls are removed getJMErrorMessage() throws WMGException
 *				16/12/2004 		K.Dhanasekaran/Raju  	- Beautification
 *														- Audit Logging for exceptions being caught
 *				16/12/2004		Prajakta Mahajan        - In createErrorXML() method where a call to mt.createErrorResponseXML now passed added parameter called rootElement.
 *				17/12/2004      Prajakta Mahajan		- In 3 parameter constructor changed the code so as to return exception to the caller if any of the parameters are null.
 *				22/12/2004		Prajakta Mahajan		- Initialised String resXML to null in createErrorXML() method.
 *				22/12/2004		Prajakta Mahajan		- Modified the createErrorXML() method to accept response messages as sub type also.
 *				23/12/2004		Raju Desai				- WMGException and  getJMErrorCode rethrows Exceptions insteading of logging earlier
 *				12/01/2005		K.Dhanasekaran			- Debug messages are removed from Constructors.
 *				11/03/2005		K.Dhanasekaran			- ICR006 implemented. (WMG_DOMAIN_ID replaced MS_ID)
 *				07/04/2005		K.Dhanasekaran			- Debug messages removed.
 * 2.0			29/04/2005		K.Dhanasekaran			- getJMErrorText method updated to read JM Error text based on faliure location
 * 
 * 3.0			29/12/2008		Sameer Sawargaonkar		- Modified code for EWOCS change of taskStatusUpdateRequest.
 * 3.1			27/07/2009		Khushboo Chhabra		- Added createErrorXMLForHPD(EAIHeader,String,String,String) to resolve HPD issue   		 		 
 * -------------------------------------------------------------------------
 */

package com.bt.metb.aggregator.util;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.exception.METBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
public class WMGException extends METBException {


	/**
	 * Job Manager Error code
	 */
	private String strJMErrorCode;

	/**
	 * ESMF Error code
	 */
	private String strESMFErrorCode;

	/**
	 * ESMF Error Message
	 */
	private String strESMFErrorMessage;

	/**
	 * Failure Location Indicator
	 */
	private String strFailureLocation;

	@Autowired
	private Environment environment;

	/**
	 * @return the strJMErrorCode
	 */
	public String getStrJMErrorCode() {
		return strJMErrorCode;
	}


	/**
	 * @return the strESMFErrorCode
	 */
	public String getStrESMFErrorCode() {
		return strESMFErrorCode;
	}


	/**
	 * @return the strESMFErrorMessage
	 */
	public String getStrESMFErrorMessage() {
		return strESMFErrorMessage;
	}


	/**
	 * @return the strFailureLocation
	 */
	public String getStrFailureLocation() {
		return strFailureLocation;
	}


	/**
	 * @param strJMErrorCode the strJMErrorCode to set
	 */
	public void setStrJMErrorCode(String strJMErrorCode) {
		this.strJMErrorCode = strJMErrorCode;
	}


	/**
	 * @param strESMFErrorCode the strESMFErrorCode to set
	 */
	public void setStrESMFErrorCode(String strESMFErrorCode) {
		this.strESMFErrorCode = strESMFErrorCode;
	}


	/**
	 * @param strESMFErrorMessage the strESMFErrorMessage to set
	 */
	public void setStrESMFErrorMessage(String strESMFErrorMessage) {
		this.strESMFErrorMessage = strESMFErrorMessage;
	}


	/**
	 * @param strFailureLocation the strFailureLocation to set
	 */
	public void setStrFailureLocation(String strFailureLocation) {
		this.strFailureLocation = strFailureLocation;
	}


	/**
	 * Default Constructor
	 */
	public WMGException() {
		super();
	}


	/**
	 * Constructor with three arguments. It takes WMGateway Managed server name
	 * as FailureLocation
	 *
	 * @param strJobManagerErrorCode This Job Manager error code will be mapped to get appropriate
	 *                               Job Manager error message.
	 * @param strESMFErrorCode       This Code will be mapped to get the ESMF Logging Error Text
	 * @param strESMFErrorMessage    This will be the error message returned by JVM.
	 */
	public WMGException(String strJobManagerErrorCode, String strESMFErrorCode,
						String strESMFErrorMessage) {
		this.strJMErrorCode = strJobManagerErrorCode;
		this.strESMFErrorCode = strESMFErrorCode;
		this.strESMFErrorMessage = strESMFErrorMessage;
		try {
			this.strFailureLocation = environment.getProperty(WMGConstant.WMG_DOMAIN_ID);
		} catch (Exception wmge) {
			this.strFailureLocation = WMGConstant.WMG_DEFAULT_FAILURE_LOCATION;
		} finally {
			setErrorCode(strESMFErrorCode);
			setErrorText(strESMFErrorMessage);
			setErrorDescription(strJobManagerErrorCode + " :: " + this.strFailureLocation);
		}
	}


	/**
	 * Constructor with all four arguments.
	 *
	 * @param strJobManagerErrorCode This Job Manager error code will be mapped to get appropriate
	 *                               Job Manager error message.
	 * @param strESMFErrorCode       This Code will be mapped to get the ESMF Logging Error Text
	 * @param strESMFErrorMessage    This will be the error message returned by JVM.
	 * @param strFailureLocation     This will be the name where the exception originates
	 */
	public WMGException(String strJobManagerErrorCode, String strESMFErrorCode,
						String strESMFErrorMessage, String strFailureLocation)
			throws WMGException {

		this.strJMErrorCode = strJobManagerErrorCode;
		this.strESMFErrorCode = strESMFErrorCode;
		this.strESMFErrorMessage = strESMFErrorMessage;
		this.strFailureLocation = strFailureLocation;
		setErrorCode(strESMFErrorCode);
		setErrorText(strESMFErrorMessage);
		setErrorDescription(strJobManagerErrorCode + " :: " + strFailureLocation);
	}
}