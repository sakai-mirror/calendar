/*
 * Copyright 2005 Open Source Applications Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osaf.caldav4j;

import static org.osaf.caldav4j.util.ICalendarUtils.getMasterEvent;
import static org.osaf.caldav4j.util.ICalendarUtils.getUIDValue;
import static org.osaf.caldav4j.util.UrlUtils.stripHost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.apache.webdav.lib.methods.PropFindMethod;
import org.apache.webdav.lib.util.WebdavStatus;
import org.osaf.caldav4j.cache.CalDAVResourceCache;
import org.osaf.caldav4j.cache.NoOpResourceCache;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.CalDAVReportMethod;
import org.osaf.caldav4j.methods.DelTicketMethod;
import org.osaf.caldav4j.methods.GetMethod;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.methods.MkTicketMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.osaf.caldav4j.model.request.CalendarData;
import org.osaf.caldav4j.model.request.CalendarQuery;
import org.osaf.caldav4j.model.request.CompFilter;
import org.osaf.caldav4j.model.request.PropFilter;
import org.osaf.caldav4j.model.request.PropProperty;
import org.osaf.caldav4j.model.request.TextMatch;
import org.osaf.caldav4j.model.request.TicketRequest;
import org.osaf.caldav4j.model.request.TimeRange;
import org.osaf.caldav4j.model.response.CalDAVResponse;
import org.osaf.caldav4j.model.response.TicketDiscoveryProperty;
import org.osaf.caldav4j.model.response.TicketResponse;
import org.osaf.caldav4j.util.ICalendarUtils;

/**
 * This class provides a high level API to a calendar collection on a CalDAV server.
 * 
 * @author bobbyrullo
 *
 */
public class CalDAVCalendarCollection {
    
    public static final PropProperty PROP_ETAG = new PropProperty(CalDAVConstants.NS_DAV,
            "D", CalDAVConstants.PROP_GETETAG);
    
    private CalDAV4JMethodFactory methodFactory = null;

    private String calendarCollectionRoot = null;

    private HostConfiguration hostConfiguration = null;

    private String prodId = null;

    private Random random = new Random();
    
    private CalDAVResourceCache cache = NoOpResourceCache.SINGLETON;
    
    public CalDAVCalendarCollection(){
        
    }
    
    /**
     * Creates a new CalDAVCalendar collection with the specified paramters
     * 
     * @param path The path to the collection 
     * @param hostConfiguration Host information for the CalDAV Server 
     * @param methodFactory methodFactory to obtail HTTP methods from
     * @param prodId String identifying who creates the iCalendar objects
     */
    public CalDAVCalendarCollection(String path,
            HostConfiguration hostConfiguration,
            CalDAV4JMethodFactory methodFactory, String prodId) {
        this.calendarCollectionRoot = path;
        this.hostConfiguration = hostConfiguration;
        this.methodFactory = methodFactory;
        this.prodId = prodId;
    }

    //Configuration Methods

    public HostConfiguration getHostConfiguration() {
        return hostConfiguration;
    }

    public void setHostConfiguration(HostConfiguration hostConfiguration) {
        this.hostConfiguration = hostConfiguration;
    }

    public CalDAV4JMethodFactory getMethodFactory() {
        return methodFactory;
    }

    public void setMethodFactory(CalDAV4JMethodFactory methodFactory) {
        this.methodFactory = methodFactory; }

    public String getCalendarCollectionRoot() {
        return calendarCollectionRoot;
    }

    public void setCalendarCollectionRoot(String path) {
        this.calendarCollectionRoot = path;
    }
    
    public CalDAVResourceCache getCache() {
        return cache;
    }

    public void setCache(CalDAVResourceCache cache) {
        this.cache = cache;
    }
    
    /*  The interesting methods */
    
    /**
     * Returns the icalendar object which contains the event with the specified
     * UID.
     * 
     * @param httpClient the httpClient which will make the request
     * @param uid The uniqueID of the event to find
     * @return the Calendar object containing the event with this UID
     * @throws CalDAV4JException if there was a problem, or if the resource could 
     *         not be found.
     */
    public Calendar getCalendarForEventUID(HttpClient httpClient, String uid)
            throws CalDAV4JException {
        return getCalDAVResourceForEventUID(httpClient, uid).getCalendar();
    }
    
    /**
     * Gets an icalendar object at the specified path, relative to the
     * collection path
     * 
     * @param httpClient the httpClient which will make the request
     * @param relativePath the path, relative to the collection path
     * @return the Calendar object at the specified path
     * @throws CalDAV4JException
     */
    public Calendar getCalendarByPath(HttpClient httpClient, String relativePath) throws CalDAV4JException{
        CalDAVResource resource = getCalDAVResource(httpClient, getAbsolutePath(relativePath));
        return resource.getCalendar();
    }
    
    /**
     * Returns all Calendars which contain events which have instances who fall within 
     * the two dates. Note that recurring events are NOT expanded. 
     * 
     * @param httpClient the httpClient which will make the request
     * @param beginDate the beginning of the date range. Must be a UTC date
     * @param endDate the end of the date range. Must be a UTC date.
     * @return a List of Calendars
     * @throws CalDAV4JException if there was a problem
     */
    public List<Calendar> getEventResources(HttpClient httpClient,
            Date beginDate, Date endDate)
            throws CalDAV4JException {
        // first create the calendar query
        CalendarQuery query = new CalendarQuery("C", "D");
        
        query.addProperty(PROP_ETAG);
        CompFilter vCalendarCompFilter = new CompFilter("C");
        vCalendarCompFilter.setName(Calendar.VCALENDAR);

        CompFilter vEventCompFilter = new CompFilter("C");
        vEventCompFilter.setName(Component.VEVENT);
        vEventCompFilter.setTimeRange(new TimeRange("C", beginDate, endDate));

        vCalendarCompFilter.addCompFilter(vEventCompFilter);
        query.setCompFilter(vCalendarCompFilter);

        CalDAVReportMethod reportMethod = methodFactory
                .createCalDAVReportMethod();
        reportMethod.setPath(calendarCollectionRoot);
        reportMethod.setReportRequest(query);
        try {
            httpClient.executeMethod(hostConfiguration, reportMethod);
        } catch (Exception he) {
            throw new CalDAV4JException("Problem executing method", he);
        }

        Enumeration<CalDAVResponse> e = reportMethod.getResponses();
        List<Calendar> list = new ArrayList<Calendar>();
        while (e.hasMoreElements()){
            CalDAVResponse response  = e.nextElement();
            String etag = response.getETag();
            CalDAVResource resource = getCalDAVResource(httpClient,
                    stripHost(response.getHref()), etag);
            list.add(resource.getCalendar());
        }
        
        return list;

    }
    
    /**
     * Deletes an event based on it's uid. If the calendar resource containing the
     * event contains no other VEVENT's, the entire resource will be deleted.
     * 
     * If the uid is for a recurring event, the master event and all exceptions will
     * be deleted
     * 
     * @param uid
     */
    public void deleteEvent(HttpClient httpClient,String uid) throws CalDAV4JException{
        CalDAVResource resource = getCalDAVResourceForEventUID(httpClient, uid);
        Calendar calendar = resource.getCalendar();
        ComponentList eventList = calendar.getComponents().getComponents(Component.VEVENT);
        List<Component> componentsToRemove = new ArrayList<Component>();
        boolean hasOtherEvents = false;
        for (Object o : eventList){
            VEvent event = (VEvent) o;
            String curUID = ICalendarUtils.getUIDValue(event);
            if (!uid.equals(curUID)){
                hasOtherEvents = true;
            } else {
                componentsToRemove.add(event);
            }
        }
        
        if (hasOtherEvents){
            if (componentsToRemove.size() == 0){
                throw new ResourceNotFoundException(
                        ResourceNotFoundException.IdentifierType.UID, uid);
            }
            
            for (Component removeMe : componentsToRemove){
                calendar.getComponents().remove(removeMe);
            }
            put(httpClient, calendar, stripHost(resource.getResourceMetadata().getHref()),
                    resource.getResourceMetadata().getETag());
            return;
        } else {
            delete(httpClient, stripHost(resource.getResourceMetadata().getHref()));
        }
    }

    /**
     * Creates a calendar at the specified path 
     *
     */
    public void createCalendar(HttpClient httpClient) throws CalDAV4JException{
        MkCalendarMethod mkCalendarMethod = new MkCalendarMethod();
        mkCalendarMethod.setPath(calendarCollectionRoot);
        try {
            httpClient.executeMethod(hostConfiguration, mkCalendarMethod);
            int statusCode = mkCalendarMethod.getStatusCode();
            if (statusCode != WebdavStatus.SC_CREATED){
                throw new CalDAV4JException("Create Failed with Status: "
                        + statusCode + " and body: \n"
                        + mkCalendarMethod.getResponseBodyAsString());
            }
        } catch (Exception e){
            throw new CalDAV4JException("Trouble executing MKCalendar", e);
        }
    }
    
    /**
     * Adds a new Calendar with the given VEvent and VTimeZone to the collection.
     * 
     * Tries to use the event UID followed by ".ics" as the name of the 
     * resource, otherwise will use the UID followed by a random number and 
     * ".ics" 
     * 
     * @param httpClient the httpClient which will make the request
     * @param vevent The VEvent to put in the Calendar
     * 
     * @param timezone The VTimeZone of the VEvent if it references one, 
     *                 otherwise null
     * @throws CalDAV4JException
     * @throws IOException 
     */
    public void addEvent(HttpClient httpClient, VEvent vevent, VTimeZone timezone)
            throws CalDAV4JException, IOException {
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId(prodId));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        if (timezone != null){
            calendar.getComponents().add(timezone);
        }
        calendar.getComponents().add(vevent);
        
        boolean didIt = false;
        for (int x = 0; x < 3 && !didIt; x++) {
            String resourceName = null;
            if (x == 0) {
                resourceName = ICalendarUtils.getUIDValue(vevent) + ".ics";
            } else {
                resourceName = ICalendarUtils.getUIDValue(vevent) + "-"
                        + random.nextInt() + ".ics";
            }
            PutMethod putMethod = createPutMethodForNewResource(resourceName,
                    calendar);
            try {
                httpClient.executeMethod(getHostConfiguration(), putMethod);
                String etag = putMethod.getResponseHeader("ETag").getValue();
                CalDAVResource calDAVResource = new CalDAVResource(calendar,
                        etag, getHref((putMethod.getPath())));
                cache.putResource(calDAVResource);
            } catch (Exception e) {
                throw new CalDAV4JException("Trouble executing PUT", e);
            }
            int statusCode = putMethod.getStatusCode();
            
            if (WebdavStatus.SC_CREATED == statusCode){
                didIt = true;
            } else if (WebdavStatus.SC_PRECONDITION_FAILED != statusCode){
                //must be some other problem, throw an exception
                throw new CalDAV4JException("Unexpected status code: "
                        + statusCode + "\n"
                        + putMethod.getResponseBodyAsString());
            }
        }
    }
    
    /**
     * Updates the resource containing the VEvent with the same UID as the given 
     * VEvent with the given VEvent
     * 
     *  TODO: Deal with SEQUENCE
     *  TODO: Handle timezone!!! Right now ignoring the param...
     *
     * @param httpClient the httpClient which will make the request
     * @param vevent the vevent to update
     * @param timezone The VTimeZone of the VEvent if it references one, 
     *                 otherwise null
     * @throws CalDAV4JException
     */
    public void updateMasterEvent(HttpClient httpClient, VEvent vevent, VTimeZone timezone)
        throws CalDAV4JException{
        String uid = getUIDValue(vevent);
        CalDAVResource resource = getCalDAVResourceForEventUID(httpClient, uid);
        Calendar calendar = resource.getCalendar();
        
        //let's find the master event first!
        VEvent originalVEvent = getMasterEvent(calendar, uid);

        calendar.getComponents().remove(originalVEvent);
        calendar.getComponents().add(vevent);
        
        put(httpClient, calendar,
                stripHost(resource.getResourceMetadata().getHref()),
                resource.getResourceMetadata().getETag());
    }
    
    public void updateMasterEventAtPath(HttpClient httpClient, VEvent vevent, String relativePath, VTimeZone timezone)
    throws CalDAV4JException{
    String uid = getUIDValue(vevent);
    CalDAVResource resource = getCalDAVResource(httpClient, relativePath);
    Calendar calendar = resource.getCalendar();
    
    //let's find the master event first!
    VEvent originalVEvent = getMasterEvent(calendar, uid);

    calendar.getComponents().remove(originalVEvent);
    calendar.getComponents().add(vevent);
    
    put(httpClient, calendar,
            stripHost(resource.getResourceMetadata().getHref()),
            resource.getResourceMetadata().getETag());
}
    /**
     * Creates a ticket for the specified resource and returns the ticket id.
     * 
     * @param httpClient the httpClient which will make the request
     * @param relativePath the path, relative to the collection path for 
     *                     which to grant the ticket on
     * @param visits
     * @param timeout
     * @param read
     * @param write
     * @return The id of the created ticket
     * @throws CalDAV4JException
     *             Is thrown if the execution of the MkTicketMethod fails
     */
    public String createTicket(HttpClient httpClient, String relativePath,
            Integer visits, Integer timeout, boolean read, boolean write)
            throws CalDAV4JException {
        TicketRequest ticketRequest = new TicketRequest();
        ticketRequest.setVisits(visits);
        ticketRequest.setTimeout(timeout);
        ticketRequest.setRead(read);
        ticketRequest.setWrite(write);

        // Make the ticket
        MkTicketMethod mkTicketMethod = methodFactory.createMkTicketMethod();
        mkTicketMethod.setPath(getAbsolutePath(relativePath));
        mkTicketMethod.setTicketRequest(ticketRequest);
        try {
            httpClient.executeMethod(hostConfiguration, mkTicketMethod);
            int statusCode = mkTicketMethod.getStatusCode();
            if (statusCode != WebdavStatus.SC_OK) {
                throw new CalDAV4JException("Create Ticket Failed with Status: "
                        + statusCode + " and body: \n"
                        + mkTicketMethod.getResponseBodyAsString());
            }
        } catch (Exception e) {
            throw new CalDAV4JException("Trouble executing MKTicket", e);
        }

        TicketResponse ticketResponse = null;

        try {
            ticketResponse = mkTicketMethod.getResponseBodyAsTicketResponse();
        } catch (Exception e) {
            throw new CalDAV4JException("Trouble handling MkTicket Response", e);
        }

        return ticketResponse.getID();

    }

    /**
     * Deletes the specified ticket on the specified resource.
     * 
     * @param httpClient the httpClient which will make the request
     * @param relativePath the path, relative to the collection path for
     *                     which to revoke the ticket 
     * @param ticketID the ticketID which to revoke
     * @throws CalDAV4JException
     *             Is thrown if the execution of the DelTicketMethod fails
     */
    public void deleteTicket(HttpClient httpClient, String relativePath, String ticketId)
            throws CalDAV4JException {
        DelTicketMethod delTicketMethod = methodFactory.createDelTicketMethod();
        delTicketMethod.setPath(getAbsolutePath(relativePath));
        delTicketMethod.setTicket(ticketId);
        try {
            httpClient.executeMethod(hostConfiguration, delTicketMethod);
            int statusCode = delTicketMethod.getStatusCode();
            if (statusCode != WebdavStatus.SC_NO_CONTENT) {
                throw new CalDAV4JException(
                        "Delete Ticket Failed with Status: " + statusCode
                                + " and body: \n"
                                + delTicketMethod.getResponseBodyAsString());
            }
        } catch (Exception e) {
            throw new CalDAV4JException("Trouble executing DelTicket", e);
        }

    }

    /**
     * Returns all the ticket ID's from all tickets the requesting user has
     * permision to view on a resource.
     * 
     * @param httpClient the httpClient which will make the request
     * @param relativePath the path, relative to the collection path for which
     *                     to get the tickets
     * @return
     * @throws CalDAV4JException
     * @throws HttpException
     * @throws IOException
     */
    public List<String> getTicketsIDs(HttpClient httpClient, String relativePath)
            throws CalDAV4JException, HttpException, IOException {

        Vector<PropertyName> properties = new Vector<PropertyName>();

        PropertyName ticketDiscoveryProperty = new PropertyName(CalDAVConstants.NS_XYTHOS,
                CalDAVConstants.ELEM_TICKETDISCOVERY);
        PropertyName ownerProperty = new PropertyName(CalDAVConstants.NS_DAV,
                "owner");

        properties.add(ticketDiscoveryProperty);
        properties.add(ownerProperty);

        PropFindMethod propFindMethod = methodFactory.createPropFindMethod();

        propFindMethod.setDepth(0);
        propFindMethod.setType(0);
        propFindMethod.setPath(getAbsolutePath(relativePath));
        propFindMethod.setPropertyNames(properties.elements());
        httpClient.executeMethod(hostConfiguration, propFindMethod);

        int statusCode = propFindMethod.getStatusCode();

        if (statusCode != WebdavStatus.SC_MULTI_STATUS) {
            throw new CalDAV4JException("PropFind Failed with Status: "
                    + statusCode + " and body: \n"
                    + propFindMethod.getResponseBodyAsString());
        }
        String href = getHref(getAbsolutePath(relativePath));
        Enumeration responses = propFindMethod.getResponseProperties(href);
        
        List<String> ticketIDList = new ArrayList<String>();
        while (responses.hasMoreElements()) {
            org.apache.webdav.lib.Property item = (org.apache.webdav.lib.Property) responses
                    .nextElement();
            if (item.getLocalName()
                    .equals(CalDAVConstants.ELEM_TICKETDISCOVERY)) {
                TicketDiscoveryProperty ticketDiscoveryProp = (TicketDiscoveryProperty) item;
                ticketIDList.addAll(ticketDiscoveryProp.getTicketIDs());
            }
        }
        return ticketIDList;
    }


    /**
     * Returns the path to the resource that contains the VEVENT with the
     * specified uid
     * 
     * @param uid
     */
    protected String getPathToResourceForEventId(HttpClient httpClient, String uid) throws CalDAV4JException{
        // first create the calendar query
        CalendarQuery query = new CalendarQuery("C", "D");
        
        query.addProperty(PROP_ETAG);
        
        CompFilter vCalendarCompFilter = new CompFilter("C");
        vCalendarCompFilter.setName(Calendar.VCALENDAR);

        CompFilter vEventCompFilter = new CompFilter("C");
        vEventCompFilter.setName(Component.VEVENT);

        PropFilter propFilter = new PropFilter("C");
        propFilter.setName(Property.UID);
        propFilter.setTextMatch(new TextMatch("C", false, uid));
        vEventCompFilter.addPropFilter(propFilter);

        vCalendarCompFilter.addCompFilter(vEventCompFilter);
        query.setCompFilter(vCalendarCompFilter);

        CalDAVReportMethod reportMethod = methodFactory
                .createCalDAVReportMethod();
        reportMethod.setPath(calendarCollectionRoot);
        reportMethod.setReportRequest(query);
        try {
            httpClient.executeMethod(hostConfiguration, reportMethod);
        } catch (Exception he) {
            throw new CalDAV4JException("Problem executing method", he);
        }

        Enumeration<CalDAVResponse> e = reportMethod.getResponses();
        if (!e.hasMoreElements()) {
            throw new ResourceNotFoundException(
                    ResourceNotFoundException.IdentifierType.UID, uid);
        }
        
        return stripHost(e.nextElement().getHref());
    }
    
    
    /**
     * Returns the path relative to the calendars path given an href
     * 
     * @param href
     * @return
     */
    protected String getRelativePath(String href){
        int start = href.indexOf(calendarCollectionRoot);
        return href.substring(start + calendarCollectionRoot.length() + 1);
    }
    
    protected CalDAVResource getCalDAVResourceForEventUID(
            HttpClient httpClient, String uid) throws CalDAV4JException {
        
        //first check the cache!
        String href = cache.getHrefForEventUID(uid);
        CalDAVResource calDAVResource = null;
        
        if (href != null) {
            calDAVResource = getCalDAVResource(httpClient, stripHost(href));

            if (calDAVResource != null) {
                return calDAVResource;
            }
        }
        
        // first create the calendar query
        CalendarQuery query = new CalendarQuery("C", "D");
        query.setCalendarDataProp(new CalendarData("C"));
        query.addProperty(PROP_ETAG);
        
        CompFilter vCalendarCompFilter = new CompFilter("C");
        vCalendarCompFilter.setName(Calendar.VCALENDAR);

        CompFilter vEventCompFilter = new CompFilter("C");
        vEventCompFilter.setName(Component.VEVENT);

        PropFilter propFilter = new PropFilter("C");
        propFilter.setName(Property.UID);
        propFilter.setTextMatch(new TextMatch("C", false, uid));
        vEventCompFilter.addPropFilter(propFilter);

        vCalendarCompFilter.addCompFilter(vEventCompFilter);
        query.setCompFilter(vCalendarCompFilter);

        CalDAVReportMethod reportMethod = methodFactory
                .createCalDAVReportMethod();
        reportMethod.setPath(calendarCollectionRoot);
        reportMethod.setReportRequest(query);
        try {
            httpClient.executeMethod(hostConfiguration, reportMethod);
        } catch (Exception he) {
            throw new CalDAV4JException("Problem executing method", he);
        }

        Enumeration<CalDAVResponse> e = reportMethod.getResponses();
        if (!e.hasMoreElements()) {
            throw new ResourceNotFoundException(
                    ResourceNotFoundException.IdentifierType.UID, uid);
        }

        calDAVResource = new CalDAVResource(e.nextElement());
        cache.putResource(calDAVResource);
        return calDAVResource;
    }
    
    /**
     * Gets the resource at the given path. Will check the cache first, and compare that to the
     * latest etag obtained using a HEAD request.
     * @param httpClient
     * @param path
     * @return
     * @throws CalDAV4JException
     */
    protected CalDAVResource getCalDAVResource(HttpClient httpClient,
            String path) throws CalDAV4JException {
        String currentEtag = getETag(httpClient, path);
        return getCalDAVResource(httpClient, path, currentEtag);
    }
    
    /**
     * Gets the resource for the given href. Will check the cache first, and if a cached
     * version exists that has the etag provided it will be returned. Otherwise, it goes
     * to the server for the resource.
     * 
     * @param httpClient
     * @param path
     * @param currentEtag
     * @return
     * @throws CalDAV4JException
     */
    protected CalDAVResource getCalDAVResource(HttpClient httpClient,
            String path, String currentEtag) throws CalDAV4JException {
        
        //first try getting from the cache
        CalDAVResource calDAVResource = cache.getResource(getHref(path));
        
        //ok, so we got the resource...but has it been changed recently?
        if (calDAVResource != null){
            String cachedEtag = calDAVResource.getResourceMetadata().getETag();
            if (cachedEtag.equals(currentEtag)){
                return calDAVResource;
            }
        }
        
        //either the etag was old, or it wasn't in the cache so let's get it
        //from the server       
        return getCalDAVResourceFromServer(httpClient, path);
        
    }
    
    /**
     * Gets a CalDAVResource from the server - in other words DOES NOT check the cache.
     * Adds the new resource to the cache, replacing any preexisting version.
     * 
     * @param httpClient
     * @param path
     * @return
     * @throws CalDAV4JException
     */
    protected CalDAVResource getCalDAVResourceFromServer(HttpClient httpClient,
            String path) throws CalDAV4JException {
        CalDAVResource calDAVResource = null;
        GetMethod getMethod = getMethodFactory().createGetMethod();
        getMethod.setPath(path);
        try {
            httpClient.executeMethod(hostConfiguration, getMethod);
            if (getMethod.getStatusCode() != WebdavStatus.SC_OK){
                throw new CalDAV4JException(
                        "Unexpected Status returned from Server: "
                                + getMethod.getStatusCode());
            }
        } catch (Exception e){
            throw new CalDAV4JException("Problem executing get method",e);
        }

        String href = getHref(path);
        Header etagHeader = getMethod.getResponseHeader("ETag");
        String etag = (etagHeader != null) ? etagHeader.getValue() : "";
        Calendar calendar = null;
        try {
            calendar = getMethod.getResponseBodyAsCalendar();
        } catch (Exception e){
            throw new CalDAV4JException("Malformed calendar resource returned.", e);
        }
        
        calDAVResource = new CalDAVResource();
        calDAVResource.setCalendar(calendar);
        calDAVResource.getResourceMetadata().setETag(etag);
        calDAVResource.getResourceMetadata().setHref(href);
        
        cache.putResource(calDAVResource);
        return calDAVResource;
        
    }
    protected void delete(HttpClient httpClient, String path)
            throws CalDAV4JException {
        DeleteMethod deleteMethod = new DeleteMethod(path);
        try {
            httpClient.executeMethod(hostConfiguration, deleteMethod);
            if (deleteMethod.getStatusCode() != WebdavStatus.SC_NO_CONTENT){
                throw new CalDAV4JException(
                        "Unexpected Status returned from Server: "
                                + deleteMethod.getStatusCode());
            }
        } catch (Exception e){
            throw new CalDAV4JException("Problem executing delete method",e);
        }
        
        cache.removeResource((getHref(path)));
    }
    
    protected void put(HttpClient httpClient, Calendar calendar, String path,
            String etag)
            throws CalDAV4JException {
        PutMethod putMethod = methodFactory.createPutMethod();
        putMethod.addEtag(etag);
        putMethod.setPath(path);
        putMethod.setIfMatch(true);
        putMethod.setRequestBody(calendar);
        try {
            httpClient.executeMethod(hostConfiguration, putMethod);
            int statusCode = putMethod.getStatusCode();
            if (statusCode!= WebdavStatus.SC_NO_CONTENT
                    && statusCode != WebdavStatus.SC_CREATED) {
                if (statusCode == WebdavStatus.SC_PRECONDITION_FAILED){
                    throw new ResourceOutOfDateException(
                            "Etag was not matched: "
                                    + etag);
                }
            }
        } catch (Exception e){
            throw new CalDAV4JException("Problem executing put method",e);
        }

        Header h = putMethod.getResponseHeader("ETag");

        if (h != null) {
            String newEtag = h.getValue();
            cache.putResource(new CalDAVResource(calendar, newEtag, getHref(path)));
        }
        

    }
    
    protected String getAbsolutePath(String relativePath){
        return   calendarCollectionRoot + "/" + relativePath;
    }
    

    
    protected String getETag(HttpClient httpClient, String path) throws CalDAV4JException{
        HeadMethod headMethod = new HeadMethod(path);
        
        try {
            httpClient.executeMethod(hostConfiguration, headMethod);
            int statusCode = headMethod.getStatusCode();
            
            if (statusCode == WebdavStatus.SC_NOT_FOUND) {
                throw new ResourceNotFoundException(
                        ResourceNotFoundException.IdentifierType.PATH, path);
            }
            
            if (statusCode != WebdavStatus.SC_OK){
                throw new CalDAV4JException(
                        "Unexpected Status returned from Server: "
                                + headMethod.getStatusCode());
            }
        } catch (Exception e){
            throw new CalDAV4JException("Problem executing get method",e);
        }
        
        Header h = headMethod.getResponseHeader("ETag");
        String etag = null;
        if (h != null) {
         etag = h.getValue();
        }
        return etag;
    }
    
    private PutMethod createPutMethodForNewResource(String resourceName,
            Calendar calendar) {
        PutMethod putMethod = methodFactory.createPutMethod();
        putMethod.setPath(calendarCollectionRoot + "/"
                + resourceName);
        putMethod.setAllEtags(true);
        putMethod.setIfNoneMatch(true);
        putMethod.setRequestBody(calendar);
        return putMethod;
    }
    
    private String getHref(String path){
        String href = hostConfiguration.getProtocol().getScheme() + "://"
        + hostConfiguration.getHost()
        + (hostConfiguration.getPort() != 80 ? ":" + hostConfiguration.getPort() : "")
        + ""
        + path;
        return href;
    }
}
