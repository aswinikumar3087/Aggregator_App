package com.bt.metb.aggregator.util;

import com.bt.metb.aggregator.constants.DaoConstants;
import com.bt.metb.aggregator.exception.METBException;
import oracle.sql.StructDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StructDescriptor.class, DBLoading.class})
public class DBLoadingTest {


    @Mock
    PreparedStatement preparedStatement;

    @Mock
    Connection connection;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    DBLoading dbLoading;

    private static Map hashMapSmntAggregator = new HashMap();

    private static Map hashMapSrcSystemAggregator = new HashMap();

    private static Map hashMapAggDataAggregator = new HashMap();

    private static Map hashMapAggMasterDataAggregator = new HashMap();

    @Before
    public void setup() throws  SQLException {
        MockitoAnnotations.initMocks(this);
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);

    }

    @Test
    public void getSourceSystemFromDBTest() throws METBException {
        PowerMockito.mockStatic(StructDescriptor.class);
        PowerMockito.mockStatic(DBLoading.class);
        List<Map<String, Object>> aggregationdataList = new ArrayList<Map<String, Object>>();
        when(jdbcTemplate.queryForList(anyString())).thenReturn(aggregationdataList);
        dbLoading.loadDB();
    }

    @Test
    public void getAggregationData() throws METBException {
        hashMapAggDataAggregator.put("038","038:CREATE");
        dbLoading.getAggregationData("038","CREATE");
    }

    @Test
    public void getSMNTSystem() throws METBException {
        hashMapSmntAggregator.put("EO","EO:http:\\create");
        dbLoading.getSMNTSystem("EO","http:\\create");
    }

    @Test
    public void getAggregationMainData() throws METBException {
        hashMapAggMasterDataAggregator.put("EO","COMPLEX:FULFILLMENT:OpenReach");
        dbLoading.getAggregationMainData("COMPLEX","FULFILLMENT","OpenReach");
    }

}
