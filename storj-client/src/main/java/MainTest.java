import storj.io.restclient.model.Frame;
import storj.io.restclient.model.Shard;
import storj.io.restclient.rest.StorjRestClient;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve on 09/07/2016.
 */
public class MainTest {

    final static int DEFAULT_SHARD_SIZE_BYTES = 1024*1024*8;


    // This main is just Stephen Nutbrown playing around trying to upload a file.
    // Please ignore it for now :)

    public static void main(String[] args){
        File inputFile = new File("C:\\Users\\steve\\Desktop\\storj-java-bridge-client.zip");
        String encryptionPassword = "MZygpewJsCpRrfOr";
        StorjRestClient client = new StorjRestClient("USER", "PASS");

        try {
            // Create encrypted file.
            File encryptedFile = File.createTempFile("storj",".storj");
            Utils.encryptFile(inputFile, encryptedFile, encryptionPassword);

            // Shard it.
            List<Shard> shards = Utils.shardFile(encryptedFile, DEFAULT_SHARD_SIZE_BYTES);


            // put it on the network.
            String bucketId = client.getAllBuckets().get(0).getId();

            List<Frame> frames = new ArrayList<Frame>();
            for(Shard shard : shards) {
                frames.add(client.addShardToFrame(client.createFrame().getId(), shard));
            }




            // get it again.


            // Remake it
            //Utils.pieceTogetherFile(shards, new File("C:\\Users\\steve\\Desktop\\encrypted.zip"));

            // Unencrypt it
           // Utils.decryptFile(new File("C:\\Users\\steve\\Desktop\\encrypted.zip"), new File("C:\\Users\\steve\\Desktop\\unencrypted.zip"), encryptionPassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
