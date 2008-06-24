package org.osaf.caldav4j.methods;

import static org.osaf.caldav4j.CalDAVConstants.INFINITY;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.Property;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.apache.webdav.lib.util.WebdavStatus;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.model.request.TicketRequest;
import org.osaf.caldav4j.model.response.TicketDiscoveryProperty;
import org.osaf.caldav4j.model.response.TicketResponse;
/**
 * 
 * @author EdBindl
 * 
 */
public class TicketTest extends BaseTestCase {
    public static final Integer TEST_TIMEOUT = 3600;
    public static final boolean TEST_READ = true;
    public static final boolean TEST_WRITE = true;
    public static final Integer TEST_VISITS = INFINITY;
    public static final String  TEST_TIMEOUT_UNITS = "Second";

    public static final String COLLECTION = "collection";

    public static final String COLLECTION_PATH = CALDAV_SERVER_WEBDAV_ROOT
            + COLLECTION;

    private static final Log log = LogFactory.getLog(TicketTest.class);

    private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

    protected void setUp() throws Exception {
        super.setUp();
        mkdir(COLLECTION_PATH);
        put(ICS_DAILY_NY_5PM, COLLECTION_PATH + "/" + ICS_DAILY_NY_5PM);
    }

    public void testMakeTicket() throws Exception {

        // Create Ticket to Make

        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setVisits(CalDAVConstants.INFINITY);
        ticketRequest.setTimeout(TEST_TIMEOUT);
        ticketRequest.setRead(true);
        ticketRequest.setWrite(true);

        boolean read = ticketRequest.getRead();
        boolean write = ticketRequest.getWrite();

        // Make the ticket
        MkTicketMethod mk = new MkTicketMethod(COLLECTION_PATH + "/"
                + BaseTestCase.ICS_DAILY_NY_5PM, ticketRequest);
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
        http.executeMethod(hostConfig, mk);

        int statusCode = mk.getStatusCode();

        assertEquals("Status code for mk:", WebdavStatus.SC_OK, statusCode);

        TicketResponse ticketResponse = mk.getResponseBodyAsTicketResponse();

        // Make sure the response has the correct values

        assertEquals("Priviliges for ticketResponse:", read, ticketResponse
                .getRead());
        assertEquals("Priviliges for ticketResponse:", write, ticketResponse
                .getWrite());
        assertEquals("Visits for ticketResponse:", CalDAVConstants.INFINITY,
                ticketResponse.getVisits());
        assertEquals("Timeout for ticketResponse:",
                TEST_TIMEOUT, ticketResponse.getTimeout());
        assertEquals("Units for ticketResponse:",
                TEST_TIMEOUT_UNITS, ticketResponse.getUnits());

        // Delete the Ticket

        DelTicketMethod del = new DelTicketMethod(COLLECTION_PATH + "/"
                + BaseTestCase.ICS_DAILY_NY_5PM, ticketResponse.getID());
        http.executeMethod(hostConfig, del);

        statusCode = del.getStatusCode();

        assertEquals("Status code for del:", WebdavStatus.SC_NO_CONTENT,
                statusCode);

        // Make sure the Ticket is gone

        Vector<PropertyName> properties = new Vector<PropertyName>();

        PropertyName propertyName = new PropertyName(CalDAVConstants.NS_XYTHOS,
                CalDAVConstants.ELEM_TICKETDISCOVERY);
        PropertyName propertyName2 = new PropertyName(CalDAVConstants.NS_DAV,
                "owner");

        properties.add(propertyName);
        properties.add(propertyName2);

        PropFindMethod propFindMethod = new PropFindMethod(COLLECTION_PATH
                + "/" + BaseTestCase.ICS_DAILY_NY_5PM, properties.elements());
        propFindMethod.setDepth(0);
        http.executeMethod(hostConfig, propFindMethod);

        statusCode = propFindMethod.getStatusCode();

        assertEquals("Status code for propFindMethod:",
                WebdavStatus.SC_MULTI_STATUS, statusCode);

        // Check to make sure we get the right number of tickets

        Enumeration responses = propFindMethod
                .getResponseProperties(BaseTestCase.CALDAV_SERVER_PROTOCOL
                        + "://" + BaseTestCase.CALDAV_SERVER_HOST + ":"
                        + BaseTestCase.CALDAV_SERVER_PORT + COLLECTION_PATH
                        + "/" + BaseTestCase.ICS_DAILY_NY_5PM);
        List<TicketResponse> ticketResponseList = new ArrayList<TicketResponse>();
        while (responses.hasMoreElements()) {
            Property item = (Property) responses.nextElement();
            if (item.getLocalName()
                    .equals(CalDAVConstants.ELEM_TICKETDISCOVERY)) {
                TicketDiscoveryProperty ticketDiscoveryProp = (TicketDiscoveryProperty) item;
                ticketResponseList.addAll(ticketDiscoveryProp.getTickets());
            }
        }

        assertEquals("Number of Tickets Returned from propFindMethod",
                ticketResponseList.size(), 0);

        // Remake the Same Ticket

        MkTicketMethod mk2 = new MkTicketMethod(COLLECTION_PATH + "/"
                + BaseTestCase.ICS_DAILY_NY_5PM, ticketRequest);
        http.executeMethod(hostConfig, mk2);

        // And Another Ticket

        MkTicketMethod mk3 = new MkTicketMethod(COLLECTION_PATH + "/"
                + BaseTestCase.ICS_DAILY_NY_5PM, ticketRequest);
        http.executeMethod(hostConfig, mk3);

        // Do a PropFind on Calendar for ticketdiscovery and owner properties

        propFindMethod = new PropFindMethod(COLLECTION_PATH + "/"
                + BaseTestCase.ICS_DAILY_NY_5PM, properties.elements());
        propFindMethod.setDepth(0);
        http.executeMethod(hostConfig, propFindMethod);

        statusCode = propFindMethod.getStatusCode();

        assertEquals("Status code for propFindMethod:",
                WebdavStatus.SC_MULTI_STATUS, statusCode);

        // Check to make sure we get the right number of tickets

        responses = propFindMethod
                .getResponseProperties(BaseTestCase.CALDAV_SERVER_PROTOCOL
                        + "://" + BaseTestCase.CALDAV_SERVER_HOST + ":"
                        + BaseTestCase.CALDAV_SERVER_PORT + COLLECTION_PATH
                        + "/" + BaseTestCase.ICS_DAILY_NY_5PM);
        ticketResponseList = new ArrayList<TicketResponse>();
        while (responses.hasMoreElements()) {
            Property item = (Property) responses.nextElement();
            if (item.getLocalName()
                    .equals(CalDAVConstants.ELEM_TICKETDISCOVERY)) {
                TicketDiscoveryProperty ticketDiscoveryProp = (TicketDiscoveryProperty) item;
                ticketResponseList.addAll(ticketDiscoveryProp.getTickets());
            }
        }

        assertEquals("Number of Tickets Returned from propFindMethod",
                ticketResponseList.size(), 2);
        TicketResponse ticketResponse2 = ticketResponseList.get(0);

        // Make sure PropFind's Response is correct

        assertEquals("Priviliges for ticketResponse2:", read, ticketResponse2
                .getRead());
        assertEquals("Priviliges for ticketResponse2:", write, ticketResponse2
                .getWrite());
        assertEquals("Visits for ticketResponse2:", CalDAVConstants.INFINITY,
                ticketResponse2.getVisits());
        assertEquals("Timeout for ticketResponse2:",
                TEST_TIMEOUT, ticketResponse2.getTimeout());
        assertEquals("Units for ticketResponse2:",
                TEST_TIMEOUT_UNITS, ticketResponse2.getUnits());

    }

    public void testUsingTicket() throws Exception {

        // Create Ticket to Make
        TicketRequest ticketRequest = new TicketRequest(
                TEST_TIMEOUT, INFINITY,
                TEST_READ, TEST_WRITE);

        // Make the ticket
        MkTicketMethod mk = new MkTicketMethod(COLLECTION_PATH + "/"
                + BaseTestCase.ICS_DAILY_NY_5PM, ticketRequest);

        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
        http.executeMethod(hostConfig, mk);

        int statusCode = mk.getStatusCode();

        assertEquals("Status code for mk:", WebdavStatus.SC_OK, statusCode);

        TicketResponse ticketResponse = mk.getResponseBodyAsTicketResponse();

        // Try to get the Calendar with the Ticket

        GetMethod get = methodFactory.createGetMethod();
        get.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_DAILY_NY_5PM);

        // Setup a HttpClient with a bad username and password
        HttpClient badhttp = createHttpClientWithNoCredentials();
        // Set the ticket
        badhttp.setTicket(ticketResponse.getID());

        // Make sure the Get method works with Bad Username/Password and valid
        // ticket in the Header (default action)
        badhttp.executeMethod(hostConfig, get);
        statusCode = get.getStatusCode();

        assertEquals("Status code for get: ", WebdavStatus.SC_OK, statusCode);

        // Make sure the Get method works with Bad Username/Password and valid
        // ticket in the URI
        GetMethod get2 = methodFactory.createGetMethod();
        get2.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_DAILY_NY_5PM);

        badhttp.setTicketLocation(HttpClient.TicketLocation.QUERY_PARAM);
        badhttp.executeMethod(hostConfig, get2);
        statusCode = get2.getStatusCode();

        assertEquals("Status code for get2: ", WebdavStatus.SC_OK, statusCode);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
        DeleteMethod delete = new DeleteMethod();
        delete.setPath(COLLECTION_PATH);
        http.executeMethod(hostConfig, delete);
    }

}
