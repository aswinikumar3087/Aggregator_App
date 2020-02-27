package com.bt.metb.aggregator.util;

import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.exception.METBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.*;


@SuppressWarnings({"rawtypes", "unchecked"})
@Component("resourceManager")
public class METResourceManager implements METResourceManagerInterface
{
    private static final Logger logger = LoggerFactory.getLogger(METResourceManager.class);
    private Map allProperties = null;
    private Map allXSLs = null;

    public METResourceManager() throws Exception{

        if ((null == allProperties))
        {
            allProperties = new HashMap();
            allXSLs = new Hashtable();

            // Load xsl files
            FileUtil.loadAllXSLs(WMGConstant.XSL_FOLDER_PATH, allXSLs);
            checkALLXSLFilesExists();
            setManageServerID();
        }
    }

    private void checkALLXSLFilesExists() throws METBException
    {

        LinkedList xslList = new LinkedList();
        xslList.add(WMGConstant.STRIP_XSD_NAMESPACE);
        xslList.add(WMGConstant.CREATE_RESPONSE_XSL);
        xslList.add(WMGConstant.AMEND_RESPONSE_XSL);
        xslList.add(WMGConstant.CANCEL_RESPONSE_XSL);
        xslList.add(WMGConstant.CREATE_TASK_REQUEST_WMIG_XSL);
        xslList.add(WMGConstant.AMEND_TASK_REQUEST_WMIG_XSL);
        xslList.add(WMGConstant.CANCEL_TASK_REQUEST_WMIG_XSL);
        xslList.add(WMGConstant.NOTIFICATION_ACKNOWLEDGEMENT_WMIG_XSL);
        xslList.add(WMGConstant.NOTIFICATION_WMIG_XSL);
        xslList.add(WMGConstant.MSI_CREATE_REQUEST_XSL);
        xslList.add(WMGConstant.MSI_RESPONSE_XSL);
        xslList.add(WMGConstant.MSI_AMEND_REQUEST_XSL);
        xslList.add(WMGConstant.STATUS_UPDATE_TASK_REQUEST_WMIG_XSL);
        xslList.add(WMGConstant.STATUS_UPDATE_RESPONSE_FOR_AMEND_REQUEST_XSL);
        xslList.add(WMGConstant.REQUEST_PARAM_XSL);
        xslList.add(WMGConstant.NOTIFICATION_PARAM_XSL);
        xslList.add(WMGConstant.AGGREGATION_INTERNAL_RESPONSE_XSL);
        xslList.add(WMGConstant.MSD_CREATE_REQUEST_XSL);
        xslList.add(WMGConstant.MNSD_RESPONSE_XSL);
        xslList.add(WMGConstant.MNSD_RESPONSE_FTTP_XSL);
        xslList.add(WMGConstant.MSI_CREATE_SEARCH_REQUEST_XSL);
        xslList.add(WMGConstant.MSI_SEARCH_SERVICE_RESPONSE_XSL);
        xslList.add(WMGConstant.MNPL_RESPONSE_XSL);
        xslList.add(WMGConstant.PMMIS_AMEND_TASK_REMOVE_FSITAGS);
        xslList.add(WMGConstant.NOTIFICATION_WMIG_XSL_4X);

        logger.debug("METResourceManager.checkALLXSLFilesExists() CHECKING XSL-LIST:{} START:",xslList);

        for (Iterator iter = xslList.iterator(); iter.hasNext();)
        {
            String element = (String) iter.next();
            if (!allXSLs.containsKey(element))
            {
            	logger.error("METResourceManager.checkALLXSLFilesExists() EXCEPTION-OCCURRED NO XSL TEMPLATE FILE FOUND WITH NAME:{}",element);

                throw new METBException(WMGConstant.PROPERTIES_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,"METResourceManager :: Constructor ::",
                    " No XSL Template file found with name" + element);
            }
        }

        logger.debug("METResourceManager.checkALLXSLFilesExists() XSL FILES:{} EXIST RETURN:",xslList);
    }

    public String getManagedServer()
    {
        String  strReturnValue = (String) allProperties.get(WMGConstant.MS_ID);
        logger.debug("METResourceManager.getManagedServer() KEY:{} VALUE:{} RETURN:",WMGConstant.MS_ID, strReturnValue);

        return strReturnValue;
    }

    public String getProperty(String strPropertyFileName, String pStrKey) throws METBException
    {
    	String strKey = pStrKey;
        if (StringUtil.isNullOrEmpty(strPropertyFileName) || StringUtil.isNullOrEmpty(strKey))
        {

            logger.error("METResourceManager.getProperty() EXCEPTION-OCCURRED PROPERTY-FILE-NAME:{} OR KEY:{} IS NULL OR EMPTY", strPropertyFileName,pStrKey);

            throw new METBException(
                WMGConstant.MET_NULL_KEY_EXC_ESMF, WMGConstant.TECHNICAL_EXCEPTION,
                "METResourceManager :: getProperty :: ",
                "Received null parameters");
        }

        String strReturnValue = null;
        Properties propFile = (Properties) allProperties.get(strPropertyFileName);

        if (propFile != null)
        {
            strKey = strKey.replaceAll(" ", "_");
            strReturnValue = propFile.getProperty(strKey);

            if ((null != strReturnValue) && (strReturnValue.trim().length() > 0))
            {
                strReturnValue = strReturnValue.trim();
            }
            else
            {
            	logger.error("METResourceManager.getProperty() EXCEPTION-OCCURRED PROPERTY-FILE-NAME:{}  KEY:{} VALUE IS NULL OR EMPTY OR MISSING", strPropertyFileName,pStrKey);

                throw new METBException(
                    WMGConstant.MET_NULL_KEY_EXC_ESMF, WMGConstant.TECHNICAL_EXCEPTION,
                    "METResourceManager :: getProperty :: Property ",
                    strKey + " is null/invalid in property file " + strPropertyFileName);
            }
        }
        else
        {
        	logger.error("METResourceManager.getProperty() EXCEPTION-OCCURRED PROPERTY-FILE-NAME:{} KEY:{} NO SUCH PROPERTY-FILE", strPropertyFileName,pStrKey);

            throw new METBException(
                WMGConstant.PROPERTIES_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                "METResourceManager :: getProperty :: Property file ",
                strPropertyFileName + " not found.");
        }

        return strReturnValue;
    }

    public void setManageServerID() throws METBException
    {
        String managedServeName = System.getProperty("weblogic.Name");
        logger.debug("METResourceManager.setManageServerID() MANAGED-SERVER-NAME:::: {} ", managedServeName);

        allProperties.put(WMGConstant.MS_ID, managedServeName);

        if (managedServeName==null || "".equals(managedServeName.trim())){

                logger.error("METResourceManager.setManageServerID() UNABLE TO SET MANAGED SERVER NAME");

                throw new METBException(
                        WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION,
                        "METTResourceManager.getManageServerID() UNABLE TO SET MANAGED SERVER NAME ",
                        "UNABLE TO SET MANAGED SERVER NAME");
            }
    }

      public DataSource getDataSource(String dataSourceName) throws METBException {

        try {
            InitialContext initialContext = new InitialContext();
            return  (DataSource) initialContext.lookup(dataSourceName);
        } catch (NamingException exception) {
            throw new METBException(WMGConstant.NAMINGEXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION, "METTResourceManager.getDataSource()", WMGUtil.getStackTrace(exception));
        } catch (Exception exception) {
            throw new METBException(WMGConstant.UNKNOWN_EXCEPTION_ERRCODE, WMGConstant.TECHNICAL_EXCEPTION, "METTResourceManager.getDataSource()", WMGUtil.getStackTrace(exception));
        }
    }
}