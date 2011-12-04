package com.app.NAMESPACE.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import com.app.NAMESPACE.base.C;

import android.util.Log;

@SuppressWarnings("rawtypes")
public class AppClient {
	
	private String apiUrl;
	private HttpParams httpParams;
	private int timeoutConnection = 3000;
	private int timeoutSocket = 5000;
	
	public AppClient (String url) {
		// initialize API URL
		this.apiUrl = C.api.base + url;
		String apiSid = AppUtil.getSessionId();
		if (apiSid != null && apiSid.length() > 0) {
			this.apiUrl += "?sid=" + apiSid;
		}
		// set client timeout
		httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
	}
	
	public String get () throws Exception {
		try {
			HttpGet httpGet = new HttpGet(this.apiUrl);
			httpGet.addHeader("Accept-Encoding", "gzip");
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String httpResult = AppUtil.gzipToString(httpResponse.getEntity());
				Log.w("AppClient.get", httpResult);
				return httpResult;
			} else {
				return null;
			}
		} catch (ConnectTimeoutException e) {
			throw new Exception(C.err.network);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String post (HashMap urlParams) throws Exception {
		try {
			HttpPost httpPost = new HttpPost(this.apiUrl);
			httpPost.addHeader("Accept-Encoding", "gzip");
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			// get post parameters
			Iterator it = urlParams.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				postParams.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
			// send post request
			HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String httpResult = AppUtil.gzipToString(httpResponse.getEntity());
				Log.w("AppClient.post", httpResult);
				return httpResult;
			} else {
				return null;
			}
		} catch (ConnectTimeoutException e) {
			throw new Exception(C.err.network);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}