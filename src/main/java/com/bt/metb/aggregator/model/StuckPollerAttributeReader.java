package com.bt.metb.aggregator.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
public class StuckPollerAttributeReader {

    @Value("${START_STUCKREQUEST_POLLER}")
    private String startStuckRequestPoller;

    @Value("${STUCKREQUEST_POLLER_PERIOD_SEC}")
    private String startStuckRequestPollerPeriodSec;

    @Value("${STUCKREQUEST_POLLER_START_DELAY_SEC}")
    private String startStuckRequestPollerStartDelaySec;

    @Value("${BATCH_SIZE_AGGREGATION}")
    private String batchSizeAggregation;

    @Value("${AGGREGATION_RETRY_LIMIT}")
    private String aggregationRetryLimit;
}
