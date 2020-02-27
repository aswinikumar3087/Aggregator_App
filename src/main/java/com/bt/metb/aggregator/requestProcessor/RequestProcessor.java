package com.bt.metb.aggregator.requestProcessor;

import com.bt.metb.aggregator.exception.METBException;


import java.util.Map;

public interface RequestProcessor {

	public Map executeDelegate(Map outerAggregator, String actionType, String metRefNumber, String validatedXML) throws METBException;

}
