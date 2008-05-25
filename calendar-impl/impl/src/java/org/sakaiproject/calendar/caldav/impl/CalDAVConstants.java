/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007, 2008 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.calendar.caldav.impl;

import java.util.HashMap;
import java.util.Map;

public class CalDAVConstants {
	
	public static final String SERVER_HOST = "ganymede.unicon.net";
	public static final int SERVER_PORT = 80;
	public static final String SERVER_BASE_PATH = "/dav/";
	public static final String TEST_USER_NAME = "test";
	public static final String TEST_PASSWORD = "password";
	public static final String TEST_COLLECTION = "unit-test";
	public static final Map<String,String> TEST_PASSWORDS = new HashMap<String,String>();
	
	static {
		TEST_PASSWORDS.put("test", "password");
		TEST_PASSWORDS.put("caluser1", "bedework");
		TEST_PASSWORDS.put("instructor01", "instructor01");
		TEST_PASSWORDS.put("instructor02", "instructor02");
		TEST_PASSWORDS.put("instructor03", "instructor03");
		TEST_PASSWORDS.put("instructor04", "instructor04");
		TEST_PASSWORDS.put("instructor05", "instructor05");
		TEST_PASSWORDS.put("student01", "student01");
		TEST_PASSWORDS.put("student02", "student02");
		TEST_PASSWORDS.put("student03", "student03");
		TEST_PASSWORDS.put("student04", "student04");
		TEST_PASSWORDS.put("student05", "student05");
	}

}
