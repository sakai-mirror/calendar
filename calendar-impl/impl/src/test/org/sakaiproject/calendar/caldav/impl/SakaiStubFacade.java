package org.sakaiproject.calendar.caldav.impl;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;
import java.util.Stack;
import java.util.TimeZone;

import org.sakaiproject.alias.api.AliasEdit;
import org.sakaiproject.alias.api.AliasService;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.AuthzPermissionException;
import org.sakaiproject.authz.api.FunctionManager;
import org.sakaiproject.authz.api.GroupAlreadyDefinedException;
import org.sakaiproject.authz.api.GroupFullException;
import org.sakaiproject.authz.api.GroupIdInvalidException;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSession;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdLengthException;
import org.sakaiproject.exception.IdUniquenessException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteAdvisor;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.thread_local.api.ThreadLocalManager;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.api.TimeBreakdown;
import org.sakaiproject.time.api.TimeRange;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolManager;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserIdInvalidException;
import org.sakaiproject.user.api.UserLockedException;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserPermissionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SakaiStubFacade implements FunctionManager, ThreadLocalManager,
		SecurityService, AliasService, SessionManager, EventTrackingService,
		TimeService, AuthzGroupService, SiteService, ContentHostingService,
		ToolManager, UserDirectoryService, EntityManager {

	public List getRegisteredFunctions() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getRegisteredFunctions(String prefix) {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerFunction(String function) {
		// TODO Auto-generated method stub

	}

	public void clear() {
		// TODO Auto-generated method stub

	}

	public Object get(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public void set(String name, Object value) {
		// TODO Auto-generated method stub

	}

	public void clearAdvisors() {
		// TODO Auto-generated method stub

	}

	public boolean hasAdvisors() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSuperUser() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSuperUser(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	public SecurityAdvisor popAdvisor() {
		// TODO Auto-generated method stub
		return null;
	}

	public void pushAdvisor(SecurityAdvisor advisor) {
		// TODO Auto-generated method stub

	}

	public boolean unlock(String lock, String reference) {
		return true;
	}

	public boolean unlock(User user, String lock, String reference) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean unlock(String userId, String lock, String reference) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean unlock(String userId, String lock, String reference,
			Collection authzGroupIds) {
		// TODO Auto-generated method stub
		return true;
	}

	public List unlockUsers(String lock, String reference) {
		// TODO Auto-generated method stub
		return null;
	}

	public AliasEdit add(String id) throws IdInvalidException, IdUsedException,
			PermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public String aliasReference(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean allowAdd() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowEdit(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemoveAlias(String alias) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemoveTargetAliases(String target) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowSetAlias(String alias, String target) {
		// TODO Auto-generated method stub
		return false;
	}

	public void cancel(AliasEdit edit) {
		// TODO Auto-generated method stub

	}

	public void commit(AliasEdit edit) {
		// TODO Auto-generated method stub

	}

	public int countAliases() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int countSearchAliases(String criteria) {
		// TODO Auto-generated method stub
		return 0;
	}

	public AliasEdit edit(String id) throws IdUnusedException,
			PermissionException, InUseException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getAliases(String target) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getAliases(String target, int first, int last) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getAliases(int first, int last) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTarget(String alias) throws IdUnusedException {
		// TODO Auto-generated method stub
		return null;
	}

	public void remove(AliasEdit edit) throws PermissionException {
		// TODO Auto-generated method stub

	}

	public void removeAlias(String alias) throws IdUnusedException,
			PermissionException, InUseException {
		// TODO Auto-generated method stub

	}

	public void removeTargetAliases(String target) throws PermissionException {
		// TODO Auto-generated method stub

	}

	public List searchAliases(String criteria, int first, int last) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAlias(String alias, String target) throws IdUsedException,
			IdInvalidException, PermissionException {
		// TODO Auto-generated method stub

	}

	public String archive(String siteId, Document doc, Stack stack,
			String archivePath, List attachments) {
		// TODO Auto-generated method stub
		return null;
	}

	public Entity getEntity(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getEntityAuthzGroups(Reference ref, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityDescription(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties getEntityResourceProperties(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityUrl(Reference ref) {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpAccess getHttpAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String merge(String siteId, Element root, String archivePath,
			String fromSiteId, Map attachmentNames, Map userIdTrans,
			Set userListAllowImport) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean parseEntityReference(String reference, Reference ref) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean willArchiveMerge() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getActiveUserCount(int secs) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Session getCurrentSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCurrentSessionUserId() {
		return "test";
	}

	public ToolSession getCurrentToolSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public Session getSession(String sessionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setCurrentSession(Session s) {
		// TODO Auto-generated method stub

	}

	public void setCurrentToolSession(ToolSession s) {
		// TODO Auto-generated method stub

	}

	public Session startSession() {
		// TODO Auto-generated method stub
		return null;
	}

	public Session startSession(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addLocalObserver(Observer observer) {
		// TODO Auto-generated method stub

	}

	public void addObserver(Observer observer) {
		// TODO Auto-generated method stub

	}

	public void addPriorityObserver(Observer observer) {
		// TODO Auto-generated method stub

	}

	public void deleteObserver(Observer observer) {
		// TODO Auto-generated method stub

	}

	public Event newEvent(String event, String resource, boolean modify) {
		// TODO Auto-generated method stub
		return null;
	}

	public Event newEvent(String event, String resource, boolean modify,
			int priority) {
		// TODO Auto-generated method stub
		return null;
	}

	public void post(Event event) {
		// TODO Auto-generated method stub

	}

	public void post(Event event, UsageSession session) {
		// TODO Auto-generated method stub

	}

	public boolean clearLocalTimeZone(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean different(Time a, Time b) {
		// TODO Auto-generated method stub
		return false;
	}

	public GregorianCalendar getCalendar(TimeZone zone, int year, int month,
			int day, int hour, int min, int sec, int ms) {
		// TODO Auto-generated method stub
		return null;
	}

	public TimeZone getLocalTimeZone() {
		// TODO Auto-generated method stub
		return null;
	}

	public Time newTime() {
		// TODO Auto-generated method stub
		return null;
	}

	public Time newTime(long value) {
		// TODO Auto-generated method stub
		return null;
	}

	public Time newTime(GregorianCalendar cal) {
		// TODO Auto-generated method stub
		return null;
	}

	public TimeBreakdown newTimeBreakdown(int year, int month, int day,
			int hour, int minute, int second, int millisecond) {
		// TODO Auto-generated method stub
		return null;
	}

	public Time newTimeGmt(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	public Time newTimeGmt(int year, int month, int day, int hour, int minute,
			int second, int millisecond) {
		// TODO Auto-generated method stub
		return null;
	}

	public Time newTimeGmt(TimeBreakdown breakdown) {
		// TODO Auto-generated method stub
		return null;
	}

	public Time newTimeLocal(int year, int month, int day, int hour,
			int minute, int second, int millisecond) {
		// TODO Auto-generated method stub
		return null;
	}

	public Time newTimeLocal(TimeBreakdown breakdown) {
		// TODO Auto-generated method stub
		return null;
	}

	public TimeRange newTimeRange(Time start, Time end, boolean startIncluded,
			boolean endIncluded) {
		// TODO Auto-generated method stub
		return null;
	}

	public TimeRange newTimeRange(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	public TimeRange newTimeRange(Time startAndEnd) {
		// TODO Auto-generated method stub
		return null;
	}

	public TimeRange newTimeRange(long start, long duration) {
		// TODO Auto-generated method stub
		return null;
	}

	public TimeRange newTimeRange(Time start, Time end) {
		// TODO Auto-generated method stub
		return null;
	}

	public AuthzGroup addAuthzGroup(String id) throws GroupIdInvalidException,
			GroupAlreadyDefinedException, AuthzPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public AuthzGroup addAuthzGroup(String id, AuthzGroup other,
			String maintainUserId) throws GroupIdInvalidException,
			GroupAlreadyDefinedException, AuthzPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean allowAdd(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowJoinGroup(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemove(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUnjoinGroup(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdate(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public String authzGroupReference(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public int countAuthzGroups(String criteria) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Set getAllowedFunctions(String role, Collection azGroups) {
		// TODO Auto-generated method stub
		return null;
	}

	public AuthzGroup getAuthzGroup(String id) throws GroupNotDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getAuthzGroupIds(String providerId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getAuthzGroups(String criteria, PagingPosition page) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getAuthzGroupsIsAllowed(String userId, String function,
			Collection azGroups) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getAuthzUserGroupIds(ArrayList authzGroupIds, String userid) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getProviderIds(String authzGroupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserRole(String userId, String azGroupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getUsersIsAllowed(String function, Collection azGroups) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getUsersRole(Collection userIds, String azGroupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAllowed(String userId, String function, String azGroupId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAllowed(String userId, String function, Collection azGroups) {
		// TODO Auto-generated method stub
		return false;
	}

	public void joinGroup(String authzGroupId, String role)
			throws GroupNotDefinedException, AuthzPermissionException {
		// TODO Auto-generated method stub

	}

	public void joinGroup(String authzGroupId, String role, int maxSize)
			throws GroupNotDefinedException, AuthzPermissionException,
			GroupFullException {
		// TODO Auto-generated method stub

	}

	public AuthzGroup newAuthzGroup(String id, AuthzGroup other,
			String maintainUserId) throws GroupAlreadyDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public void refreshUser(String userId) {
		// TODO Auto-generated method stub

	}

	public void removeAuthzGroup(AuthzGroup azGroup)
			throws AuthzPermissionException {
		// TODO Auto-generated method stub

	}

	public void removeAuthzGroup(String id) throws AuthzPermissionException {
		// TODO Auto-generated method stub

	}

	public void save(AuthzGroup azGroup) throws GroupNotDefinedException,
			AuthzPermissionException {
		// TODO Auto-generated method stub

	}

	public void unjoinGroup(String authzGroupId)
			throws GroupNotDefinedException, AuthzPermissionException {
		// TODO Auto-generated method stub

	}

	public Site addSite(String id, String type) throws IdInvalidException,
			IdUsedException, PermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public Site addSite(String id, Site other) throws IdInvalidException,
			IdUsedException, PermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public void addSiteAdvisor(SiteAdvisor advisor) {
		// TODO Auto-generated method stub

	}

	public boolean allowAccessSite(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowAddSite(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemoveSite(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUnjoinSite(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateGroupMembership(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateSite(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateSiteMembership(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowViewRoster(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public int countSites(SelectionType type, Object ofType, String criteria,
			Map propertyCriteria) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Group findGroup(String refOrId) {
		// TODO Auto-generated method stub
		return null;
	}

	public SitePage findPage(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public ToolConfiguration findTool(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getLayoutNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public Site getSite(String id) throws IdUnusedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SiteAdvisor> getSiteAdvisors() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteDisplay(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteSkin(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteSpecialId(String site) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getSiteTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteUserId(String site) {
		// TODO Auto-generated method stub
		return null;
	}

	public Site getSiteVisit(String id) throws IdUnusedException,
			PermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getSites(SelectionType type, Object ofType, String criteria,
			Map propertyCriteria, SortType sort, PagingPosition page) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSpecialSiteId(String special) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserSiteId(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSpecialSite(String site) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isUserSite(String site) {
		// TODO Auto-generated method stub
		return false;
	}

	public void join(String id) throws IdUnusedException, PermissionException {
		// TODO Auto-generated method stub

	}

	public String merge(String toSiteId, Element e, String creatorId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeSite(Site site) throws PermissionException {
		// TODO Auto-generated method stub

	}

	public boolean removeSiteAdvisor(SiteAdvisor advisor) {
		// TODO Auto-generated method stub
		return false;
	}

	public void save(Site site) throws IdUnusedException, PermissionException {
		// TODO Auto-generated method stub

	}

	public void saveGroupMembership(Site site) throws IdUnusedException,
			PermissionException {
		// TODO Auto-generated method stub

	}

	public void saveSiteInfo(String id, String description, String infoUrl)
			throws IdUnusedException, PermissionException {
		// TODO Auto-generated method stub

	}

	public void saveSiteMembership(Site site) throws IdUnusedException,
			PermissionException {
		// TODO Auto-generated method stub

	}

	public void setSiteSecurity(String siteId, Set updateUsers,
			Set visitUnpUsers, Set visitUsers) {
		// TODO Auto-generated method stub

	}

	public void setUserSecurity(String userId, Set updateSites,
			Set visitUnpSites, Set visitSites) {
		// TODO Auto-generated method stub

	}

	public boolean siteExists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public String siteGroupReference(String siteId, String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String sitePageReference(String siteId, String pageId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String siteReference(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public String siteToolReference(String siteId, String toolId) {
		// TODO Auto-generated method stub
		return null;
	}

	public void unjoin(String id) throws IdUnusedException, PermissionException {
		// TODO Auto-generated method stub

	}

	public ContentResource addAttachmentResource(String name, String type,
			byte[] content, ResourceProperties properties)
			throws IdInvalidException, InconsistentException, IdUsedException,
			PermissionException, OverQuotaException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentResource addAttachmentResource(String name, String site,
			String tool, String type, byte[] content,
			ResourceProperties properties) throws IdInvalidException,
			InconsistentException, IdUsedException, PermissionException,
			OverQuotaException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentResourceEdit addAttachmentResource(String name)
			throws IdInvalidException, InconsistentException, IdUsedException,
			PermissionException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentCollection addCollection(String id,
			ResourceProperties properties) throws IdUsedException,
			IdInvalidException, PermissionException, InconsistentException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentCollection addCollection(String id,
			ResourceProperties properties, Collection groups)
			throws IdUsedException, IdInvalidException, PermissionException,
			InconsistentException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentCollection addCollection(String id,
			ResourceProperties properties, Collection groups, boolean hidden,
			Time releaseDate, Time retractDate) throws IdUsedException,
			IdInvalidException, PermissionException, InconsistentException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentCollectionEdit addCollection(String id)
			throws IdUsedException, IdInvalidException, PermissionException,
			InconsistentException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentCollectionEdit addCollection(String collectionId, String name)
			throws PermissionException, IdUnusedException, IdUsedException,
			IdLengthException, IdInvalidException, TypeException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties addProperty(String id, String name, String value)
			throws PermissionException, IdUnusedException, TypeException,
			InUseException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentResource addResource(String id, String type, byte[] content,
			ResourceProperties properties, int priority)
			throws PermissionException, IdUsedException, IdInvalidException,
			InconsistentException, OverQuotaException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentResource addResource(String name, String collectionId,
			int limit, String type, byte[] content,
			ResourceProperties properties, int priority)
			throws PermissionException, IdUniquenessException,
			IdLengthException, IdInvalidException, InconsistentException,
			IdLengthException, OverQuotaException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentResource addResource(String id, String type, byte[] content,
			ResourceProperties properties, Collection groups, int priority)
			throws PermissionException, IdUsedException, IdInvalidException,
			InconsistentException, OverQuotaException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentResource addResource(String name, String collectionId,
			int limit, String type, byte[] content,
			ResourceProperties properties, Collection groups, int priority)
			throws PermissionException, IdUniquenessException,
			IdLengthException, IdInvalidException, InconsistentException,
			IdLengthException, OverQuotaException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentResource addResource(String name, String collectionId,
			int limit, String type, byte[] content,
			ResourceProperties properties, Collection groups, boolean hidden,
			Time releaseDate, Time retractDate, int priority)
			throws PermissionException, IdUniquenessException,
			IdLengthException, IdInvalidException, InconsistentException,
			IdLengthException, OverQuotaException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentResourceEdit addResource(String id)
			throws PermissionException, IdUsedException, IdInvalidException,
			InconsistentException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentResourceEdit addResource(String collectionId,
			String basename, String extension, int maximum_tries)
			throws PermissionException, IdUniquenessException,
			IdLengthException, IdInvalidException, IdUnusedException,
			OverQuotaException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean allowAddAttachmentResource() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowAddCollection(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowAddProperty(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowAddResource(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowCopy(String id, String new_id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowGetCollection(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowGetProperties(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowGetResource(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemoveCollection(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemoveProperty(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemoveResource(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRename(String id, String new_id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateCollection(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateResource(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public String archiveResources(List resources, Document doc, Stack stack,
			String archivePath) {
		// TODO Auto-generated method stub
		return null;
	}

	public void cancelCollection(ContentCollectionEdit edit) {
		// TODO Auto-generated method stub

	}

	public void cancelResource(ContentResourceEdit edit) {
		// TODO Auto-generated method stub

	}

	public void checkCollection(String id) throws IdUnusedException,
			TypeException, PermissionException {
		// TODO Auto-generated method stub

	}

	public void checkResource(String id) throws PermissionException,
			IdUnusedException, TypeException {
		// TODO Auto-generated method stub

	}

	public void commitCollection(ContentCollectionEdit edit) {
		// TODO Auto-generated method stub

	}

	public void commitResource(ContentResourceEdit edit)
			throws OverQuotaException, ServerOverloadException {
		// TODO Auto-generated method stub

	}

	public void commitResource(ContentResourceEdit edit, int priority)
			throws OverQuotaException, ServerOverloadException {
		// TODO Auto-generated method stub

	}

	public boolean containsLockedNode(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public String copy(String id, String new_id) throws PermissionException,
			IdUnusedException, TypeException, InUseException,
			OverQuotaException, IdUsedException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public String copyIntoFolder(String id, String folder_id)
			throws PermissionException, IdUnusedException, TypeException,
			InUseException, OverQuotaException, IdUsedException,
			ServerOverloadException, InconsistentException, IdLengthException,
			IdUniquenessException {
		// TODO Auto-generated method stub
		return null;
	}

	public void createDropboxCollection() {
		// TODO Auto-generated method stub

	}

	public void createDropboxCollection(String siteId) {
		// TODO Auto-generated method stub

	}

	public void createIndividualDropbox(String siteId) {
		// TODO Auto-generated method stub

	}

	public ContentCollectionEdit editCollection(String id)
			throws IdUnusedException, TypeException, PermissionException,
			InUseException {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentResourceEdit editResource(String id)
			throws PermissionException, IdUnusedException, TypeException,
			InUseException {
		// TODO Auto-generated method stub
		return null;
	}

	public void eliminateDuplicates(Collection resourceIds) {
		// TODO Auto-generated method stub

	}

	public List findResources(String type, String primaryMimeType,
			String subMimeType) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getAllEntities(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getAllResources(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentCollection getCollection(String id) throws IdUnusedException,
			TypeException, PermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getCollectionMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCollectionSize(String id) throws IdUnusedException,
			TypeException, PermissionException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getContainingCollectionId(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getDepth(String resourceId, String baseCollectionId) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getDropboxCollection() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDropboxCollection(String siteId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDropboxDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDropboxDisplayName(String siteId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getGroupsWithAddPermission(String collectionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getGroupsWithReadAccess(String collectionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getGroupsWithRemovePermission(String collectionId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getIndividualDropboxId(String entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getLocks(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties getProperties(String id)
			throws PermissionException, IdUnusedException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getQuota(ContentCollection collection) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getReference(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContentResource getResource(String id) throws PermissionException,
			IdUnusedException, TypeException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<ContentResource> getResourcesOfType(String resourceType,
			int pageSize, int page) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSiteCollection(String siteId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUrl(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUrl(String id, String rootProperty) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUuid(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAttachmentResource(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAvailabilityEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAvailable(String entityId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCollection(String entityId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isContentHostingHandlersEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDropboxMaintainer() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDropboxMaintainer(String siteId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInDropbox(String entityId) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isInheritingPubView(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isLocked(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPubView(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRootCollection(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isShortRefs() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSortByPriorityEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	public void lockObject(String id, String lockId, String subject,
			boolean system) {
		// TODO Auto-generated method stub

	}

	public String moveIntoFolder(String id, String folder_id)
			throws PermissionException, IdUnusedException, TypeException,
			InUseException, OverQuotaException, IdUsedException,
			InconsistentException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public Comparator newContentHostingComparator(String property,
			boolean ascending) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourcePropertiesEdit newResourceProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeAllLocks(String id) {
		// TODO Auto-generated method stub

	}

	public void removeCollection(String id) throws IdUnusedException,
			TypeException, PermissionException, InUseException,
			ServerOverloadException {
		// TODO Auto-generated method stub

	}

	public void removeCollection(ContentCollectionEdit edit)
			throws TypeException, PermissionException, InconsistentException,
			ServerOverloadException {
		// TODO Auto-generated method stub

	}

	public void removeLock(String id, String lockId) {
		// TODO Auto-generated method stub

	}

	public ResourceProperties removeProperty(String id, String name)
			throws PermissionException, IdUnusedException, TypeException,
			InUseException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeResource(String id) throws PermissionException,
			IdUnusedException, TypeException, InUseException {
		// TODO Auto-generated method stub

	}

	public void removeResource(ContentResourceEdit edit)
			throws PermissionException {
		// TODO Auto-generated method stub

	}

	public String rename(String id, String new_id) throws PermissionException,
			IdUnusedException, TypeException, InUseException,
			OverQuotaException, InconsistentException, IdUsedException,
			ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public String resolveUuid(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPubView(String id, boolean pubview) {
		// TODO Auto-generated method stub

	}

	public void setUuid(String id, String uuid) throws IdInvalidException {
		// TODO Auto-generated method stub

	}

	public ContentResource updateResource(String id, String type, byte[] content)
			throws PermissionException, IdUnusedException, TypeException,
			InUseException, OverQuotaException, ServerOverloadException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean usingResourceTypeRegistry() {
		// TODO Auto-generated method stub
		return false;
	}

	public Set findTools(Set categories, Set keywords) {
		// TODO Auto-generated method stub
		return null;
	}

	public Placement getCurrentPlacement() {
		// TODO Auto-generated method stub
		return null;
	}

	public Tool getCurrentTool() {
		// TODO Auto-generated method stub
		return null;
	}

	public Tool getTool(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void register(Tool tool) {
		// TODO Auto-generated method stub

	}

	public void register(Document toolXml) {
		// TODO Auto-generated method stub

	}

	public void register(File toolXmlFile) {
		// TODO Auto-generated method stub

	}

	public void register(InputStream toolXmlStream) {
		// TODO Auto-generated method stub

	}

	public UserEdit addUser(String id, String eid)
			throws UserIdInvalidException, UserAlreadyDefinedException,
			UserPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public User addUser(String id, String eid, String firstName,
			String lastName, String email, String pw, String type,
			ResourceProperties properties) throws UserIdInvalidException,
			UserAlreadyDefinedException, UserPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean allowAddUser() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowRemoveUser(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateUser(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateUserEmail(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateUserName(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateUserPassword(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean allowUpdateUserType(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	public User authenticate(String loginId, String password) {
		// TODO Auto-generated method stub
		return null;
	}

	public void cancelEdit(UserEdit user) {
		// TODO Auto-generated method stub

	}

	public void commitEdit(UserEdit user) throws UserAlreadyDefinedException {
		// TODO Auto-generated method stub

	}

	public int countSearchUsers(String criteria) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int countUsers() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void destroyAuthentication() {
		// TODO Auto-generated method stub

	}

	public UserEdit editUser(String id) throws UserNotDefinedException,
			UserPermissionException, UserLockedException {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection findUsersByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	public User getAnonymousUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public User getCurrentUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public User getUser(String id) throws UserNotDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public User getUserByEid(String eid) throws UserNotDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserEid(String id) throws UserNotDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserId(String eid) throws UserNotDefinedException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getUsers(Collection ids) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getUsers(int first, int last) {
		// TODO Auto-generated method stub
		return null;
	}

	public UserEdit mergeUser(Element el) throws UserIdInvalidException,
			UserAlreadyDefinedException, UserPermissionException {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeUser(UserEdit user) throws UserPermissionException {
		// TODO Auto-generated method stub

	}

	public List searchUsers(String criteria, int first, int last) {
		// TODO Auto-generated method stub
		return null;
	}

	public String userReference(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean checkReference(String ref) {
		// TODO Auto-generated method stub
		return false;
	}

	public List getEntityProducers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected class StubReference implements Reference {

		public void addSiteContextAuthzGroup(Collection rv) {
			// TODO Auto-generated method stub
			
		}

		public void addUserAuthzGroup(Collection rv, String id) {
			// TODO Auto-generated method stub
			
		}

		public void addUserTemplateAuthzGroup(Collection rv, String id) {
			// TODO Auto-generated method stub
			
		}

		public Collection getAuthzGroups() {
			// TODO Auto-generated method stub
			return null;
		}

		public Collection getAuthzGroups(String userId) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getContainer() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getContext() {
			return "site-context";
		}

		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}

		public Entity getEntity() {
			// TODO Auto-generated method stub
			return null;
		}

		public EntityProducer getEntityProducer() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getId() {
			return "test-calendar";
		}

		public ResourceProperties getProperties() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getReference() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getSubType() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getType() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getUrl() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isKnownType() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean set(String type, String subType, String id,
				String container, String context) {
			// TODO Auto-generated method stub
			return false;
		}

		public void updateReference(String ref) {
			// TODO Auto-generated method stub
			
		}
		
	}

	public Reference newReference(String refString) {
		return new StubReference();
	}

	public Reference newReference(Reference copyMe) {
		// TODO Auto-generated method stub
		return null;
	}

	public List newReferenceList() {
		return new ArrayList<String>();
	}

	public List newReferenceList(List copyMe) {
		// TODO Auto-generated method stub
		return null;
	}

	public void registerEntityProducer(EntityProducer manager,
			String referenceRoot) {
		// TODO Auto-generated method stub
		
	}

}
