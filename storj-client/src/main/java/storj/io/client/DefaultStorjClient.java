package storj.io.client;

import com.j256.simplemagic.ContentInfoUtil;
import org.glassfish.tyrus.client.ClientManager;
import storj.io.client.encryption.EncryptionUtils;
import storj.io.client.sharding.ShardingUtils;
import storj.io.client.websockets.WebsocketShardSender;
import storj.io.client.websockets.WebsocketFileRetriever;
import storj.io.restclient.model.*;
import storj.io.restclient.rest.StorjRestClient;

import javax.websocket.ClientEndpointConfig;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;


/**
 * Created by Stephen Nutbrown on 23/07/2016.
 */
public class DefaultStorjClient implements StorjClient {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    StorjConfiguration config;
    StorjRestClient storjRestClient;

    /**
     * Construct a storj.io.client.DefaultStorjClient client using the config.
     * @param config the config for the client.
     */
    public DefaultStorjClient(StorjConfiguration config) {
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
        EncryptionUtils.encryptFile(inputFile, encryptedFile, encryptionPassword);

        // Shard the file.
        List<Shard> shards = ShardingUtils.shardFile(inputFile, config.getShardSizeInBytes());

        // create a frame.
        Frame frame = storjRestClient.createFrame();

        // upload shards.
        for (Shard shard : shards) {
            AddShardResponse response = storjRestClient.addShardToFrame(frame.getId(), shard, 8);

            storjRestClient.getFrameById(frame.getId());

            String address = "ws://" + response.getFarmer().getAddress() + ":" + response.getFarmer().getPort();
            CountDownLatch latch;
            latch = new CountDownLatch(1);
            ClientManager wsClient = ClientManager.createClient();
            try {
                wsClient.connectToServer(new WebsocketShardSender(shard, response, latch), new URI(address));
                latch.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                // clean up shard files..
                File shardFile = new File(shard.getPath());
                shardFile.delete();
            }
        }



        // Create the bucket entry.
        BucketEntry bucketEntry = new BucketEntry();
        // Library for getting mime type (Should work on linux which Files.probe won't always).
        ContentInfoUtil mimeTypeFinder = new ContentInfoUtil();
        bucketEntry.setMimetype(mimeTypeFinder.findMatch(inputFile).getMimeType());
        bucketEntry.setFilename(inputFile.getName());
        bucketEntry.setFrame(frame.getId());
        bucketEntry.setSize(encryptedFile.length());

        // clean up encrypted file
        encryptedFile.delete();

        // store the bucket entry.
        return storjRestClient.storeFile(bucketId, bucketEntry);

    }

    public BucketEntry uploadFile(File file, Bucket bucket) throws Exception {
        return uploadFile(file, bucket.getId());
    }

    public void deleteFile(String bucketId, String fileId){
        storjRestClient.destroyFileEntry(bucketId, fileId);
    }

    public File downloadFile(BucketEntry bucketEntry, File outputFile) {
        return downloadFile(bucketEntry.getBucket(), bucketEntry.getId(), outputFile);
    }

    public File downloadFile(String bucketId, String bucketEntryId, File outputFile) {
        File encryptedOutputFile = null;
        try {
            encryptedOutputFile = File.createTempFile("temp","encrypted");
            logger.info(encryptedOutputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }


        Token token = storjRestClient.getTokenForBucket(bucketId, Operation.PULL);
        List<FilePointer> pointers = storjRestClient.getFilePointers(bucketId, bucketEntryId, token.getToken());

        // upload shards.
        for (FilePointer pointer : pointers) {
            CountDownLatch latch;
            latch = new CountDownLatch(1);
            ClientManager wsClient = ClientManager.createClient();
            try {
                wsClient.setDefaultMaxBinaryMessageBufferSize(Integer.MAX_VALUE);
                wsClient.setDefaultMaxTextMessageBufferSize(Integer.MAX_VALUE);
                logger.info("CONNECTING TO: " + "ws://" + pointer.getFarmer().getAddress() + ":" + pointer.getFarmer().getPort());

                final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

                wsClient.connectToServer(new WebsocketFileRetriever(pointer, encryptedOutputFile, latch), new URI("ws://" + pointer.getFarmer().getAddress() + ":" + pointer.getFarmer().getPort()));
                latch.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        try {
            EncryptionUtils.decryptFile(encryptedOutputFile, outputFile, config.getEncryptionKey());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    public List<Bucket> listBuckets() {
        return storjRestClient.getAllBuckets();
    }

    public Bucket getBucket(String bucketId) {
        return storjRestClient.getBucketById(bucketId);
    }

    public Bucket createBucket(String bucketName) {
        Bucket bucket = new Bucket();
        bucket.setName(bucketName);
        return storjRestClient.createBucket(bucket);
    }

    public void deleteBucket(String buckeId) {
        storjRestClient.deleteBucket(buckeId);
    }

    public void deleteBucket(Bucket bucket) {
        deleteBucket(bucket.getId());
    }

    public void resetPassword(String emailAddress) {
        storjRestClient.resetPassword(emailAddress);
    }

    public void resetPassword(User user) {
        resetPassword(user.getEmail());
    }

    public User createUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return storjRestClient.createUser(user);
    }

    public List<BucketEntry> listFiles(String bucketId) {
        return storjRestClient.getFilesInBucket(bucketId);
    }
}
