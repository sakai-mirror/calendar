package org.sakaiproject.calendar.caldav.impl;

import java.io.InputStream;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.Privilege;
import org.apache.webdav.lib.methods.AclMethod;
import org.apache.webdav.lib.methods.DeleteMethod;
import org.osaf.caldav4j.CalDAVCalendarCollection;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.methods.PutMethod;

public class CalDAVUtility {
	
	protected static CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String path = "/dav/student01/CS+310";
		CalDAVCalendarCollection calendarCollection = createCalDAVCalendarCollection();
		HttpClient http = createHttpClient("student01", "student01");
		del(path, http);
		//mkdir(path, http);
		//grantReadWrite("authenticated", path, http);
		
		//calendarCollection.deleteEvent(http, "1556ddcc-0e55-4b26-a839-a72eecd02c8b");
		System.out.println("Done.");

	}
	
	public static HttpClient createHttpClient(String username, String password){
        HttpClient http = new HttpClient();

        Credentials credentials = new UsernamePasswordCredentials(username, password);
        http.getState().setCredentials(null, null, credentials);
        http.getState().setAuthenticationPreemptive(true);
        return http;
    }
	
	public static void mkdir(String path, HttpClient http){
        MkCalendarMethod mk = new MkCalendarMethod();
        mk.setPath(path);
        try {
        http.executeMethod(createHostConfiguration(), mk);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
	
	public static void del(String path, HttpClient http){
        DeleteMethod delete = new DeleteMethod();
        delete.setPath(path);
        try {
        	http.executeMethod(createHostConfiguration(), delete);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
	
	public static void grantReadWrite(String principal, String path, HttpClient http){
        AclMethod acl = new AclMethod();
        Ace ace = new Ace(principal);
        ace.addPrivilege(Privilege.READ);
        ace.addPrivilege(Privilege.WRITE);
        acl.addAce(ace);
        acl.setPath(path);
        try {
        http.executeMethod(createHostConfiguration(), acl);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
	
	public static void put(InputStream iCalendar, String path, HttpClient http) {
        PutMethod put = methodFactory.createPutMethod();
        try {
        	put.setRequestBody(iCalendar);
        	put.setPath(path);
            http.executeMethod(createHostConfiguration(), put);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
	
	private static HostConfiguration createHostConfiguration() {
		return createHostConfiguration(CalDAVConstants.SERVER_HOST, CalDAVConstants.SERVER_PORT);
	}

	public static HostConfiguration createHostConfiguration(String serverHost, int serverPort){
        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(serverHost, serverPort);
        return hostConfig;
    }
	
	private static CalDAVCalendarCollection createCalDAVCalendarCollection() {
        CalDAVCalendarCollection calendarCollection = new CalDAVCalendarCollection(
                CalDAVConstants.SERVER_BASE_PATH + CalDAVConstants.TEST_USER_NAME + "/" + CalDAVConstants.TEST_COLLECTION, createHostConfiguration(), methodFactory,
                org.osaf.caldav4j.CalDAVConstants.PROC_ID_DEFAULT);
        return calendarCollection;
    }

}
