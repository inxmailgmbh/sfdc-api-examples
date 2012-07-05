package com.inxmail.sfdc.snippets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;


/**
 * Simple <a href="http://www.salesforce.com/us/developer/docs/api/index.htm">soap-api</a> examples.
 * 
 * @author lgf 20.06.2012
 */
public class SoapSnippet
{
	public static void main( String[] args ) throws ConnectionException
	{
		SoapSnippet snippets = new SoapSnippet();

		System.out.println( "Aborted Campaigns: " + snippets.queryCampaigns().size() );
		System.out.println( "Campaign update successfull: "
				+ snippets.updateCampaignName( "701d0000000UJlB", "Campaign updated on: " + new Date() ) );
	}


	private List<SObject> queryCampaigns() throws ConnectionException
	{
		PartnerConnection conn = null;
		try
		{
			conn = Connector.newConnection( User.NAME, User.PASSWORD );

			QueryResult qresult = conn.query( "SELECT Id, Name FROM Campaign c WHERE c.Status = 'Aborted'" );

			return queryAll( conn, qresult );
		}
		finally
		{
			logout( conn );
		}
	}


	private List<SaveResult> updateCampaignName( String id, String name ) throws ConnectionException
	{
		PartnerConnection conn = null;
		try
		{
			conn = Connector.newConnection( User.NAME, User.PASSWORD );

			SObject campaign = new SObject();
			campaign.setType( "Campaign" );
			campaign.setId( id );
			campaign.setField( "Name", name );

			return Arrays.asList( conn.update( new SObject[] { campaign } ) );
		}
		finally
		{
			logout( conn );
		}
	}


	private List<SObject> queryAll( PartnerConnection conn, QueryResult qr ) throws ConnectionException
	{
		final List<SObject> all = new ArrayList<>();

		boolean done = false;
		while( !done )
		{
			for( SObject o : qr.getRecords() )
				all.add( o );

			if( !qr.isDone() )
				qr = conn.queryMore( qr.getQueryLocator() );
			else
				done = true;
		}

		return all;
	}


	private void logout( PartnerConnection conn ) throws ConnectionException
	{
		if( null != conn )
			conn.logout();
	}
}