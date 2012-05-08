package com.gurudigitalsolutions.xdroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ParseException;
import android.util.Log;


public class Scraper implements Runnable {

	/**
	 * @param args
	 */
	public Context parentContext;
	
	public DefaultHttpClient httpclient;
	public HttpPost httppost;
	public HttpGet httpget;
	public CookieStore cookieStore;
	public BasicHttpContext httpContext = new BasicHttpContext();

	
	private String UserID = "";
	private String Password = "";
	public String URL = "";
	public Boolean UsePost = true;
	
	public String UserAgent = "Mozilla/5.0 (Windows NT 6.2; rv:9.0.1) Gecko/20100101 Firefox/9.0.1";
	
	public String ResultHTML = "";
	public byte[] ResultBytes;
	public Boolean StoreAsBytes = false;
	
	public int PreviousStatusCode = 0;
	public Header[] PreviousResultHeaders;
	
	public Scraper(Context UseContext, String UseUserID, String UsePassword)
	{
	
		UserID = UseUserID;
		Password = UsePassword;
		parentContext = UseContext;
	}
	
	public void run() 
	{
		//	Something with some data
		Log.w("dbg", "Running the scraper");
		
		//httpclient = new DefaultHttpClient();
		httpclient =(DefaultHttpClient) getNewHttpClient();
		//httpclient.getParams().setParameter(HTTP.USER_AGENT, 
		//		"Mozilla/5.0 (Windows NT 6.2; rv:9.0.1) Gecko/20100101 Firefox/9.0.1");
		
		
		cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		httpclient.setCookieStore(cookieStore);
		//httpclient.setRedirectHandler(D)
		if(LoginToSystem())
		{
			//	Logged in.
			//	Need to load the specified page.
			String pagetext = "";
			if(this.UsePost) { pagetext = GetPagePost(this.URL, null); }
			else { pagetext = GetPageGet(this.URL); }
			this.ResultHTML = pagetext;
		}
		else
		{
			Log.w("dbg", "Login failed.");
			
		}
		
		/*if(cookieStore.getCookies().isEmpty()) { Log.w("dbg", "Cookie store is empty"); }
		else 
		{
			Log.w("dbg", "Cookie store is NOT empty!  (final cookies)");
			List<Cookie> dacoo = cookieStore.getCookies();
			for(int ec = 0; ec < dacoo.size(); ec++)
			{
				Log.w("dbg", "COOKIE: " + dacoo.get(ec).getName() + " " + dacoo.get(ec).getValue());
			}
		}*/
		
	
		Log.w("dbg", "Done Running Scraper");
	}
	
	public String GetPagePost(String url, List<NameValuePair> PostVars)
	{
		
		
		
		httppost = new HttpPost(url);

		HttpResponse response;
		HttpEntity entity;
		String toret = "";
		
		try {
		    // Add your data
		    /*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    nameValuePairs.add(new BasicNameValuePair("id", "12345"));
		    nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));*/
		    
			if(PostVars != null)
			{
				httppost.setEntity(new UrlEncodedFormEntity(PostVars));
				
			}
			
			

		    // Execute HTTP Post Request
			httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
		    //httppost.
			response = httpclient.execute(httppost, httpContext);
			Log.w("dbg", "HTTP STATUS: " + response.getStatusLine().getReasonPhrase() + " " + Integer.toString(response.getStatusLine().getStatusCode()));
		    //response.
		    entity = response.getEntity();
		    
		    this.PreviousStatusCode = response.getStatusLine().getStatusCode();
		    this.PreviousResultHeaders = response.getAllHeaders();
		    
		    toret = _getResponseBody(entity);
		    
		    /*Header[] responseHeaders = response.getAllHeaders();
		    String newloc = "";
		    for(int eh = 0; eh < responseHeaders.length; eh ++)
		    {
		    	Log.w("dbg", "Response Header: " + responseHeaders[eh].getName() + ": " + responseHeaders[eh].getValue());
		    	if(responseHeaders[eh].getName().toLowerCase().equals("location"))
		    	{
		    		newloc = responseHeaders[eh].getValue();
		    		Log.w("dbg", "New Location found: " + newloc);
		    	}
		    }*/
		    
		    entity.consumeContent();

		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		}
		
		/*List<Cookie> Cooklist = cookieStore.getCookies();
		if(Cooklist.isEmpty()) { Log.w("dbg", "No cookies sent from server."); }
		else
		{
			Log.w("dbg", "---!!!@@@");
			Log.w("dbg", "Cookies Returned:");
			for(int ecook = 0; ecook < Cooklist.size(); ecook++)
			{
				Log.w("dbg", "COOKIE: " + Cooklist.get(ecook).getName() + " " + Cooklist.get(ecook).getValue());
			}
			Log.w("dbg", "-----------");
			
		}*/
		//HttpEntity entity = response.getEntity();
		
		//String toret = response.getEntity().
		
		
		
		return toret;
	}
	
	public String GetPageGet(String url)
	{
		
		
		
		httpget = new HttpGet(url);

		HttpResponse response;
		HttpEntity entity;
		String toret = "";
		
		try {
		   
		    // Execute HTTP Post Request
			httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
		    //httppost.
			response = httpclient.execute(httpget, httpContext);
			
			Log.w("dbg", "HTTP STATUS: " + response.getStatusLine().getReasonPhrase() + " " + Integer.toString(response.getStatusLine().getStatusCode()));
		    //response.
		    entity = response.getEntity();
		    
		    this.PreviousStatusCode = response.getStatusLine().getStatusCode();
		    this.PreviousResultHeaders = response.getAllHeaders();
		    
		    toret = _getResponseBody(entity);
		    
		    /*Header[] responseHeaders = response.getAllHeaders();
		    String newloc = "";
		    for(int eh = 0; eh < responseHeaders.length; eh ++)
		    {
		    	Log.w("dbg", "Response Header: " + responseHeaders[eh].getName() + ": " + responseHeaders[eh].getValue());
		    	if(responseHeaders[eh].getName().toLowerCase().equals("location"))
		    	{
		    		newloc = responseHeaders[eh].getValue();
		    		Log.w("dbg", "New Location found: " + newloc);
		    	}
		    }*/
		    
		    entity.consumeContent();

		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		}
		
		/*List<Cookie> Cooklist = cookieStore.getCookies();
		if(Cooklist.isEmpty()) { Log.w("dbg", "No cookies sent from server."); }
		else
		{
			Log.w("dbg", "---!!!@@@");
			Log.w("dbg", "Cookies Returned:");
			for(int ecook = 0; ecook < Cooklist.size(); ecook++)
			{
				Log.w("dbg", "COOKIE: " + Cooklist.get(ecook).getName() + " " + Cooklist.get(ecook).getValue());
			}
			Log.w("dbg", "-----------");
			
		}*/
		//HttpEntity entity = response.getEntity();
		
		//String toret = response.getEntity().
		
		
		
		return toret;
	}
	
	public Boolean LoginToSystem()
	{
		//	Login to the xdroid webgui
		
		/*List<NameValuePair> PostVars = new ArrayList<NameValuePair>(2);
	    PostVars.add(new BasicNameValuePair("userid", this.UserID));
	    PostVars.add(new BasicNameValuePair("pwd", this.Password));
	    PostVars.add(new BasicNameValuePair("timezoneOffset", "300"));
	   
	    PagePost = GetPagePost("https://associateconnection.staples.com/psp/psext/?cmd=login", null);
	    
	    */
	    
		return true;
	}
	
	public static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException
	{
	
		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }
		InputStream instream = entity.getContent();
		if (instream == null) { return ""; }
		if (entity.getContentLength() > Integer.MAX_VALUE)
		{
			throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
		}
	
		String charset = getContentCharSet(entity);
	
		if (charset == null)
		{
	
			charset = HTTP.DEFAULT_CONTENT_CHARSET;
	
		}
	
		Reader reader = new InputStreamReader(instream, charset);
	
		StringBuilder buffer = new StringBuilder();
		//ByteBuilder bytebuffer = new ByteBuilder();
		
		try {
			char[] tmp = new char[1024];
			int l;
	
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
	
		} finally {
	
			reader.close();
	
		}
	
		
		return buffer.toString();
	
	}

	public static String getContentCharSet(final HttpEntity entity) throws ParseException 
	{

		if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }

		String charset = null;

		if (entity.getContentType() != null)
		{
			HeaderElement values[] = entity.getContentType().getElements();

			if (values.length > 0)
			{
				NameValuePair param = values[0].getParameterByName("charset");

				if (param != null) 
				{
					charset = param.getValue();
				}
			}
		}

		return charset;

	}
	
	public HttpClient getNewHttpClient() 
	{
	    try 
	    {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new SSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        params.setParameter("User-Agent", this.UserAgent);
	        
	        
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	        HttpProtocolParams.setUserAgent(params, UserAgent);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e)
	    {
	    	Log.w("dbg", "Excepting while creating custom HTTP Client, using default.");
	        return new DefaultHttpClient();
	    }
	}

	public String PreviousResultHeader(String headkey)
	{
		if(this.PreviousResultHeaders == null) { return ""; }
		
		for(int ehead = 0; ehead < this.PreviousResultHeaders.length; ehead++)
		{
			if(this.PreviousResultHeaders[ehead].getName().toLowerCase().equals(headkey.toLowerCase()))
			{
				return PreviousResultHeaders[ehead].getValue();
		
			}
		}
		return "";
	}
}
