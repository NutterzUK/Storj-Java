import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;
import storj.io.restclient.model.AddShardResponse;
import storj.io.restclient.model.Frame;
import storj.io.restclient.model.Shard;
import storj.io.restclient.rest.StorjRestClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephen Nutbrown on 09/07/2016.
 */
public class MainTest {

    final static int DEFAULT_SHARD_SIZE_BYTES = 1024*1024*8;

    public static void main(String[] args){
        File inputFile = new File("C:\\Users\\steve\\Desktop\\storj-java-bridge-client.zip");

        try {

            // My shard.
            byte[] fileBytes =  Files.readAllBytes(inputFile.toPath());

            // the challenge + file.
            byte[] toHash = Bytes.concat("103ae9a37613aab740249a1258709b8fa113119fcceee261f9aa386707e30a7a".getBytes(), fileBytes);

            // Print out the RMD160(SHA256(RMD160(SHA256(challenge + shard))))
            System.out.println(new String(Utils.rmd160Sha256(Utils.rmd160Sha256(toHash))));


        } catch (IOException e) {
            e.printStackTrace();
        }


        System.exit(1);

/*
        String encryptionPassword = "MZygpewJsCpRrfOr";
        StorjRestClient client = new StorjRestClient("steveswfc@gmail.com", "MYPASS");


        // TREE LEAVES ARE (EACH CHALLENGE)
        //RMD160(SHA256(RMD160(SHA256(challenge + shard))))

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
                System.out.println((client.addShardToFrame(frame.getId(), shard)));
            }




            // get it again.


            // Remake it
            //Utils.pieceTogetherFile(shards, new File("C:\\Users\\steve\\Desktop\\encrypted.zip"));

            // Unencrypt it
           // Utils.decryptFile(new File("C:\\Users\\steve\\Desktop\\encrypted.zip"), new File("C:\\Users\\steve\\Desktop\\unencrypted.zip"), encryptionPassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}
