import com.j256.simplemagic.ContentInfoUtil;
import org.glassfish.tyrus.client.ClientManager;
import storj.io.restclient.model.*;
import storj.io.restclient.rest.StorjRestClient;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * Created by Stephen Nutbrown on 23/07/2016.
 */
public class Storj implements StorjClient {

    StorjConfiguration config;
    StorjRestClient storjRestClient;

    public Storj(StorjConfiguration config) {
        this.config = config;
        storjRestClient = new StorjRestClient(config.getApiRoot(), config.getAuth());
    }

    public void setConfiguration(StorjConfiguration configuration) {
        this.config = configuration;
    }

    public StorjConfiguration getConguration() {
        return config;
    }

    public BucketEntry uploadFile(File inputFile, String bucketId) throws Exception {
        String encryptionPassword = config.getEncryptionKey();
        // Create encrypted file.

        // TODO: neaten this.
        File encryptedFile = new File(config.getTempDirectoryForShards().getPath()+ "/" + inputFile.getName()+ ".encrypted");

        // Create encrypted file.
        Utils.encryptFile(inputFile, encryptedFile, encryptionPassword);

        // Shard the file.
        List<Shard> shards = Utils.shardFile(inputFile, config.getShardSizeInBytes());

        // create a frame.
        Frame frame = storjRestClient.createFrame();

        // upload shards.
        for (Shard shard : shards) {
            AddShardResponse response = storjRestClient.addShardToFrame(frame.getId(), shard);
            String address = "ws://" + response.getFarmer().getAddress() + ":" + response.getFarmer().getPort();
            CountDownLatch latch;
            latch = new CountDownLatch(1);
            ClientManager wsClient = ClientManager.createClient();
            try {
                wsClient.connectToServer(new StorjWebsocketClient(shard, response, latch), null, new URI(address));
                latch.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // clean up encrypted file
            encryptedFile.delete();

            // clean up shard files..
            File shardFile = new File(shard.getPath());
            shardFile.delete();
        }

        // Create the bucket entry.
        BucketEntry bucketEntry = new BucketEntry();
        // Library for getting mime type (Should work on linux which Files.probe won't always).
        ContentInfoUtil mimeTypeFinder = new ContentInfoUtil();
        bucketEntry.setMimetype(mimeTypeFinder.findMatch(inputFile).getMimeType());
        bucketEntry.setFilename(inputFile.getName());
        bucketEntry.setFrame(frame.getId());

        // store the bucket entry.
        return storjRestClient.storeFile(bucketId, bucketEntry);

    }

    public BucketEntry uploadFile(File file, Bucket bucket) throws Exception {
        return uploadFile(file, bucket.getId());
    }

    public File downloadFile(BucketEntry bucketEntry) {
        throw new NotImplementedException();
    }

    public File downloadFile(String bucketId, String bucketEntryId) {
        throw new NotImplementedException();
    }

    /**
     * Get all buckets.
     * @return all buckets for the user.
     */
    public List<Bucket> getBuckets() {
        return storjRestClient.getAllBuckets();
    }

    /**
     * Get a bucket by it's ID.
     * @param bucketId the bucket ID.
     * @return the bucket.
     */
    public Bucket getBucket(String bucketId) {
        return storjRestClient.getBucketById(bucketId);
    }

    /**
     * Create a bucket.
     * @param bucketName the name of the bucket to create.
     * @return the created bucket.
     */
    public Bucket createBucket(String bucketName) {
        Bucket bucket = new Bucket();
        bucket.setName(bucketName);
        return storjRestClient.createBucket(bucket);
    }

    /**
     * Delete a bucket.
     * @param buckeId the ID of the bucket to delete.
     */
    public void deleteBucket(String buckeId) {
        storjRestClient.deleteBucket(buckeId);
    }

    /**
     * Deletes a bucket.
     * @param bucket the bucket to delete.
     */
    public void deleteBucket(Bucket bucket) {
        deleteBucket(bucket.getId());
    }

    /**
     * Request a password reset email.
     * @param emailAddress the email address of the account to reset.
     */
    public void resetPassword(String emailAddress) {
        storjRestClient.resetPassword(emailAddress);
    }

    /**
     * Request a password reset email.
     * @param user the user account to reset.
     */
    public void resetPassword(User user) {
        resetPassword(user.getEmail());
    }

    public void createUser() {
        throw new NotImplementedException();
    }
}
