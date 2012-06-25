/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/calendar/trunk/calendar-api/api/src/java/org/sak
aiproject/calendar/api/CalendarEdit.java $
 * $Id: CalendarEdit.java 105078 2012-02-24 23:00:38Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.calendar.api;

/*
 * Used with permission from Leidse Onderwijsinstellingen. All Rights Reserved.
 */

import java.util.List;
import java.util.Map;

import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.event.api.SessionState;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.time.api.TimeRange;

/**
 * Proxy for Sakai services.
 * 
 * @author Tania Tritean, ISDC Romania!
 */
public interface SakaiProxy {

	/**
	 * Schedule tool id.
	 */
	String	SCHEDULE_TOOL_ID	= "sakai.schedule";

	/**
	 * Returns the current site id.
	 * 
	 * @return current site id;
	 */
	String getCurrentSiteId();

	/**
	 * Returns the site based on id.
	 * 
	 * @param siteId
	 *            the siteId
	 * @return site ;
	 */
	Site getSite(String siteId);

	/**
	 * Add an attribute to Sakai user session.
	 * 
	 * @param name
	 *            the name of the session attribute
	 * @param value
	 *            the value of the session attribute
	 */
	void addSessionAttribute(final String name, final Object value);

	/**
	 * Retrieve the session state associated with the given tool id..
	 * 
	 * @param toolId
	 *            the tool for which to add a session state attribute
	 * @param siteId
	 *            the site id
	 * @return SessionState
	 * @throws IdUnusedException
	 *             exception thrown
	 */
	SessionState getSessionState(final String toolId, String siteId) throws IdUnusedException;

	/**
	 * Return the current user id that is logged in sakai.
	 * 
	 * @return currentUserId.
	 */
	String getCurrentUserId();

	/**
	 * Gets the URL for a given tool.
	 * 
	 * @param toolregId
	 *            the id of the tool
	 * @param localView
	 *            the local view
	 * @param params
	 *            parameters
	 * @param locationReference
	 *            the location ref (eg. /site/)
	 * @return the URL string
	 */
	String getUrlForTool(final String toolregId, final String localView, final Map<String, String> params,
			final String locationReference);

	/**
	 * Get the current placement context.
	 * 
	 * @return the current placement context
	 */
	String getCurrentPlacementContext();

	/**
	 * Return the current tool id from for current context.
	 * 
	 * @return current tool id.
	 */
	String getCurrentPlacementToolId();

	/**
	 * Verifies if the current user has required function.
	 * 
	 * @param requiredFunction
	 *            the required function.
	 * @return true if the current user has the given function
	 */
	boolean hasCurrentUserPermissionForCurrentSite(String requiredFunction);

	/**
	 * Verifies if the provided user has required function.
	 * 
	 * @param userId
	 *            the id of the user
	 * @param requiredFunction
	 *            the required function.
	 * @return true if the current user has the given function
	 */
	boolean hasUserPermissionForCurrentSite(final String requiredFunction, final String userId);

	/**
	 * Returns true if current user has given permission in given site.
	 * 
	 * @param requiredFunction
	 *            the required function.s
	 * @param siteId
	 *            the site id
	 * @return true if the current user has the given function in giuven site.
	 */
	boolean hasCurentUserPermissionForSite(final String requiredFunction, final String siteId);

	/**
	 * Gets tool from current site by common id.
	 * 
	 * @param commonId
	 *            id
	 * @return tool
	 */
	ToolConfiguration getToolByCommonIdInCurrentSite(String commonId);

	/**
	 * Return true is the site is user site
	 * 
	 * @param siteId
	 *            site id
	 * @return true is the site is user site
	 */
	boolean isUserSite(String siteId);

	/**
	 * Return true is current user is super user
	 * 
	 * @return true is current user is super user
	 */
	boolean isSuperUser();

	/**
	 * Pulls excluded site ids from Tabs preferences
	 * 
	 * @return excluded sites
	 */
	List getExcludedSitesFromTabs();

	/**
	 * Session user.
	 * 
	 * @return user
	 */
	String getCurrentSessionUserId();

	/**
	 * Gets range.
	 * 
	 * @return range
	 */
	TimeRange getICalTimeRange();

	/**
	 * Create a new Reference object, from the given reference string.
	 * 
	 * @param refString
	 *        The reference string.
	 * @return a new reference object made from the given reference string.
	 */
	Reference createReference(String refString);
	
	/**
	 * Gets portal url.
	 * 
	 * @return portal url
	 */
	String getPortalUrl();
	
	/**
	 * Gets the portal string. In sakai properties can be under key portalPath. If nothing is set in sakai.properties
	 * file, the current portal will be returned.
	 * 
	 * @return the portal path.
	 */
	String getPortalPath();

}
