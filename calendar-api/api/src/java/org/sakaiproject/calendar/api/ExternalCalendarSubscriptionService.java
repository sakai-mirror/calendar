package org.sakaiproject.calendar.api;

import java.util.Collection;
import java.util.Set;



public interface ExternalCalendarSubscriptionService {

	public final static String	SAK_PROP_EXTSUBSCRIPTIONS_ENABLED			= "calendar.external.subscriptions.enable";
	public final static String	SAK_PROP_EXTSUBSCRIPTIONS_URL				= "calendar.external.subscriptions.url";
	public final static String	SAK_PROP_EXTSUBSCRIPTIONS_NAME				= "calendar.external.subscriptions.name";
	public final static String	SAK_PROP_EXTSUBSCRIPTIONS_EVENTTYPE			= "calendar.external.subscriptions.eventtype";
	public final static String	SAK_PROP_EXTSUBSCRIPTIONS_INST_CACHEENTRIES	= "calendar.external.subscriptions.institutional.cacheentries";
	public final static String	SAK_PROP_EXTSUBSCRIPTIONS_INST_CACHETIME	= "calendar.external.subscriptions.institutional.cachetime";
	public final static String	SAK_PROP_EXTSUBSCRIPTIONS_USER_CACHEENTRIES	= "calendar.external.subscriptions.user.cacheentries";
	public final static String	SAK_PROP_EXTSUBSCRIPTIONS_USER_CACHETIME	= "calendar.external.subscriptions.user.cachetime";
	public final static String	TC_PROP_SUBCRIPTIONS						= "externalCalendarSubscriptions";
	public final static String	SUBS_REF_DELIMITER							= "_,_";
	public final static String	SUBS_NAME_DELIMITER							= "_::_";

	public boolean isEnabled();

	public String calendarSubscriptionReference(String context, String id);

	public Calendar getCalendarSubscription(String reference);
	
	public Set<String> getCalendarSubscriptionChannelsForChannel(String reference);
	
	public Set<ExternalSubscription> getAvailableInstitutionalSubscriptionsForChannel(String reference);
	
	public Set<ExternalSubscription> getSubscriptionsForChannel(String reference, boolean loadCalendar);	
	public void setSubscriptionsForChannel(String reference, Collection<ExternalSubscription> subscriptions);
	
	public String getIdFromSubscriptionUrl(String url);
	
	public String getSubscriptionUrlFromId(String id);
	
	public boolean isInstitutionalCalendar(String reference);
	
}