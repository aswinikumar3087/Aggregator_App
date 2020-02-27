package com.bt.metb.aggregator.dao;

import com.bt.metb.aggregator.dataobjects.AggregationResponseDO;
import com.bt.metb.aggregator.dataobjects.TaskDetailsDO;
import com.bt.metb.aggregator.exception.METBException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;


 public interface DAOInterface {

     Connection getDBConnection() throws METBException, SQLException;

     Map<String,Object> getExistingHashData(String taskid, String replytoContextInstanceId, String replytoAddress, Map<String,Object> outerAggregatorMap) throws METBException;

     void storeAggregationDetails(String taskIdentifier, Map hashMap, boolean deleteFlag) throws METBException;

     void updateResponse(String metReferenceNumber, String respStatus) throws METBException;

     void updateRequestStatus(String requestStatus, Timestamp lastupdated, String metReferenceNumber) throws METBException;

     void storeAggregationLog(String metRefNumber, String errorCode, String errorDesc, String errorText) throws METBException;

     void insertAggReqXML(String requestXML, String aggName, String metrefNumber, String messageType) throws METBException;

     void updateRequestStatusInITR(String requestStatus, Timestamp lastupdated, String metReferenceNumber) throws METBException;

     String updateStuckRequestStatus(String metReferenceNumber) throws METBException;

     void updateRequestStatus(String requestStatus, String retryStatus, String serverName, Timestamp lastupdated, String metReferenceNumber) throws METBException;

     String updateStuckRetryRequest(Timestamp dateSystemDate, int aggregationRetryCount, String metReferenceNumber) throws METBException;

     void updateAggStatusWithReqXML(Timestamp dateSystemDate, String aggName, String requestStatus, String metrefNumber, String msgID) throws METBException;

     void updateServerLookupLastProcessdate(String serverName) throws METBException;

     void deleteAggregationSpecificData(String metreferencenumber, String aggName) throws METBException;

     AggregationResponseDO getAggregationResponseDetailsObject(String messageId) throws METBException;

     String getValidatedXMLForMetrefId(String metRefNum) throws METBException;

     void updateAggReqXML(String aggName, String metrefNumber);

     Map<String, Object> getRequestMapWithErrorData(String taskRefNum, String aggregationName);

}