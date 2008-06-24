package org.osaf.caldav4j.methods;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.apache.webdav.lib.util.WebdavStatus;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.util.ICalendarUtils;

public class PutGetTest extends BaseTestCase {
    private static final Log log = LogFactory.getLog(PutGetTest.class);
    private CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
     
    
    public static final String COLLECTION      = "collection";
    public static final String COLLECTION_PATH = CALDAV_SERVER_WEBDAV_ROOT
            + COLLECTION;
    
    protected void setUp() throws Exception {
        super.setUp();
        mkdir(COLLECTION_PATH);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
        DeleteMethod delete = new DeleteMethod();
        delete.setPath(COLLECTION_PATH);
        http.executeMethod(hostConfig, delete);
    }

    public void testAddRemoveCalendarResource() throws Exception{
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();

        Calendar cal = getCalendarResource(BaseTestCase.ICS_DAILY_NY_5PM);
        PutMethod put = methodFactory.createPutMethod();
        put.setIfNoneMatch(true);
        put.setAllEtags(true);
        put.setRequestBody(cal);
        put.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_DAILY_NY_5PM);
        http.executeMethod(hostConfig, put);
        int statusCode = put.getStatusCode();
        assertEquals("Status code for put:", WebdavStatus.SC_CREATED, statusCode);

        //ok, so we created it...let's make sure it's there!
        GetMethod get = methodFactory.createGetMethod();
        get.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_DAILY_NY_5PM);
        http.executeMethod(hostConfig, get);
        statusCode = get.getStatusCode();
        assertEquals("Status code for get: ", WebdavStatus.SC_OK, statusCode);
        
        //now let's make sure we can get the resource body as a calendar
        Calendar calendar = get.getResponseBodyAsCalendar();
        VEvent event = ICalendarUtils.getFirstEvent(calendar);
        String uid = ICalendarUtils.getUIDValue(event);
        assertEquals(ICS_DAILY_NY_5PM_UID, uid);
        
        //let's make sure that a subsequent put with "if-none-match: *" fails
        put = methodFactory.createPutMethod();
        put.setIfNoneMatch(true);
        put.setAllEtags(true);
        put.setRequestBody(cal);
        put.setPath(COLLECTION_PATH + "/" + BaseTestCase.ICS_DAILY_NY_5PM);
        http.executeMethod(hostConfig, put);
        statusCode = put.getStatusCode();
        assertEquals("Status code for put:",
                WebdavStatus.SC_PRECONDITION_FAILED, statusCode);
   }
    

}