package org.osaf.caldav4j.methods;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.apache.webdav.lib.util.WebdavStatus;
import org.osaf.caldav4j.BaseTestCase;

public class MkCalendarTest extends BaseTestCase {
    private static final Log log = LogFactory.getLog(MkCalendarTest.class);
    
    public void testCreateRemoveCalendarCollection() throws Exception{
        MkCalendarMethod mk = new MkCalendarMethod();
        mk.setPath(CALDAV_SERVER_WEBDAV_ROOT + "collection");
        HttpClient http = createHttpClient();
        HostConfiguration hostConfig = createHostConfiguration();
        http.executeMethod(hostConfig, mk);
        
        int statusCode = mk.getStatusCode();
        assertEquals("Status code for mk:", WebdavStatus.SC_CREATED, statusCode);
        
        //now let's try and get it, make sure it's there
        GetMethod get = new GetMethod();
        get.setPath(CALDAV_SERVER_WEBDAV_ROOT + "collection");
        http.executeMethod(hostConfig, get);
        statusCode = get.getStatusCode();
        assertEquals("Status code for get:", WebdavStatus.SC_OK, statusCode);
        
        
        DeleteMethod delete = new DeleteMethod();
        delete.setPath(CALDAV_SERVER_WEBDAV_ROOT + "collection");
        http.executeMethod(hostConfig, delete);
        
        statusCode = delete.getStatusCode();
        assertEquals("Status code for delete:", WebdavStatus.SC_NO_CONTENT, statusCode);

        //Now make sure that it goes away
        get = new GetMethod();
        get.setPath(CALDAV_SERVER_WEBDAV_ROOT + "collection");
        http.executeMethod(hostConfig, get);
        statusCode = get.getStatusCode();
        assertEquals("Status code for get:", WebdavStatus.SC_NOT_FOUND, statusCode);
        
        
    }
    
    /*public void testFun() throws Exception {
        CalendarQuery calquery = new CalendarQuery("C", "D");
        calquery.addProperty(CalDAVConstants.NS_DAV, "D", "getetag");
        
        CalendarData calendarData = new CalendarData("C");
        calendarData.setExpandOrLimitRecurrenceSet(null);
        Comp compVCAL = new Comp("C");
        compVCAL.setAllProp(true);
        compVCAL.setName("VCALENDAR");
        Comp compVEVENT = new Comp("C");
        compVEVENT.setName("VEVENT");
        compVEVENT.addProp("X-ABC-GUID");
        compVEVENT.addProp("UID");
        compVEVENT.addProp("DTSTART");
        compVEVENT.addProp("DTEND");
        compVEVENT.addProp("DURATION");
        compVEVENT.addProp("EXDATE");
        compVEVENT.addProp("EXRULE");

        Comp compVTIMEZONE = new Comp("C");
        compVTIMEZONE.setName("VTIMEZONE");
        compVTIMEZONE.setAllProp(true);
        compVTIMEZONE.setAllComp(true);
        
        List compList = new ArrayList();
        compList.add(compVEVENT);
        compList.add(compVTIMEZONE);
        compVCAL.setComps(compList);
        calendarData.setComp(compVCAL);
        calquery.setCalendarDataProp(calendarData);
        List compFilters = null;
        CompFilter compFilterVCALENDAR = new CompFilter("C", "VCALENDAR",
                false, null, null, compFilters, null);
        DateTime startTime = DateUtils.createDateTime(2004, 8, 2, 0, 0, null,
                true);
        DateTime endTime = DateUtils.createDateTime(2004, 8, 3, 0, 0, null,
                true);

        CompFilter compFilterVEVENT = new CompFilter("C", "VEVENT", false,
                startTime, endTime, null, null);
        compFilterVCALENDAR.addCompFilter(compFilterVEVENT);
        calquery.setCompFilter(compFilterVCALENDAR);

        Document d = calquery
                .createNewDocument(XMLUtils.getDOMImplementation());
        log.debug(XMLUtils.toPrettyXML(d));
    }*/
}