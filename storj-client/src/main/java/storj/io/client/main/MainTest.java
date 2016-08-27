package storj.io.client.main;

import storj.io.client.DefaultStorjClient;
import storj.io.client.StorjClient;
import storj.io.client.StorjConfiguration;
import storj.io.restclient.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Created by Stephen Nutbrown on 09/07/2016.
 */
public class MainTest {

    public static void main(String[] args){

        // Default username/password has been set in CodeTestUtils.
        StorjConfiguration configuration = new StorjConfiguration(CodeTestUtils.getEncryptionKey(), CodeTestUtils.getStorjUsername(), CodeTestUtils.getStorjPassword());
        configuration.setApiRoot(CodeTestUtils.getStorjBasePath());
        StorjClient storj = new DefaultStorjClient(configuration);

        String bucketId = "57c190e16d89ebce22c512c7";
        String bucketEntryId = "57c19121a04f078a52c72221";

        try {
            File temp = File.createTempFile("temp-file-name", ".tmp");
            storj.downloadFile(bucketId, bucketEntryId, temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        List<Bucket> buckets = client.listBuckets();
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
        StorjConfiguration config = new StorjConfiguration((CodeTestUtils.getEncryptionKey()));
        config.setApiRoot(CodeTestUtils.getStorjBasePath());
        StorjClient storj = new DefaultStorjClient(config);
        storj.createUser(CodeTestUtils.getStorjUsername(), (CodeTestUtils.getStorjPassword()));
    }
}
