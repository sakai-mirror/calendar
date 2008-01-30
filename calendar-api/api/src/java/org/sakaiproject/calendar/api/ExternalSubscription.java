package org.sakaiproject.calendar.api;

public interface ExternalSubscription {

	public String getSubscriptionName();
	public void setSubscriptionName(String subscriptionName);
	
	public String getSubscriptionUrl();
	public void setSubscriptionUrl(String subscriptionUrl);
	
	public String getContext();
	public void setContext(String context);
	
	public String getReference();
	
	public Calendar getCalendar();
	public void setCalendar(Calendar calendar);
	
	public boolean isInstitutional();
	public void setInstitutional(boolean isInstitutional);
	
}
