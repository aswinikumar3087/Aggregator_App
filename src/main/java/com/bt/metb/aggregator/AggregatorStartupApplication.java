package com.bt.metb.aggregator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.WebApplicationInitializer;


/**
 *  Main class to start Aggregator application.
 *
 * @author Ananya Banerjee
 * @version 1.0
 */

@EnableRabbit
@SpringBootApplication
public class AggregatorStartupApplication extends SpringBootServletInitializer implements RabbitListenerConfigurer, WebApplicationInitializer {

	private static final Logger logger = LoggerFactory.getLogger(AggregatorStartupApplication.class);


    public static void main(String[] args) {
        logger.debug("AggregatorStartupApplication.main()");
        SpringApplication.run(AggregatorStartupApplication.class);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AggregatorStartupApplication.class);
    }


	public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
		//commented out the implementation

	}



}