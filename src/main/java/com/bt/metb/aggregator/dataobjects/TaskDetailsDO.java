package com.bt.metb.aggregator.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDetailsDO {

    private String metRefNumber;
    private String taskRefNumber;
    private String transformedXML;
    private String taskStatus;
    private String taskStatusCode;
    private String wmsRefNumber;
    private String wmsId;
    private String responseStatus;
    private String replyTo;
    private String sourceInstanceId;
    private String sourceAddress;
    private Date lastUpdated;
    private String product;
    private String taskCategory;
    private int wmRetryCount;
    private String lob;
    private String timesheetRefNumber;
    private String lastestOperation;
    private String requestXML;
    private String requestStatus;
    private int snmtRetryCount;
    private String correlationId;
    private int aggRetryCount;
    private String aggName;
    private String retryStatus;
    private String aggRequestXML;
    private String serverName;
    private String e2eData;
    private String a1141Code;
    private int aggProcessedCount;
    private int aggRequestCount;


    /*@Override
    public boolean equals(Object input) {

        TaskDetailsDO compObj = null;
        if (input == null) {
            return false;
        }
        if (!(input instanceof TaskDetailsDO)) {
            return false;
        } else {
            compObj = (TaskDetailsDO) input;
        }
        if (compObj == this) {
            return true;
        }


        if (
                ((this.getA1141Code() == compObj.getA1141Code())
                        || (this.getA1141Code() != null && (this.getA1141Code().equals(compObj.getA1141Code()))))
                        && ((this.getAggName() == compObj.getAggName())
                        || (this.getAggName() != null && (this.getAggName().equals(compObj.getAggName()))))
                        && ((this.getAggProcessedCount() == compObj.getAggProcessedCount()))
                        && ((this.getAggRequestCount() == compObj.getAggRequestCount()))
                        && ((this.getAggRequestXML() == compObj.getAggRequestXML())
                        || (this.getAggRequestXML() != null && (this.getAggRequestXML().equals(compObj.getAggRequestXML()))))
                        && ((this.getAggRetryCount() == compObj.getAggRetryCount()))
                        && ((this.getCorrelationId() == compObj.getCorrelationId())
                        || (this.getCorrelationId() != null && (this.getCorrelationId().equals(compObj.getCorrelationId()))))
                        && ((this.getE2eData() == compObj.getE2eData())
                        || (this.getE2eData() != null && (this.getE2eData().equals(compObj.getE2eData()))))
                        && ((this.getLastestOperation() == compObj.getLastestOperation())
                        || (this.getLastestOperation() != null && (this.getLastestOperation().equals(compObj.getLastestOperation()))))
                        && ((this.getLastUpdated() == compObj.getLastUpdated())
                        || (this.getLastUpdated() != null && (this.getLastUpdated().equals(compObj.getLastUpdated()))))
                        && ((this.getLob() == compObj.getLob())
                        || (this.getLob() != null && (this.getLob().equals(compObj.getLob()))))
                        && ((this.getMetRefNumber() == compObj.getMetRefNumber())
                        || (this.getMetRefNumber() != null && (this.getMetRefNumber().equals(compObj.getMetRefNumber()))))
                        && ((this.getProduct() == compObj.getProduct())
                        || (this.getProduct() != null && (this.getProduct().equals(compObj.getProduct()))))
                        && ((this.getReplyTo() == compObj.getReplyTo())
                        || (this.getReplyTo() != null && (this.getReplyTo().equals(compObj.getReplyTo()))))
                        && ((this.getRequestStatus() == compObj.getRequestStatus())
                        || (this.getRequestStatus() != null && (this.getRequestStatus().equals(compObj.getRequestStatus()))))
                        && ((this.getRequestXML() == compObj.getRequestXML())
                        || (this.getRequestXML() != null && (this.getRequestXML().equals(compObj.getRequestXML()))))
                        && ((this.getResponseStatus() == compObj.getResponseStatus())
                        || (this.getResponseStatus() != null && (this.getResponseStatus().equals(compObj.getResponseStatus()))))
                        && ((this.getRetryStatus() == compObj.getRetryStatus())
                        || (this.getRetryStatus() != null && (this.getRetryStatus().equals(compObj.getRetryStatus()))))
                        && ((this.getServerName() == compObj.getServerName())
                        || (this.getServerName() != null && (this.getServerName().equals(compObj.getServerName()))))
                        && ((this.getSnmtRetryCount() == compObj.getSnmtRetryCount()))
                        && ((this.getSourceAddress() == compObj.getSourceAddress())
                        || (this.getSourceAddress() != null && (this.getSourceAddress().equals(compObj.getSourceAddress()))))
                        && ((this.getSourceInstanceId() == compObj.getSourceInstanceId())
                        || (this.getSourceInstanceId() != null && (this.getSourceInstanceId().equals(compObj.getSourceInstanceId()))))
                        && ((this.getTaskCategory() == compObj.getTaskCategory())
                        || (this.getTaskCategory() != null && (this.getTaskCategory().equals(compObj.getTaskCategory()))))
                        && ((this.getTaskRefNumber() == compObj.getTaskRefNumber())
                        || (this.getTaskRefNumber() != null && (this.getTaskRefNumber().equals(compObj.getTaskRefNumber()))))
                        && ((this.getTaskStatus() == compObj.getTaskStatus())
                        || (this.getTaskStatus() != null && (this.getTaskStatus().equals(compObj.getTaskStatus()))))
                        && ((this.getTaskStatusCode() == compObj.getTaskStatusCode())
                        || (this.getTaskStatusCode() != null && (this.getTaskStatusCode().equals(compObj.getTaskStatusCode()))))
                        && ((this.getTimesheetRefNumber() == compObj.getTimesheetRefNumber())
                        || (this.getTimesheetRefNumber() != null && (this.getTimesheetRefNumber().equals(compObj.getTimesheetRefNumber()))))
                        && ((this.getTransformedXML() == compObj.getTransformedXML())
                        || (this.getTransformedXML() != null && (this.getTransformedXML().equals(compObj.getTransformedXML()))))
                        && ((this.getWmRetryCount() == compObj.getWmRetryCount()))
                        && ((this.getWmsId() == compObj.getWmsId())
                        || (this.getWmsId() != null && (this.getWmsId().equals(compObj.getWmsId()))))
                        && ((this.getWmsRefNumber() == compObj.getWmsRefNumber())
                        || (this.getWmsRefNumber() != null && (this.getWmsRefNumber().equals(compObj.getWmsRefNumber()))))
        ) {
            return true;
        }


        return false;
    }


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == this.getA1141Code() ? 0 : this.getA1141Code().hashCode());
        hash = 31 * hash + (null == this.getAggName() ? 0 : this.getAggName().hashCode());
        hash = 31 * hash + this.getAggProcessedCount();
        hash = 31 * hash + this.getAggRequestCount();
        hash = 31 * hash + (null == this.getAggRequestXML() ? 0 : this.getAggRequestXML().hashCode());
        hash = 31 * hash + this.getAggRetryCount();
        hash = 31 * hash + (null == this.getCorrelationId() ? 0 : this.getCorrelationId().hashCode());
        hash = 31 * hash + (null == this.getE2eData() ? 0 : this.getE2eData().hashCode());
        hash = 31 * hash + (null == this.getLastestOperation() ? 0 : this.getLastestOperation().hashCode());
        hash = 31 * hash + (null == this.getLastUpdated() ? 0 : this.getLastUpdated().hashCode());
        hash = 31 * hash + (null == this.getLob() ? 0 : this.getLob().hashCode());
        hash = 31 * hash + (null == this.getMetRefNumber() ? 0 : this.getMetRefNumber().hashCode());
        hash = 31 * hash + (null == this.getProduct() ? 0 : this.getProduct().hashCode());
        hash = 31 * hash + (null == this.getReplyTo() ? 0 : this.getReplyTo().hashCode());
        hash = 31 * hash + (null == this.getRequestStatus() ? 0 : this.getRequestStatus().hashCode());
        hash = 31 * hash + (null == this.getRequestXML() ? 0 : this.getRequestXML().hashCode());
        hash = 31 * hash + (null == this.getResponseStatus() ? 0 : this.getResponseStatus().hashCode());
        hash = 31 * hash + (null == this.getRetryStatus() ? 0 : this.getRetryStatus().hashCode());
        hash = 31 * hash + (null == this.getServerName() ? 0 : this.getServerName().hashCode());
        hash = 31 * hash + this.getSnmtRetryCount();
        hash = 31 * hash + (null == this.getSourceAddress() ? 0 : this.getSourceAddress().hashCode());
        hash = 31 * hash + (null == this.getSourceInstanceId() ? 0 : this.getSourceInstanceId().hashCode());
        hash = 31 * hash + (null == this.getTaskCategory() ? 0 : this.getTaskCategory().hashCode());
        hash = 31 * hash + (null == this.getTaskRefNumber() ? 0 : this.getTaskRefNumber().hashCode());
        hash = 31 * hash + (null == this.getTaskStatus() ? 0 : this.getTaskStatus().hashCode());
        hash = 31 * hash + (null == this.getTaskStatusCode() ? 0 : this.getTaskStatusCode().hashCode());
        hash = 31 * hash + (null == this.getTimesheetRefNumber() ? 0 : this.getTimesheetRefNumber().hashCode());
        hash = 31 * hash + (null == this.getTransformedXML() ? 0 : this.getTransformedXML().hashCode());
        hash = 31 * hash + this.getWmRetryCount();
        hash = 31 * hash + (null == this.getWmsId() ? 0 : this.getWmsId().hashCode());
        hash = 31 * hash + (null == this.getWmsRefNumber() ? 0 : this.getWmsRefNumber().hashCode());

        return hash;
    }*/
}
