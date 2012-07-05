package com.inxmail.sfdc;

/**
 * Indicates an error during an external call to salesforce.com.
 * 
 * @author lgf 20.06.2012
 */
public class SfdcException extends RuntimeException
{

	public SfdcException( String message )
	{
		super( message );
	}


	public SfdcException( Throwable cause )
	{
		super( cause );
	}


	public SfdcException( String message, Throwable cause )
	{
		super( message, cause );
	}
}