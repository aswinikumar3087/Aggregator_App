package com.bt.metb.aggregator.parser.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Calendar;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface GetPlanDetailsRequestIgnore {

    @JsonIgnore
    void setSeconds(int seconds);

    @JsonIgnore
    void setTime(Calendar date);
}
