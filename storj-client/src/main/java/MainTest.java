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
        uploadFile();
    }


    private static void uploadFile(){
        StorjConfiguration configuration = new StorjConfiguration(CodeTestUtils.getEncryptionKey(), CodeTestUtils.getStorjUsername(), CodeTestUtils.getStorjPassword());
        StorjClient storj = new Storj(configuration);

        try {
            // upload a file to the first bucket.
            storj.uploadFile(new File("C:\\Users\\steve\\Desktop\\cat1.jpg"), storj.listBuckets().get(0));
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
        StorjClient storj = new Storj(new StorjConfiguration(CodeTestUtils.getEncryptionKey()));
        User user = new User();
        user.setEmail(CodeTestUtils.getStorjUsername());
        user.setPassword(CodeTestUtils.getStorjPassword());
        storj.createUser();
    }
}
