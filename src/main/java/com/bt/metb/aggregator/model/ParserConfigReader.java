package com.bt.metb.aggregator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@RefreshScope
public class ParserConfigReader {

    @Value("${spring.taskforce.dtd.path}")
    private String dtdPath;

    @Value("${spring.wmig.messageid}")
    private String wmigMessageId;

}
