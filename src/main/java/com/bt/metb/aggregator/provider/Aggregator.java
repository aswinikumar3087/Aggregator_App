package com.bt.metb.aggregator.provider;


import java.util.Map;

public interface Aggregator {
     Map executeDelegate(Map hMapOuterAggregator, String actionValue, String metRefNumber, String validatedXML) throws Exception;
}
