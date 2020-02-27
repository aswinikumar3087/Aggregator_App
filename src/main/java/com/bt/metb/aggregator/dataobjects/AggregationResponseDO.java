package com.bt.metb.aggregator.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregationResponseDO  {

    private String reqStatus;
    private String metRefNumber;
    private String taskRefNumber;
    private String product;
    private String reqXML;
    private String aggregationName;
    private String taskCategory;
    private String e2eData;
    private String replyTo;
    private String lob;
    private String smntRetryCount;
    private String sourceAddress;
    private String sourceInstanceId;
    private Timestamp lastUpdateDate;
    private String businessExceptionRequirement;
    private String technicalExceptionRequirement;
    private boolean messageIdNotFound;
    private String latestTaskOperation;
    private String correlationId;
    private String businessExceptionVal;
    private String technicalExceptionVal;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AggregationResponseDO that = (AggregationResponseDO) o;

        if (messageIdNotFound != that.messageIdNotFound) return false;
        if (reqStatus != null ? !reqStatus.equals(that.reqStatus) : that.reqStatus != null) return false;
        if (metRefNumber != null ? !metRefNumber.equals(that.metRefNumber) : that.metRefNumber != null) return false;
        if (taskRefNumber != null ? !taskRefNumber.equals(that.taskRefNumber) : that.taskRefNumber != null)
            return false;
        if (product != null ? !product.equals(that.product) : that.product != null) return false;
        if (reqXML != null ? !reqXML.equals(that.reqXML) : that.reqXML != null) return false;
        if (aggregationName != null ? !aggregationName.equals(that.aggregationName) : that.aggregationName != null)
            return false;
        if (taskCategory != null ? !taskCategory.equals(that.taskCategory) : that.taskCategory != null) return false;
        if (e2eData != null ? !e2eData.equals(that.e2eData) : that.e2eData != null) return false;
        if (replyTo != null ? !replyTo.equals(that.replyTo) : that.replyTo != null) return false;
        if (lob != null ? !lob.equals(that.lob) : that.lob != null) return false;
        if (smntRetryCount != null ? !smntRetryCount.equals(that.smntRetryCount) : that.smntRetryCount != null)
            return false;
        if (sourceAddress != null ? !sourceAddress.equals(that.sourceAddress) : that.sourceAddress != null)
            return false;
        if (sourceInstanceId != null ? !sourceInstanceId.equals(that.sourceInstanceId) : that.sourceInstanceId != null)
            return false;
        if (lastUpdateDate != null ? !lastUpdateDate.equals(that.lastUpdateDate) : that.lastUpdateDate != null)
            return false;
        if (businessExceptionRequirement != null ? !businessExceptionRequirement.equals(that.businessExceptionRequirement) : that.businessExceptionRequirement != null)
            return false;
        if (technicalExceptionRequirement != null ? !technicalExceptionRequirement.equals(that.technicalExceptionRequirement) : that.technicalExceptionRequirement != null)
            return false;
        if (latestTaskOperation != null ? !latestTaskOperation.equals(that.latestTaskOperation) : that.latestTaskOperation != null)
            return false;
        if (correlationId != null ? !correlationId.equals(that.correlationId) : that.correlationId != null)
            return false;
        if (businessExceptionVal != null ? !businessExceptionVal.equals(that.businessExceptionVal) : that.businessExceptionVal != null)
            return false;
        return technicalExceptionVal != null ? technicalExceptionVal.equals(that.technicalExceptionVal) : that.technicalExceptionVal == null;
    }

    @Override
    public int hashCode() {
        int result = reqStatus != null ? reqStatus.hashCode() : 0;
        result = 31 * result + (metRefNumber != null ? metRefNumber.hashCode() : 0);
        result = 31 * result + (taskRefNumber != null ? taskRefNumber.hashCode() : 0);
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (reqXML != null ? reqXML.hashCode() : 0);
        result = 31 * result + (aggregationName != null ? aggregationName.hashCode() : 0);
        result = 31 * result + (taskCategory != null ? taskCategory.hashCode() : 0);
        result = 31 * result + (e2eData != null ? e2eData.hashCode() : 0);
        result = 31 * result + (replyTo != null ? replyTo.hashCode() : 0);
        result = 31 * result + (lob != null ? lob.hashCode() : 0);
        result = 31 * result + (smntRetryCount != null ? smntRetryCount.hashCode() : 0);
        result = 31 * result + (sourceAddress != null ? sourceAddress.hashCode() : 0);
        result = 31 * result + (sourceInstanceId != null ? sourceInstanceId.hashCode() : 0);
        result = 31 * result + (lastUpdateDate != null ? lastUpdateDate.hashCode() : 0);
        result = 31 * result + (businessExceptionRequirement != null ? businessExceptionRequirement.hashCode() : 0);
        result = 31 * result + (technicalExceptionRequirement != null ? technicalExceptionRequirement.hashCode() : 0);
        result = 31 * result + (messageIdNotFound ? 1 : 0);
        result = 31 * result + (latestTaskOperation != null ? latestTaskOperation.hashCode() : 0);
        result = 31 * result + (correlationId != null ? correlationId.hashCode() : 0);
        result = 31 * result + (businessExceptionVal != null ? businessExceptionVal.hashCode() : 0);
        result = 31 * result + (technicalExceptionVal != null ? technicalExceptionVal.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AggregationResponseDO{" +
                "reqStatus='" + reqStatus + '\'' +
                ", metRefNumber='" + metRefNumber + '\'' +
                ", taskRefNumber='" + taskRefNumber + '\'' +
                ", product='" + product + '\'' +
                ", reqXML='" + reqXML + '\'' +
                ", aggregationName='" + aggregationName + '\'' +
                ", taskCategory='" + taskCategory + '\'' +
                ", e2eData='" + e2eData + '\'' +
                ", replyTo='" + replyTo + '\'' +
                ", lob='" + lob + '\'' +
                ", smntRetryCount='" + smntRetryCount + '\'' +
                ", sourceAddress='" + sourceAddress + '\'' +
                ", sourceInstanceId='" + sourceInstanceId + '\'' +
                ", lastUpdateDate=" + lastUpdateDate +
                ", businessExceptionRequirement='" + businessExceptionRequirement + '\'' +
                ", technicalExceptionRequirement='" + technicalExceptionRequirement + '\'' +
                ", messageIdNotFound=" + messageIdNotFound +
                ", latestTaskOperation='" + latestTaskOperation + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", businessExceptionVal='" + businessExceptionVal + '\'' +
                ", technicalExceptionVal='" + technicalExceptionVal + '\'' +
                '}';
    }
}
