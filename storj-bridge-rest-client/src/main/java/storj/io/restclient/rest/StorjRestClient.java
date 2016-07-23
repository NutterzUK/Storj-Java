package storj.io.restclient.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
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

	private static final String INCOMING_MEDIA_TYPE = MediaType.APPLICATION_JSON;
	private static final String OUTGOING_MEDIA_TYPE = MediaType.APPLICATION_JSON;

	/**
	 * AuthType represents the type of authentication used for the storj API.
	 * Currently can be NoAuthType, BasicAuthType or ECDSA auth type.
	 */
	private AuthType auth;

	/**
	 * Create a storj restclient.
	 */
	public StorjRestClient(String apiRoot, AuthType authType) {
		setApiRoot(apiRoot);
		this.auth = authType;
		configureJersey();
	}

	private void setApiRoot(String apiRoot) {
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
	 * 
	 * @param pagination
	 *            The pagination indicator.
	 * @param connected
	 *            Filter results by connection status.
	 * @return A page of contacts.
	 */
	public List<Contact> getContacts(int pagination, boolean connected) {
		String queryParamPage = "page=" + pagination;
		String queryParamConnected = connected ? "connected=1" : "connected=0";
		String requestUrl = storjApiContacts + "?" + queryParamPage + "&" + queryParamConnected;
		return getBuilder(requestUrl).get(new GenericType<List<Contact>>() {
		});
	}

	/**
	 * Retrieve a page of contacts from the API
	 * 
	 * @param nodeID
	 *            Node ID of the contact to lookup
	 * @return The contact.
	 */
	public Contact getContact(String nodeID) {
		String requestUrl = storjApiContacts + "/" + nodeID;
		return getBuilder(requestUrl).get(Contact.class);
	}

	/**
	 * Creates a user.
	 * 
	 * @param user
	 *            the user to create.
	 * @return The user.
	 */
	public User createUser(User user) {
		return getBuilder(storjApiUsers, user).post(User.class);
	}

	/**
	 * Deletes a user.
	 * 
	 * @param userEmail
	 *            the user to delete.
	 */
	public void deleteUser(String userEmail) {
		String requestUrl = storjApiUsers + "/" + userEmail;
		getBuilder(requestUrl).delete();
	}

	/**
	 * Sends a PATCH request to reset a users password.
	 * 
	 * @param userEmail
	 *            the users email to reset.
	 */
	public void resetPassword(String userEmail) {
		String requestUrl = storjApiUsers + "/" + userEmail;
		getBuilder(requestUrl).method("PATCH", String.class);
	}

	/**
	 * Sends a GET request to confirm the password reset.
	 * 
	 * @param token
	 *            the password reset token.
	 * @return the User if OK
	 */
	public User confirmPasswordReset(String token) {
		String requestUrl = storjApiUsersPassReset + "/" + token;
		return getBuilder(requestUrl).get(User.class);
	}

	/**
	 * Activates a registered user.
	 * 
	 * @param token
	 *            activation token.
	 * @return The user which is now activated.
	 */
	public User activateRegisteredUser(String token) {
		String requestUrl = storjApiUsersActivate + "/" + token;
		return getBuilder(requestUrl).get(User.class);
	}

	/**
	 * Deactivates a registered user.
	 * 
	 * @param token
	 *            deactivation token.
	 * @return The user which is now deactivated.
	 */
	public User deactivateRegisteredUser(String token) {
		String requestUrl = storjApiUsersDeactivate + "/" + token;
		return getBuilder(requestUrl).get(User.class);
	}

	/**
	 * Get all ECDSA Keys.
	 * 
	 * @return a list of ECDSA keys associated with the authorized user.
	 */
	public List<ECDSAKey> ecdsaKeyGetAll() {
		return getBuilder(storjApiKeys).get(new GenericType<List<ECDSAKey>>() {
		});
	}

	/**
	 * Registers a ECDSA public key for the user account
	 * 
	 * @param key
	 *            the public key to register
	 * @return The ECDSA key returned by the server.
	 */
	public ECDSAKey ecdsaKeyRegister(String key) {
		Map<String, Object> postBody = new HashMap<String, Object>();
		postBody.put("key", key);
		return getBuilder(storjApiKeys).post(ECDSAKey.class, postBody);
	}

	/**
	 * Destroys a ECDSA public key for the user account
	 * 
	 * @param publicKey
	 *            the public key to destroy.
	 */
	public void ecdsaKeyDestroy(String publicKey) {
		String requestUrl = storjApiKeys + "/" + publicKey;
		getBuilder(requestUrl).delete();
	}

	/**
	 * Returns all of the open file stages for the caller.
	 * 
	 * @return all of the open file stages for the caller
	 */
	public List<Frame> getFrames() {
		return getBuilder(storjApiFrames).get(new GenericType<List<Frame>>() {
		});
	}

	/**
	 * Creates a new file staging frame.
	 * 
	 * @return the created frame.
	 */
	public Frame createFrame() {
		return getBuilder(storjApiFrames).post(Frame.class);
	}

	/**
	 * Destroy a frame.
	 * 
	 * @param frameId
	 *            the ID of the frame to destroy.
	 */
	public void destroyFrame(String frameId) {
		String requestUrl = storjApiFrames + "/" + frameId;
		getBuilder(requestUrl).delete();
	}

	/**
	 * Get a drame by ID.
	 * 
	 * @param frameId
	 *            The ID of the frame to get.
	 * @return the frame.
	 */
	public Frame getFrameById(String frameId) {
		String requestUrl = storjApiFrames + "/" + frameId;
		return getBuilder(requestUrl).get(Frame.class);
	}

	/**
	 * Add a shard to a frame.
	 * 
	 * @param frameId
	 *            the ID of the frame to add a shard to.
	 * @param shard
	 *            the shard to add.
	 * @return the Frame with the shard added.
	 */
	public AddShardResponse addShardToFrame(String frameId, Shard shard) {
		String requestUrl = storjApiFrames + "/" + frameId;
		return getBuilder(requestUrl, shard).put(AddShardResponse.class);
	}

	/**
	 * Get a list of all the buckets the user has access to.
	 * 
	 * @return the list of buckets.
	 */
	public List<Bucket> getAllBuckets() {

		return getBuilder(storjApiBuckets).get(new GenericType<List<Bucket>>() {
		});
	}

	/**
	 * Creates a bucket on the server.
	 * 
	 * @param bucket
	 *            The bucket to create.
	 * @return The bucket which has been created.
	 */
	public Bucket createBucket(Bucket bucket) {
		return getBuilder(storjApiBuckets, bucket).post(Bucket.class);
	}

	/**
	 * Delete the bucket on the server.
	 * 
	 * @param bucketId
	 *            The id of the bucket to destroy.
	 */
	public void deleteBucket(String bucketId) {
		String requestUrl = storjApiBuckets + "/" + bucketId;
		getBuilder(requestUrl).delete();
	}

    /**
     * Retrieves a bucket by ID.
     * @param bucketId the ID of the bucket.
     * @return The bucket.
     */
	public Bucket getBucketById(String bucketId){
	    String requestUrl = storjApiBuckets + "/" + bucketId;
        return getBuilder(storjApiBuckets).get(Bucket.class);
    }

	/**
	 * Update a bucket via a patch request.
	 * 
	 * @param bucket
	 *            the bucket to update on the server.
	 * @return The bucket which has been updated.
	 */
	public Bucket updateBucket(Bucket bucket) {
		String requestUrl = storjApiBuckets + "/" + bucket.getId();
		return getBuilder(requestUrl, bucket).method("PATCH", Bucket.class);
	}

	/**
	 * Create a token for the specified bucket and operation.
	 * 
	 * @param bucketId
	 *            the ID of the bucket to create a token for.
	 * @param operation
	 *            The operation to create a token for.
	 * @return The created token.
	 */
	public Token getTokenForBucket(String bucketId, Operation operation) {
		String requestUrl = storjApiBuckets + "/" + operation + "/" + "tokens";
		return getBuilder(requestUrl).post(Token.class, operation);
	}

	/**
	 * Get a list of all the files in a bucket.
	 * 
	 * @param bucketId
	 *            the bucket id.
	 * @return the meta data for the files.
	 */
	public List<BucketEntry> getFilesInBucket(String bucketId) {
		String requestUrl = storjApiBuckets + "/" + bucketId + "/" + "files";
		return getBuilder(requestUrl).get(new GenericType<List<BucketEntry>>() {
		});
	}

	/**
	 * Add a file in the storj network through the storj bridge
	 * 
	 * @param bucketId
	 *            the bucket ID to add to.
	 * @param bucketEntry
	 *            The bucket entry.
	 * @return a bucket entry.
	 */
	public BucketEntry storeFile(String bucketId, BucketEntry bucketEntry) {
		String requestUrl = storjApiBuckets + "/" + bucketId + "/" + "files";
		return getBuilder(requestUrl, bucketEntry).post(BucketEntry.class);
	}

	/**
	 * Destroy a file entry.
	 * 
	 * @param bucketId
	 *            The bucket ID the file belongs to.
	 * @param fileId
	 *            The fileId of the file to destroy.
	 */
	public void destroyFileEntry(String bucketId, String fileId) {
		String requestUrl = storjApiBuckets + "/" + bucketId + "/files/" + fileId;
		getBuilder(requestUrl).delete();
	}

	/**
	 * Get file pointers for a file on the storj network.
	 * 
	 * @param bucketId
	 *            The ID of the bucket the file belongs in.
	 * @param fileId
	 *            The ID of the file.
	 * @param xToken
	 *            The access token.
	 * @return A list of filepointers to retrieve the file.
	 */
	public List<FilePointer> getFilePointers(String bucketId, String fileId, String xToken) {
		String requestUrl = storjApiBuckets + "/" + bucketId + "/files/" + fileId;
		WebResource.Builder builder = getBuilder(requestUrl);
		builder.header("x-token", xToken);
		return builder.get(new GenericType<List<FilePointer>>() {
		});
	}

	/**
	 * Configure Jersey to use Jackson for unmarshalling and marshalling JSON.
	 */
	private void configureJersey() {

		ClientConfig cc = new DefaultClientConfig();
		cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		// Required for PATCH.
		cc.getProperties().put(URLConnectionClientHandler.PROPERTY_HTTP_URL_CONNECTION_SET_METHOD_WORKAROUND, true);
		jerseyClient = Client.create(cc);
		jerseyClient.addFilter(new LoggingFilter(System.out));
	}

	/**
	 * Helper method to create a WebResource.Builder creating a request to the
	 * Storj API. Sets the request to accept media types consisting of
	 * {@value #INCOMING_MEDIA_TYPE} and sets the authentication headers using
	 * the currently set implementation of {@link AuthType}.
	 * 
	 * @param requestUrl
	 *            the URL to create the request builder for.
	 * @return WebResource.Builder the request builder
	 */
	private WebResource.Builder getBuilder(String requestUrl) {

		WebResource.Builder builder = jerseyClient.resource(requestUrl).getRequestBuilder();

		builder = builder.accept(INCOMING_MEDIA_TYPE);
		builder = auth.setRequiredAuthHeaders(builder);

		return builder;
	}

	/**
	 * Creates a WebResource.Builder using the same logic as
	 * {@link #getBuilder(String requestUrl)} and includes a request entity. The
	 * media type of the request entity is set to {@value #OUTGOING_MEDIA_TYPE}.
	 * 
	 * @param requestUrl
	 *            the URL to create the request builder for.
	 * @param entity the request entity
	 * @return WebResource.Builder the request builder
	 */
	private WebResource.Builder getBuilder(String requestUrl, Object entity) {

		WebResource.Builder builder = getBuilder(requestUrl);

		builder.type(OUTGOING_MEDIA_TYPE);
		builder.entity(entity);

		return builder;
	}
}
