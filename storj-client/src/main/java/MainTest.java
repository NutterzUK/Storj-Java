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
        uploadFile(new File("C:\\Users\\steve\\Desktop\\cat.jpg"));
    }


    private static void uploadFile(File file){
        StorjConfiguration configuration = new StorjConfiguration(CodeTestUtils.getEncryptionKey(), CodeTestUtils.getStorjUsername(), CodeTestUtils.getStorjPassword());
        configuration.setApiRoot(CodeTestUtils.getStorjBasePath());
        StorjClient storj = new Storj(configuration);

        try {
            // upload a file to the first bucket we have.
            storj.uploadFile(file, findFirstBucket(storj));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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

    private static Bucket createBucket(StorjClient client, String bucketName){
        Bucket bucket = new Bucket();
        bucket.setName(bucketName);
        return client.createBucket(bucketName);
    }

    private static void createUser(){
        StorjClient storj = new Storj(new StorjConfiguration(CodeTestUtils.getEncryptionKey()));
        User user = new User();
        user.setEmail(CodeTestUtils.getStorjUsername());
        user.setPassword(CodeTestUtils.getStorjPassword());
        storj.createUser();
    }
}
