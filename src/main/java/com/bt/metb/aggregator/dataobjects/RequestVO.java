package com.bt.metb.aggregator.dataobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestVO {

    private String receivedXMLSoap;
    private String version;
    private String protocol;
    private String validatedXml;

}
