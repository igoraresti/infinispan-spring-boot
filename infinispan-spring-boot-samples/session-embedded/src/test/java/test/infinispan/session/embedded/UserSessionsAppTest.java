package test.infinispan.session.embedded;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.infinispan.tutorial.simple.spring.session.UserSessionsApp;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class UserSessionsAppTest {

    /**
     * Root path of the web server
     */
    private static final String URI = "http://localhost:8080/";

    /**
     * Initialize SpringBoot Application
     */
    @BeforeClass
    public static void start(){
        new SpringApplicationBuilder().sources(UserSessionsApp.class).run();
    }

    /**
     * Clean session cache before each test
     * @throws IOException in case of a problem or the connection was aborted
     */
    @Before
    public void clear() throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpUriRequest request = new HttpGet( String.format("%s%s", URI, "sessions/clear") );
        client.execute(request);
    }

    @Test
    public void two_request_same_session() throws IOException
    {
        HttpClient client = HttpClientBuilder.create().build();
        HttpUriRequest request = new HttpGet( URI );
        // First request
        HttpResponse response = client.execute( request );
        String resource = retrieveResourceFromResponse(response);
        assertEquals( "Nobody to ciao", resource );
        // Second request
        response = client.execute( request );
        resource = retrieveResourceFromResponse(response);
        assertEquals( "ciao World", resource );
    }

    @Test
    public void two_request_different_session() throws IOException
    {
        HttpClient client1 = HttpClientBuilder.create().build();
        HttpClient client2 = HttpClientBuilder.create().build();
        HttpUriRequest request = new HttpGet( URI );
        // First request
        HttpResponse response = client1.execute( request );
        String resource = retrieveResourceFromResponse(response);
        assertEquals( "Nobody to ciao", resource );
        // Second request
        response = client2.execute( request );
        resource = retrieveResourceFromResponse(response);
        assertEquals( "Nobody to ciao", resource );
    }

    @Test
    public void singleSession() throws IOException, JSONException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpUriRequest request = new HttpGet( URI );
        client.execute( request );
        request = new HttpGet( String.format("%s%s", URI, "sessions") );
        // Get sessions: Should be one session
        HttpResponse response = client.execute( request );
        String resource = retrieveResourceFromResponse(response);
        JSONArray jsonArray = new JSONArray(resource);
        assertEquals( "Should be 1 session", 1, jsonArray.length() );
        // Get this session id and request latest name
        String sessionId = jsonArray.getString(0);
        request = new HttpGet( String.format("%s%s%s", URI, "sessions/", sessionId) );
        response = client.execute( request );
        resource = retrieveResourceFromResponse(response);
        assertEquals("Latest World", resource);
        // Request latest name of non exist session id
        request = new HttpGet( String.format("%s%s", URI, "sessions/kaka") );
        response = client.execute( request );
        resource = retrieveResourceFromResponse(response);
        assertEquals("Session not found", resource);
    }

    @Test
    public void multipleSession() throws IOException, JSONException {
        HttpUriRequest request = new HttpGet( URI );
        // First session
        HttpClient client1 = HttpClientBuilder.create().build();
        client1.execute(request);
        // Second session
        HttpClient client2 = HttpClientBuilder.create().build();
        client2.execute(request);
        // Get sessions
        HttpUriRequest request2 = new HttpGet( String.format("%s%s", URI, "sessions") );
        HttpResponse response = client1.execute( request2 );
        String resource = retrieveResourceFromResponse(response);
        JSONArray jsonArray = new JSONArray(resource);
        assertEquals( "Should be 2 session", 2, jsonArray.length() );

    }


    /**
     * Gets de String value of HTTP Response
     * @param response HTTP response object
     * @return String value of the response
     * @throws IOException if an error occurs reading the input HttpResponse
     */
    private static String retrieveResourceFromResponse(HttpResponse response)
            throws IOException {
        return EntityUtils.toString(response.getEntity());
    }



}
