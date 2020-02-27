package com.bt.metb.aggregator.dao;

import com.bt.metb.aggregator.constants.DaoConstants;
import com.bt.metb.aggregator.constants.WMGConstant;
import com.bt.metb.aggregator.exception.METBException;
import com.bt.metb.aggregator.util.METResourceManagerInterface;
import oracle.jdbc.OraclePreparedStatement;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DAOTest {

    @Mock
    PreparedStatement preparedStatement;

    @Mock
    OraclePreparedStatement oraclePreparedStatement;

    @Mock
    Connection connection;

    @Mock
    CallableStatement callableStmt;
    @Mock
    private METResourceManagerInterface resourceManager;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetExtractor<Map<String,Object>> resultSetExtractor;

    @InjectMocks
    DAO dao;

    String internalResponseMap="{EMPPAL={AGG_STATUS=Business Exception is optional}, aggregationSt\n" +
            "atus={STATUS=REQCONT}, AGG_NAME={REQUIREMENT_TECHNICAL=O, AGG_NAME=MNPL, REQUIREMENT_BUSINESS=O}, RequestMap={ReplyToAddress=http://ccm.intra.bt.com/EWOCS, ActionType=http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskReques\n" +
            "t, PrimaryCommLatestStart=24/10/2019 07:00:00, TaskIdentifier=KUMAR19, TaskMessageId=49211449, ReqResponseCode=NONE, TaskTypeID=COMPBDUK, ReqReportedDateTime=25/09/2019 10:14:54, TaskLOB=Openreach, ValidatedXML=<?xml version=\"1.0\" encodin\n" +
            "g=\"UTF-8\"?><Envelope>\n" +
            "        <Body>\n" +
            "                <createTaskRequest>\n" +
            "                        <standardHeader>\n" +
            "                                <e2e>\n" +
            "                                        <E2EDATA>E2E.busTxnHdr=PCK002069</E2EDATA>\n" +
            "                                </e2e>\n" +
            "                                <serviceState>\n" +
            "                                        <stateCode>OK</stateCode>\n" +
            "                                </serviceState>\n" +
            "                                <serviceAddressing>\n" +
            "                                        <from>http://ccm.intra.bt.com/EWOCS</from>\n" +
            "                                        <to>\n" +
            "                                                <address>http://ccm.intra.bt.com/METsk</address>\n" +
            "                                        </to>\n" +
            "                                        <replyTo>\n" +
            "                                                <address>http://ccm.intra.bt.com/EWOCS</address>\n" +
            "                                                <contextItemList>\n" +
            "                                                        <contextItem contextId=\"http://ccm.intra.bt.com/EWOCS\" contextName=\"serviceType\">EWOCS</contextItem>\n" +
            "                                                        <contextItem contextId=\"http://ccm.intra.bt.com/EWOCS\" contextName=\"instanceId\">EO</contextItem>\n" +
            "                                                        <contextItem contextId=\"http://ccm.intra.bt.com/EWOCS\" contextName=\"taskIdentifier\">3379685</contextItem>\n" +
            "                                                </contextItemList>\n" +
            "                                        </replyTo>\n" +
            "                                        <messageId>49211449</messageId>\n" +
            "                                        <serviceName>http://capabilities.nat.bt.com/ManageEngineeringTask</serviceName>\n" +
            "                                        <action>http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskRequest</action>\n" +
            "                                </serviceAddressing>\n" +
            "                                <serviceSpecification>\n" +
            "                                        <payloadFormat>XML</payloadFormat>\n" +
            "                                        <version>1.0</version>\n" +
            "                                        <revision>0</revision>\n" +
            "                                </serviceSpecification>\n" +
            "                        </standardHeader>\n" +
            "                        <task>\n" +
            "                                <telephoneNumber>3379685</telephoneNumber>\n" +
            "                                <expectedFinishDateTime>24/10/2019 07:00:00</expectedFinishDateTime>\n" +
            "                                <secondaryCommitmentLatestFinish>24/10/2019 23:59:00</secondaryCommitmentLatestFinish>\n" +
            "                                <responseIndicator>NONE</responseIndicator>\n" +
            "                                <workLocationQualifier>C</workLocationQualifier>\n" +
            "                                <importantClass>18</importantClass>\n" +
            "                                <jIN>\n" +
            "                                        <name>TASKIDENTIFIER</name>\n" +
            "                                        <value>KUMAR19</value>\n" +
            "                                </jIN>\n" +
            "                                <associateTask>\n" +
            "                                        <name>TIMESHEETREFERENCENUMBER</name>\n" +
            "                                        <value>CJF/GRVF4Y</value>\n" +
            "                                </associateTask>\n" +
            "                                <associateTask>\n" +
            "                                        <name>DUMMYTASKID</name>\n" +
            "                                        <value>KUMAR19</value>\n" +
            "                                </associateTask>\n" +
            "                                <taskCategory>\n" +
            "                                        <name>FULFILMENT</name>\n" +
            "                                </taskCategory>\n" +
            "                                <problem>\n" +
            "                                        <problemReportCode>NDT</problemReportCode>\n" +
            "                                        <mainproblemLocation>LN</mainproblemLocation>\n" +
            "                                        <problemCategory>\n                                                <productType>COMPLEX</productType>\n" +
            "                                        </problemCategory>\n" +
            "                                        <fault>\n" +
            "                                                <dateTimeRaised>25/09/2019 10:14:54</dateTimeRaised>\n" +
            "                                        </fault>\n" +
            "                                </problem>\n" +
            "                                <careLevel>\n" +
            "                                        <name>STANDARD CARE</name>\n" +
            "                                </careLevel>\n" +
            "                                <taskNote>\n" +
            "                                        <name>HAZARD</name>\n" +
            "                                        <value>\n" +
            "                                                No Data Available Via Ewocs\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>WARNING</name>\n" +
            "                                        <value>\n" +
            "                                                No Data Available Via Ewocs\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>LINETEST</name>\n" +
            "                                        <value>\n" +
            "                                                No Data Available Via Ewocs\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>TECHNICAL</name>\n" +
            "                                        <value>\n" +
            "                                                There are no Technical Notes\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>ACCESS</name>\n" +
            "                                        <value>\n" +
            "                                                There are NO ACCESS Notes\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>GENERAL</name>\n" +
            "                                        <value>\n" +
            "                                                CASE NOTES\n" +
            "\n" +
            "\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name> PRIORITY</name>\n" +
            "                                        <value>\n" +
            "\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>TASK</name>\n" +
            "                                        <value>\n" +
            "                                                ************** AUTO BUILT EWOCS Primary Task **************\n" +
            "****** Reminder: Only Complete Fully completed tasks  ******\nContact Name: FIRSTNAME  SURNAME\n" +
            "CONTACT: 01908 6248644\n" +
            "ADDRESS IS LONDON\n" +
            "ORDER DESCRIPTION CORE JUNCTION FIBRE\n" +
            "\n" +
            "CASE TASKS\n" +
            "1#NI6F#Splice Fib Sbqt Fib. Per Sbqt Fib.\n" +
            "\n" +
            "ClAIMED WAU's\n" +
            "0#NI6F# (0:00)\n" +
            "\n" +
            "Remaining WAU's\n" +
            "1#NI6F\n" +
            "# (0:04)\n" +
            "************************************************************CASE NOTES\n" +
            "\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <order>\n" +
            "                                        <toReference>IBG160DN</toReference>\n" +
            "                                        <orderItem>\n" +
            "                                                <note>\n" +
            "                                                        <noteType>PRODUCTDESCRIPTION</noteType>\n" +
            "                                                        <text>Splice Fib Sbqt Fib. Per Sbqt Fib.</text>\n" +
            "                                                </note>\n" +
            "                                                <quantity>\n" +
            "                                                        <units>UNITS</units>\n" +
            "                                                        <amount>+1</amount>\n" +
            "                                                </quantity>\n" +
            "                                                <productInstance>\n" +
            "                                                        <productName>\n" +
            "                                                                <name>NAMES</name>\n" +
            "                                                                <identifier>\n" +
            "                                                                        <name>NI6F</name>\n" +
            "                                                                </identifier>\n" +
            "                                                        </productName>\n" +
            "                                                </productInstance>\n" +
            "                                                <wauProductInstance>\n" +
            "                                                        <wauProductType>\n" +
            "                                                                <name>BTWAUProductType</name>\n" +
            "                                                                <value>\n" +
            "                                                                        P\n" +
            "                                                                </value>\n" +
            "                                                        </wauProductType>\n" +
            "                                                        <note>\n" +
            "                                                                <noteType>BTWAUProductLongDescription</noteType>\n" +
            "                                                                <text>\n" +
            "                                                                        All activities necessary to splice subsequent fibres including but not limited to: Fibre Splice;  inserting splitter or WDM; Continuity test.\n" +
            "                                                                </text>\n" +
            "                                                        </note>\n" +
            "                                                        <note>\n" +
            "                                                                <noteType>BTWAUUsage</noteType>\n" +
            "                                                                <text>\n" +
            "                                                                        Fibre Jointing/Terminating. Covers second and additional fibres in any single location.\n" +
            "                                                                </text>\n" +
            "                                                        </note>\n" +
            "                                                        <wauUnitOfIssue>\n" +
            "                                                                <name>BTUnitOfIssue</name>\n" +
            "                                                                <value>\n" +
            "                                                                        Per Subsequent Fibre\n" +
            "                                                                </value>\n                                                                </value>\n" +
            "                                                        </wauUnitOfIssue>\n" +
            "                                                        <wauSingleUnitTime>\n" +
            "                                                                <name>BTSingleUnitTime</name>\n" +
            "                                                                <value>.07</value>\n" +
            "                                                        </wauSingleUnitTime>\n" +
            "                                                        <wauEngineeringActivity>\n" +
            "                                                                <name>BTEngineeringActivity</name>\n" +
            "                                                                <value>Fibre Provide</value>\n" +
            "                                                        </wauEngineeringActivity>\n" +
            "                                                </wauProductInstance>\n" +
            "                                        </orderItem>\n" +
            "                                </order>\n" +
            "                                <taskRole>\n" +
            "                                        <name>LOB</name>\n" +
            "                                        <value>Openreach</value>\n" +
            "                                </taskRole>\n" +
            "                                <managedElement>\n" +
            "                                        <aliasNameList>\n" +
            "                                                <attributeName>CODE1141</attributeName>\n" +
            "                                                <attributeValue>AA</attributeValue>\n" +
            "                                        </aliasNameList>\n" +
            "                                        <equipmentHolder>\n" +
            "                                                <location>\n" +
            "                                                        <accessFor24hr>true</accessFor24hr>\n" +
            "                                                </location>\n" +
            "                                        </equipmentHolder>\n" +
            "                                </managedElement>\n" +
            "                                <siteContact>\n" +
            "                                        <customerSite>\n" +
            "                                                <customerContact>\n" +
            "                                                        <contactDetails>\n" +
            "                                                                <person>\n" +
            "                                                                        <personName>\n" +
            "                                                                                <forename>FIRSTNAME </forename>\n" +
            "                                                                                <surname>SURNAME</surname>\n" +
            "                                                                        </personName>\n" +
            "                                                                </person>\n" +
            "                                                                <phoneNumber>\n" +
            "                                                                        <phoneNumber>01908 6248644</phoneNumber>\n" +
            "                                                                </phoneNumber>\n" +
            "                                                                <address>\n" +
            "                                                                        <welshPostalAddress>\n" +
            "                                                                                <uKPostalAddress>\n" +
            "                                                                                        <thoroughfareNumber/>\n" +
            "                                                                                        <thoroughfareName>LONDON</thoroughfareName>\n" +
            "                                                                                        <dependentThoroughfareName/>\n" +
            "                                                                                        <dependentLocality/>\n" +
            "                                                                                        <subPremise/>\n" +
            "                                                                                        <organization/>\n" +
            "                                                                                        <postTown/>\n" +
            "                                                                                        <buildingName/>\n" +
            "                                                                                        <county/>\n" +
            "                                                                                        <poBox/>\n" +
            "                                                                                        <postCode>CF447AA</postCode>\n" +
            "                                                                                </uKPostalAddress>\n" +
            "                                                                        </welshPostalAddress>\n" +
            "                                                                        <country>\n" +
            "                                                                                <name>GB</name>\n" +
            "                                                                        </country>\n" +
            "                                                                </address>\n                                                        </contactDetails>\n" +
            "                                                </customerContact>\n" +
            "                                        </customerSite>\n" +
            "                                </siteContact>\n" +
            "                                <productInstance>\n" +
            "                                        <productSpecification>\n" +
            "                                                <serviceSpecification>\n" +
            "                                                        <id>3379685</id>\n" +
            "                                                </serviceSpecification>\n" +
            "                                        </productSpecification>\n" +
            "                                </productInstance>\n" +
            "                                <serviceInstance>\n" +
            "                                        <definitelyAffectedFlag>true</definitelyAffectedFlag>\n" +
            "                                </serviceInstance>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>GANGDETAILS</name>\n" +
            "                                        <value>\n" +
            "                                                000000000+000000000+0000000+true\n" +
            "                                        </value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>BTJOBPACKURL</name>\n" +
            "                                        <value>\n" +
            "                                                http://10.54.214.122:80/ORjobpackretrievalweb/default.aspx?id=1301414\n" +
            "                                        </value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>UPSTREAMDATABANDWIDTH</name>\n" +
            "                                        <value/>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>DOWNSTREAMDATABANDWIDTH</name>\n" +
            "                                        <value/>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>JOBTYPEID</name>\n" +
            "                                        <value>COMPBDUK</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>PROPOSEDIND</name>\n" +
            "                                        <value>P</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>TECHNOLOGYTYPE</name>\n" +
            "                                        <value>Fibre</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>Rplusidentifier</name>\n" +
            "                                        <value>N</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>Rplusfunction</name>\n" +
            "                                        <value>Z</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>VisualiserDataAvailable</name>\n" +
            "                                        <value>N</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskInstallation>\n" +
            "                                        <id>012987-011</id>\n" +
            "                                </taskInstallation>\n" +
            "                                <taskSpecification>\n<skillSpecification>\n" +
            "                                                <id>AJBS</id>\n" +
            "                                                <type>PRIMARY</type>\n" +
            "                                                <skillDuration>00004</skillDuration>\n" +
            "                                                <numberOfPeople>1</numberOfPeople>\n" +
            "                                        </skillSpecification>\n" +
            "                                </taskSpecification>\n" +
            "                                <taskCharge>\n" +
            "                                        <personInformedOnCharges>N</personInformedOnCharges>\n" +
            "                                </taskCharge>\n" +
            "                                <resourceRequirements>\n" +
            "                                        <minResourceNumber>1</minResourceNumber>\n" +
            "                                        <maxResourceNumber>2</maxResourceNumber>\n" +
            "                                        <optimumResourceNumber>2</optimumResourceNumber>\n" +
            "                                        <resourceType>ENGINEER</resourceType>\n" +
            "                                </resourceRequirements>\n" +
            "                        </task>\n" +
            "                </createTaskRequest>\n" +
            "        </Body>\n" +
            "</Envelope>\n" +
            ", EstimateId=IBG160DN, WorkLocationQualifier=C, wmsTaskCategory=N, MainWorkLocation=LN, TaskMFL=LN, GangDetails=000000000+000000000+0000000+true, SerSpecId=3379685, TaskTelephoneNo=012987-011, identifier=KUMAR19, ResponseCode=NONE, secondaryCommLatestFinish=24/10/2019 23:59:00, TaskCategory=FULFILMENT, receptorId=58, ProductType=COMPLEX, StateCode=OK, ReplyToInstanceId=EO, CareLevel=STANDARD CARE, ReqWorkLocationQualifier=C, CustomerType=BM, Postcode=CF447AA, TaskDuration=00004, actionType=CreateTask, A1141Code=AA, BusinessTargetDateTime=24/10/2019 07:00:00, NoOfAffectedONTs=0, e2eData=E2E.busTxnHdr=PCK002069, ReqMainWorkLocation=LN, TaskAssociateTask=KUMAR19, metTaskIdentifier=399, SourceSystem=EWOEO}, METDATA={isResumeFlow=true}}\n";

    String resusltSet ="{EMPPAL={AGG_STATUS=Business Exception is optional}, aggregationStatus={STATUS=REQCONT}, AGG_NAME={REQUIREM\n" +
            "ENT_TECHNICAL=O, AGG_NAME=MNPL, REQUIREMENT_BUSINESS=O}, RequestMap={ReplyToAddress=http://ccm.intra.bt.com/EWOCS, ActionType=http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskRequest, PrimaryCommLatestStart=24/10/2019 07:0\n" +
            "0:00, TaskIdentifier=KUMAR19, TaskMessageId=49211449, ReqResponseCode=NONE, TaskTypeID=COMPBDUK, ReqReportedDateTime=25/09/2019 10:14:54, TaskLOB=Openreach, ValidatedXML=<?xml version=\"1.0\" encoding=\"UTF-8\"?><Envelope>\n" +
            "        <Body>\n" +
            "                <createTaskRequest>\n" +
            "                        <standardHeader>\n" +
            "                                <e2e>\n" +
            "                                        <E2EDATA>E2E.busTxnHdr=PCK002069</E2EDATA>\n" +
            "                                </e2e>\n" +
            "                                <serviceState>\n" +
            "                                        <stateCode>OK</stateCode>\n" +
            "                                </serviceState>\n" +
            "                                <serviceAddressing>\n" +
            "                                        <from>http://ccm.intra.bt.com/EWOCS</from>\n" +
            "                                        <to>\n" +
            "                                                <address>http://ccm.intra.bt.com/METsk</address>\n" +
            "                                        </to>\n" +
            "                                        <replyTo>\n" +
            "                                                <address>http://ccm.intra.bt.com/EWOCS</address>\n" +
            "                                                <contextItemList>\n" +
            "                                                        <contextItem contextId=\"http://ccm.intra.bt.com/EWOCS\" contextName=\"serviceType\">EWOCS</contextItem>\n" +
            "                                                        <contextItem contextId=\"http://ccm.intra.bt.com/EWOCS\" contextName=\"instanceId\">EO</contextItem>\n" +
            "                                                        <contextItem contextId=\"http://ccm.intra.bt.com/EWOCS\" contextName=\"taskIdentifier\">3379685</contextItem>\n" +
            "                                                </contextItemList>\n" +
            "                                        </replyTo>\n" +
            "                                        <messageId>49211449</messageId>\n" +
            "                                        <serviceName>http://capabilities.nat.bt.com/ManageEngineeringTask</serviceName>\n" +
            "                                        <action>http://capabilities.intra.bt.com/ManageEngineeringTask#CreateTaskRequest</action>\n" +
            "                                </serviceAddressing>\n" +
            "                                <serviceSpecification>\n" +
            "                                        <payloadFormat>XML</payloadFormat>\n                                     <version>1.0</version>\n" +
            "                                        <revision>0</revision>\n" +
            "                                </serviceSpecification>\n" +
            "                        </standardHeader>\n" +
            "                        <task>\n" +
            "                                <telephoneNumber>3379685</telephoneNumber>\n" +
            "                                <expectedFinishDateTime>24/10/2019 07:00:00</expectedFinishDateTime>\n" +
            "                                <secondaryCommitmentLatestFinish>24/10/2019 23:59:00</secondaryCommitmentLatestFinish>\n" +
            "                                <responseIndicator>NONE</responseIndicator>\n" +
            "                                <workLocationQualifier>C</workLocationQualifier>\n" +
            "                                <importantClass>18</importantClass>\n" +
            "                                <jIN>\n" +
            "                                        <name>TASKIDENTIFIER</name>\n" +
            "                                        <value>KUMAR19</value>\n" +
            "                                </jIN>\n" +
            "                                <associateTask>\n" +
            "                                        <name>TIMESHEETREFERENCENUMBER</name>\n" +
            "                                        <value>CJF/GRVF4Y</value>\n" +
            "                                </associateTask>\n" +
            "                                <associateTask>\n" +
            "                                        <name>DUMMYTASKID</name>\n" +
            "                                        <value>KUMAR19</value>\n" +
            "                                </associateTask>\n" +
            "                                <taskCategory>\n" +
            "                                        <name>FULFILMENT</name>\n" +
            "                                </taskCategory>\n" +
            "                                <problem>\n" +
            "                                        <problemReportCode>NDT</problemReportCode>\n" +
            "                                        <mainproblemLocation>LN</mainproblemLocation>\n" +
            "                                        <problemCategory>\n" +
            "                                                <productType>COMPLEX</productType>\n" +
            "                                        </problemCategory>\n" +
            "                                        <fault>\n" +
            "                                                <dateTimeRaised>25/09/2019 10:14:54</dateTimeRaised>\n" +
            "                                        </fault>\n" +
            "                                </problem>\n" +
            "                                <careLevel>\n" +
            "                                        <name>STANDARD CARE</name>\n" +
            "                                </careLevel>\n" +
            "                                <taskNote>\n" +
            "                                        <name>HAZARD</name>\n" +
            "                                        <value>\n" +
            "                                                No Data Available Via Ewocs\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>WARNING</name>\n" +
            "                                        <value>\n" +
            "                                                No Data Available Via Ewocs\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>LINETEST</name>\n" +
            "                                        <value>\n" +
            "                                                No Data Available Via Ewocs\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>TECHNICAL</name>\n" +
            "                                        <value>\n" +
            "                                                There are no Technical Notes\n                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>ACCESS</name>\n" +
            "                                        <value>\n" +
            "                                                There are NO ACCESS Notes\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>GENERAL</name>\n" +
            "                                        <value>\n" +
            "                                                CASE NOTES\n" +
            "\n" +
            "\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name> PRIORITY</name>\n" +
            "                                        <value>\n" +
            "\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <taskNote>\n" +
            "                                        <name>TASK</name>\n" +
            "                                        <value>\n" +
            "                                                ************** AUTO BUILT EWOCS Primary Task **************\n" +
            "****** Reminder: Only Complete Fully completed tasks  ******\n" +
            "Contact Name: FIRSTNAME  SURNAME\n" +
            "CONTACT: 01908 6248644\n" +
            "ADDRESS IS LONDON\n" +
            "ORDER DESCRIPTION CORE JUNCTION FIBRE\n" +
            "\n" +
            "CASE TASKS\n" +
            "1#NI6F#Splice Fib Sbqt Fib. Per Sbqt Fib.\n" +
            "\n" +
            "ClAIMED WAU's\n" +
            "0#NI6F# (0:00)\n" +
            "\n" +
            "Remaining WAU's\n" +
            "1#NI6F\n" +
            "# (0:04)\n" +
            "************************************************************CASE NOTES\n" +
            "\n" +
            "                                        </value>\n" +
            "                                </taskNote>\n" +
            "                                <order>\n" +
            "                                        <toReference>IBG160DN</toReference>\n" +
            "                                        <orderItem>\n" +
            "                                                <note>\n" +
            "                                                        <noteType>PRODUCTDESCRIPTION</noteType>\n" +
            "                                                        <text>Splice Fib Sbqt Fib. Per Sbqt Fib.</text>\n" +
            "                                                </note>\n" +
            "                                                <quantity>\n" +
            "                                                        <units>UNITS</units>\n" +
            "                                                        <amount>+1</amount>\n" +
            "                                                </quantity>\n" +
            "                                                <productInstance>\n" +
            "                                                        <productName>\n" +
            "                                                                <name>NAMES</name>\n" +
            "                                                                <identifier>\n" +
            "                                                                        <name>NI6F</name>\n                                                             </identifier>\n" +
            "                                                        </productName>\n" +
            "                                                </productInstance>\n" +
            "                                                <wauProductInstance>\n" +
            "                                                        <wauProductType>\n" +
            "                                                                <name>BTWAUProductType</name>\n" +
            "                                                                <value>\n" +
            "                                                                        P\n" +
            "                                                                </value>\n" +
            "                                                        </wauProductType>\n" +
            "                                                        <note>\n" +
            "                                                                <noteType>BTWAUProductLongDescription</noteType>\n" +
            "                                                                <text>\n" +
            "                                                                        All activities necessary to splice subsequent fibres including but not limited to: Fibre Splice;  inserting splitter or WDM; Continuity test.\n" +
            "                                                                </text>\n" +
            "                                                        </note>\n" +
            "                                                        <note>\n" +
            "                                                                <noteType>BTWAUUsage</noteType>\n" +
            "                                                                <text>\n" +
            "                                                                        Fibre Jointing/Terminating. Covers second and additional fibres in any single location.\n" +
            "                                                                </text>\n" +
            "                                                        </note>\n" +
            "                                                        <wauUnitOfIssue>\n" +
            "                                                                <name>BTUnitOfIssue</name>\n" +
            "                                                                <value>\n" +
            "                                                                        Per Subsequent Fibre\n" +
            "                                                                </value>\n" +
            "                                                        </wauUnitOfIssue>\n" +
            "                                                        <wauSingleUnitTime>\n" +
            "                                                                <name>BTSingleUnitTime</name>\n" +
            "                                                                <value>.07</value>\n" +
            "                                                        </wauSingleUnitTime>\n" +
            "                                                        <wauEngineeringActivity>\n" +
            "                                                                <name>BTEngineeringActivity</name>\n" +
            "                                                                <value>Fibre Provide</value>\n" +
            "                                                        </wauEngineeringActivity>\n" +
            "                                                </wauProductInstance>\n" +
            "                                        </orderItem>\n" +
            "                                </order>\n" +
            "                                <taskRole>\n" +
            "                                        <name>LOB</name>\n" +
            "                                        <value>Openreach</value>\n" +
            "                                </taskRole>\n" +
            "                                <managedElement>\n" +
            "                                        <aliasNameList>\n" +
            "                                                <attributeName>CODE1141</attributeName>\n" +
            "                                                <attributeValue>AA</attributeValue>\n" +
            "                                        </aliasNameList>\n" +
            "                                        <equipmentHolder>\n" +
            "                                                <location>\n" +
            "                                                        <accessFor24hr>true</accessFor24hr>\n" +
            "                                                </location>\n" +
            "                                        </equipmentHolder>\n" +
            "                                </managedElement>\n" +
            "                                <siteContact>\n" +
            "                                        <customerSite>\n" +
            "                                                <customerContact>\n" +
            "                                                        <contactDetails>\n" +
            "                                                                <person>\n" +
            "                                                                        <personName>\n                                                                             <forename>FIRSTNAME </forename>\n" +
            "                                                                                <surname>SURNAME</surname>\n" +
            "                                                                        </personName>\n" +
            "                                                                </person>\n" +
            "                                                                <phoneNumber>\n" +
            "                                                                        <phoneNumber>01908 6248644</phoneNumber>\n" +
            "                                                                </phoneNumber>\n" +
            "                                                                <address>\n" +
            "                                                                        <welshPostalAddress>\n" +
            "                                                                                <uKPostalAddress>\n" +
            "                                                                                        <thoroughfareNumber/>\n" +
            "                                                                                        <thoroughfareName>LONDON</thoroughfareName>\n" +
            "                                                                                        <dependentThoroughfareName/>\n" +
            "                                                                                        <dependentLocality/>\n" +
            "                                                                                        <subPremise/>\n" +
            "                                                                                        <organization/>\n" +
            "                                                                                        <postTown/>\n" +
            "                                                                                        <buildingName/>\n" +
            "                                                                                        <county/>\n" +
            "                                                                                        <poBox/>\n" +
            "                                                                                        <postCode>CF447AA</postCode>\n" +
            "                                                                                </uKPostalAddress>\n" +
            "                                                                        </welshPostalAddress>\n" +
            "                                                                        <country>\n" +
            "                                                                                <name>GB</name>\n" +
            "                                                                        </country>\n" +
            "                                                                </address>\n" +
            "                                                        </contactDetails>\n" +
            "                                                </customerContact>\n" +
            "                                        </customerSite>\n" +
            "                                </siteContact>\n" +
            "                                <productInstance>\n" +
            "                                        <productSpecification>\n" +
            "                                                <serviceSpecification>\n" +
            "                                                        <id>3379685</id>\n" +
            "                                                </serviceSpecification>\n" +
            "                                        </productSpecification>\n" +
            "                                </productInstance>\n" +
            "                                <serviceInstance>\n" +
            "                                        <definitelyAffectedFlag>true</definitelyAffectedFlag>\n" +
            "                                </serviceInstance>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>GANGDETAILS</name>\n" +
            "                                        <value>\n" +
            "                                                000000000+000000000+0000000+true\n" +
            "                                        </value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>BTJOBPACKURL</name>\n" +
            "                                        <value>\n" +
            "                                                http://10.54.214.122:80/ORjobpackretrievalweb/default.aspx?id=1301414\n" +
            "                                        </value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>UPSTREAMDATABANDWIDTH</name>\n" +
            "                                        <value/>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>DOWNSTREAMDATABANDWIDTH</name>\n" +
            "                                        <value/>\n </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>JOBTYPEID</name>\n" +
            "                                        <value>COMPBDUK</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>PROPOSEDIND</name>\n" +
            "                                        <value>P</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>TECHNOLOGYTYPE</name>\n" +
            "                                        <value>Fibre</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>Rplusidentifier</name>\n" +
            "                                        <value>N</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>Rplusfunction</name>\n" +
            "                                        <value>Z</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskCharacteristic>\n" +
            "                                        <name>VisualiserDataAvailable</name>\n" +
            "                                        <value>N</value>\n" +
            "                                </taskCharacteristic>\n" +
            "                                <taskInstallation>\n" +
            "                                        <id>012987-011</id>\n" +
            "                                </taskInstallation>\n" +
            "                                <taskSpecification>\n" +
            "                                        <skillSpecification>\n" +
            "                                                <id>AJBS</id>\n" +
            "                                                <type>PRIMARY</type>\n" +
            "                                                <skillDuration>00004</skillDuration>\n" +
            "                                                <numberOfPeople>1</numberOfPeople>\n" +
            "                                        </skillSpecification>\n" +
            "                                </taskSpecification>\n" +
            "                                <taskCharge>\n" +
            "                                        <personInformedOnCharges>N</personInformedOnCharges>\n" +
            "                                </taskCharge>\n" +
            "                                <resourceRequirements>\n" +
            "                                        <minResourceNumber>1</minResourceNumber>\n" +
            "                                        <maxResourceNumber>2</maxResourceNumber>\n" +
            "                                        <optimumResourceNumber>2</optimumResourceNumber>\n" +
            "                                        <resourceType>ENGINEER</resourceType>\n" +
            "                                </resourceRequirements>\n" +
            "                        </task>\n" +
            "                </createTaskRequest>\n" +
            "        </Body>\n" +
            "</Envelope>\n" +
            ", EstimateId=IBG160DN, WorkLocationQualifier=C, wmsTaskCategory=N, MainWorkLocation=LN, TaskMFL=LN, GangDetails=000000000+000000000+0000000+true, SerSpecId=3379685, TaskTelephoneNo=012987-011, identifier=KUMAR19, ResponseCode=NONE, secondaryCommLatestFinish=24/10/2019 23:59:00, TaskCategory=FULFILMENT, receptorId=58, ProductType=COMPLEX, StateCode=OK, ReplyToInstanceId=EO, CareLevel=STANDARD CARE, ReqWorkLocationQualifier=C, CustomerType=BM, Postcode=CF447AA, TaskDuration=00004, actionType=CreateTask, A1141Code=AA, BusinessTargetDateTime=24/10/2019 07:00:00, NoOfAffectedONTs=0, e2eData=E2E.busTxnHdr=PCK002069, ReqMainWorkLocation=LN, TaskAssociateTask=KUMAR19, metTaskIdentifier=399, SourceSystem=EWOEO}, METDATA={isResumeFlow=true}}";
    @Before
    public void setup() throws METBException, SQLException, NamingException {
        MockitoAnnotations.initMocks(this);
        DataSource dataSource = mock(DataSource.class);
        when(resourceManager.getDataSource(DaoConstants.ORMETB_DATASOURCE)).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        //when(connection.prepareCall(anyString())).thenReturn(callableStmt);
    }

    @Test
    public void getDBConnection() throws METBException, SQLException{

        dao.getDBConnection();
    }

    @Test
    public void storeAggregationDetails() throws METBException, SQLException{
        Map internalMap = new HashMap();
        int[] updateCounts = {1,1,1,1,1};
        internalMap.put("a","a");
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(dataSource.getConnection().prepareStatement(DaoConstants.DELETE_HASHDATA_QUERY)).thenReturn(preparedStatement);
        when((OraclePreparedStatement)(dataSource.getConnection().prepareStatement(DaoConstants.INSERT_HASHDATA))).thenReturn(oraclePreparedStatement);
        when(oraclePreparedStatement.executeBatch()).thenReturn(updateCounts);
        dao.storeAggregationDetails("ZRECTY7890",internalMap,true);
    }

    @Test(expected= METBException.class)
    public void storeAggregationDetailsWithMapInstance() throws METBException, SQLException{
        Map internalMap = new HashMap();
        Map internalMap1 = new HashMap();
        internalMap1.put("b","b");
        int[] updateCounts = {-1,-1,-1,-1,-1};
        internalMap.put("a","a");
        internalMap.put("d","d");
        internalMap.put("c",internalMap);
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(dataSource.getConnection().prepareStatement(DaoConstants.DELETE_HASHDATA_QUERY)).thenReturn(preparedStatement);
        when((OraclePreparedStatement)(dataSource.getConnection().prepareStatement(DaoConstants.INSERT_HASHDATA))).thenReturn(oraclePreparedStatement);
        dao.storeAggregationDetails("ZRECTY7890",internalMap,true);
    }



    @Test(expected= METBException.class)
    public void storeAggregationDetailsWithException() throws METBException, SQLException{
        Map internalMap = new HashMap();
        int[] updateCounts = {-1,-1,-1,-1,-1};
        internalMap.put("a","a");
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(dataSource.getConnection().prepareStatement(DaoConstants.DELETE_HASHDATA_QUERY)).thenReturn(preparedStatement);
        when((OraclePreparedStatement)(dataSource.getConnection().prepareStatement(DaoConstants.INSERT_HASHDATA))).thenReturn(oraclePreparedStatement);
        when(oraclePreparedStatement.executeBatch()).thenReturn(updateCounts);
        dao.storeAggregationDetails("ZRECTY7890",internalMap,true);
    }

    @Test
    public void updateStuckRequestStatusSuccess() throws METBException, SQLException{
        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(dataSource.getConnection().prepareStatement(DaoConstants.UPDATE_STUCK_REQUEST_TASKDETAILS_DATA_QUERY)).thenReturn(preparedStatement);
        dao.updateStuckRequestStatus("ZRECTY7890");
    }

    @Test
    public void updateStuckRequestStatusFailure() throws METBException, SQLException{

        DataSource dataSource = mock(DataSource.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(dataSource.getConnection().prepareStatement(DaoConstants.UPDATE_STUCK_REQUEST_TASKDETAILS_DATA_QUERY)).thenReturn(preparedStatement);
        dao.updateStuckRequestStatus("ZRECTY7890");
    }

    @Test
    public void getAggregationResponseDetailsObjectWithDetails() throws METBException, SQLException{

        Map<String, Object> queryForMap = new HashMap<String, Object>();
        queryForMap.put(DaoConstants.REQUEST_STATUS,"active");
        queryForMap.put(DaoConstants.TASK_REF_NUMBER,"ZRDCT6789");
        queryForMap.put(DaoConstants.MET_REF_NUMBER,"fr456789");
        queryForMap.put(DaoConstants.PRODUCT,"COMPLEX");
        queryForMap.put(DaoConstants.AGG_NAME,"MNPL");
        queryForMap.put(DaoConstants.TASK_CATEGORY,"FULFILLMENT");
        queryForMap.put(DaoConstants.LASTUPDATED,new Timestamp(System.currentTimeMillis()));
        queryForMap.put(DaoConstants.LATEST_OPERATION,"CREATE");
        queryForMap.put(DaoConstants.E2E_DATA,"http://create.com");
        queryForMap.put(DaoConstants.REPLY_TO,"http://create.com");
        queryForMap.put(DaoConstants.LOB,"OpenReach");
        queryForMap.put(DaoConstants.SMNT_RETRY_COUNT,"3");
        queryForMap.put(DaoConstants.SOURCE_ADDRESS,"http://create.com");
        queryForMap.put(DaoConstants.SOURCE_INSTANCE_ID,"EO");

        Object[] params = new Object[]{"MANAGEDSERVER0110101010"};
        int[] types = new int[]{Types.VARCHAR};
        when(jdbcTemplate.queryForMap(DaoConstants.SELECT_AGGREGATION_RESPONSE_DETAIL_OBJ_QUERY, params, types)).thenReturn(queryForMap);
        dao.getAggregationResponseDetailsObject("MANAGEDSERVER0110101010");
    }

    @Test
    public void getAggregationResponseDetailsObjectWithoutDetails() throws METBException, SQLException{

        dao.getAggregationResponseDetailsObject("MANAGEDSERVER0110101010");
    }

    @Test
    public void insertAggReqXML() throws METBException, SQLException{

        dao.insertAggReqXML("abbbb","MNPL","ZRECTY7890","abcd");
    }


    @Test
    public void getExistingHashData(){

        Map internalMap = new HashMap();
        dao.getExistingHashData("ZRECTY7890","EO","http://ccm.intra.bt.com/EWOCS",internalMap);
    }

    @Test
    public void getRequestMapWithErrorData() throws SQLException{

        Map internalMap = new HashMap();
        Object[] params = new Object[]{"ZRECTY7890", "RequestMap"};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};
        Map<String, Object> outerAggregatorMap = new HashMap<>();

        /*Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);
        when(resultSet.getString("KEY")).thenReturn("MNPL");
        when(resultSet.getString("VALUE")).thenReturn("<xml>");

        when(jdbcTemplate.query(DaoConstants.SELECT_AGGREGATION_DATA_FOR_RESPONSE, params, types,resultSetExtractor)).thenReturn(outerAggregatorMap);
        */dao.getRequestMapWithErrorData("ZRECTY7890","MNPL");
        dao.getRequestMapWithErrorData("ZRECTY7890","EMPPAL");
    }

    @Test(expected = METBException.class)
    public void updateResponseWithException() throws METBException{

        Object[] params = new Object[]{"ACTIVE", "ZRECTY7890"};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};
        when(jdbcTemplate.update(DaoConstants.UPDATE_RESPONSE_DATA_QUERY, params, types)).thenReturn(0);
        dao.updateResponse("ZRECTY7890","ACTIVE");
    }
    @Test
    public void updateResponse() throws METBException{

        Object[] params = new Object[]{"ACTIVE", "ZRECTY7890"};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};
        when(jdbcTemplate.update(DaoConstants.UPDATE_RESPONSE_DATA_QUERY, params, types)).thenReturn(1);
        dao.updateResponse("ZRECTY7890","ACTIVE");
    }

    @Test(expected = METBException.class)
    public void updateRequestStatusWithException() throws METBException{

        dao.updateRequestStatus("ACTIVE",new Timestamp(System.currentTimeMillis()),"ZRECTY7890");
    }

    @Test
    public void getValidatedXMLForMetrefId(){

        when(jdbcTemplate.queryForObject(DaoConstants.SELECT_REQUEST_XML_FOR_TASK,  new Object[]{"ZRECTY7890"}, String.class)).thenReturn(internalResponseMap);
        dao.getValidatedXMLForMetrefId("ZRECTY7890");
    }

    @Test(expected = METBException.class)
    public void updateRequestStatusInITR() throws METBException{

        dao.updateRequestStatusInITR("ACTIVE",new Timestamp(System.currentTimeMillis()),"ZRECTY7890");
    }


    @Test(expected = METBException.class)
    public void updateRequestStatus() throws METBException{

        dao.updateRequestStatus("ACTIVE","RETRY","MANAGEDSERVER01",new Timestamp(System.currentTimeMillis()),"ZRECTY7890");
    }

    @Test(expected = METBException.class)
    public void updateAggStatusWithReqXML() throws METBException{

        dao.updateAggStatusWithReqXML(new Timestamp(System.currentTimeMillis()),"MNPL","RETRY","ZRECTY7890","managedserver0110101001");
    }

    @Test
    public void updateAggReqXML() {

        when(jdbcTemplate.queryForObject(DaoConstants.SELECT_ROW_EXISTS,  Integer.class, "ZRECTY7890")).thenReturn(1);
        dao.updateAggReqXML("MNPL","ZRECTY7890");
    }

    @Test
    public void updateAggReqXMLNoRows(){

        when(jdbcTemplate.queryForObject(DaoConstants.SELECT_ROW_EXISTS,  Integer.class, "ZRECTY7890")).thenReturn(0);
        dao.updateAggReqXML("MNPL","ZRECTY7890");
    }

    @Test(expected = METBException.class)
    public void updateServerLookupLastProcessdate() throws METBException{

        dao.updateServerLookupLastProcessdate("managedserver");
    }

    @Test(expected = METBException.class)
    public void deleteAggregationSpecificData() throws METBException{

        Object[] params = new Object[]{"ZRECTY7890","MNPL"};
        int[] types = new int[]{Types.VARCHAR, Types.VARCHAR};
        when(jdbcTemplate.update(DaoConstants.DELETE_AGGREGATION_SPECIFIC_QUERY,  params, types)).thenReturn(0);
        dao.deleteAggregationSpecificData("ZRECTY7890","MNPL");
    }

    @Test
    public void storeAggregationLog() throws METBException{

        dao.storeAggregationLog("ZRECTY7890", "1007", "Project id not found", "Project id not found");
    }

    @Test
    public void updateStuckRetryRequest() throws METBException{

        dao.updateStuckRetryRequest(new Timestamp(System.currentTimeMillis()), 2,"ZRECTY7890");
    }

}
