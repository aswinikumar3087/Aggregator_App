/*
 * Project: Field Data Enhancement
 * Module: WMG Business Exception
 * Filename: WMGBusinessException.java
 * Prepared By: K.Dhanasekaran (dhanak@mahindrabt.com)
 * Description: This exception will be thrown in case of any J2EE or SQL Exceptions. (Java exceptions, in general)
 * Copyright (c) 2004 BT
 *
 * MODIFICATION HISTORY
 * Version         Date            Author                    Remarks
 * -------------------------------------------------------------------------
 * 1.0             06/10/04        K.Dhanasekaran            Initial Version
 * 1.0             14/12/04        Raju Desai                 - throws NULL_KEY exception in case of null/"" input  parameters
 * 1.0            15/12/04        K.Dhanasekaran            -  getJMErrorMessage() throws WMGException
 * -------------------------------------------------------------------------
 */
package com.bt.metb.aggregator.util;

import com.bt.metb.aggregator.constants.WMGConstant;


public class WMGTechnicalException
    extends WMGException
{
	private static final long serialVersionUID = 1L;

	public WMGTechnicalException(String strJobManagerErrorCode, String strESMFErrorCode, String strESMFErrorMessage){
		super(strJobManagerErrorCode, strESMFErrorCode, strESMFErrorMessage);
		setErrorType(WMGConstant.TECHNICAL_EXCEPTION);
	}

}
