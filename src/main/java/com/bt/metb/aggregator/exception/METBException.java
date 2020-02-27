package com.bt.metb.aggregator.exception;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.util.StringUtil;

public class METBException extends Exception {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 5433674399669019127L;

/**
 * Creates a METException object using the erroCode, errorType and errorText as parameters 
 * @param errorCode : Error code
 * @param errorType : Type of error
 * @param errorDescription : Error description 
 * @param errorText : Error text
 * @throws com.bt.wmgateway.util.METBException : Raise when METException occurred
 */
	
	//Added as part of refactoring METB : gaurav govilkar : 15th April 2013
	
	public METBException() {
		super();
	}

	
	public METBException(String errorCode, String errorType, String errorDescription, String errorText) throws METBException {

		if (StringUtil.isNullOrEmpty(errorCode) || StringUtil.isNullOrEmpty(errorType) || StringUtil.isNullOrEmpty(errorDescription) || StringUtil.isNullOrEmpty(errorText)) {
			throw new METBException(WMGConstant.MET_NULL_KEY_EXC_ESMF,WMGConstant.TECHNICAL_EXCEPTION,
					"METException :: METException",WMGConstant.NULL_KEY_EXC_ESMF_MSG);
		}

		this.errorCode = errorCode;
		this.errorType = errorType;
		this.errorDescription = errorDescription;
		this.errorText = errorText;

	}
	
	
	//removing constructor with accept extra exception parameter but did not use it. It calls back the other constructor
	
	//constructor for wrapping exception.
	public METBException(String errorCode, String errorType, String errorDescription, Throwable t) throws METBException {
		super(t);

		if (StringUtil.isNullOrEmpty(errorCode) || StringUtil.isNullOrEmpty(errorType) || StringUtil.isNullOrEmpty(errorDescription) ) {
			throw new METBException(WMGConstant.MET_NULL_KEY_EXC_ESMF,WMGConstant.TECHNICAL_EXCEPTION,
					"METException :: METException",WMGConstant.NULL_KEY_EXC_ESMF_MSG);
		}

		this.errorCode = errorCode;
		this.errorType = errorType;
		this.errorDescription = errorDescription;
		
		
		

	}
	
	
	/**
	 * 
	 * @return errorCode
	 */

	public String getErrorCode() {
		return errorCode;

	}

	/**
	 * 
	 * @return errorType
	 */

	public String getErrorType() {
		return errorType;

	}
	/**
	 * 
	 * @return errorText
	 */
	public String getErrorText() {
		return errorCode+" :: "+errorText;

	}

	/**
	 * 
	 * @return errorDescription : Error description
	 */
	public String getErrorDescription() {
		return errorDescription;
	}

	protected void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


	protected void setErrorType(String errorType) {
		this.errorType = errorType;
	}


	protected void setErrorText(String errorText) {
		this.errorText = errorText;
	}


	protected void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	/**
	 * Error code that describes the error 
	 */

	private String errorCode;

	/**
	 * Error Type (Business/Technical) 
	 */

	private String errorType;

	/**
	 * Error Text that will have the description of the error 
	 */

	private String errorText;
	
	private String errorDescription;

}
