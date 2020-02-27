/*
* @author-aswini.kumarparida@bt.com
* */
package com.bt.metb.aggregator.util;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dataobjects.AggregationMainDBSchema;
import com.bt.metb.aggregator.exception.METBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class DBLoading {

    private static Map hashMapSmntAggregator = new HashMap();

    private static Map hashMapSrcSystemAggregator = new HashMap();

    private static Map hashMapAggDataAggregator = new HashMap();

    private static Map hashMapAggMasterDataAggregator = new HashMap();

    private static final Logger logger = LoggerFactory.getLogger(DBLoading.class);

    @Autowired
    @Qualifier("resourceManager")
    private METResourceManagerInterface resourceManagerInterface;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void loadDB() throws METBException {

        try {
            logger.debug("DBLoading.loadDB() in loadDB");
            getMainDataFromDB();
            getAggregationDataFromDB();
            getSourceSystemFromDB();

        } catch (Exception exception) {
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                    "Unknown Exception :: DBLoading ", WMGUtil.getStackTrace(exception));
        }
    }

    private void getMainDataFromDB() {
        String mainDataQuery = "Select PRODUCT,TASKCATEGORY,AGGREGATIONID," +
                "WMSTASKCATEGORY,SORCEFORWMSLOGIC,WMSCUSTOMERTYPE,MAINWORKLOCATION," +
                "TASKTYPEID,SKILLCODE,TASKDURATION,RESPONSECODE,WORKLOCATIONQUALIFIER," +
                "LOB,ENABLED,ISSANITY from AGGREGATIONMASTER";

        List<Map<String, Object>> aggregationMasterList = jdbcTemplate.queryForList(mainDataQuery);
        AggregationMainDBSchema mainObjModel;
        for (Map<String, Object> aggregationMaster : aggregationMasterList) {
            mainObjModel = new AggregationMainDBSchema();
            mainObjModel.setProductName(String.valueOf(aggregationMaster.get("PRODUCT")));
            mainObjModel.setTaskCategoryName(String.valueOf(aggregationMaster.get("TASKCATEGORY")));
            mainObjModel.setAggregationId(String.valueOf(aggregationMaster.get("AGGREGATIONID")));
            mainObjModel.setWmsTaskCategory(String.valueOf(aggregationMaster.get("WMSTASKCATEGORY")));
            mainObjModel.setSourceForWMSLogic(String.valueOf(aggregationMaster.get("SORCEFORWMSLOGIC")));
            mainObjModel.setWmsCustomerType(String.valueOf(aggregationMaster.get("WMSCUSTOMERTYPE")));
            mainObjModel.setMainWorkLocation(String.valueOf(aggregationMaster.get("MAINWORKLOCATION")));
            mainObjModel.setTaskTypeId(String.valueOf(aggregationMaster.get("TASKTYPEID")));
            mainObjModel.setSkillCode(String.valueOf(aggregationMaster.get("SKILLCODE")));
            mainObjModel.setTaskDuration(String.valueOf(aggregationMaster.get("TASKDURATION")));
            mainObjModel.setResponseCode(String.valueOf(aggregationMaster.get("RESPONSECODE")));
            mainObjModel.setWorkLocationQualifier(String.valueOf(aggregationMaster.get("WORKLOCATIONQUALIFIER")));
            mainObjModel.setLob(String.valueOf(aggregationMaster.get("LOB")));
            mainObjModel.setEnabled(String.valueOf(aggregationMaster.get("ENABLED")));
            mainObjModel.setSanityCheck(String.valueOf(aggregationMaster.get("ISSANITY")));
            String strKey = mainObjModel.getProductName() + ":" + mainObjModel.getTaskCategoryName() + ":" + mainObjModel.getLob();
            hashMapAggMasterDataAggregator.put(strKey, mainObjModel);
        }
    }

    private void getAggregationDataFromDB() {

        String strKey;
        String aggregationId;
        String aggregationName;
        String requestType;
        String aggSequence;
        String requirementBusiness;
        String requirementTechnical;
        String interfaceProtocol;
        HashMap inner;
        HashMap outer;

        String aggregationDataQuery = "Select AGGREGATION_ID,AGGREGATION_NAME," +
                "REQUEST_TYPE,AGG_SEQUENCE,REQUIREMENT_BUSINESS,REQUIREMENT_TECHNICAL," +
                "INTERFACE_PROTOCOL from AGGREGATION";
        List<Map<String, Object>> aggregationdataList = jdbcTemplate.queryForList(aggregationDataQuery);
        for (Map<String, Object> aggregationData : aggregationdataList) {
            aggregationId = String.valueOf(aggregationData.get("AGGREGATION_ID"));
            aggregationName = String.valueOf(aggregationData.get("AGGREGATION_NAME"));
            requestType = String.valueOf(aggregationData.get("REQUEST_TYPE"));
            aggSequence = String.valueOf(aggregationData.get("AGG_SEQUENCE"));
            requirementBusiness = String.valueOf(aggregationData.get("REQUIREMENT_BUSINESS"));
            requirementTechnical = String.valueOf(aggregationData.get("REQUIREMENT_TECHNICAL"));
            interfaceProtocol = String.valueOf(aggregationData.get("INTERFACE_PROTOCOL"));
            strKey = aggregationId + ":" + requestType;
            if (hashMapAggDataAggregator.containsKey(strKey)) {
                outer = (HashMap) hashMapAggDataAggregator.get(strKey);
            } else {
                outer = new HashMap();
            }
            inner = new HashMap();
            inner.put(WMGConstant.AGGREGATIONTYPE, aggregationName);
            inner.put(WMGConstant.REQUIREMENT_BUSINESS, requirementBusiness);
            inner.put(WMGConstant.REQUIREMENT_TECHNICAL, requirementTechnical);
            inner.put(WMGConstant.INTERFACE_PROTOCOL, interfaceProtocol);
            outer.put(aggSequence, inner);
            hashMapAggDataAggregator.put(strKey, outer);
        }


    }

	/*private void getMainDataFromDB() throws SQLException, METBException {
		String mainDataQuery = "Select PRODUCT,TASKCATEGORY,AGGREGATIONID," +
				"WMSTASKCATEGORY,SORCEFORWMSLOGIC,WMSCUSTOMERTYPE,MAINWORKLOCATION," +
				"TASKTYPEID,SKILLCODE,TASKDURATION,RESPONSECODE,WORKLOCATIONQUALIFIER," +
				"LOB,ENABLED,ISSANITY from AGGREGATIONMASTER";

		AggregationMainDBSchema mainObjModel;
		try(Connection connection = getDataSourceConnection();PreparedStatement pstmt = connection.prepareStatement(mainDataQuery)){
			try(ResultSet rs = pstmt.executeQuery();){
				while(rs.next()){
					mainObjModel = new AggregationMainDBSchema();
					mainObjModel.setProductName(rs.getString("PRODUCT"));
					mainObjModel.setTaskCategoryName(rs.getString("TASKCATEGORY"));
					mainObjModel.setAggregationId(rs.getString("AGGREGATIONID"));
					mainObjModel.setWmsTaskCategory(rs.getString("WMSTASKCATEGORY"));
					mainObjModel.setSourceforWMSLogic(rs.getString("SORCEFORWMSLOGIC"));
					mainObjModel.setWmsCustomerType(rs.getString("WMSCUSTOMERTYPE"));
					mainObjModel.setMainWorkLocation(rs.getString("MAINWORKLOCATION"));
					mainObjModel.setTaskTypeId(rs.getString("TASKTYPEID"));
					mainObjModel.setSkillCode(rs.getString("SKILLCODE"));
					mainObjModel.setTaskDuration(rs.getString("TASKDURATION"));
					mainObjModel.setResponseCode(rs.getString("RESPONSECODE"));
					mainObjModel.setWorkLocationQualifier(rs.getString("WORKLOCATIONQUALIFIER"));
					mainObjModel.setLob(rs.getString("LOB"));
					mainObjModel.setEnabled(rs.getString("ENABLED"));
					mainObjModel.setSanityCheck(rs.getString("ISSANITY"));

					String strKey = mainObjModel.getProductName() + ":" + mainObjModel.getTaskCategoryName()+ ":" + mainObjModel.getLob();
					hashMapAggMasterDataAggregator.put(strKey, mainObjModel);
				}
			}
		}


	}*/

   /* private void getAggregationDataFromDB() throws SQLException, METBException {

        String strKey;
        String aggregationId;
        String aggregationName;
        String requestType;
        String aggSequence;
        String requirementBusiness;
        String requirementTechnical;
        String interfaceProtocol;
        HashMap inner, outer;

        String aggregationDataQuery = "Select AGGREGATION_ID,AGGREGATION_NAME," +
                "REQUEST_TYPE,AGG_SEQUENCE,REQUIREMENT_BUSINESS,REQUIREMENT_TECHNICAL," +
                "INTERFACE_PROTOCOL from AGGREGATION";

        try (Connection connection = getDataSourceConnection(); PreparedStatement pstmt = connection.prepareStatement(aggregationDataQuery)) {

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    aggregationId = rs.getString("AGGREGATION_ID");
                    aggregationName = rs.getString("AGGREGATION_NAME");
                    requestType = rs.getString("REQUEST_TYPE");
                    aggSequence = rs.getString("AGG_SEQUENCE");
                    requirementBusiness = rs.getString("REQUIREMENT_BUSINESS");
                    requirementTechnical = rs.getString("REQUIREMENT_TECHNICAL");
                    interfaceProtocol = rs.getString("INTERFACE_PROTOCOL");

                    strKey = aggregationId + ":" + requestType;
                    if (hashMapAggDataAggregator.containsKey(strKey)) {
                        outer = (HashMap) hashMapAggDataAggregator.get(strKey);
                    } else {
                        outer = new HashMap();
                    }
                    inner = new HashMap();
                    inner.put(WMGConstant.AGGREGATIONTYPE, aggregationName);
                    inner.put(WMGConstant.REQUIREMENT_BUSINESS, requirementBusiness);
                    inner.put(WMGConstant.REQUIREMENT_TECHNICAL, requirementTechnical);
                    inner.put(WMGConstant.INTERFACE_PROTOCOL, interfaceProtocol);
                    outer.put(aggSequence, inner);
                    hashMapAggDataAggregator.put(strKey, outer);
                }
            }

        }

    }*/
   private void getSourceSystemFromDB() {

   	 String sourceSystemQuery = "Select REPLY_TO_INSTANCE_ID,REPLY_TO_ADDRESS," + "PRODUCT,TASKCATEGORY,SOURCE_SYSTEM,SMNT_SYSTEM,LOB from SMNTHEADERS";
	 List<Map<String,Object>> srcSystemDetails =jdbcTemplate.queryForList(sourceSystemQuery);

     for(Map<String,Object> map : srcSystemDetails){
		 String replyToInstanceId = String.valueOf(map.get("REPLY_TO_INSTANCE_ID"));
		 String replyToAddrs = String.valueOf(map.get("REPLY_TO_ADDRESS"));
		 String product = String.valueOf(map.get("PRODUCT"));
		 String taskCategory = String.valueOf(map.get("TASKCATEGORY"));
		 String srcSystem = String.valueOf(map.get("SOURCE_SYSTEM"));
		 String smntSystem = String.valueOf(map.get("SMNT_SYSTEM"));
		 String lob = String.valueOf(map.get("LOB"));
		 String smntKey = replyToInstanceId + "," + replyToAddrs;
		 hashMapSmntAggregator.put(smntKey, smntSystem);
		 hashMapSrcSystemAggregator.put(getSrcSystemKey(replyToInstanceId, replyToAddrs, product, taskCategory, lob), srcSystem);
	 }
   }
   /* private void getSourceSystemFromDB() throws SQLException, METBException {

        String sourceSystemQuery = "Select REPLY_TO_INSTANCE_ID,REPLY_TO_ADDRESS," + "PRODUCT,TASKCATEGORY,SOURCE_SYSTEM,SMNT_SYSTEM,LOB from SMNTHEADERS";
        try (Connection connection = getDataSourceConnection(); PreparedStatement pstmt = connection.prepareStatement(sourceSystemQuery);) {
            try (ResultSet rs = pstmt.executeQuery();) {
                while (rs.next()) {
                    String replyToInstanceId = rs.getString("REPLY_TO_INSTANCE_ID");
                    String replyToAddrs = rs.getString("REPLY_TO_ADDRESS");
                    String product = rs.getString("PRODUCT");
                    String taskCategory = rs.getString("TASKCATEGORY");
                    String srcSystem = rs.getString("SOURCE_SYSTEM");
                    String smntSystem = rs.getString("SMNT_SYSTEM");
                    String lob = rs.getString("LOB");
                    String smntKey = replyToInstanceId + "," + replyToAddrs;
                    hashMapSmntAggregator.put(smntKey, smntSystem);
                    hashMapSrcSystemAggregator.put(getSrcSystemKey(replyToInstanceId, replyToAddrs, product, taskCategory, lob), srcSystem);
                }
            }
        }

    }*/

    public static AggregationMainDBSchema getAggregationMainData(String productName, String taskCategoryName, String lob) throws METBException {
        AggregationMainDBSchema MainDataRow;
        try {
            String newLob = lob;
            if ((null == newLob)) {
                newLob = null;
            }
            String mainDataKey = productName + ":" + taskCategoryName + ":" + newLob;
            logger.debug("DBLoading.getAggregationMainData :::mainDataKey: {}",mainDataKey);
            MainDataRow = (AggregationMainDBSchema) hashMapAggMasterDataAggregator.get(mainDataKey);
        } catch (Exception exception) {
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                    "Unknown Exception :: DBLoading :: getMainData ", WMGUtil.getStackTrace(exception));
        }
        return MainDataRow;
    }


    public static Map getAggregationData(String aggregationID, String actionType) throws METBException {
        HashMap outer = new HashMap();
        try {
            String aggDataKey = aggregationID + ":" + actionType;
            if (hashMapAggDataAggregator.containsKey(aggDataKey)) {
                outer = (HashMap) hashMapAggDataAggregator.get(aggDataKey);
            }
        } catch (Exception exception) {
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                    "Unknown Exception :: DBLoading :: getAggregationData ", WMGUtil.getStackTrace(exception));
        }

        return outer;
    }

    public static String getSMNTSystem(String replytoInstanceId, String replytoAddress) throws METBException {

        String smntSystem;
        try {
            String smntKey = replytoInstanceId + "," + replytoAddress;
            smntSystem = (String) hashMapSmntAggregator.get(smntKey);
        } catch (Exception exception) {
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                    "Unknown Exception :: DBLoading :: getSMNTSystem ", WMGUtil.getStackTrace(exception));
        }
        return smntSystem;
    }

    private static String getSrcSystemKey(String... subKey) {
        StringBuilder sb = new StringBuilder();
        List<String> keyList = new ArrayList<>(Arrays.asList(subKey));
        int lastIdx = keyList.size() - 1;
        String lastKey = keyList.get(lastIdx);
        keyList.remove(lastIdx);

        for (String k : keyList) {
            sb.append(k);
            sb.append(",");
        }
        sb.append(lastKey);
        return sb.toString();
    }

}



