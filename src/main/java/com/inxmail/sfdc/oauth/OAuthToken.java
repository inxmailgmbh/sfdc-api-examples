package com.inxmail.sfdc.oauth;

/**
 * Encapsulates an OAuth 2 token.
 * 
 * @author lgf 20.06.2012
 */
class OAuthToken
{
	private final String accessToken;

	private final String refreshToken;// NOTE: in production this should be stored secured!


	OAuthToken( String accessToken, String refreshToken )
	{
		this.refreshToken = refreshToken;
		this.accessToken = accessToken;
	}


	String getAccessToken()
	{
		return accessToken;
	}


	String getRefreshToken()
	{
		return refreshToken;
	}
}