package com.bt.metb.aggregator.dao;

import com.bt.metb.aggregator.constants.DaoConstants;
import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dataobjects.AggregationResponseDO;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import com.bt.metb.aggregator.util.WMGUtil;
import oracle.jdbc.OraclePreparedStatement;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import static com.bt.metb.aggregator.constants.DaoConstants.*;

@Repository("dao")
public class DAO implements DAOInterface {

    @Autowired
    @Qualifier("resourceManager")
    private METResourceManagerInterface resourceManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String CLASSNAME = "DAO";
    private String strMetReferenceNumber;

    public DAO() {
    }
    private static final Logger log = LoggerFactory.getLogger(DAO.class);
    @Override
    public Map<String, Object> getExistingHashData(String taskRefNum, String sourceInstanceId, String sourceAddress, Map<String, Object> outerAggregatorMap) {

        log.debug("DAO.getExistingHashData() :: taskid {} replytoContextInstanceId {} replytoAddress {} ", taskRefNum, sourceInstanceId, sourceAddress);

        Object[] params = new Object[]{taskRefNum, sourceInstanceId, sourceAddress};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};

        Map<String, Object> mapForAggregatorCore = jdbcTemplate.query(DaoConstants.SELECT_HASHDATA_QUERY, params, types, resultSet -> {
            boolean isFirst = true;
            HashMap inner;
            while (resultSet.next()) {
                if (isFirst) {
                    strMetReferenceNumber = resultSet.getString(DaoConstants.MET_REF_NUMBER);
                    isFirst = false;
                }
                String strAggregation = resultSet.getString("AGGREGATION");
                if (outerAggregatorMap.containsKey(strAggregation)) {
                    inner = (HashMap) outerAggregatorMap.get(strAggregation);
                } else {
                    inner = new HashMap();
                }
                String strKey = resultSet.getString("KEY");
                String strValue = resultSet.getString("VALUE");
                inner.put(strKey, strValue);
                outerAggregatorMap.put(strAggregation, inner);

            }
            return outerAggregatorMap;
        });

        log.debug("DAO.getExistingHashData() :: metReferenceNumber {} END ", strMetReferenceNumber);
        return mapForAggregatorCore;
    }


    @Override
    public Map<String, Object> getRequestMapWithErrorData(String taskRefNum, String aggregationName) {

        log.debug("DAO.getRequestMapWithErrorData() :: taskid {}  ", taskRefNum);
        Object[] params = new Object[]{taskRefNum, "RequestMap"};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};
        Map<String, Object> outerAggregatorMap = new HashMap<>();

        Map<String, Object> mapForAggregatorCore = jdbcTemplate.query(DaoConstants.SELECT_AGGREGATION_DATA_FOR_RESPONSE, params, types, resultSet -> {
            HashMap inner = new HashMap();
            if (StringUtils.equals(aggregationName, WMGConstant.EMPPAL)){
                inner.put("errorCode", WMGConstant.EMPPAL_TECHNICAL_ERROR_CODE);
                inner.put("errorDesc", WMGConstant.EMPPAL_TECHNICAL_ERROR_DESC);
                inner.put("errorText", WMGConstant.EMPPAL_TECHNICAL_ERROR_DESC);
            }else{
                inner.put("errorCode", WMGConstant.ORMNPL_TECHNICAL_ERROR_CODE);
                inner.put("errorDesc", WMGConstant.ORMNPL_TECHNICAL_ERROR_DESC);
                inner.put("errorText", WMGConstant.ORMNPL_TECHNICAL_ERROR_DESC);
            }
            while (resultSet.next()) {
                String strKey = resultSet.getString("KEY");
                String strValue = resultSet.getString("VALUE");
                inner.put(strKey, strValue);
                outerAggregatorMap.put("RequestMap", inner);
            }
            return outerAggregatorMap;
        });

        log.debug("DAO.getRequestMapWithErrorData() :: Existing Map {} strMetReferenceNumber {}", mapForAggregatorCore, taskRefNum);
        return mapForAggregatorCore;
    }

    @Transactional
    @Override
    public void storeAggregationDetails(String metTaskIdentifier, Map hashMap, boolean deleteFlag) throws METBException {

        int[] updateCounts = null;
        int iProcessed;
        boolean bError = false;
        final String METHOD_NAME = "storeAggregationDetails";

        String key = "";
        String innerKey = "";
        String innerValue = "";

        log.debug("DAO.storeAggregationDetails() :: TaskIdentifier ::{} hashMap {} ", metTaskIdentifier, hashMap);
        try (Connection connection = getDBConnection()) {
            if (deleteFlag) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(DaoConstants.DELETE_HASHDATA_QUERY)) {
                    preparedStatement.setString(1, metTaskIdentifier);
                    preparedStatement.executeUpdate();
                }
            }
            try (OraclePreparedStatement preparedStmt = (OraclePreparedStatement) connection.prepareStatement(DaoConstants.INSERT_HASHDATA)) {

                for (Iterator iterator = hashMap.keySet().iterator(); iterator.hasNext(); ) {
                    key = (String) iterator.next();

                    log.debug("DAO.storeAggregationDetails() aggregation name:: {} ", key);

                    Map valueHM = null;

                    if (hashMap.get(key) instanceof Map) {
                        valueHM = (HashMap) hashMap.get(key);

                        for (Iterator inneriterator = valueHM.keySet().iterator(); inneriterator.hasNext(); ) {

                            innerKey = (String) inneriterator.next();
                            innerValue = (String) valueHM.get(innerKey);

                            log.debug("DAO.storeAggregationDetails() innerKey {} innerValue {}", innerKey, innerValue);
                            preparedStmt.setString(1, metTaskIdentifier);
                            preparedStmt.setString(2, key);
                            preparedStmt.setString(3, innerKey);
                            preparedStmt.setStringForClob(4, innerValue);
                            preparedStmt.addBatch();
                        }
                    }
                }
                updateCounts = preparedStmt.executeBatch();
            }
            log.debug("DAO.storeAggregationDetails:: updateCounts:{}", updateCounts);

            if ((null != updateCounts)) {
                for (int i = 0; i < updateCounts.length; i++) {
                    iProcessed = updateCounts[i];
                    log.debug("iProcessed:{}", iProcessed);
                    if (!(iProcessed > 0 || iProcessed == -2)) {
                        bError = true;
                        break;
                    }
                }
                if (bError) {
                    log.error("EXCEPTION-OCCURRED  \nSQL-2:{} INPUT-PARAMS:  1:{} 2:{} 3:{} 4:{} \nSTACK-TRACE:{}", DaoConstants.INSERT_HASHDATA, metTaskIdentifier, key, innerKey, innerValue, "UPDATE COUNT PROBLEM");
                    throw new METBException(WMGConstant.SQL_EXCEPTION, WMGConstant.TECHNICAL_EXCEPTION, CLASSNAME
                            + WMGConstant.COLON + METHOD_NAME
                            + WMGConstant.COLON, " Error occurred while performing batch updates");
                }
            }

        } catch (SQLException exception) {
            log.error("EXCEPTION-OCCURRED  \nSQL-1:{} INPUT-PARAMS:  1:{} \nSQL-2:{} INPUT-PARAMS:  1:{} 2:{} 3:{} 4:{} \nSTACK-TRACE:", DaoConstants.DELETE_HASHDATA_QUERY, metTaskIdentifier, DaoConstants.INSERT_HASHDATA, metTaskIdentifier, key, innerKey, innerValue, exception);
            if (exception.getErrorCode() == DaoConstants.ERRORCODE_UNIQUE_CONSTRAINT_VIOLATED) {
                throw new METBException(WMGConstant.ERROR_CODE_TO_REJECT_AGGREGATION_RESPONSE, WMGConstant.BUSINESS_EXCEPTION,
                        WMGConstant.ERROR_CODE_TO_REJECT_AGGREGATION_RESPONSE_DESC, "Response is ignored");
            }

            throw new METBException(WMGConstant.SQL_EXCEPTION, WMGConstant.TECHNICAL_EXCEPTION, CLASSNAME
                    + WMGConstant.COLON + METHOD_NAME
                    + WMGConstant.COLON, WMGUtil.getStackTrace(exception));

        } catch (Exception exception) {
            log.error("EXCEPTION-OCCURRED \nSTACK-TRACE:", exception);
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                    "DAO :: storeAggregationDetails :: Unknown Exception ", WMGUtil.getStackTrace(exception));
        }

    }

    @Transactional
    @Override
    public void updateResponse(String metReferenceNumber, String respStatus) throws METBException {

        final String METHOD_NAME = "updateResponse";
        log.debug("DAO.updateResponse  : START: METREF:{}", metReferenceNumber);
        // define query arguments
        Object[] params = new Object[]{respStatus, metReferenceNumber};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};

        try {
            int noOfRows = jdbcTemplate.update(DaoConstants.UPDATE_RESPONSE_DATA_QUERY, params, types);
            log.debug("DAO.updateResponse  : Number of rows affected :{}", noOfRows);
            if (noOfRows <= 0) {
                throw new METBException(WMGConstant.SQL_EXCEPTION, WMGConstant.TECHNICAL_EXCEPTION, CLASSNAME
                        + WMGConstant.COLON + METHOD_NAME
                        + WMGConstant.COLON
                        , "-Unable to store request Status due to unknown reasons");
            }
        } catch (DataAccessException dex) {
            log.error("Data Exception occured:-  {}", dex);
        }
        log.debug("DAO.updateResponse() METRefrenceNumber: {} END: SUCCESFULLY UPDATED TASKDETAILS", metReferenceNumber);
    }

    @Transactional
    @Override
    public void updateRequestStatus(String requestStatus, Timestamp lastupdated, String metReferenceNumber) throws METBException {
        final String METHOD_NAME = WMGConstant.UPDATEREQUESTSTATUS;
        log.debug("DAO.updateRequestStatus  : START: METREF:{}", metReferenceNumber);
        // define query arguments
        Object[] params = new Object[]{requestStatus, lastupdated, metReferenceNumber};
        int[] types = new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR};

        try {
            int noOfRows = jdbcTemplate.update(DaoConstants.UPDATE_TASKDETAILS_REQUESTSTATUS_QUERY, params, types);
            log.debug("DAO.updateRequestStatus  : Number of rows affected :{}", noOfRows);
            if (noOfRows <= 0) {
                throw new METBException(WMGConstant.SQL_EXCEPTION, WMGConstant.TECHNICAL_EXCEPTION, CLASSNAME
                        + WMGConstant.COLON + METHOD_NAME
                        + WMGConstant.COLON
                        , "->Unable to store request Status due to unknown reasons");
            }
        } catch (DataAccessException dex) {
            log.error("Data Exception occured: -> {}", dex);
        }
        log.debug("DAO.updateRequestStatus() METRefrenceNumber: {} END: SUCCESFULLY UPDATED TASKDETAILS", metReferenceNumber);
    }


    @Override
    public String getValidatedXMLForMetrefId(String metRefNum) {
        String requestXMl = null;
        log.debug("DAO.getValidatedXMLForMetrefId() METRefrenceNumber: {} START", metRefNum);
        try {
            requestXMl = jdbcTemplate.queryForObject(DaoConstants.SELECT_REQUEST_XML_FOR_TASK, new Object[]{metRefNum}, String.class);

        } catch (DataAccessException dex) {
            log.error("Data Exception occured:-  {}", dex);
        }
        log.debug("DAO.getValidatedXMLForMetrefId() METRefrenceNumber: {} END", metRefNum);
        return requestXMl;
    }


    @Transactional
    @Override
    public void updateRequestStatusInITR(String requestStatus, Timestamp lastupdated, String metReferenceNumber) throws METBException {

        final String METHOD_NAME = "updateRequestStatusInITR";
        log.debug("DAO.updateRequestStatusInITR() METRefrenceNumber: {} START", metReferenceNumber);
        Object[] params = new Object[]{requestStatus, lastupdated, metReferenceNumber, WMGConstant.AGG_ITR_STATUS};
        int[] types = new int[]{Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR};

        try {
            int noOfRows = jdbcTemplate.update(DaoConstants.UPDATE_TASKDETAILS_REQUESTSTATUSITR_QUERY, params, types);
            log.debug("DAO.updateRequestStatusInITR  : Number of rows affected :{}", noOfRows);
            if (noOfRows <= 0) {
                throw new METBException(WMGConstant.SQL_EXCEPTION, WMGConstant.TECHNICAL_EXCEPTION, CLASSNAME
                        + WMGConstant.COLON + METHOD_NAME
                        + WMGConstant.COLON
                        , "-->Unable to store request Status due to unknown reasons");
            }
        } catch (DataAccessException dex) {
            log.error("Data Exception occured->  {}", dex);
        }
        log.debug("DAO.updateRequestStatusInITR() METRefrenceNumber: {} END: SUCCESFULLY UPDTAED TASKDETAILS", metReferenceNumber);
    }

    @Transactional
    @Override
    public String updateStuckRequestStatus(String metReferenceNumber) throws METBException {
        Timestamp lastupdated = null;
        final String METHOD_NAME = "updateRequestStatus";
        String status = null;
        try(Connection connection = getDBConnection(); PreparedStatement updateRequestStatusStmt = connection.prepareStatement(DaoConstants.UPDATE_STUCK_REQUEST_TASKDETAILS_DATA_QUERY);) {

            lastupdated = new Timestamp(System.currentTimeMillis());
            updateRequestStatusStmt.setTimestamp(1, lastupdated);
            updateRequestStatusStmt.setString(2, metReferenceNumber);
            int noOfRows = updateRequestStatusStmt.executeUpdate();
            if (noOfRows <= 0) {
                status = WMGConstant.FAILURE;
            } else {
                status = WMGConstant.SUCCESS;
            }
        } catch (SQLException exception) {
            log.error("EXCEPTION-OCCURRED  SQL:{} INPUT-PARAM:{} \nSTACK-TRACE:", DaoConstants.UPDATE_STUCK_REQUEST_TASKDETAILS_DATA_QUERY, metReferenceNumber, exception);
            throw new METBException(WMGConstant.SQL_EXCEPTION, WMGConstant.TECHNICAL_EXCEPTION,
                    CLASSNAME + WMGConstant.COLON + METHOD_NAME
                            + WMGConstant.COLON, WMGUtil.getStackTrace(exception));
        } catch (Exception exception) {
            log.error("EXCEPTION-OCCURRED  MET-REF-NO:{} \nSTACK-TRACE:", metReferenceNumber, exception);
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                    CLASSNAME + WMGConstant.COLON + METHOD_NAME
                            + WMGConstant.COLON, WMGUtil.getStackTrace(exception));
        }
        return status;
    }


    @Transactional
    @Override
    public void updateRequestStatus(String requestStatus, String retryStatus, String serverName, Timestamp lastupdated, String metReferenceNumber) throws METBException {
        final String METHOD_NAME = "updateRequestStatus";

        Object[] params = new Object[]{requestStatus, retryStatus, serverName, lastupdated, metReferenceNumber};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP, Types.VARCHAR};

        try {
            int updatedRowCount = jdbcTemplate.update(UPDATE_TASKDETAILS_DATA_QUERY, params, types);
            if (updatedRowCount <= 0) {
                log.error("EXCEPTION-OCCURRED \nSQL:{} \nINPUT-PARAM: 1:{} 2:{} 3:{} 4:{} 5:{} \nSTACK-TRACE:{}", DaoConstants.UPDATE_TASKDETAILS_DATA_QUERY, requestStatus, retryStatus, serverName, lastupdated, metReferenceNumber, "NO DATA");
                throw new METBException(WMGConstant.SQL_EXCEPTION, WMGConstant.TECHNICAL_EXCEPTION, CLASSNAME
                        + WMGConstant.COLON + METHOD_NAME
                        + WMGConstant.COLON
                        , "  Unable to update request Status due to unknown reasons");
            }
        } catch (Exception exception) {
            log.error("EXCEPTION-OCCURRED  MET-REF-NO:{} \nSTACK-TRACE:", metReferenceNumber, exception);
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                    "DAO:updateRequestStatus:", WMGUtil.getStackTrace(exception));
        }

    }


    @Transactional
    @Override
    public void updateAggStatusWithReqXML(Timestamp dateSystemDate, String aggName, String requestStatus, String metrefNumber, String msgID) throws METBException {
        final String METHOD_NAME = "updateAggStatusWithReqXML";

        log.debug("DAO.updateAggStatusWithReqXML() METRefrenceNumber: {} START", metrefNumber);
        Object[] params = new Object[]{dateSystemDate, aggName, requestStatus, msgID, metrefNumber};
        int[] types = new int[]{Types.TIMESTAMP, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};

        try {
            int noOfRows = jdbcTemplate.update(DaoConstants.UPDATE_AGG_STATUS_WITH_REQUEST_XML, params, types);
            log.debug("DAO.updateAggStatusWithReqXML  : Number of rows affected :{}", noOfRows);
            if (noOfRows <= 0) {
                throw new METBException(WMGConstant.SQL_EXCEPTION, WMGConstant.TECHNICAL_EXCEPTION, CLASSNAME
                        + WMGConstant.COLON + METHOD_NAME
                        + WMGConstant.COLON
                        , "  Unable to store request Status due to unknown reasons");
            }
        } catch (DataAccessException dex) {
            log.error("Data Exception occured--  {}", dex);
        }
        log.debug("DAO.updateAggStatusWithReqXML() METRefrenceNumber: {} END: SUCCESFULLY UPDTAED TASKDETAILS", metrefNumber);
    }


    @Transactional
    public void updateAggReqXML(String aggName, String metrefNumber) {

        log.debug("DAO.updateAggReqXML  : START: METREF:{}", metrefNumber);
        try {
            int row;
            Integer count = jdbcTemplate.queryForObject(DaoConstants.SELECT_ROW_EXISTS, Integer.class, metrefNumber);

            if (count > 0) {
                Object[] paramsForUpdate = new Object[]{metrefNumber, aggName};
                int[] typesForUpdate = new int[]{Types.VARCHAR, Types.VARCHAR};
                row = jdbcTemplate.update(DaoConstants.UPDATE_AGG_MESSAGE_XML_NULL, paramsForUpdate, typesForUpdate);
                log.debug("DAO.updateAggReqXML  : Number of rows Updated :{}", row);
            } else {
                log.debug("DAO.updateAggReqXML  : NO DATA FOUND");
            }

        } catch (DataAccessException dex) {
            log.error("Data Exception occured::  {}", dex);
        }
        log.debug("DAO.updateAggReqXML  : END: METREF:{}", metrefNumber);
    }

    @Transactional
    public void insertAggReqXML(String requestXML, String aggName, String metrefNumber, String messageType) {
        log.debug("DAO.insertAggReqXML : Inserting TASKDETAILS_AGG_MESSAGE");
        log.debug("DAO.insertAggReqXML : metref {}", metrefNumber);
        Timestamp insertedDateTime = new Timestamp(System.currentTimeMillis());
        Object[] args = new Object[]{metrefNumber, aggName, messageType, requestXML, insertedDateTime};
        int[] argsTypes = new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.CLOB, Types.TIMESTAMP};
        try {
            jdbcTemplate.update(DaoConstants.INSERT_TASKDETAILS_AGG_MESSAGE, args, argsTypes);
        } catch (Exception ex) {
            log.debug("Exception occurred while inserting into TASKDETAILS_AGG_MESSAGE");
            log.error("Exception ::", ex);
        }
    }

    @Transactional
    @Override
    public void updateServerLookupLastProcessdate(String serverName) throws METBException {
        final String METHOD_NAME = "updateServerLookupLastProcessdate";
        Timestamp lastupdated = null;

        try {
            lastupdated = new Timestamp(System.currentTimeMillis());
            Object[] params = new Object[]{lastupdated, serverName};
            int[] types = new int[]{Types.TIMESTAMP, Types.VARCHAR,};

            int noOfRows = jdbcTemplate.update(DaoConstants.UPDATE_SERVERLOOKUP_RECENT_DATETIME_QUERY, params, types);
            log.debug("DAO.updateServerLookupLastProcessdate  : Number of rows affected :{}", noOfRows);
            if (noOfRows <= 0) {
                throw new METBException(WMGConstant.SQL_EXCEPTION, WMGConstant.TECHNICAL_EXCEPTION, CLASSNAME
                        + WMGConstant.COLON + METHOD_NAME
                        + WMGConstant.COLON
                        , "  Unable to update retry status due to unknown reasons");
            }
        } catch (DataAccessException dex) {
            log.error("DAO.updateServerLookupLastProcessdate ::Data Exception occured:  {}", dex);
        }
    }

    @Transactional
    @Override
    public void deleteAggregationSpecificData(String metreferencenumber, String aggName) throws METBException {

        final String METHOD_NAME = "deleteAggregationSpecificData";
        log.debug("DAO.deleteAggregationSpecificData() METRefrenceNumber: {} START: ", metreferencenumber);
        Object[] params = new Object[]{metreferencenumber, aggName};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};

        try {
            int noOfRows = jdbcTemplate.update(DELETE_AGGREGATION_SPECIFIC_QUERY, params, types);
            log.debug("DAO.deleteAggregationSpecificData  : Number of rows affected :{}", noOfRows);
            if (noOfRows <= 0) {
                throw new METBException(WMGConstant.SQL_EXCEPTION, WMGConstant.TECHNICAL_EXCEPTION, CLASSNAME
                        + WMGConstant.COLON + METHOD_NAME
                        + WMGConstant.COLON
                        , "  Unable to store request Status due to unknown reasons");
            }
        } catch (DataAccessException ex) {
            log.error("Data Exception occured:  {}", ex);
        }
        log.debug("DAO.deleteAggregationSpecificData() METRefrenceNumber: {} END: SUCCESFULLY deleted AGGREGATION_DATA ", metreferencenumber);
    }



    @Override
    public AggregationResponseDO getAggregationResponseDetailsObject(String messageId) throws METBException {
        log.debug("DAO.getAggregationResponseDetailsObject() :: START:messageId {} ",messageId);
        String sql = "{call pkg_met_aggregation.proc_aggregation_requirement(?,?,?)}";
        AggregationResponseDO aggregationDetails = null;
        String requirementBusiness;
        String requirementTechnical;
        String metReferenceNumber = null;

        List<SqlParameter> parameters = Arrays.asList(
                new SqlParameter("v_met_ref_number", Types.VARCHAR),
                new SqlOutParameter("v_business", Types.VARCHAR),
                new SqlOutParameter("v_technical", Types.VARCHAR));

        Object[] params = new Object[]{messageId};
        int[] types = new int[]{Types.VARCHAR};

        Map<String, Object> queryForMap = jdbcTemplate.queryForMap(SELECT_AGGREGATION_RESPONSE_DETAIL_OBJ_QUERY, params, types);

        if (queryForMap.size() != 0) {
            aggregationDetails = new AggregationResponseDO();
            aggregationDetails.setReqStatus((String) queryForMap.get(DaoConstants.REQUEST_STATUS));
            aggregationDetails.setTaskRefNumber((String) queryForMap.get(DaoConstants.TASK_REF_NUMBER));
            aggregationDetails.setMetRefNumber((String) queryForMap.get(DaoConstants.MET_REF_NUMBER));
            aggregationDetails.setProduct((String) queryForMap.get(DaoConstants.PRODUCT));
            aggregationDetails.setAggregationName((String) queryForMap.get(DaoConstants.AGG_NAME));
            aggregationDetails.setTaskCategory((String) queryForMap.get(DaoConstants.TASK_CATEGORY));
            aggregationDetails.setLastUpdateDate((Timestamp) queryForMap.get(DaoConstants.LASTUPDATED));
            aggregationDetails.setLatestTaskOperation((String) queryForMap.get(DaoConstants.LATEST_OPERATION));
            aggregationDetails.setE2eData((String) queryForMap.get(DaoConstants.E2E_DATA));
            aggregationDetails.setReplyTo((String) queryForMap.get(DaoConstants.REPLY_TO));
            aggregationDetails.setLob((String) queryForMap.get(DaoConstants.LOB));
            aggregationDetails.setSmntRetryCount((String) queryForMap.get(DaoConstants.SMNT_RETRY_COUNT));
            aggregationDetails.setSourceAddress((String) queryForMap.get(DaoConstants.SOURCE_ADDRESS));
            aggregationDetails.setSourceInstanceId((String) queryForMap.get(DaoConstants.SOURCE_INSTANCE_ID));
            metReferenceNumber = aggregationDetails.getMetRefNumber();
            Map<String, Object> map = jdbcTemplate.call(connection -> {
                CallableStatement callableStatement = connection.prepareCall(sql);
                callableStatement.setString(1, (String) queryForMap.get(DaoConstants.MET_REF_NUMBER));
                callableStatement.registerOutParameter(2, Types.VARCHAR);
                callableStatement.registerOutParameter(3, Types.VARCHAR);
                return callableStatement;
            }, parameters);
            requirementBusiness = (String) map.get("v_business");
            requirementTechnical = (String) map.get("v_technical");
            aggregationDetails.setBusinessExceptionRequirement(requirementBusiness);
            aggregationDetails.setTechnicalExceptionRequirement(requirementTechnical);
            aggregationDetails.setMessageIdNotFound(false);
            log.debug("DAO.getAggregationResponseDetailsObject() METRefNum: {} :requirementBusiness:: {} ," +
                    "requirementTechnical:: {}", metReferenceNumber, requirementTechnical, requirementTechnical);
        } else {
            aggregationDetails = new AggregationResponseDO();
            aggregationDetails.setMessageIdNotFound(true);
        }

        log.debug("DAO.getAggregationResponseDetailsObject() METRefNum: {} END {}", metReferenceNumber, aggregationDetails);
        return aggregationDetails;
    }


    @Transactional
    @Override
    public void storeAggregationLog(String metRefNumber, String errorCode, String errorDesc, String errorText) throws METBException {

        Object[] params = new Object[]{metRefNumber, errorCode, errorDesc, errorText};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.CLOB};

        try {
            int noOfRows = jdbcTemplate.update(INSERT_AGGREGATION_LOG, params, types);
            log.debug("DAO.updateStuckRetryRequest  : Number of rows affected :{}", noOfRows);
            if (noOfRows <= 0) {
                log.debug("DAO.storeAggregationLog::NO ROWS UPDATED for MET_REF_NUMBER::{}", metRefNumber);
            }
        } catch (DataAccessException dex) {
            log.error("DAO.storeAggregationLog ::Data Exception occured:  {}", dex);
        }
    }

    public Connection getDBConnection() throws SQLException, METBException{
        DataSource dataSource = resourceManager.getDataSource(ORMETB_DATASOURCE);
        log.debug(" DAO.storeDetails() :: datasource {}", dataSource);
        Connection newConnection = null;
        try {
            newConnection = dataSource.getConnection();
            log.debug(" DAO.storeDetails() :: newConnection {}", newConnection);
            newConnection.setAutoCommit(false);
        } catch (Exception exception) {
            log.error("EXCEPTION OCURRED STACK-TRACE", exception);
            throw exception;
        }
        return newConnection;
    }

    @Transactional
    @Override
    public String updateStuckRetryRequest(Timestamp dateSystemDate, int aggregationRetryCount, String metReferenceNumber) throws METBException {
        final String METHOD_NAME = "updateStuckRetryRequest";
        String status = null;
        int param2 = aggregationRetryCount + 1;
        Object[] params = new Object[]{dateSystemDate, param2, WMGConstant.AGG_REQ_STATUS, metReferenceNumber};
        int[] types = new int[]{Types.TIMESTAMP, Types.INTEGER, Types.VARCHAR, Types.VARCHAR,};

        try {
            int noOfRows = jdbcTemplate.update(UPDATE_STUCK_RETRY_REQUEST, params, types);
            log.debug("DAO.updateStuckRetryRequest  : Number of rows affected :{}", noOfRows);
            if (noOfRows <= 0) {
                status = "FAILURE";
            } else {
                status = "UPDATED_SUCCESS";
            }
        } catch (DataAccessException dex) {
            log.error("DAO.updateStuckRetryRequest ::Data Exception occured:  {}", dex);
        } catch (Exception exception) {
            log.error("EXCEPTION-OCCURRED MET-REF-NO:{} \nSTACK-TRACE:", metReferenceNumber, exception);
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                    CLASSNAME + WMGConstant.COLON + METHOD_NAME
                            + WMGConstant.COLON
                    , WMGUtil.getStackTrace(exception));
        }
        return status;

    }
}
