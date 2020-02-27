package com.bt.metb.aggregator.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregationCollateInfoDO  {

    private String errorMessage;
    private String errorCode;
    private String errorText;
    private String responseStatus;
}
