package com.zoxis.qa.automation.seleniumframework.core.utilities;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

/**
 * Generates a threadsafe httpclient
 */
public class HttpConnectionFactory {
    private static final HttpConnectionFactory INSTANCE = new HttpConnectionFactory();

    private static HttpParams params;

    private static ClientConnectionManager cm;

    private HttpConnectionFactory() {
        if (cm == null) {
            // init HTTP connection manager
            params = new BasicHttpParams();
            ConnManagerParams.setMaxTotalConnections(params, 100);
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

            // Create and initialize scheme registry
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

            // Create an HttpClient with the ThreadSafeClientConnManager.
            // This connection manager must be used if more than one thread will
            // be using the HttpClient.
            cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        }
    }

    public static HttpClient getClient() {
        return new DefaultHttpClient(cm, params);
    }
}
