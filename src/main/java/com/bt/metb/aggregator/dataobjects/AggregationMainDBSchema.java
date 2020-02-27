package com.bt.metb.aggregator.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Map;

@Data
@AllArgsConstructor
public class AggregationMainDBSchema {

    private String productName, taskCategoryName;
    private String aggregationId, wmsTaskCategory, sourceForWMSLogic, wmsCustomerType;
    private String mainWorkLocation, taskTypeId, skillCode, taskDuration, responseCode, workLocationQualifier;
    private String lob, enabled;
    private String sanityCheck;

    public AggregationMainDBSchema(AggregationMainDBSchema copyRow) {
        productName = copyRow.getProductName();
        taskCategoryName = copyRow.getTaskCategoryName();
        aggregationId = copyRow.getAggregationId();
        wmsTaskCategory = copyRow.getWmsTaskCategory();
        sourceForWMSLogic = copyRow.getSourceForWMSLogic();
        wmsCustomerType = copyRow.getWmsCustomerType();
        mainWorkLocation = copyRow.getMainWorkLocation();
        taskTypeId = copyRow.getTaskTypeId();
        skillCode = copyRow.getSkillCode();
        taskDuration = copyRow.getTaskDuration();
        responseCode = copyRow.getResponseCode();
        workLocationQualifier = copyRow.getWorkLocationQualifier();
        lob = copyRow.getLob();
        enabled = copyRow.getEnabled();
        sanityCheck = copyRow.getSanityCheck();

    }


    public AggregationMainDBSchema(Map row) {
        productName = (String) row.get("PRODUCT");
        taskCategoryName = (String) row.get("TASKCATEGORY");
        aggregationId = (String) row.get("AGGREGATIONID");
        wmsTaskCategory = (String) row.get("WMSTASKCATEGORY");
        sourceForWMSLogic = (String) row.get("SORCEFORWMSLOGIC");
        wmsCustomerType = (String) row.get("WMSCUSTOMERTYPE");
        mainWorkLocation = (String) row.get("MAINWORKLOCATION");
        taskTypeId = (String) row.get("TASKTYPEID");
        skillCode = (String) row.get("SKILLCODE");
        taskDuration = (String) row.get("TASKDURATION");
        responseCode = (String) row.get("RESPONSECODE");
        workLocationQualifier = (String) row.get("WORKLOCATIONQUALIFIER");
        lob = (String) row.get("LOB");
        enabled = (String) row.get("ENABLED");
        sanityCheck = (String) row.get("ISSANITY");
    }

    public AggregationMainDBSchema() {
        productName = null;
        taskCategoryName = null;
        aggregationId = null;
        wmsTaskCategory = null;
        sourceForWMSLogic = null;
        wmsCustomerType = null;
        mainWorkLocation = null;
        taskTypeId = null;
        skillCode = null;
        taskDuration = null;
        responseCode = null;
        workLocationQualifier = null;
        lob = null;
        enabled = null;
        sanityCheck = null;
    }


}
