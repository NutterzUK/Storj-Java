import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;

import datatransfer.CodeTestUtils;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;
import org.glassfish.tyrus.client.ClientManager;
import storj.io.restclient.model.*;
import storj.io.restclient.rest.StorjRestClient;

import javax.websocket.DeploymentException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Stephen Nutbrown on 09/07/2016.
 */
public class MainTest {

    final static String STORJ_API_LOCAL = "http://localhost:6382";
    final static int DEFAULT_SHARD_SIZE_BYTES = 1024*1024*8;

    public static void main(String[] args){
       // createUser();
        StorjRestClient client = new StorjRestClient(CodeTestUtils.getStorjBasePath(), CodeTestUtils.getStorjUsername(), CodeTestUtils.getStorjPassword());

      //  createBucket(client, "TestBucket");
        attemptUpload(client);

    }

    private static void attemptUpload(StorjRestClient client){
        File inputFile = new File("C:\\Users\\steve\\Desktop\\cat.jpg");
        String encryptionPassword = "MZygpewJsCpRrfOr";

        try {
            // Create encrypted file.
            File encryptedFile = File.createTempFile("storj",".storj");
            Utils.encryptFile(inputFile, encryptedFile, encryptionPassword);

            // Shard it.

            List<Shard> shards = Utils.shardFile(inputFile, DEFAULT_SHARD_SIZE_BYTES);

            // Set new hash.

            // put it on the network.
            String bucketId = client.getAllBuckets().get(0).getId();

            List<AddShardResponse> addShardResponses = new ArrayList<AddShardResponse>();
            Frame frame = client.createFrame();
            for(Shard shard : shards){

                AddShardResponse response = client.addShardToFrame(frame.getId(), shard);

                String address = "ws://" + response.getFarmer().getAddress() + ":" + response.getFarmer().getPort();

                CountDownLatch latch;
                latch = new CountDownLatch(1);

                ClientManager wsClient = ClientManager.createClient();

                try {
                    wsClient.connectToServer(new StorjWebsocketClient(shard, response, latch), null, new URI(address));
                    latch.await();
                } catch (Exception  e) {
                    throw new RuntimeException(e);
                }

            }

            // push the data.
            BucketEntry bucketEntry = new BucketEntry();
            bucketEntry.setMimetype("image/jpeg");
            bucketEntry.setFilename("cat.jpg");
            bucketEntry.setFrame(frame.getId());
            client.storeFile(bucketId, bucketEntry);

            // Remake it
            //Utils.pieceTogetherFile(shards, new File("C:\\Users\\steve\\Desktop\\encrypted.zip"));

            // Unencrypt it
            // Utils.decryptFile(new File("C:\\Users\\steve\\Desktop\\encrypted.zip"), new File("C:\\Users\\steve\\Desktop\\unencrypted.zip"), encryptionPassword);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void createBucket(StorjRestClient client, String bucketName){
        Bucket bucket = new Bucket();
        bucket.setName(bucketName);
        client.createBucket(bucket);
    }

    private static void createUser(){
        StorjRestClient client = new StorjRestClient(STORJ_API_LOCAL);
        User user = new User();
        user.setEmail("steveswfc@gmail.com");
        user.setPassword("testpassword");
        client.createUser(user);
    }
}
