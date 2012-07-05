package com.inxmail.sfdc.oauth;

/**
 * Encapsulates the necessary informations to connect to an salesforce.com organization.
 * 
 * @author lgf 20.06.2012
 */
public class OAuthLogin
{
	private final UserIdentity userIdentity;

	private final String instanceUrl;

	private final OAuthToken token;


	OAuthLogin( String instanceUrl, UserIdentity userIdentity, OAuthToken token )
	{
		this.instanceUrl = instanceUrl;
		this.userIdentity = userIdentity;
		this.token = token;
	}


	public String getOrgId()
	{
		return userIdentity.getOrgId();
	}


	public String getUserId()
	{
		return userIdentity.getId();
	}


	public String getUserName()
	{
		return userIdentity.getName();
	}


	public String getInstanceUrl()
	{
		return instanceUrl;
	}


	public String getRestEndpoint()
	{
		return userIdentity.getRestEndpoint();
	}


	public String getPartnerEndpoint()
	{
		return userIdentity.getPartnerEndpoint();
	}


	public String getBulkEndpoint()
	{
		return instanceUrl + userIdentity.getBulkEndpoint();
	}


	public String getAccessToken()
	{
		return token.getAccessToken();
	}


	String getRefreshToken()
	{
		return token.getRefreshToken();
	}

}