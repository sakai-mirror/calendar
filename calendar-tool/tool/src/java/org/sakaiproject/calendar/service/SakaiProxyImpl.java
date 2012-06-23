/**********************************************************************************
 * $URL: $
 * $Id: CalendarAction.java 105079 2012-02-24 23:08:11Z ottenhoff@longsight.com $
 ***********************************************************************************
 *
 * Copyright (c) 2012 The Sakai Foundation
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

/*
 * Modified from code with permission granted by Leidse Onderwijsinstellingen. 
 */

package org.sakaiproject.calendar.tool.service;


import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.event.api.SessionState;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.api.TimeRange;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.PreferencesService;
import org.sakaiproject.user.api.UserDirectoryService;

import org.sakaiproject.calendar.api.SakaiProxy;

/**
 * @author Tania Tritean,ISDC!
 */
public class SakaiProxyImpl implements SakaiProxy {
	
	/**
	 * Logger class.
	 */
	private static final Log				LOGGER					= LogFactory.getLog(SakaiProxyImpl.class);

	/** Used to retrieve non-notification sites for MyWorkspace page */
	private static final String				TABS_EXCLUDED_PREFS		= "sakai:portal:sitenav";

	/**
	 * Excluded site.
	 */
	private final String					TAB_EXCLUDED_SITES		= "exclude";

	/**
	 * The Sakai portal path key from sakai.properties.
	 */
	private static final String				SAKAI_PORTAL_PATH_KEY	= "portalPath";

	/**
	 * Reference to site service.
	 */
	private SiteService						siteService;

	/**
	 * Reference to the tool manager.
	 */
	private ToolManager						toolManager;

	/**
	 * Reference to the session manager.
	 */
	private SessionManager					sessionManager;

	/**
	 * Reference to user directory service.
	 */
	private UserDirectoryService			userDirectoryService;

	/**
	 * Reference to the developer helper service.
	 */
	private DeveloperHelperService			developerHelperService;

	/**
	 * Usage session service.
	 */
	private UsageSessionService				usageSessionService;

	/**
	 * Sakai server configuration service.
	 */
	private ServerConfigurationService		serverConfigurationService;

	/**
	 * Reference to security service.
	 */
	private SecurityService					securityService;

	/**
	 * Time service.
	 */
	private transient TimeService			timeService;

	/**
	 * Preferences service.
	 */
	private transient PreferencesService	preferencesService;

	/**
	 * Entity manager.
	 */
	private transient EntityManager			entityManager;

	/**
	 * Constructor with no parameters.
	 */
	public SakaiProxyImpl() {
		super();
	}

	/**
	 * Create a new Reference object, from the given reference string.
	 * 
	 * @param refString
	 *            The reference string.
	 * @return a new reference object made from the given reference string.
	 */
	public Reference createReference(final String refString) {
		return this.entityManager.newReference(refString);
	}

	/**
	 * Gets tool from current site by common id.
	 * 
	 * @return tool
	 */
	public ToolConfiguration getToolByCommonIdInCurrentSite(final String commonId) {
		final ToolConfiguration tc = this.getCurrentSite().getToolForCommonId(commonId);
		return tc;
	}

	/**
	 * Gets tool from current site by common id.
	 * 
	 * @return tool
	 */
	public ToolConfiguration getToolByCommonIdInSite(final String commonId, final String siteId) {
		final ToolConfiguration tc = this.getCurrentSite().getToolForCommonId(commonId);
		return tc;
	}

	/**
	 * Session user.
	 * 
	 * @return user
	 */
	public String getCurrentSessionUserId() {
		return this.sessionManager.getCurrentSessionUserId();
	}

	/**
	 * @param securityService
	 *            the securityService
	 */
	public void setSecurityService(final SecurityService securityService) {
		this.securityService = securityService;
	}

	/**
	 * Return the site id for the current user.
	 * 
	 * @return the id of the user's site
	 */
	public String getCurrentSiteId() {
		return this.getCurrentSite().getId();
	}

	/**
	 * Pulls excluded site ids from Tabs preferences
	 * 
	 * @return excluded sites
	 */
	public List getExcludedSitesFromTabs() {
		final Preferences prefs = this.preferencesService.getPreferences(this.sessionManager.getCurrentSessionUserId());
		final ResourceProperties props = prefs.getProperties(SakaiProxyImpl.TABS_EXCLUDED_PREFS);
		final List l = props.getPropertyList(this.TAB_EXCLUDED_SITES);
		return l;
	}

	/**
	 * Return true is the site is user site
	 * 
	 * @param siteId
	 *            site id
	 * @return true is the site is user site
	 */
	public boolean isUserSite(final String siteId) {
		return this.siteService.isUserSite(siteId);
	}

	/**
	 * Return true is current user is super user
	 * 
	 * @return true is current user is super user
	 */
	public boolean isSuperUser() {
		return this.securityService.isSuperUser();
	}

	/**
	 * Return current site.
	 * 
	 * @return current site
	 */
	private Site getCurrentSite() {
		try {
			final String userSiteId;
			if (this.toolManager.getCurrentPlacement() != null) {
				userSiteId = this.siteService.getSite(this.toolManager.getCurrentPlacement().getContext()).getId();
			} else {
				userSiteId = this.siteService.getUserSiteId(this.getCurrentUserId());
			}
			return this.siteService.getSite(userSiteId);
		} catch (final IdUnusedException e) {
			SakaiProxyImpl.LOGGER.error("Current site does not exist or is not used.", e);
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Returns the site based on id.
	 * 
	 * @param siteId
	 *            the siteId
	 * @return site
	 */
	public Site getSite(final String siteId) {
		try {
			return this.siteService.getSite(siteId);
		} catch (final IdUnusedException e) {
			SakaiProxyImpl.LOGGER.error("Site does not exist or is not used.", e);
			throw new IllegalArgumentException(e);
		}
	}

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
	public String getUrlForTool(final String toolregId, final String localView, final Map params,
			final String locationReference) {

		return this.developerHelperService.getToolViewURL(toolregId, localView, params, locationReference);
	}

	/**
	 * Gets range.
	 * 
	 * @return range
	 */
	public TimeRange getICalTimeRange() {
		final Time now = this.timeService.newTime();

		final Calendar c = Calendar.getInstance();
		c.setTimeInMillis(now.getTime());

		final Time startTime = this.timeService.newTime(c.getTimeInMillis());
		
		c.add(Calendar.YEAR, 1);
		
		final Time endTime = this.timeService.newTime(c.getTimeInMillis());

		return this.timeService.newTimeRange(startTime, endTime, true, true);
	}

	/**
	 * Add an attribute to Sakai user session.
	 * 
	 * @param name
	 *            the name of the session attribute
	 * @param value
	 *            the value of the session attribute
	 */
	public void addSessionAttribute(final String name, final Object value) {
		this.sessionManager.getCurrentSession().setAttribute(name, value);
	}

	/**
	 * Retrieve the session state associated with the given tool id..
	 * 
	 * @param toolId
	 *            the tool for which to add a session state attribute
	 * @param siteId
	 *            the site id
	 * @return SessionState the sessionState
	 * @throws IdUnusedException
	 *             exception thrown
	 */
	public SessionState getSessionState(final String toolId, final String siteId) throws IdUnusedException {
		final Site currentSite = this.siteService.getSite(siteId);
		final Placement targetToolPlacement = (Placement) (currentSite.getTools(toolId).toArray()[0]);
		final String toolPlacementId = targetToolPlacement.getId();

		return this.usageSessionService.getSessionState(toolPlacementId);
	}

	/**
	 * Return the current user id that is logged in sakai.
	 * 
	 * @return currentUserId.
	 */
	public String getCurrentUserId() {
		return this.userDirectoryService.getCurrentUser().getId();
	}

	/**
	 * Return the current tool id from for current context.
	 * 
	 * @return current tool id.
	 */
	public String getCurrentPlacementToolId() {
		return this.toolManager.getCurrentPlacement().getToolId();
	}

	/**
	 * @param siteService
	 *            the siteService to set
	 */
	public void setSiteService(final SiteService siteService) {
		this.siteService = siteService;
	}

	/**
	 * @param toolManager
	 *            the toolManager to set
	 */
	public void setToolManager(final ToolManager toolManager) {
		this.toolManager = toolManager;
	}

	/**
	 * @param sessionManager
	 *            the sessionManager to set
	 */
	public void setSessionManager(final SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	/**
	 * @param userDirectoryService
	 *            the userDirectoryService to set
	 */
	public void setUserDirectoryService(final UserDirectoryService userDirectoryService) {
		this.userDirectoryService = userDirectoryService;
	}

	/**
	 * @param developerHelperService
	 *            the developerHelperService to set
	 */
	public void setDeveloperHelperService(final DeveloperHelperService developerHelperService) {
		this.developerHelperService = developerHelperService;
	}

	/**
	 * @param usageSessionService
	 *            the usageSessionService to set
	 */
	public void setUsageSessionService(final UsageSessionService usageSessionService) {
		this.usageSessionService = usageSessionService;
	}

	/**
	 * @param serverConfigurationService
	 *            the serverConfigurationService to set
	 */
	public void setServerConfigurationService(final ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}

	/**
	 * Get the current placement context.
	 * 
	 * @return the current placement context
	 */
	public String getCurrentPlacementContext() {
		return this.toolManager.getCurrentPlacement().getContext();
	}

	/**
	 * Verifies if the current user has required function.
	 * 
	 * @param requiredFunction
	 *            the required function
	 * @return true if the current user has the given function
	 */
	public boolean hasCurrentUserPermissionForCurrentSite(final String requiredFunction) {
		final String userId = this.userDirectoryService.getCurrentUser().getId();
		return this.hasUserPermissionForCurrentSite(requiredFunction, userId);
	}

	/**
	 * Verifies if the provided user has required function.
	 * 
	 * @param requiredFunction
	 *            the required function.
	 * @param userId
	 *            the id of the user
	 * @return true if the current user has the given function
	 */
	public boolean hasUserPermissionForCurrentSite(final String requiredFunction, final String userId) {
		final String siteId = this.getCurrentSiteId();
		final String siteRef = this.siteService.siteReference(siteId);

		return this.securityService.unlock(userId, requiredFunction, siteRef);
	}

	/**
	 * Returns true if current user has given permission in given site.
	 * 
	 * @param requiredFunction
	 *            the required function.
	 * @param siteId
	 *            the site id
	 * @return true if the current user has the given function in giuven site.
	 */
	public boolean hasCurentUserPermissionForSite(final String requiredFunction, final String siteId) {
		final String siteRef = this.siteService.siteReference(siteId);
		final String userId = this.userDirectoryService.getCurrentUser().getId();

		return this.securityService.unlock(userId, requiredFunction, siteRef);
	}

	/**
	 * Gets the portal string. In sakai properties can be under key portalPath. If nothing is set in sakai.properties
	 * file, the current portal will be returned.
	 * 
	 * @return the portal path.
	 */
	public String getPortalPath() {
		return this.serverConfigurationService.getString(SakaiProxyImpl.SAKAI_PORTAL_PATH_KEY);
	}

	/**
	 * Gets portal url.
	 * 
	 * @return portal url
	 */
	public String getPortalUrl() {
		return this.serverConfigurationService.getPortalUrl();
	}

	/**
	 * @return the serverConfigurationService
	 */
	public ServerConfigurationService getServerConfigurationService() {
		return this.serverConfigurationService;
	}

	/**
	 * @param timeService
	 *            the timeService to set
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/**
	 * @param preferencesService
	 *            the preferencesService to set
	 */
	public void setPreferencesService(final PreferencesService preferencesService) {
		this.preferencesService = preferencesService;
	}

	/**
	 * @param entityManager
	 *            the entityManager to set
	 */
	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
