package com.bt.metb.aggregator.responseProcessor;

import com.bt.metb.aggregator.exception.METBException;
import org.springframework.amqp.core.Message;

public interface ResponseProcessor {
    void processMessage(Message inputData) throws METBException;
    void retryHandler(final Message data);
}
