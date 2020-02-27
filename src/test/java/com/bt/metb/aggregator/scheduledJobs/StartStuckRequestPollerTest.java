package com.bt.metb.aggregator.scheduledJobs;

import com.bt.metb.aggregator.dao.ServerLookUpDAO;
import com.bt.metb.aggregator.model.StuckPollerAttributeReader;
import com.bt.metb.aggregator.processor.StuckRequestPollerBeanProcessor;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StartStuckRequestPollerTest {

    @InjectMocks
    private StartStuckRequestPoller startStuckRequestPoller;

    @Mock
    private METResourceManagerInterface resourceManager;

    @Mock
    private StuckPollerAttributeReader stuckPollerAttributeReader;

    @Mock
    private StuckRequestPollerBeanProcessor poller;

    @Mock
    private ServerLookUpDAO serverLookUpDAO;

    @Test
    public void execute(){

        when(stuckPollerAttributeReader.getStartStuckRequestPoller()).thenReturn("true");
        startStuckRequestPoller.execute();
        verify(stuckPollerAttributeReader).getStartStuckRequestPoller();
    }

    @After
    public void tearDown() {
        stuckPollerAttributeReader = null;
        startStuckRequestPoller = null;
    }

    @Test
    public void ThrowExceptionInPoller() throws Exception {

        startStuckRequestPoller.execute();
        verify(stuckPollerAttributeReader).getStartStuckRequestPoller();
    }

}
