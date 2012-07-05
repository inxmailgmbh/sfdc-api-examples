package com.inxmail.sfdc.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.inxmail.sfdc.oauth.OAuthLogin;


/**
 * @author lgf 20.06.2012
 */
public abstract class BaseServlet extends HttpServlet
{
	private static final String LOGIN_SESS_KEY = "sfdc-examples.login";


	protected final void storeLoginInSession( HttpServletRequest request, OAuthLogin login )
	{
		request.getSession().setAttribute( LOGIN_SESS_KEY, login );
	}


	protected final OAuthLogin getLoginFromSession( HttpServletRequest request )
	{
		return (OAuthLogin)request.getSession().getAttribute( LOGIN_SESS_KEY );
	}
}