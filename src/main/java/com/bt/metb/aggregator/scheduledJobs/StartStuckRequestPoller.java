package com.bt.metb.aggregator.scheduledJobs;


import com.bt.metb.aggregator.dao.ServerLookUpDAO;
import com.bt.metb.aggregator.model.StuckPollerAttributeReader;
import com.bt.metb.aggregator.processor.StuckRequestPollerBeanProcessor;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;



/**
 * This is a Quartz Job
 * It calls StuckRequestPoller to select eligible Stucked Requests and repost them to applicable Aggregator Outbound Queue.
 */
@Component
@RefreshScope
public class StartStuckRequestPoller {

    private boolean serverLookUpTableUpdated = false;

    private static final Logger SLF4J_LOGGER = LoggerFactory.getLogger(StartStuckRequestPoller.class);
    @Autowired
    @Qualifier("resourceManager")
    private METResourceManagerInterface resourceManager;

    @Autowired
    private StuckPollerAttributeReader stuckPollerAttributeReader;

    @Autowired
    private StuckRequestPollerBeanProcessor poller;

    @Autowired
    private ServerLookUpDAO serverLookUpDAO;

    @Value("${start.stuckRequest.poller.delay}")
    private int delay;

    public void execute()  {
        SLF4J_LOGGER.debug("StartStuckRequestPoller.execute() QUARTZ-JOB-STARTED");
        try {

            boolean startStuckRequestPoller = stuckPollerAttributeReader.getStartStuckRequestPoller().equalsIgnoreCase("true");

            SLF4J_LOGGER.debug("StartStuckRequestPoller.execute() :{}", startStuckRequestPoller);

            if (startStuckRequestPoller) {
                //Will be executed only once when application starts up
                if (!serverLookUpTableUpdated) {
                    serverLookUpTableUpdated = serverLookUpDAO.updateServerStatus(resourceManager.getManagedServer());
                }

                poller.routeStuckRequest();
                SLF4J_LOGGER.debug("execute() QUARTZ-JOB-COMPLETED");
            }
        } catch (Exception exception) {
            SLF4J_LOGGER.error("StartStuckRequestPoller.execute() EXCEPTION OCCURRED: \n ", exception);
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }
}
