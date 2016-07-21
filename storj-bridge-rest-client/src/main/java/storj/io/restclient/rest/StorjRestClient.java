package storj.io.restclient.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import storj.io.restclient.model.*;
import storj.io.restclient.auth.AuthType;
import storj.io.restclient.auth.BasicAuthType;
import storj.io.restclient.auth.NoAuthType;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Stephen Nutbrown on 03/07/2016.
 */
public class StorjRestClient {

    // Storj API root.
    private static String apiRoot;

    // Contact end points
    private String storjApiContacts;

    // User end points.
    private String storjApiUsers;
    private String storjApiUsersPassReset;
    private String storjApiUsersActivate;
    private String storjApiUsersDeactivate;

    // Keys
    private String storjApiKeys = apiRoot + "/keys";

    // Frames
    private String storjApiFrames = apiRoot + "/frames";

    // Buckets
    private String storjApiBuckets = apiRoot + "/buckets";

    private Client jerseyClient;

    /**
     * AuthType represents the type of authentication used for the storj API.
     * Currently can be NoAuthType, BasicAuthType or ECDSA auth type.
     */
    private AuthType auth;

    /**
     * Create a storj restclient with no authentication mechanism.
     */
    public StorjRestClient(String apiRoot){
        setApiRoot(apiRoot);
        this.auth = new NoAuthType();
        configureJersey();
    }

    /**
     * Creates a storj restclient with BASIC authentication (Recommended to use ECDSA instead!)
     * @param userEmail
     * @param password
     */
    public StorjRestClient(String apiRoot, String userEmail, String password){
        setApiRoot(apiRoot);
        configureJersey();
        auth = new BasicAuthType(userEmail, password);
    }

    private void setApiRoot(String apiRoot){
        this.apiRoot = apiRoot;
        storjApiContacts = apiRoot + "/contacts";
        storjApiUsers = apiRoot + "/users";
        storjApiUsersPassReset = storjApiUsers + "/resets";
        storjApiUsersActivate = apiRoot + "/activations";
        storjApiUsersDeactivate = storjApiUsers + "/deactivations";
        storjApiKeys = apiRoot + "/keys";
        storjApiFrames = apiRoot + "/frames";
        storjApiBuckets = apiRoot + "/buckets";
    }

    /**
     * Retrieve a page of contacts from the API
     * @param pagination The pagination indicator.
     * @param connected Filter results by connection status.
     * @return A page of contacts.
     */
    public List<Contact> getContacts(int pagination, boolean connected){
        String queryParamPage = "page=" + pagination;
        String queryParamConnected = connected ? "connected=1" : "connected=0";
        String requestUrl = storjApiContacts + "?" + queryParamPage + "&" + queryParamConnected;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        return auth.setRequiredAuthHeaders(builder).get(new GenericType<List<Contact>>(){});
    }

    /**
     * Retrieve a page of contacts from the API
     * @param nodeID Node ID of the contact to lookup
     * @return The contact.
     */
    public Contact getContact(String nodeID){
        String requestUrl = storjApiContacts + "/" + nodeID;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        return auth.setRequiredAuthHeaders(builder).get(Contact.class);
    }

    /**
     * Creates a user.
     * @param user the user to create.
     * @return The user.
     */
    public User createUser(User user){
        String requestUrl = storjApiUsers;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        builder.entity(user, MediaType.APPLICATION_JSON);
        builder = auth.setRequiredAuthHeaders(builder);
        return builder.post(User.class);
    }

    /**
     * Deletes a user.
     * @param userEmail the user to delete.
     */
    public void deleteUser(String userEmail){
        String requestUrl = storjApiUsers + "/" + userEmail;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        builder = auth.setRequiredAuthHeaders(builder);
        builder.delete();
    }

    /**
     * Sends a PATCH request to reset a users password.
     * @param userEmail the users email to reset.
     */
    public void resetPassword(String userEmail){
        String requestUrl = storjApiUsers + "/" + userEmail;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        builder = auth.setRequiredAuthHeaders(builder);
        builder.method("PATCH", String.class);
    }

    /**
     * Sends a GET request to confirm the password reset.
     * @param token the password reset token.
     * @return the User if OK
     */
    public User confirmPasswordReset(String token){
        String requestUrl = storjApiUsersPassReset + "/" + token;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        builder = auth.setRequiredAuthHeaders(builder);
        return builder.get(User.class);
    }

    /**
     * Activates a registered user.
     * @param token activation token.
     * @return The user which is now activated.
     */
    public User activateRegisteredUser(String token){
        String requestUrl = storjApiUsersActivate + "/" + token;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        builder = auth.setRequiredAuthHeaders(builder);
        return builder.get(User.class);
    }

    /**
     * Deactivates a registered user.
     * @param token deactivation token.
     * @return The user which is now deactivated.
     */
    public User deactivateRegisteredUser(String token){
        String requestUrl = storjApiUsersDeactivate + "/" + token;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        builder = auth.setRequiredAuthHeaders(builder);
        return builder.get(User.class);
    }

    /**
     * Get all ECDSA Keys.
     * @return a list of  ECDSA keys associated with the authorized user.
     */
    public List<ECDSAKey> ecdsaKeyGetAll(){
        WebResource.Builder builder = jerseyClient.resource(storjApiKeys).accept(MediaType.APPLICATION_JSON);
        return auth.setRequiredAuthHeaders(builder).get(new GenericType<List<ECDSAKey>>(){});
    }

    /**
     * Registers a ECDSA public key for the user account
     * @param key the public key to register
     * @return The ECDSA key returned by the server.
     */
    public ECDSAKey ecdsaKeyRegister(String key){
        Map<String,Object> postBody = new HashMap<String,Object>();
        postBody.put("key", key);
        WebResource.Builder builder = jerseyClient.resource(storjApiKeys).accept(MediaType.APPLICATION_JSON);
        return auth.setRequiredAuthHeaders(builder).post(ECDSAKey.class, postBody);
    }

    /**
     * Destroys a ECDSA public key for the user account
     * @param publicKey the public key to destroy.
     */
    public void ecdsaKeyDestroy(String publicKey){
        String requestUrl = storjApiKeys + "/" + publicKey;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        auth.setRequiredAuthHeaders(builder).delete();
    }

    /**
     * Returns all of the open file stages for the caller.
     * @return all of the open file stages for the caller
     */
    public List<Frame> getFrames(){
        WebResource.Builder builder = jerseyClient.resource(storjApiFrames).accept(MediaType.APPLICATION_JSON);
        return auth.setRequiredAuthHeaders(builder).get(new GenericType<List<Frame>>(){});
    }

    /**
     * Creates a new file staging frame.
     * @return the created frame.
     */
    public Frame createFrame(){
        WebResource.Builder builder = jerseyClient.resource(storjApiFrames).accept(MediaType.APPLICATION_JSON);
        return auth.setRequiredAuthHeaders(builder).post(Frame.class);
    }

    /**
     * Destroy a frame.
     * @param frameId the ID of the frame to destroy.
     */
    public void destroyFrame(String frameId){
        String requestUrl = storjApiFrames + "/" + frameId;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        auth.setRequiredAuthHeaders(builder).delete();
    }

    /**
     * Get a drame by ID.
     * @param frameId The ID of the frame to get.
     * @return the frame.
     */
    public Frame getFrameById(String frameId){
        String requestUrl = storjApiFrames + "/" + frameId;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        builder = auth.setRequiredAuthHeaders(builder);
        return builder.get(Frame.class);
    }

    /**
     * Add a shard to a frame.
     * @param frameId the ID of the frame to add a shard to.
     * @param shard the shard to add.
     * @return the Frame with the shard added.
     */
    public AddShardResponse addShardToFrame(String frameId, Shard shard){
        String requestUrl = storjApiFrames + "/" + frameId;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        builder.entity(shard, MediaType.APPLICATION_JSON);
        builder = auth.setRequiredAuthHeaders(builder);
        return builder.put(AddShardResponse.class);
    }

    /**
     * Get a list of all the buckets the user has access to.
     * @return the list of buckets.
     */
    public List<Bucket> getAllBuckets(){
        WebResource.Builder builder = jerseyClient.resource(storjApiBuckets).accept(MediaType.APPLICATION_JSON);
        return auth.setRequiredAuthHeaders(builder).get(new GenericType<List<Bucket>>(){});
    }

    /**
     * Creates a bucket on the server.
     * @param bucket The bucket to create.
     * @return The bucket which has been created.
     */
    public Bucket createBucket(Bucket bucket){
        WebResource.Builder builder = jerseyClient.resource(storjApiBuckets).accept(MediaType.APPLICATION_JSON);
        builder.entity(bucket, MediaType.APPLICATION_JSON);
        return auth.setRequiredAuthHeaders(builder).post(Bucket.class);
    }

    /**
     * Destroy the bucket on the server.
     * @param bucketId The id of the bucket to destroy.
     */
    public void destroyBucket(String bucketId){
        String requestUrl = storjApiBuckets + "/" + bucketId;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        auth.setRequiredAuthHeaders(builder).delete();
    }

    /**
     * Update a bucket via a patch request.
     * @param bucket the bucket to update on the server.
     * @return The bucket which has been updated.
     */
    public Bucket updateBucket(Bucket bucket){
        String requestUrl = storjApiBuckets + "/" + bucket.getId();
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        builder = auth.setRequiredAuthHeaders(builder);
        builder = builder.entity(bucket);
        return builder.method("PATCH", Bucket.class);
    }

    /**
     * Create a token for the specified bucket and operation.
     * @param bucketId the ID of the bucket to create a token for.
     * @param operation The operation to create a token for.
     * @return The created token.
     */
    public Token getTokenForBucket(String bucketId, Operation operation){
        String requestURL = storjApiBuckets + "/" + operation + "/" + "tokens";
        WebResource.Builder builder = jerseyClient.resource(requestURL).accept(MediaType.APPLICATION_JSON);
        return auth.setRequiredAuthHeaders(builder).post(Token.class, operation);
    }

    /**
     * Get a list of all the files in a bucket.
     * @param bucketId the bucket id.
     * @return the meta data for the files.
     */
    public List<BucketEntry> getFilesInBucket(String bucketId){
        String requestURL = storjApiBuckets + "/" + bucketId + "/" + "files";
        WebResource.Builder builder = jerseyClient.resource(requestURL).accept(MediaType.APPLICATION_JSON);
        return auth.setRequiredAuthHeaders(builder).get(new GenericType<List<BucketEntry>>(){});
    }

    /**
     * Add a file in the storj network through the storj bridge
     * @param bucketId the bucket ID to add to.
     * @param bucketEntry The bucket entry.
     * @return a bucket entry.
     */
    public BucketEntry storeFile(String bucketId, BucketEntry bucketEntry){
        String requestURL = storjApiBuckets + "/" + bucketId + "/" + "files";
        WebResource.Builder builder = jerseyClient.resource(requestURL).accept(MediaType.APPLICATION_JSON);
        builder.entity(bucketEntry, MediaType.APPLICATION_JSON);
        return auth.setRequiredAuthHeaders(builder).post(BucketEntry.class);
    }


    /**
     * Destroy a file entry.
     * @param bucketId The bucket ID the file belongs to.
     * @param fileId The fileId of the file to destroy.
     */
    public void destroyFileEntry(String bucketId, String fileId){
        String requestUrl = storjApiBuckets + "/" + bucketId + "/files/" + fileId;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        auth.setRequiredAuthHeaders(builder).delete();
    }

    /**
     * Get file pointers for a file on the storj network.
     * @param bucketId The ID of the bucket the file belongs in.
     * @param fileId The ID of the file.
     * @param xToken The access token.
     * @return A list of filepointers to retrieve the file.
     */
    public List<FilePointer> getFilePointers(String bucketId, String fileId, String xToken){
        String requestUrl = storjApiBuckets + "/" + bucketId + "/files/" + fileId;
        WebResource.Builder builder = jerseyClient.resource(requestUrl).accept(MediaType.APPLICATION_JSON);
        builder.header("x-token", xToken);
        return auth.setRequiredAuthHeaders(builder).get(new GenericType<List<FilePointer>>(){});
    }

    /**
     * Configure Jersey to use Jackson for unmarshalling and marshalling JSON.
     */
    private void configureJersey(){
        jerseyClient = Client.create();
        ClientConfig cc = new DefaultClientConfig();
        cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,Boolean.TRUE);

        // Required for PATCH.
        cc.getProperties().put(URLConnectionClientHandler.PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND, true);
        jerseyClient = Client.create(cc);
        jerseyClient.addFilter(new LoggingFilter(System.out));
    }
}
