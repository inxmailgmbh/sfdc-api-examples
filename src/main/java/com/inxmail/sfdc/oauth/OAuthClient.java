package com.inxmail.sfdc.oauth;

/**
 * Static information about a salesforce.com client application needed for OAuth2 authentication.<br/>
 * NOTE: Enter your client informations here.
 * 
 * @author lgf 20.06.2012
 */
class OAuthClient
{
	/**
	 * OAuth2 <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-10#section-1.2">client-identifier</a>.
	 */
	static final String ID = "";

	/**
	 * OAuth2 client-secret.
	 */
	static final String SECRET = "";// NOTE: in production this should be stored secured!

	/**
	 * OAuth2 <a href="http://tools.ietf.org/html/draft-ietf-oauth-v2-10#section-1.4.1">redirect uri</a>.
	 */
	static final String REDIRECT_URI = "";
}