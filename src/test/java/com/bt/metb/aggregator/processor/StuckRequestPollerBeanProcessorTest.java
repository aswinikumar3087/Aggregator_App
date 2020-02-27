package com.bt.metb.aggregator.processor;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.dao.DAOInterface;
import com.bt.metb.aggregator.messageprocessor.AggregatorCoreProcessor;
import com.bt.metb.aggregator.model.StuckPollerAttributeReader;
import com.bt.metb.aggregator.mqConfig.EmppalAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.MetbAggregatorMqConfigReader;
import com.bt.metb.aggregator.mqConfig.MnplAggregatorMqConfigReader;
import com.bt.metb.aggregator.parser.interfaces.ParserServiceInterface;
import com.bt.metb.aggregator.producer.MessagePublisher;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StuckRequestPollerBeanProcessorTest {
    


    @Mock
    private StuckPollerAttributeReader stuckPollerAttributeReader;

    @Mock
    private MessagePublisher messagePublisher;

    @Mock
    private SimpleMessageConverter simpleMessageConverter;

    @Mock
    private MnplAggregatorMqConfigReader mnplAggregatorMqConfigReader;

    @Mock
    private EmppalAggregatorMqConfigReader emppalAggregatorMqConfigReader;

    @Mock
    private MetbAggregatorMqConfigReader metbAggregatorMqConfigReader;

    @Mock
    private ParserServiceInterface parserServiceInterface;

    @Mock
    private METResourceManagerInterface resourceManager;

    @Mock
    private AggregatorCoreProcessor aggregatorCoreProcessor;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DAOInterface dao;

    @Mock
    private Environment environment;

    @InjectMocks
    private StuckRequestPollerBeanProcessor stuckRequestPollerBeanProcessor;

    private static final String METHOD = "postRequestToComponentOutBoundForProcessing";

    @Test
    public void routeStuckRequestForMnplBelowRetryLimit() throws Exception {


        when(resourceManager.getManagedServer()).thenReturn("managedserver01");
        doNothing().when(dao).updateServerLookupLastProcessdate("managedserver01");
        when(stuckPollerAttributeReader.getBatchSizeAggregation()).thenReturn("15");
        when(stuckPollerAttributeReader.getAggregationRetryLimit()).thenReturn("3");

        Object[] params = new Object[]{"managedserver01", "15"};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};

        Map<String,Object> m1 = new HashMap<>();
        m1.put(WMGConstant.MET_REF_NUMBER,"667");
        m1.put(WMGConstant.AGG_NAME,"MNPL");
        m1.put(WMGConstant.AGG_RETRY_COUNT,"2");
        m1.put(WMGConstant.RETRY_STATUS,"RETRY");
        m1.put(WMGConstant.AGG_XML,"{\n" +
                "  \"standardHeader\" : {\n" +
                "    \"e2E\" : null,\n" +
                "    \"serviceState\" : {\n" +
                "      \"stateCode\" : \"OK\",\n" +
                "      \"errorCode\" : null,\n" +
                "      \"errorDesc\" : null,\n" +
                "      \"errorText\" : null,\n" +
                "      \"errorTrace\" : null,\n" +
                "      \"resendIndicator\" : true,\n" +
                "      \"retriesRemaining\" : 0,\n" +
                "      \"retryInterval\" : 0\n" +
                "    },\n" +
                "    \"serviceAddressing\" : {\n" +
                "      \"from\" : {\n" +
                "        \"queryString\" : null,\n" +
                "        \"path\" : \"/METsk\",\n" +
                "        \"host\" : \"ccm.intra.bt.com\",\n" +
                "        \"absoluteURI\" : true,\n" +
                "        \"userinfo\" : null,\n" +
                "        \"regBasedAuthority\" : null,\n" +
                "        \"scheme\" : \"http\",\n" +
                "        \"fragment\" : null,\n" +
                "        \"genericURI\" : true,\n" +
                "        \"schemeSpecificPart\" : \"//ccm.intra.bt.com/METsk\",\n" +
                "        \"port\" : -1\n" +
                "      },\n" +
                "      \"to\" : {\n" +
                "        \"address\" : {\n" +
                "          \"queryString\" : null,\n" +
                "          \"path\" : \"/wfmt\",\n" +
                "          \"host\" : \"ccm.intra.bt.com\",\n" +
                "          \"absoluteURI\" : true,\n" +
                "          \"userinfo\" : null,\n" +
                "          \"regBasedAuthority\" : null,\n" +
                "          \"scheme\" : \"http\",\n" +
                "          \"fragment\" : null,\n" +
                "          \"genericURI\" : true,\n" +
                "          \"schemeSpecificPart\" : \"//ccm.intra.bt.com/wfmt\",\n" +
                "          \"port\" : -1\n" +
                "        },\n" +
                "        \"contextItemList\" : null\n" +
                "      },\n" +
                "      \"replyTo\" : {\n" +
                "        \"address\" : {\n" +
                "          \"queryString\" : null,\n" +
                "          \"path\" : \"/METsk\",\n" +
                "          \"host\" : \"ccm.intra.bt.com\",\n" +
                "          \"absoluteURI\" : true,\n" +
                "          \"userinfo\" : null,\n" +
                "          \"regBasedAuthority\" : null,\n" +
                "          \"scheme\" : \"http\",\n" +
                "          \"fragment\" : null,\n" +
                "          \"genericURI\" : true,\n" +
                "          \"schemeSpecificPart\" : \"//ccm.intra.bt.com/METsk\",\n" +
                "          \"port\" : -1\n" +
                "        },\n" +
                "        \"contextItemList\" : null\n" +
                "      },\n" +
                "      \"relatesTo\" : null,\n" +
                "      \"faultTo\" : {\n" +
                "        \"address\" : {\n" +
                "          \"queryString\" : null,\n" +
                "          \"path\" : \"/wfmt\",\n" +
                "          \"host\" : \"ccm.intra.bt.com\",\n" +
                "          \"absoluteURI\" : true,\n" +
                "          \"userinfo\" : null,\n" +
                "          \"regBasedAuthority\" : null,\n" +
                "          \"scheme\" : \"http\",\n" +
                "          \"fragment\" : null,\n" +
                "          \"genericURI\" : true,\n" +
                "          \"schemeSpecificPart\" : \"//ccm.intra.bt.com/wfmt\",\n" +
                "          \"port\" : -1\n" +
                "        },\n" +
                "        \"contextItemList\" : null\n" +
                "      },\n" +
                "      \"messageId\" : \"1747489\",\n" +
                "      \"serviceName\" : {\n" +
                "        \"queryString\" : null,\n" +
                "        \"path\" : \"/manageNetworkPlanning\",\n" +
                "        \"host\" : \"capabilties.nat.bt.com\",\n" +
                "        \"absoluteURI\" : true,\n" +
                "        \"userinfo\" : null,\n" +
                "        \"regBasedAuthority\" : null,\n" +
                "        \"scheme\" : \"http\",\n" +
                "        \"fragment\" : null,\n" +
                "        \"genericURI\" : true,\n" +
                "        \"schemeSpecificPart\" : \"//capabilties.nat.bt.com/manageNetworkPlanning\",\n" +
                "        \"port\" : -1\n" +
                "      },\n" +
                "      \"action\" : {\n" +
                "        \"queryString\" : null,\n" +
                "        \"path\" : \"/wfmt\",\n" +
                "        \"host\" : \"capability.nat.bt.com\",\n" +
                "        \"absoluteURI\" : true,\n" +
                "        \"userinfo\" : null,\n" +
                "        \"regBasedAuthority\" : null,\n" +
                "        \"scheme\" : \"http\",\n" +
                "        \"fragment\" : \"getPlanDetails\",\n" +
                "        \"genericURI\" : true,\n" +
                "        \"schemeSpecificPart\" : \"//capability.nat.bt.com/wfmt#getPlanDetails\",\n" +
                "        \"port\" : -1\n" +
                "      }\n" +
                "    },\n" +
                "    \"serviceProperties\" : null,\n" +
                "    \"serviceSpecification\" : {\n" +
                "      \"payloadFormat\" : \"XML\",\n" +
                "      \"version\" : \"2.2\",\n" +
                "      \"revision\" : \"1\"\n" +
                "    },\n" +
                "    \"serviceSecurity\" : null\n" +
                "  },\n" +
                "  \"planRequestList\" : {\n" +
                "    \"plan\" : [ {\n" +
                "      \"projectID\" : \"NU7PT\",\n" +
                "      \"projectName\" : null,\n" +
                "      \"description\" : null,\n" +
                "      \"type\" : null,\n" +
                "      \"subType\" : null,\n" +
                "      \"status\" : null,\n" +
                "      \"correlationId\" : null,\n" +
                "      \"build\" : null,\n" +
                "      \"startDate\" : null,\n" +
                "      \"endDate\" : null,\n" +
                "      \"requestedBy\" : null,\n" +
                "      \"requestedTime\" : null,\n" +
                "      \"projectFormat\" : null,\n" +
                "      \"programmeName\" : null,\n" +
                "      \"managerResourceId\" : null,\n" +
                "      \"phase\" : null,\n" +
                "      \"size\" : null,\n" +
                "      \"action\" : \"getPlanDetails#VIEWCSP\",\n" +
                "      \"updateReason\" : null,\n" +
                "      \"projectMilestone\" : null,\n" +
                "      \"scheme\" : null,\n" +
                "      \"orderData\" : null,\n" +
                "      \"subNP\" : null,\n" +
                "      \"planningActivity\" : [ {\n" +
                "        \"activityId\" : \"WAGRFF8A\",\n" +
                "        \"activityName\" : null,\n" +
                "        \"activitydescription\" : null,\n" +
                "        \"activityType\" : null,\n" +
                "        \"activityCategory\" : null,\n" +
                "        \"subCategory\" : null,\n" +
                "        \"activityStatus\" : null,\n" +
                "        \"priority\" : null,\n" +
                "        \"criticalFlag\" : null,\n" +
                "        \"milestoneFlag\" : null,\n" +
                "        \"parentId\" : null,\n" +
                "        \"plannedStartDate\" : null,\n" +
                "        \"plannedFinishDate\" : null,\n" +
                "        \"startDate\" : null,\n" +
                "        \"finishDate\" : null,\n" +
                "        \"baselineDuration\" : null,\n" +
                "        \"lastUpdatedBy\" : null,\n" +
                "        \"lastUpdatedDate\" : null,\n" +
                "        \"activityIssuedBy\" : null,\n" +
                "        \"activityNote\" : null,\n" +
                "        \"contactInfo\" : null,\n" +
                "        \"activityCharges\" : null,\n" +
                "        \"task\" : null,\n" +
                "        \"customerAppointmentList\" : null\n" +
                "      } ],\n" +
                "      \"contact\" : null,\n" +
                "      \"location\" : null,\n" +
                "      \"person\" : null,\n" +
                "      \"customerAppointmentList\" : null,\n" +
                "      \"note\" : null,\n" +
                "      \"projectRelatedFlags\" : null,\n" +
                "      \"actionRequiredDetails\" : null,\n" +
                "      \"prjUpdateHistory\" : null,\n" +
                "      \"requestMetaData\" : null\n" +
                "    } ]\n" +
                "  }\n" +
                "}");
        m1.put(WMGConstant.CORRELATION_ID,"MANAGEDSERVER0112345699");
        List<Map<String, Object>> selectTaskDetails = new ArrayList<Map<String, Object>>();
        selectTaskDetails.add(m1);

        when(jdbcTemplate.queryForList(WMGConstant.SELECT_STUCK_REQUEST, params, types)).thenReturn(selectTaskDetails);
        when(dao.updateStuckRetryRequest(any(Timestamp.class),anyInt(),anyString())).thenReturn("UPDATED_SUCCESS");
        StuckRequestPollerBeanProcessor spy = PowerMockito.spy(stuckRequestPollerBeanProcessor);
        MessageProperties mp = new MessageProperties();
        mp.getHeaders().put("msgId", "MANAGEDSERVER0112345699");
        stuckRequestPollerBeanProcessor.routeStuckRequest();
        verify(stuckPollerAttributeReader).getAggregationRetryLimit();
    }

    @Test
    public void routeStuckRequestForMnplRetryLimitExhausted() throws Exception {


        when(resourceManager.getManagedServer()).thenReturn("managedserver01");
        doNothing().when(dao).updateServerLookupLastProcessdate("managedserver01");
        when(stuckPollerAttributeReader.getBatchSizeAggregation()).thenReturn("15");
        when(stuckPollerAttributeReader.getAggregationRetryLimit()).thenReturn("3");

        Object[] params = new Object[]{"managedserver01", "15"};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};

        Map<String,Object> m1 = new HashMap<>();
        m1.put(WMGConstant.MET_REF_NUMBER,"667");
        m1.put(WMGConstant.AGG_NAME,"MNPL");
        m1.put(WMGConstant.AGG_RETRY_COUNT,"3");
        m1.put(WMGConstant.RETRY_STATUS,"RETRY");
        m1.put(WMGConstant.AGG_XML,"{\n" +
                "  \"standardHeader\" : {\n" +
                "    \"e2E\" : null,\n" +
                "    \"serviceState\" : {\n" +
                "      \"stateCode\" : \"OK\",\n" +
                "      \"errorCode\" : null,\n" +
                "      \"errorDesc\" : null,\n" +
                "      \"errorText\" : null,\n" +
                "      \"errorTrace\" : null,\n" +
                "      \"resendIndicator\" : true,\n" +
                "      \"retriesRemaining\" : 0,\n" +
                "      \"retryInterval\" : 0\n" +
                "    },\n" +
                "    \"serviceAddressing\" : {\n" +
                "      \"from\" : {\n" +
                "        \"queryString\" : null,\n" +
                "        \"path\" : \"/METsk\",\n" +
                "        \"host\" : \"ccm.intra.bt.com\",\n" +
                "        \"absoluteURI\" : true,\n" +
                "        \"userinfo\" : null,\n" +
                "        \"regBasedAuthority\" : null,\n" +
                "        \"scheme\" : \"http\",\n" +
                "        \"fragment\" : null,\n" +
                "        \"genericURI\" : true,\n" +
                "        \"schemeSpecificPart\" : \"//ccm.intra.bt.com/METsk\",\n" +
                "        \"port\" : -1\n" +
                "      },\n" +
                "      \"to\" : {\n" +
                "        \"address\" : {\n" +
                "          \"queryString\" : null,\n" +
                "          \"path\" : \"/wfmt\",\n" +
                "          \"host\" : \"ccm.intra.bt.com\",\n" +
                "          \"absoluteURI\" : true,\n" +
                "          \"userinfo\" : null,\n" +
                "          \"regBasedAuthority\" : null,\n" +
                "          \"scheme\" : \"http\",\n" +
                "          \"fragment\" : null,\n" +
                "          \"genericURI\" : true,\n" +
                "          \"schemeSpecificPart\" : \"//ccm.intra.bt.com/wfmt\",\n" +
                "          \"port\" : -1\n" +
                "        },\n" +
                "        \"contextItemList\" : null\n" +
                "      },\n" +
                "      \"replyTo\" : {\n" +
                "        \"address\" : {\n" +
                "          \"queryString\" : null,\n" +
                "          \"path\" : \"/METsk\",\n" +
                "          \"host\" : \"ccm.intra.bt.com\",\n" +
                "          \"absoluteURI\" : true,\n" +
                "          \"userinfo\" : null,\n" +
                "          \"regBasedAuthority\" : null,\n" +
                "          \"scheme\" : \"http\",\n" +
                "          \"fragment\" : null,\n" +
                "          \"genericURI\" : true,\n" +
                "          \"schemeSpecificPart\" : \"//ccm.intra.bt.com/METsk\",\n" +
                "          \"port\" : -1\n" +
                "        },\n" +
                "        \"contextItemList\" : null\n" +
                "      },\n" +
                "      \"relatesTo\" : null,\n" +
                "      \"faultTo\" : {\n" +
                "        \"address\" : {\n" +
                "          \"queryString\" : null,\n" +
                "          \"path\" : \"/wfmt\",\n" +
                "          \"host\" : \"ccm.intra.bt.com\",\n" +
                "          \"absoluteURI\" : true,\n" +
                "          \"userinfo\" : null,\n" +
                "          \"regBasedAuthority\" : null,\n" +
                "          \"scheme\" : \"http\",\n" +
                "          \"fragment\" : null,\n" +
                "          \"genericURI\" : true,\n" +
                "          \"schemeSpecificPart\" : \"//ccm.intra.bt.com/wfmt\",\n" +
                "          \"port\" : -1\n" +
                "        },\n" +
                "        \"contextItemList\" : null\n" +
                "      },\n" +
                "      \"messageId\" : \"1747489\",\n" +
                "      \"serviceName\" : {\n" +
                "        \"queryString\" : null,\n" +
                "        \"path\" : \"/manageNetworkPlanning\",\n" +
                "        \"host\" : \"capabilties.nat.bt.com\",\n" +
                "        \"absoluteURI\" : true,\n" +
                "        \"userinfo\" : null,\n" +
                "        \"regBasedAuthority\" : null,\n" +
                "        \"scheme\" : \"http\",\n" +
                "        \"fragment\" : null,\n" +
                "        \"genericURI\" : true,\n" +
                "        \"schemeSpecificPart\" : \"//capabilties.nat.bt.com/manageNetworkPlanning\",\n" +
                "        \"port\" : -1\n" +
                "      },\n" +
                "      \"action\" : {\n" +
                "        \"queryString\" : null,\n" +
                "        \"path\" : \"/wfmt\",\n" +
                "        \"host\" : \"capability.nat.bt.com\",\n" +
                "        \"absoluteURI\" : true,\n" +
                "        \"userinfo\" : null,\n" +
                "        \"regBasedAuthority\" : null,\n" +
                "        \"scheme\" : \"http\",\n" +
                "        \"fragment\" : \"getPlanDetails\",\n" +
                "        \"genericURI\" : true,\n" +
                "        \"schemeSpecificPart\" : \"//capability.nat.bt.com/wfmt#getPlanDetails\",\n" +
                "        \"port\" : -1\n" +
                "      }\n" +
                "    },\n" +
                "    \"serviceProperties\" : null,\n" +
                "    \"serviceSpecification\" : {\n" +
                "      \"payloadFormat\" : \"XML\",\n" +
                "      \"version\" : \"2.2\",\n" +
                "      \"revision\" : \"1\"\n" +
                "    },\n" +
                "    \"serviceSecurity\" : null\n" +
                "  },\n" +
                "  \"planRequestList\" : {\n" +
                "    \"plan\" : [ {\n" +
                "      \"projectID\" : \"NU7PT\",\n" +
                "      \"projectName\" : null,\n" +
                "      \"description\" : null,\n" +
                "      \"type\" : null,\n" +
                "      \"subType\" : null,\n" +
                "      \"status\" : null,\n" +
                "      \"correlationId\" : null,\n" +
                "      \"build\" : null,\n" +
                "      \"startDate\" : null,\n" +
                "      \"endDate\" : null,\n" +
                "      \"requestedBy\" : null,\n" +
                "      \"requestedTime\" : null,\n" +
                "      \"projectFormat\" : null,\n" +
                "      \"programmeName\" : null,\n" +
                "      \"managerResourceId\" : null,\n" +
                "      \"phase\" : null,\n" +
                "      \"size\" : null,\n" +
                "      \"action\" : \"getPlanDetails#VIEWCSP\",\n" +
                "      \"updateReason\" : null,\n" +
                "      \"projectMilestone\" : null,\n" +
                "      \"scheme\" : null,\n" +
                "      \"orderData\" : null,\n" +
                "      \"subNP\" : null,\n" +
                "      \"planningActivity\" : [ {\n" +
                "        \"activityId\" : \"WAGRFF8A\",\n" +
                "        \"activityName\" : null,\n" +
                "        \"activitydescription\" : null,\n" +
                "        \"activityType\" : null,\n" +
                "        \"activityCategory\" : null,\n" +
                "        \"subCategory\" : null,\n" +
                "        \"activityStatus\" : null,\n" +
                "        \"priority\" : null,\n" +
                "        \"criticalFlag\" : null,\n" +
                "        \"milestoneFlag\" : null,\n" +
                "        \"parentId\" : null,\n" +
                "        \"plannedStartDate\" : null,\n" +
                "        \"plannedFinishDate\" : null,\n" +
                "        \"startDate\" : null,\n" +
                "        \"finishDate\" : null,\n" +
                "        \"baselineDuration\" : null,\n" +
                "        \"lastUpdatedBy\" : null,\n" +
                "        \"lastUpdatedDate\" : null,\n" +
                "        \"activityIssuedBy\" : null,\n" +
                "        \"activityNote\" : null,\n" +
                "        \"contactInfo\" : null,\n" +
                "        \"activityCharges\" : null,\n" +
                "        \"task\" : null,\n" +
                "        \"customerAppointmentList\" : null\n" +
                "      } ],\n" +
                "      \"contact\" : null,\n" +
                "      \"location\" : null,\n" +
                "      \"person\" : null,\n" +
                "      \"customerAppointmentList\" : null,\n" +
                "      \"note\" : null,\n" +
                "      \"projectRelatedFlags\" : null,\n" +
                "      \"actionRequiredDetails\" : null,\n" +
                "      \"prjUpdateHistory\" : null,\n" +
                "      \"requestMetaData\" : null\n" +
                "    } ]\n" +
                "  }\n" +
                "}");
        m1.put(WMGConstant.CORRELATION_ID,"MANAGEDSERVER0112345699");
        List<Map<String, Object>> selectTaskDetails = new ArrayList<Map<String, Object>>();
        selectTaskDetails.add(m1);

        when(jdbcTemplate.queryForList(WMGConstant.SELECT_STUCK_REQUEST, params, types)).thenReturn(selectTaskDetails);
        when(dao.updateStuckRequestStatus(anyString())).thenReturn("SUCCESS");
        StuckRequestPollerBeanProcessor spy = PowerMockito.spy(stuckRequestPollerBeanProcessor);
        MessageProperties mp = new MessageProperties();
        mp.getHeaders().put("msgId", "MANAGEDSERVER0112345699");
        String request= "{\n" +
                "  \"standardHeader\" : {\n" +
                "    \"e2E\" : null,\n" +
                "    \"serviceState\" : {\n" +
                "      \"stateCode\" : \"OK\",\n" +
                "      \"errorCode\" : null,\n" +
                "      \"errorDesc\" : null,\n" +
                "      \"errorText\" : null,\n" +
                "      \"errorTrace\" : null,\n" +
                "      \"resendIndicator\" : true,\n" +
                "      \"retriesRemaining\" : 0,\n" +
                "      \"retryInterval\" : 0\n" +
                "    },\n" +
                "    \"serviceAddressing\" : {\n" +
                "      \"from\" : {\n" +
                "        \"queryString\" : null,\n" +
                "        \"path\" : \"/METsk\",\n" +
                "        \"host\" : \"ccm.intra.bt.com\",\n" +
                "        \"absoluteURI\" : true,\n" +
                "        \"userinfo\" : null,\n" +
                "        \"regBasedAuthority\" : null,\n" +
                "        \"scheme\" : \"http\",\n" +
                "        \"fragment\" : null,\n" +
                "        \"genericURI\" : true,\n" +
                "        \"schemeSpecificPart\" : \"//ccm.intra.bt.com/METsk\",\n" +
                "        \"port\" : -1\n" +
                "      },\n" +
                "      \"to\" : {\n" +
                "        \"address\" : {\n" +
                "          \"queryString\" : null,\n" +
                "          \"path\" : \"/wfmt\",\n" +
                "          \"host\" : \"ccm.intra.bt.com\",\n" +
                "          \"absoluteURI\" : true,\n" +
                "          \"userinfo\" : null,\n" +
                "          \"regBasedAuthority\" : null,\n" +
                "          \"scheme\" : \"http\",\n" +
                "          \"fragment\" : null,\n" +
                "          \"genericURI\" : true,\n" +
                "          \"schemeSpecificPart\" : \"//ccm.intra.bt.com/wfmt\",\n" +
                "          \"port\" : -1\n" +
                "        },\n" +
                "        \"contextItemList\" : null\n" +
                "      },\n" +
                "      \"replyTo\" : {\n" +
                "        \"address\" : {\n" +
                "          \"queryString\" : null,\n" +
                "          \"path\" : \"/METsk\",\n" +
                "          \"host\" : \"ccm.intra.bt.com\",\n" +
                "          \"absoluteURI\" : true,\n" +
                "          \"userinfo\" : null,\n" +
                "          \"regBasedAuthority\" : null,\n" +
                "          \"scheme\" : \"http\",\n" +
                "          \"fragment\" : null,\n" +
                "          \"genericURI\" : true,\n" +
                "          \"schemeSpecificPart\" : \"//ccm.intra.bt.com/METsk\",\n" +
                "          \"port\" : -1\n" +
                "        },\n" +
                "        \"contextItemList\" : null\n" +
                "      },\n" +
                "      \"relatesTo\" : null,\n" +
                "      \"faultTo\" : {\n" +
                "        \"address\" : {\n" +
                "          \"queryString\" : null,\n" +
                "          \"path\" : \"/wfmt\",\n" +
                "          \"host\" : \"ccm.intra.bt.com\",\n" +
                "          \"absoluteURI\" : true,\n" +
                "          \"userinfo\" : null,\n" +
                "          \"regBasedAuthority\" : null,\n" +
                "          \"scheme\" : \"http\",\n" +
                "          \"fragment\" : null,\n" +
                "          \"genericURI\" : true,\n" +
                "          \"schemeSpecificPart\" : \"//ccm.intra.bt.com/wfmt\",\n" +
                "          \"port\" : -1\n" +
                "        },\n" +
                "        \"contextItemList\" : null\n" +
                "      },\n" +
                "      \"messageId\" : \"1747489\",\n" +
                "      \"serviceName\" : {\n" +
                "        \"queryString\" : null,\n" +
                "        \"path\" : \"/manageNetworkPlanning\",\n" +
                "        \"host\" : \"capabilties.nat.bt.com\",\n" +
                "        \"absoluteURI\" : true,\n" +
                "        \"userinfo\" : null,\n" +
                "        \"regBasedAuthority\" : null,\n" +
                "        \"scheme\" : \"http\",\n" +
                "        \"fragment\" : null,\n" +
                "        \"genericURI\" : true,\n" +
                "        \"schemeSpecificPart\" : \"//capabilties.nat.bt.com/manageNetworkPlanning\",\n" +
                "        \"port\" : -1\n" +
                "      },\n" +
                "      \"action\" : {\n" +
                "        \"queryString\" : null,\n" +
                "        \"path\" : \"/wfmt\",\n" +
                "        \"host\" : \"capability.nat.bt.com\",\n" +
                "        \"absoluteURI\" : true,\n" +
                "        \"userinfo\" : null,\n" +
                "        \"regBasedAuthority\" : null,\n" +
                "        \"scheme\" : \"http\",\n" +
                "        \"fragment\" : \"getPlanDetails\",\n" +
                "        \"genericURI\" : true,\n" +
                "        \"schemeSpecificPart\" : \"//capability.nat.bt.com/wfmt#getPlanDetails\",\n" +
                "        \"port\" : -1\n" +
                "      }\n" +
                "    },\n" +
                "    \"serviceProperties\" : null,\n" +
                "    \"serviceSpecification\" : {\n" +
                "      \"payloadFormat\" : \"XML\",\n" +
                "      \"version\" : \"2.2\",\n" +
                "      \"revision\" : \"1\"\n" +
                "    },\n" +
                "    \"serviceSecurity\" : null\n" +
                "  },\n" +
                "  \"planRequestList\" : {\n" +
                "    \"plan\" : [ {\n" +
                "      \"projectID\" : \"NU7PT\",\n" +
                "      \"projectName\" : null,\n" +
                "      \"description\" : null,\n" +
                "      \"type\" : null,\n" +
                "      \"subType\" : null,\n" +
                "      \"status\" : null,\n" +
                "      \"correlationId\" : null,\n" +
                "      \"build\" : null,\n" +
                "      \"startDate\" : null,\n" +
                "      \"endDate\" : null,\n" +
                "      \"requestedBy\" : null,\n" +
                "      \"requestedTime\" : null,\n" +
                "      \"projectFormat\" : null,\n" +
                "      \"programmeName\" : null,\n" +
                "      \"managerResourceId\" : null,\n" +
                "      \"phase\" : null,\n" +
                "      \"size\" : null,\n" +
                "      \"action\" : \"getPlanDetails#VIEWCSP\",\n" +
                "      \"updateReason\" : null,\n" +
                "      \"projectMilestone\" : null,\n" +
                "      \"scheme\" : null,\n" +
                "      \"orderData\" : null,\n" +
                "      \"subNP\" : null,\n" +
                "      \"planningActivity\" : [ {\n" +
                "        \"activityId\" : \"WAGRFF8A\",\n" +
                "        \"activityName\" : null,\n" +
                "        \"activitydescription\" : null,\n" +
                "        \"activityType\" : null,\n" +
                "        \"activityCategory\" : null,\n" +
                "        \"subCategory\" : null,\n" +
                "        \"activityStatus\" : null,\n" +
                "        \"priority\" : null,\n" +
                "        \"criticalFlag\" : null,\n" +
                "        \"milestoneFlag\" : null,\n" +
                "        \"parentId\" : null,\n" +
                "        \"plannedStartDate\" : null,\n" +
                "        \"plannedFinishDate\" : null,\n" +
                "        \"startDate\" : null,\n" +
                "        \"finishDate\" : null,\n" +
                "        \"baselineDuration\" : null,\n" +
                "        \"lastUpdatedBy\" : null,\n" +
                "        \"lastUpdatedDate\" : null,\n" +
                "        \"activityIssuedBy\" : null,\n" +
                "        \"activityNote\" : null,\n" +
                "        \"contactInfo\" : null,\n" +
                "        \"activityCharges\" : null,\n" +
                "        \"task\" : null,\n" +
                "        \"customerAppointmentList\" : null\n" +
                "      } ],\n" +
                "      \"contact\" : null,\n" +
                "      \"location\" : null,\n" +
                "      \"person\" : null,\n" +
                "      \"customerAppointmentList\" : null,\n" +
                "      \"note\" : null,\n" +
                "      \"projectRelatedFlags\" : null,\n" +
                "      \"actionRequiredDetails\" : null,\n" +
                "      \"prjUpdateHistory\" : null,\n" +
                "      \"requestMetaData\" : null\n" +
                "    } ]\n" +
                "  }\n" +
                "}";
        Message textMessage = new Message(request.getBytes(), mp);
        stuckRequestPollerBeanProcessor.routeStuckRequest();
        verify(stuckPollerAttributeReader).getAggregationRetryLimit();
    }


    @Test
    public void routeStuckRequestForEmppalBelowRetryLimit() throws Exception {


        when(resourceManager.getManagedServer()).thenReturn("managedserver01");
        doNothing().when(dao).updateServerLookupLastProcessdate("managedserver01");
        when(stuckPollerAttributeReader.getBatchSizeAggregation()).thenReturn("15");
        when(stuckPollerAttributeReader.getAggregationRetryLimit()).thenReturn("3");

        Object[] params = new Object[]{"managedserver01", "15"};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};

        Map<String,Object> m2 = new HashMap<>();
        m2.put(WMGConstant.MET_REF_NUMBER,"668");
        m2.put(WMGConstant.AGG_NAME,"EMPPAL");
        m2.put(WMGConstant.AGG_RETRY_COUNT,"2");
        m2.put(WMGConstant.RETRY_STATUS,"RETRY");
        m2.put(WMGConstant.AGG_XML,"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<xsl:stylesheet version=\"1.0\"\n" +
                "\txmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
                "\t<xsl:param name=\"messageId\" />\n" +
                "\t<xsl:param name=\"estimateId\" />\n" +
                "\t<xsl:template match=\"createTaskRequest\">\n" +
                "\t\t<xsl:call-template name=\"empPalRequest\" />\n" +
                "\t</xsl:template>\n" +
                "\t<xsl:template match=\"amendTaskRequest\">\n" +
                "\t\t<xsl:call-template name=\"empPalRequest\" />\n" +
                "\t</xsl:template>\n" +
                "\t<xsl:template name=\"empPalRequest\">\n" +
                "\t\t<orm:storesInformation xmlns:orm=\"http://ccm.intra.bt.com/METsk/orm\">\n" +
                "\t\t\t<orm:header>\n" +
                "\t\t\t\t<orm:mtos>http://wsi.nat.bt.com/2005/06/StandardHeader/</orm:mtos>\n" +
                "\t\t\t\t<orm:schemaLocation>http://capabilities.nat.bt.com/xsd/storesInformation/2014/09/28E:\\storesInformation\\xsd\\storesInformation.20140928.xsd</orm:schemaLocation>\n" +
                "\t\t\t\t<xsl:if test=\"standardHeader/e2e/E2EDATA!=''\">\n" +
                "\t\t\t\t\t<E2EDATA>\n" +
                "\t\t\t\t\t\t<xsl:value-of select=\"standardHeader/e2e/E2EDATA\" />\n" +
                "\t\t\t\t\t</E2EDATA>\n" +
                "\t\t\t\t</xsl:if>\n" +
                "\t\t\t\t<orm:stateCode>OK</orm:stateCode>\n" +
                "\t\t\t\t<orm:errorCode>0</orm:errorCode>\n" +
                "\t\t\t\t<orm:errorDesc>Success</orm:errorDesc>\n" +
                "\t\t\t\t<orm:errorText>Success</orm:errorText>\n" +
                "\t\t\t\t<orm:fromAddress>http://ccm.intra.bt.com/METsk</orm:fromAddress>\n" +
                "\t\t\t\t<orm:toAddress>http://EMPPAL</orm:toAddress>\n" +
                "\t\t\t\t<xsl:if test=\"$messageId!=''\">\n" +
                "\t\t\t\t\t<orm:messageId>\n" +
                "\t\t\t\t\t\t<xsl:value-of select=\"$messageId\" />\n" +
                "\t\t\t\t\t</orm:messageId>\n" +
                "\t\t\t\t</xsl:if>\n" +
                "\t\t\t\t<orm:action>http://capabilities.nat.bt.com/xsd/storesInformation/2014/09/28#requestStoresInformation</orm:action>\n" +
                "\t\t\t</orm:header>\n" +
                "\t\t\t<orm:body>\n" +
                "\t\t\t\t<xsl:if test=\"$estimateId!=''\">\n" +
                "\t\t\t\t\t<orm:estimateNumber>\n" +
                "\t\t\t\t\t\t<xsl:value-of select=\"$estimateId\" />\n" +
                "\t\t\t\t\t</orm:estimateNumber>\n" +
                "\t\t\t\t</xsl:if>\n" +
                "\t\t\t</orm:body>\n" +
                "\t\t</orm:storesInformation>\n" +
                "\t</xsl:template>\n" +
                "</xsl:stylesheet>");
        m2.put(WMGConstant.CORRELATION_ID,"MANAGEDSERVER0112345678");

        List<Map<String, Object>> selectTaskDetails = new ArrayList<Map<String, Object>>();
        selectTaskDetails.add(m2);

        when(jdbcTemplate.queryForList(WMGConstant.SELECT_STUCK_REQUEST, params, types)).thenReturn(selectTaskDetails);
        when(dao.updateStuckRetryRequest(any(Timestamp.class),anyInt(),anyString())).thenReturn("UPDATED_SUCCESS");
        MessageProperties mp = new MessageProperties();
        mp.getHeaders().put("msgId", "MANAGEDSERVER0112345678");
        stuckRequestPollerBeanProcessor.routeStuckRequest();
        verify(stuckPollerAttributeReader).getAggregationRetryLimit();
    }
    @After
    public void tearDown() {
        stuckPollerAttributeReader = null;
        simpleMessageConverter = null;
        stuckRequestPollerBeanProcessor = null;

    }

}
