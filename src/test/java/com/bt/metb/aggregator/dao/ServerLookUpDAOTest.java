package com.bt.metb.aggregator.dao;


import com.bt.metb.aggregator.exception.METBException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Types;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerLookUpDAOTest {

    static final String UPDATE_SERVER_STATUS_QUERY = "UPDATE SERVERLOOKUP SET STATUS = ? WHERE SERVERNAME = ?";

    static final String SERVER_STATUS = "VALID";

    @InjectMocks
    ServerLookUpDAO serverLookUpDAO;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() throws METBException {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = METBException.class)
    public void updateServerStatusWithException() throws METBException {
        serverLookUpDAO.updateServerStatus("managedserver_01");
    }

    @Test
    public void updateServerStatusSuccess() throws METBException {

        Object[] params = new Object[]{SERVER_STATUS, "managedserver_01"};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};
        when(jdbcTemplate.update(UPDATE_SERVER_STATUS_QUERY, params, types)).thenReturn(1);
        serverLookUpDAO.updateServerStatus("managedserver_01");
    }
}
