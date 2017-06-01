package storj.io.restclient.rest;

import com.github.kristofa.test.http.Method;
import com.github.kristofa.test.http.MockHttpServer;
import com.github.kristofa.test.http.SimpleHttpResponseProvider;
import com.github.kristofa.test.http.UnsatisfiedExpectationException;
import com.github.kristofa.test.http.client.HttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.BasicConfigurator;
import org.junit.*;
import org.junit.Assert.*;
import storj.io.restclient.auth.BasicAuthType;
import storj.io.restclient.auth.NoAuthType;
import storj.io.restclient.model.Bucket;
import storj.io.restclient.model.User;
import org.junit.Assume.*;

import javax.ws.rs.core.MediaType;

import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Stephen Nutbrown on 03/07/2016.
 */
public class StorjRestClientTest {

    /**
     * To also run tests against a live server (Requires active internet connection and access to storj bridge,
     * set these properties.
     */
    private static final boolean TEST_AGAINST_LIVE_SERVER = true;
    private static final String TEST_LIVE_PASSWORD = "pass";
    private static final String TEST_LIVE_USERNAME = "user";
    private static final String TEST_LIVE_API_ROOT = "https://api.storj.io";

    // Mock server.
    private MockHttpServer server;
    private SimpleHttpResponseProvider responseProvider;

    // Clients under test.
    private StorjRestClient noAuthClient;
    private StorjRestClient basicAuthClient;
    private StorjRestClient noAuthLiveClient;
    private StorjRestClient basicAuthLiveClient;

    // Storj API root.
    private static final int PORT = 51234;

    // Contact end points
    private static final String storjApiContacts = "/contacts";

    // User end points.
    private static final String storjApiUsers = "/users";
    private static final String storjApiUsersPassReset = "/resets";
    private static final String storjApiUsersActivate = "/activations";
    private static final String storjApiUsersDeactivate = "/deactivations";
    private static final String storjApiKeys = "/keys";
    private static final String storjApiFrames = "/frames";
    private static final String storjApiBuckets = "/buckets";

    @Before
    public void before() throws Exception {
        // Configure logger.
        BasicConfigurator.configure();

        // Start mock server.
        responseProvider = new SimpleHttpResponseProvider();
        server = new MockHttpServer(PORT, responseProvider);
        server.start();

        // Create clients to test against mock server.
        String apiRootForClient = "http://localhost:" + PORT;
        noAuthClient = new StorjRestClient(apiRootForClient, new NoAuthType());
        basicAuthClient = new StorjRestClient(apiRootForClient, new BasicAuthType("user", "pass"));

        // Create clients to test against LIVE server
        if(TEST_AGAINST_LIVE_SERVER) {
            noAuthLiveClient = new StorjRestClient(TEST_LIVE_API_ROOT, new NoAuthType());
            basicAuthLiveClient = new StorjRestClient(TEST_LIVE_API_ROOT, new BasicAuthType(TEST_LIVE_USERNAME, TEST_LIVE_PASSWORD));
        }
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
    public void createBucket() throws Exception {
        String mockResponse = "{\"user\":\"test@test.com\",\"created\":\"2016-07-30T21:40:05.012Z\",\"name\":\"testy\",\"pubkeys\":[],\"status\":\"Active\",\"transfer\":0,\"storage\":0,\"id\":\"123456\"}";
        String requestBody = "{\"storage\":0,\"transfer\":0,\"status\":null,\"pubkeys\":null,\"user\":null,\"name\":\"testy\",\"created\":null,\"id\":null}";

        responseProvider.expect(Method.POST, storjApiBuckets, MediaType.APPLICATION_JSON, requestBody).respondWith(201, MediaType.APPLICATION_JSON, mockResponse);
        Bucket bucketToCreate = new Bucket();
        String name = "testy";
        bucketToCreate.setName(name);

        Bucket createdBucket = basicAuthClient.createBucket(bucketToCreate);
        assertEquals(name, createdBucket.getName());
        assertEquals("test@test.com", createdBucket.getUser());
        assertEquals(0, createdBucket.getTransfer());
        assertEquals("123456", createdBucket.getId());
        responseProvider.verify();

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
    public void getAllBuckets() {
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

    @Test
    public void liveTestCreateUser(){
        Assume.assumeTrue(TEST_AGAINST_LIVE_SERVER);

        // Set up test user.
        Random rn = new Random();
        int randomNumber = rn.nextInt(5000);
        String emailAddress = randomNumber + "" + System.currentTimeMillis() + "@notreal.com";
        User user = new User();
        user.setEmail(emailAddress);
        user.setPassword("TestPassword");

        // Send request.
        User createdUser = noAuthLiveClient.createUser(user);

        // Check created user object.
        assertEquals(emailAddress, createdUser.getEmail());
        assertFalse(StringUtils.isEmpty(createdUser.getId()));
        assertNotNull(createdUser.getCreated());
        assertFalse(createdUser.isActivated());
    }
}
