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
import storj.io.restclient.model.Bucket;
import storj.io.restclient.model.User;

import javax.ws.rs.core.MediaType;

import java.util.List;

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
    public void getContacts() throws Exception {

    }

    @Test
    public void getContact() throws Exception {

    }


    @Test
    public void deleteUser() throws Exception {

    }

    @Test
    public void resetPassword() throws Exception {

    }

    @Test
    public void confirmPasswordReset() throws Exception {

    }

    @Test
    public void activateRegisteredUser() throws Exception {

    }

    @Test
    public void deactivateRegisteredUser() throws Exception {

    }

    @Test
    public void ecdsaKeyGetAll() throws Exception {

    }

    @Test
    public void ecdsaKeyRegister() throws Exception {

    }

    @Test
    public void ecdsaKeyDestroy() throws Exception {

    }

    @Test
    public void getFrames() throws Exception {

    }

    @Test
    public void createFrame() throws Exception {

    }

    @Test
    public void destroyFrame() throws Exception {

    }

    @Test
    public void getFrameById() throws Exception {

    }

    @Test
    public void addShardToFrame() throws Exception {

    }

    @Test
    public void getAllBuckets() throws Exception {

    }

    @Test
    public void createBucket() throws Exception {

    }

    @Test
    public void deleteBucket() throws Exception {

    }

    @Test
    public void getBucketById() throws Exception {

    }

    @Test
    public void updateBucket() throws Exception {

    }

    @Test
    public void getTokenForBucket() throws Exception {

    }

    @Test
    public void getFilesInBucket() throws Exception {

    }

    @Test
    public void storeFile() throws Exception {

    }

    @Test
    public void destroyFileEntry() throws Exception {

    }

    @Test
    public void getFilePointers() throws Exception {

    }

    @Test
    public void createUser() throws UnsatisfiedExpectationException {
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

    @Test
    public void getBuckets() {
        String mockResponse = "[{\"user\":\"test@test.com\",\"created\":\"2016-07-23T08:43:00.253Z\",\"name\":\"TestBucket\",\"pubkeys\":[],\"status\":\"Active\",\"transfer\":0,\"storage\":0,\"id\":\"1234\"}]";
        responseProvider.expect(Method.GET, storjApiBuckets).respondWith(200, MediaType.APPLICATION_JSON, mockResponse);

        List<Bucket> response = basicAuthClient.getAllBuckets();
        assertEquals(1, response.size());

        Bucket bucket = response.get(0);
        assertEquals("test@test.com", bucket.getUser());
        assertEquals("2016-07-23T08:43:00.253Z", bucket.getCreated());
        assertEquals("TestBucket", bucket.getName());
        assertEquals("Active", bucket.getStatus());
        assertEquals("1234", bucket.getId());
        assertEquals(0, bucket.getTransfer());
        assertEquals(0, bucket.getStorage());
    }
}
