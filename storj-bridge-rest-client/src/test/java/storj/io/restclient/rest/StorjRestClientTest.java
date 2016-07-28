package storj.io.restclient.rest;

import com.github.kristofa.test.http.Method;
import com.github.kristofa.test.http.MockHttpServer;
import com.github.kristofa.test.http.SimpleHttpResponseProvider;
import com.github.kristofa.test.http.UnsatisfiedExpectationException;
import com.github.kristofa.test.http.client.HttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import storj.io.restclient.auth.BasicAuthType;
import storj.io.restclient.auth.NoAuthType;
import storj.io.restclient.model.User;

import javax.ws.rs.core.MediaType;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by steve on 03/07/2016.
 */
public class StorjRestClientTest {

    private MockHttpServer server;
    private SimpleHttpResponseProvider responseProvider;
    private StorjRestClient noAuthClient;
    private StorjRestClient basicAuthClient;

    // Storj API root.

    private static final int PORT = 51234;

    // Contact end points
    private String storjApiContacts = "/contacts";

    // User end points.
    private String storjApiUsers = "/users";
    private String storjApiUsersPassReset = "/resets";
    private String storjApiUsersActivate = "/activations";
    private String storjApiUsersDeactivate = "/deactivations";

    // Keys
    private String storjApiKeys = "/keys";

    // Frames
    private String storjApiFrames = "/frames";

    // Buckets
    private String storjApiBuckets = "/buckets";

    @Before
    public void setUp() throws Exception {
        // Configure logger.
        BasicConfigurator.configure();

        // Start mock server.
        responseProvider = new SimpleHttpResponseProvider();
        server = new MockHttpServer(PORT, responseProvider);
        server.start();

        // Create clients to test.
        String apiRootForClient = "http://localhost:" + PORT;
        //String apiRootForClient = "https://api.storj.io";

        noAuthClient = new StorjRestClient(apiRootForClient, new NoAuthType());
        basicAuthClient = new StorjRestClient(apiRootForClient, new BasicAuthType("user", "pass"));
    }


    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testCreateUser() throws UnsatisfiedExpectationException {
        String expectedContentType = MediaType.APPLICATION_JSON;
        String email = "testemail@test.com";
        String password = "testpassword";

        String expectedRequestBody = "{\"email\":\"testemail@test.com\",\"password\":\"9f735e0df9a1ddc702bf0a1a7b83033f9f7153a00c29de82cedadc9957289b05\"}";
        String mockResponse = "{\"activated\":false,\"created\":\"2016-07-24T09:11:53.604Z\",\"email\":\"testemail@test.com\",\"id\":\"testemail@test.com\"}";

        responseProvider.expect(Method.POST, storjApiUsers, MediaType.APPLICATION_JSON, expectedRequestBody).respondWith(201, MediaType.APPLICATION_JSON, mockResponse);

        // Set up user model.
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        // create
        User responseUser = noAuthClient.createUser(user);

        // check only expected requests were received.
        server.verify();

        assertEquals(email, responseUser.getEmail());
        assertFalse(responseUser.isActivated());
        assertEquals(email, responseUser.getEmail());
    }

}
