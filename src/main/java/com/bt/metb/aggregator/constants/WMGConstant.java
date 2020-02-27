package com.bt.metb.aggregator.constants;

public final class WMGConstant
{
    private WMGConstant() {
    }
    public static final String OTHER_EXC_ESMF = "WMG_0000";

    public static final String TRANSFORMER_EXC_ESMF = "WMG_5006";

    public static final String TRANSFORMER_EXC_JM = "2005";

    public static final String OTHER_EXC_JM = "2005";

    public static final String NULL_KEY_EXC_JM = "9999";

    public static final String NULL_KEY_EXC_ESMF = "WMG_1008";

    public static final String STRING_TRUE = "true";

    public static final String WMG_DOMAIN_ID = "WMG_DOMAIN_ID";

    public static final String EMPTYSTRING = "";

    public static final String RETRY = "RETRY";

    public static final String REQCONT = "REQCONT";

    public static final String AGG_RETRY_COUNT = "AGG_RETRY_COUNT";

    public static final String RETRY_STATUS = "RETRY_STATUS";

    public static final String AGG_XML = "AGG_XML";

    public static final String COLON = ":";

    public static final String TASK_CATEGORY = "TaskCategory";

    public static final String STRING_TRUE_CAPS = "TRUE";

    public static final String WMIG_DUMMY_XSL = "WMIG_DUMMY_XSL.xsl";

    public static final String TASK_TYPE_ID = "TaskTypeID";

    public static final String SUCCESS = "SUCCESS";

    public static final String FAILURE = "FAILURE";

    public static final String AUDITLOG_PROGRESSMSG_CODE = "0001";

    public static final String UNKNOWN_EXCEPTION_ERRCODE = "MET_0000";

    public static final String NAMINGEXCEPTION_ERRCODE = "MET_1015";

    public static final String TECHNICAL_EXCEPTION = "Technical";

    public static final String PROPERTIES_ERRCODE = "MET_1007";

    public static final String XSL_FOLDER_PATH = "./applications/metb/xsl/";

    public static final String XSL_EXTENSION = ".xsl";

    public static final String NULL_KEY_EXC_ESMF_MSG = "Encountered Null Values";

    public static final String CREATE_REQUEST_OPERATION ="CreateTask";

    public static final String AMEND_REQUEST_OPERATION ="AmendTask";

    public static final String CANCEL_REQUEST_OPERATION ="CancelTask";

    public static final String UPDATE_TASK_REQUEST_OPERATION ="UpdateTask";

    public static final String XSD_VALIDATION_FAILED_CODE = "1001";

    public static final String XSD_VALIDATION_FAILED_DESC = "Order data corrupted";

    public static final String STRIP_XSD_NAMESPACE = "Strip_XSD_Namespace.xsl";

    public static final String PMMIS_AMEND_TASK_REMOVE_FSITAGS = "RemoveFSITagFromWM.xsl";

    public static final String CREATE_RESPONSE_XSL = "CreateTaskResponse.xsl";

    public static final String AMEND_RESPONSE_XSL = "AmendTaskResponse.xsl";

    public static final String CANCEL_RESPONSE_XSL = "CancelTaskResponse.xsl";

    public static final String CREATE_TASK_REQUEST_WMIG_XSL = "CreateTaskRequestMETtoWMIG.xsl";

    public static final String AMEND_TASK_REQUEST_WMIG_XSL = "AmendTaskRequestMETtoWMIG.xsl";

    public static final String CANCEL_TASK_REQUEST_WMIG_XSL = "CancelTaskRequestMETtoWMIG.xsl";

    public static final String FROM_ADDRESS = "http://ccm.intra.bt.com/METsk";

    public static final String SOURCE_FOR_DIPLOMAT = "MET";

    public static final String PAL_TO_ADDRESS = "http://ccm.intra.bt.com/NEO";

    public static final String RESPONSE_TO_PAL = "http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskResponse";

    public static final String AMEND_RESPONSE_TO_PAL = "http://capabilities.intra.bt.com/ManageEngineeringTask#AmendTaskResponse";

    public static final String UPDATE_RESPONSE_TO_PAL = "http://capabilities.intra.bt.com/ManageEngineeringTask#UpdateTaskResponse";

    public static final String CANCEL_RESPONSE_TO_PAL = "http://capabilities.intra.bt.com/ManageEngineeringTask#CancelTaskResponse";

    public static final String AGGREGATIONTYPE = "AGGREGATIONTYPE";

    public static final String TF_CREATE_RESPONSE = "taskInitiateResponse";

    public static final String TF_AMEND_RESPONSE = "taskModifyResponse";

    public static final String TF_CANCEL_RESPONSE = "taskCancelResponse";

    public static final String BIP_BUSINESS_ERROR_CODE = "1011";

    public static final String MNSD_BUSINESS_ERROR_CODE = "1010";

    public static final String COMMA_DELIMITER = ",";

    public static final String MLI_BUSINESS_ERROR_CODE = "1008";

    public static final String PMMIS_BUSINESS_ERROR_CODE = "1009";

    public static final String NOTIFICATION_ACKNOWLEDGEMENT_WMIG_XSL = "updateTaskAcknowledgement.xsl";

    public static final String NOTIFICATION_WMIG_XSL = "UpdateTaskNotificationRequestWMIG.xsl";

    public static final String NOTIFICATION_WMIG_XSL_4X = "UpdateTaskNotificationRequestWMIG_4x.xsl";

    public static final String FAILED_STATUS = "FAILED";

    public static final String TRUE = "true";

    public static final String MAC_BUSINESS_ERROR_CODE = "1021";

    public static final String MPIC_BUSINESS_ERROR_CODE = "1018";

    public static final String MCP_BUSINESS_ERROR_CODE = "MET_1024";

    public static final String STAA_GETREPAIRDETAILS_BUSINESS_ERROR_CODE = "1023";

    public static final String STAA_DISPLAYFRAMETERMINATIONS_BUSINESS_ERROR_CODE = "1025";

    public static final String MET_EWOCS_DESTINATION_TYPE = "METEWOCS";

    public static final String MSI_CREATE_REQUEST_XSL = "createRequest_MSI.xsl";

    public static final String MSI_CREATE_SEARCH_REQUEST_XSL = "MSISearchServiceRequest.xsl";

    public static final String MSI_RESPONSE_XSL = "msiResponse.xsl";

    public static final String MSI_BUSINESS_ERROR_CODE = "1031";

    public static final String MET_DEFAULT_DATA_EXCEPTION = "MET_Default_Data";

    public static final String MSI_AMEND_REQUEST_XSL = "amendRequest_MSI.xsl";

    public static final String MET_ERRCODE = "MET_1035";

    public static final String STATUS_UPDATE_TASK_REQUEST_WMIG_XSL = "TaskStatusUpdateRequestMETtoWMIG.xsl";

    public static final String TF_STATUS_UPDATE_RESPONSE = "taskStatusUpdateResponse";

    public static final String STATUS_UPDATE_RESPONSE_FOR_AMEND_REQUEST_XSL = "TaskStatusUpdateResponse.xsl";

    public static final String STATUS_UPDATE_RESPONSE_FOR_UPDATE_REQUEST_XSL = "TaskStatusUpdateResponseForUpdateRequest.xsl";

    public static final String MNSD_RESPONSE_XSL = "mnsdResponse.xsl";

    public static final String MNSD_RESPONSE_FTTP_XSL = "mnsdResponse_FTTP.xsl";

    public static final String ORMNSD_BUSINESS_ERROR_CODE = "1033";

    public static final String REQUIREMENT_BUSINESS="REQUIREMENT_BUSINESS";

    public static final String REQUIREMENT_TECHNICAL="REQUIREMENT_TECHNICAL";

    public static final String CREATE_ACTION="http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskRequest";

    public static final String AMEND_ACTION="http://capabilities.intra.bt.com/ManageEngineeringTask#AmendTaskRequest";

    public static final String CANCEL_ACTION="http://capabilities.intra.bt.com/ManageEngineeringTask#CancelTaskRequest";

    public static final String UPDATE_ACTION="http://capabilities.intra.bt.com/ManageEngineeringTask#UpdateTaskRequest";

    public static final String PAL_DATE_FORMAT="dd/MM/yyyy HH:mm:ss";

    public static final String FH_DATE_FORMAT="dd/MM/yyyy";

    public static final String WMIG_DATE_FORMAT_APPDATE="yyyyMMdd";

    public static final String WMIG_DATE_FORMAT_AUTHTIME="HHmm";

    public static final String PAL_MET_ACCESSD_TIME_FORMAT = "HH:mm:SS";

    public static final String WMIG_ACCESSD_TIME_FORMAT = "HHmmSS";

    public static final String REQUEST_PARAM_XSL = "RequestParam.xsl";

    public static final String PRODUCT_TYPE ="ProductType";

    public static final String TASK_MESSAGEID ="TaskMessageId";

    public static final String CARELEVEL ="CareLevel";

    public static final String REPLY_TO_ADDRESS ="ReplyToAddress";

    public static final String ACTION_TYPE ="ActionType";

    public static final String TASK_LOB ="TaskLOB";

    public static final String STATUS_UPDATE_TASK_REQUEST="StatusUpdateTaskRequest";

    public static final String GANG_DETAILS ="GangDetails";

    public static final String NOTIFICATION_PARAM_XSL = "NotificationParameters.xsl";

    public static final String AGG_IGR_STATUS ="AGG_IGR";

    public static final String AGG_RES_STATUS ="AGG_RES";

    public static final String AGG_ITR_STATUS ="AGG_ITR";

    public static final String AGG_TE_STATUS ="AGG_TE";

    public static final String AGG_BE_STATUS ="AGG_BE";

    public static final String AGGREGATION_INTERNAL_RESPONSE_XSL ="aggInternalResponse.xsl";

    public static final String AGGREGATION_STATUS = "AGG_STATUS";

    public static final String AGG_STATUS_TECHNICAL_MSG = "Technical Exception is optional";

    public static final String AGG_STATUS_BUSINESS_MSG = "Business Exception is optional";

    public static final String AGG_FAILURE = "FAILURE";

    public static final String SELECT_STUCK_REQUEST = "SELECT TD.MET_REF_NUMBER, TD.AGG_NAME, TD.CORRELATION_ID, TD.AGG_RETRY_COUNT, TD.RETRY_STATUS,TADM.AGG_XML FROM TASKDETAILS TD INNER JOIN \n" +
            "TASKDETAILS_AGG_MESSAGE TADM ON TD.MET_REF_NUMBER=TADM.MET_REF_NUMBER\n" +
            "WHERE TD.RETRY_STATUS IN ('RETRY') AND TD.REQUEST_STATUS IN ('AGG_REQ','AGG_TE') AND TADM.TYPE_OF_AGGREGATION=TD.AGG_NAME AND TD.SERVER_NAME = ? AND rownum <= ?";

    public static final String AGGREGATOR_RETRY="RETRY";

    public static final String AGGREGATOR_REQCONT="REQCONT";

    public static final String AGG_CONTINUE = "CONTINUE";

    public static final String AGG_REQ_STATUS ="AGG_REQ";

    public static final String AGG_STATUS = "STATUS";

    public static final String INTERFACE_PROTOCOL = "INTERFACE_PROTOCOL";

    public static final String INTERFACE_PROTOCOL_MQ = "MQ";

    public static final String AGGREGATION_ASYNCH_FLAG = "ASYNCHRONOUS";

    public static final String CONTINUE = "CONTINUE";

    public static final String IS_RESUME_FLOW = "isResumeFlow";

    public static final String MSD_CREATE_REQUEST_XSL="createMsdRequest.xsl";

    public static final String XML_DATE_TIME_FORMAT="yyyy-MM-dd'T'HH:mm:ss";

    public static final String XML_DATE_FORMAT="yyyy-MM-dd";

    public static final String XML_TIME_FORMAT="HH:mm:ss";

    public static final String MANUAL_INTERVENTION_ERROR_CODE = "MET_1038";

    public static final String ERROR_CODE_TO_REJECT_AGGREGATION_RESPONSE = "MET_1039";

    public static final String ERROR_CODE_TO_REJECT_AGGREGATION_RESPONSE_DESC = "Response is already processed";

    public static final String MPC_BUSINESS_ERROR_CODE = "1038";

    public static final String MSI_SEARCH_SERVICE_RESPONSE_XSL = "MSISearchServiceResponse.xsl";

    public static final String SUCCESS_ERROR_CODE = "0";

    public static final String IGNORE_EXCEPTION = "IGNORE_EXCEPTION";

    public static final String IGNORE_MESSAGE = "Ignore_Message";

    public static final String MANDATORY = "M";

    public static final String OPTIONAL = "O";

    public static final String CORRELATION_ID = "CORRELATION_ID";

    public static final String TASK_TYPE ="TaskTypeId";

    public static final String MNPL_RESPONSE = "MNPLResponse";

    public static final String STATUS = "status";

    public static final String MET_NULL_KEY_EXC_ESMF = "MET_1010";

    public static final String MET_SERVICE_NAME = "http://capabilities.nat.bt.com/ManageEngineeringTask";

    public static final String MNPL = "MNPL";

    public static final String PROJECT_ID = "ProjectId";

    public static final String ESTIMATE_ID = "EstimateId";

    public static final String ORMNPL_BUSINESS_ERROR_CODE = "1047";

    public static final String ORMNPL_BUSINESS_ERROR_DESC = "Unable to get projectId related details";

    public static final String ORMNPL_TECHNICAL_ERROR_CODE = "1048";

    public static final String ORMNPL_TECHNICAL_ERROR_DESC = "Technical error from METask Broker due to MNPL call - getPlanDetailsRequest";

    public static final String MNPL_RESPONSE_XSL = "mnplResponse.xsl";

    public static final String ACTION_ID = "getPlanDetails#VIEWCSP";

    public static final String WMG_DEFAULT_FAILURE_LOCATION="WMGATEWAY";

    public static final String EMPPAL_AGGREGATOR_NAME="EMPPAL";

    public static final String EMPPAL_REQUEST_XSL = "EMPPALRequest.xsl";

    public static final String EMPPAL_RESPONSE_XSL = "EMPPALResponse.xsl";

    public static final String EMPPAL_TECHNICAL_ERROR_CODE = "1014";

    public static final String EMPPAL_TECHNICAL_ERROR_DESC = "Technical error from MET Broker due to EMPPAL problem";

    public static final String EMPPAL = "EMPPAL";

    public static final String RETRY_COUNT_IN_HEADER = "RetryCount";

    public static final String EMPPAL_BUSINESS_ERROR_CODE = "1008";

    public static final String EMPPAL_BUSINESS_ERROR_DESC = "Business Error for EMPPAL Data Aggregation";

    public static final String EMPPAL_KEY = "emppalResponse";

    public static final String EMPPAL_MET_DEFAULT_DATA_EXCEPTION_ERROR_CODE = "MET_1027";

    public static final String EMPPAL_MET_DEFAULT_DATA_EXCEPTION_ERROR_DESC = "Default Data Error for MSI Data Aggregation";

    public static final String INTERFACE_PROTOCOL_JMS = "JMS";

    public static final String PLOT_ID = "PlotId";

    public static final String CSP_ID = "CSPId";

    public static final String SPLITTER_ID = "SplitterId";

    public static final String AGGREGATION_BUSINESSERROR_SKIP ="AGGREGATION_BUSINESSERROR_SKIP";

    public static final String AGGREGATION_IGNORE_SKIP ="AGGREGATION_IGNORE_SKIP";

    public static final String AGGREGATION_BUSINESSERROR="Null values Received from Input Request or some bussiness error occured";

    public static final String MNPL_BUSINESS_ERROR_CODE = "1047";

    public static final String MNPL_BUSINESS_ERROR_DESC = "Unable to get project id related details for MNPL Data Aggregation";

    public static final String DELIBERATE_SKIP="DeliberateSkip";

    public static final String MET_REF_NUM="metTaskIdentifier";

    public static final String INTERFACE_PROTOCOL_WS = "WS";

    public static final String MSG_ID = "msgId";

    public static final String MESSAGE_ID = "messageId";

    public static final String REQUEST_MAP = "RequestMap";

    public static final String UPDATEREQUESTSTATUS ="updateRequestStatus";

    public static final String AGG_NAME="AGG_NAME";

    public static final String MET_REF_NUMBER = "MET_REF_NUMBER";

    public static final String BUSINESS_EXCEPTION = "Business";

    public static final String SQL_EXCEPTION = "MET_ORA_5001";

    public static final String INVALID_OR_NULL_SMNT_SYSTEM_FAILED_DESC = "Null or Invalid SMNT System found";

    public static final String STAA_GETEXCHANGEDETAILS_BUSINESS_ERROR_CODE = "1024";

    public static final String INVALID_OR_NULL_SMNT_SYSTEM_ERROR_CODE = "MET_1033";

    public static final String MS_ID = "MS_ID";
}
