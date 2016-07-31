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

    public static void main(String[] args){
        StorjConfiguration configuration = new StorjConfiguration(CodeTestUtils.getEncryptionKey(), CodeTestUtils.getStorjUsername(), CodeTestUtils.getStorjPassword());
        configuration.setApiRoot(CodeTestUtils.getStorjBasePath());
        StorjClient storj = new Storj(configuration);
        createBucket(storj, "testy");

      //  String bucketId = findFirstBucket(storj).getId();
      //  String bucketEntryId = uploadFile(storj, new File("C:\\Users\\steve\\Desktop\\cat.jpg"), bucketId);

      //  downloadFile(storj, bucketId, bucketEntryId);
    }

    /**
     * Download a file. Currently not yet fully implemented.
     * @param storj
     * @param bucketId
     * @param bucketEntryId
     */
    private static void downloadFile(StorjClient storj, String bucketId, String bucketEntryId){
        storj.downloadFile(bucketId, bucketEntryId);
    }


    /**
     * Upload a file to storj as a bucket entry.
     * @param storj the client to use.
     * @param file the file to upload
     * @param bucketId the bucket ID of the bucket to upload to.
     * @return The ID of the uploaded bucket entry.
     */
    private static String uploadFile(StorjClient storj, File file, String bucketId){
        try {
            // upload a file to the first bucket we have.
            return storj.uploadFile(file, bucketId).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Just gets a bucket from the storj server or creates one if not existing.
     * @param client the storj client.
     * @return a bucket.
     */
    private static Bucket findFirstBucket(StorjClient client){
        Bucket bucket;
        List<Bucket> buckets = client.getBuckets();
        if(buckets.isEmpty()){
            bucket = createBucket(client, "TestBucket");
        }else{
            bucket = buckets.get(0);
        }
        return bucket;
    }

    /**
     * Create a bucket.
     * @param client The storj client.
     * @param bucketName the name of the bucket.
     * @return the bucket.
     */
    private static Bucket createBucket(StorjClient client, String bucketName){
        Bucket bucket = new Bucket();
        bucket.setName(bucketName);
        return client.createBucket(bucketName);
    }

    /**
     * Creates a user. Note that the user will need the email address verifying.
     */
    private static void createUser(){
        StorjClient storj = new Storj(new StorjConfiguration(CodeTestUtils.getEncryptionKey()));
        storj.createUser(CodeTestUtils.getStorjUsername(), (CodeTestUtils.getStorjPassword()));
    }
}
