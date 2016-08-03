package storj.io.client;

import storj.io.restclient.model.Bucket;
import storj.io.restclient.model.BucketEntry;
import storj.io.restclient.model.User;

import java.io.File;
import java.util.List;

/**
 * Created by steve on 03/08/2016.
 */
public interface StorjClient {
    /**
     * Set the storj configuration.
     * @param configuration the storj configuration to set.
     */
    void setConfiguration(StorjConfiguration configuration);

    /**
     * Return the configuration of this storj.io.client.DefaultStorjClient client.
     * @return the storj client configuration.
     */
    StorjConfiguration getConguration();

    /**
     * Upload a file
     * @param inputFile the file to upload.
     * @param bucketId the ID of the bucket to upload to.
     * @return A bucket entry representing the file on the bridge.
     * @throws Exception Problem uploading file.
     */
    BucketEntry uploadFile(File inputFile, String bucketId) throws Exception;

    /**
     * Upload a file to the storj network.
     * @param file the file to upload.
     * @param bucket the bucket to upload to.
     * @return a bucket entry representing the file stored on the bridge.
     * @throws Exception problem uploading file.
     */
    BucketEntry uploadFile(File file, Bucket bucket) throws Exception;

    /**
     * Download a file into the temp directory specified in the storj.io.client.StorjConfiguration.
     * This call takes care of retrieving the file shards and piecing the file back together.
     * @param bucketEntry the Bucket entry to retrieve.
     * @return A file pointer to the retrieved file.
     */
    File downloadFile(BucketEntry bucketEntry, File outputFile);

    /**
     * Download a file into the temp directory specified in the storj.io.client.StorjConfiguration.
     * This call takes care of retrieving the file shards and piecing the file back together.
     * @param bucketId the ID of the bucket.
     * @param bucketEntryId the ID of the bucketEntry
     * @param outputFile The file to output to.
     * @return A file pointer to the retrieved file.
     */
    File downloadFile(String bucketId, String bucketEntryId, File outputFile);

    /**
     * Get all buckets.
     * @return all buckets for the user.
     */
    List<Bucket> listBuckets();

    /**
     * Get a bucket by it's ID.
     * @param bucketId the bucket ID.
     * @return the bucket.
     */
    Bucket getBucket(String bucketId);

    /**
     * Create a bucket.
     * @param bucketName the name of the bucket to create.
     * @return the created bucket.
     */
    Bucket createBucket(String bucketName);

    /**
     * Delete a bucket.
     * @param buckeId the ID of the bucket to delete.
     */
    void deleteBucket(String buckeId);

    /**
     * Deletes a bucket.
     * @param bucket the bucket to delete.
     */
    void deleteBucket(Bucket bucket);

    /**
     * Request a password reset email.
     * @param emailAddress the email address of the account to reset.
     */
    void resetPassword(String emailAddress);

    /**
     * Request a password reset email.
     * @param user the user account to reset.
     */
    void resetPassword(User user);

    /**
     * Create a user. Warning: The users account will need verifying before using.
     * @param email the email address of the user.
     * @param password the password of the user.
     * @return An object from the bridge representing a user.
     */
    User createUser(String email, String password);

    /**
     * Get a list of files stored in a bucket.
     * @param bucketId the ID of the bucket.
     * @return the files stored in the bucket.
     */
    List<BucketEntry> listFiles(String bucketId);
}
