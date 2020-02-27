package com.bt.metb.aggregator.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregationSingleRow {
    private String aggregationId;
    private String aggregationName;
    private String agregationSequence;
    private String requirementBusiness;
    private String interfaceprotocol;
    private String product;
    private String taskcategory;
    private String requestType;
}
