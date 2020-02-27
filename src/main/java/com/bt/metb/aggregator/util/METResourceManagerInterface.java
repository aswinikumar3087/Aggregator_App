package com.bt.metb.aggregator.util;

import com.bt.metb.aggregator.exception.METBException;

import javax.sql.DataSource;

public interface METResourceManagerInterface {


	String getProperty(String strPropertyFileName, String strKey) throws METBException;

	String getManagedServer()throws METBException;

    DataSource getDataSource(String dataSourceName) throws METBException;
}
