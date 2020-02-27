package com.bt.metb.aggregator.listeners;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

public interface AggregatorMessageSubscriber {
   void getMessageFromInboundToAggregator(final Message data, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag);
}
