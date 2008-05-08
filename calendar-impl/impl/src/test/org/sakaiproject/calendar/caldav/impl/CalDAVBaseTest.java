package org.sakaiproject.calendar.caldav.impl;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.methods.PutMethod;
import org.sakaiproject.time.impl.BasicTimeService;

import junit.framework.TestCase;

public abstract class CalDAVBaseTest extends TestCase {
	
	private static final Log log = LogFactory.getLog(CalDAVBaseTest.class);
	
	protected CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();
	protected String calDAVServerHost = "localhost";
	protected int calDAVServerPort = 8080;
	protected String calDAVServerBasePath = "/chandler/dav";
	
	protected void put(InputStream iCalendar, String path, HttpClient http) {
        PutMethod put = methodFactory.createPutMethod();
        try {
        	put.setRequestBody(iCalendar);
        	put.setPath(path);
            http.executeMethod(createHostConfiguration(), put);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
	
	protected HttpClient createHttpClient(String username, String password){
        HttpClient http = new HttpClient();

        Credentials credentials = new UsernamePasswordCredentials(username, password);
        http.getState().setCredentials(null, null, credentials);
        http.getState().setAuthenticationPreemptive(true);
        return http;
    }
	
	protected void mkdir(String path, HttpClient http){
        MkCalendarMethod mk = new MkCalendarMethod();
        mk.setPath(path);
        try {
        http.executeMethod(createHostConfiguration(), mk);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
	
	protected void del(String path, HttpClient http){
        DeleteMethod delete = new DeleteMethod();
        delete.setPath(path);
        try {
        	http.executeMethod(createHostConfiguration(), delete);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
	
	protected HostConfiguration createHostConfiguration(){
        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(getCalDAVServerHost(), getCalDAVServerPort());
        return hostConfig;
    }
	
	protected InputStream getResourceAsStreamForName(String resourceName) {
		ClassLoader currentThreadClassLoader
        = Thread.currentThread().getContextClassLoader();

       // Add the conf dir to the classpath
       // Chain the current thread classloader
       URLClassLoader urlClassLoader;
	try {
		urlClassLoader = new URLClassLoader(new URL[]{new File("/Users/zach/").toURL()}, currentThreadClassLoader);
		// Replace the thread classloader - assumes
	       // you have permissions to do so
	       Thread.currentThread().setContextClassLoader(urlClassLoader);
	} catch (MalformedURLException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}

	return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
	}

	public String getCalDAVServerBasePath() {
		return calDAVServerBasePath;
	}

	public void setCalDAVServerBasePath(String calDAVServerBasePath) {
		this.calDAVServerBasePath = calDAVServerBasePath;
	}
	
	public String getCalDAVServerHost() {
		return calDAVServerHost;
	}

	public void setCalDAVServerHost(String calDAVServerHost) {
		this.calDAVServerHost = calDAVServerHost;
	}

	public int getCalDAVServerPort() {
		return calDAVServerPort;
	}

	public void setCalDAVServerPort(int calDAVServerPort) {
		this.calDAVServerPort = calDAVServerPort;
	}

	protected CalDAVCalendarService createCalDAVCalendarService() {
		SakaiStubFacade sakaiStub = new SakaiStubFacade();
		BasicTimeService timeService = new BasicTimeService();
		timeService.setSessionManager(sakaiStub);
		timeService.setPreferenceService(new StubPreferencesService());
		CalDAVCalendarService calDavCalendarService = new CalDAVCalendarService();
		((CalDAVCalendarService)calDavCalendarService).setEntityManager(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setFunctionManager(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setThreadLocalManager(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setSecurityService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setSessionManager(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setTimeService(timeService);
		((CalDAVCalendarService)calDavCalendarService).setAliasService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setAuthzGroupService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setEventTrackingService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setUserDirectoryService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setToolManager(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setContentHostingService(sakaiStub);
		((CalDAVCalendarService)calDavCalendarService).setIdManager(new org.sakaiproject.id.impl.UuidV4IdComponent());
		((CalDAVCalendarService)calDavCalendarService).setCalDAVServerBasePath("/chandler/dav/");
		((CalDAVCalendarService)calDavCalendarService).setCalDAVServerHost("localhost");
		((CalDAVCalendarService)calDavCalendarService).setCalDAVServerPort(8080);
		((CalDAVCalendarService)calDavCalendarService).init();
		return calDavCalendarService;
	}

}
