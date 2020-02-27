package com.bt.metb.aggregator.dao;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.exception.METBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
public class ServerLookUpDAO {

     static final String UPDATE_SERVER_STATUS_QUERY = "UPDATE SERVERLOOKUP SET STATUS = ? WHERE SERVERNAME = ?";

     static final String SERVER_STATUS = "VALID";

    private static final String CLASSNAME = "ServerLookUpDAO";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ServerLookUpDAO(){
    }
    private static final Logger log = LoggerFactory.getLogger(ServerLookUpDAO.class);
    public boolean updateServerStatus(String serverName) throws METBException {

        log.debug("ServerLookUpDAO.updateServerStatus() EXECUTING:{} SERVER-NAME:{} ",UPDATE_SERVER_STATUS_QUERY,serverName);
        final String METHOD_NAME = "updateServerStatus";

        boolean successfullyUpdated = false;
        Object[] params = new Object[]{SERVER_STATUS, serverName};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};

        try {
            int noOfRows = jdbcTemplate.update(UPDATE_SERVER_STATUS_QUERY, params, types);
            log.debug("ServerLookUpDAO.updateServerStatus() : Number of rows affected :{}", noOfRows);
            successfullyUpdated = noOfRows > 0;
            if (noOfRows <= 0) {
                throw new METBException(WMGConstant.SQL_EXCEPTION, WMGConstant.TECHNICAL_EXCEPTION, CLASSNAME
                        + WMGConstant.COLON + METHOD_NAME
                        + WMGConstant.COLON
                        , "  Unable to store request Status due to unknown reasons");
            }

        } catch (DataAccessException dex) {
            log.error("EXCEPTION-OCCURRED \nSQL:{}  \nINPUT-PARAMS: 1:{}  2:{} \nSTACK-TRACE:",UPDATE_SERVER_STATUS_QUERY, SERVER_STATUS,serverName,dex);

        }
        log.debug("ServerLookUpDAO.updateServerStatus() SUCCESSFULLY UPDATED SERVER LOOKUP TABLE WITH SERVER-NAME:{}",serverName);
        return successfullyUpdated;
    }

}
