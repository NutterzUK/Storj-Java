package storj.io.client.sharding;

import com.google.common.base.Charsets;
import com.google.common.primitives.Bytes;
import org.bouncycastle.util.encoders.Hex;
import storj.io.client.encryption.EncryptionUtils;
import storj.io.restclient.model.Shard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.nio.file.Files.readAllBytes;

/**
 * Created by Stephen Nutbrown on 09/07/2016.
 */
public class ShardingUtils {

    /**
     * Utility method to split a file into several shards.
     * @param input the file to split into shards.
     * @param shardSize the size (in bytes) the shards should be.
     * @return the shards.
     * @throws Exception problem sharding the file.
     */
    public static List<Shard> shardFile(File input, int shardSize) throws Exception {
        ArrayList<Shard> shards = new ArrayList<Shard>();

        int numChallenges = 8;

        FileInputStream inputStream = new FileInputStream(input);
        byte[] buffer = new byte[shardSize];
        int length;
        int shardIndex = 0;
        int index = 0;
        // read file, one "buffer" at a time. The buffer is set to the shard size
        // So each read is 1 shard :).
        while ((length = inputStream.read(buffer)) > 0){

            // Write the file
            File fileShard = File.createTempFile("shard",".shard");
            FileOutputStream out = new FileOutputStream(fileShard);
            out.write(buffer, 0, length);
            out.close();

            // create shard object pointing to the file.
            Shard shard = new Shard();
            shard.setSize(fileShard.length());
            shard.setHash(new String(EncryptionUtils.getRipemdSha256File(fileShard)));
            shard.setPath(fileShard.getAbsolutePath());
            shard.setIndex(index++);
            addChallenges(shard, buffer, numChallenges);

            shard.setIndex(shardIndex++);
            shards.add(shard);
        }
        return shards;
    }

    /**
     * Create challenges for a shard.
     * @param shard the shard to create challenges for.
     * @param shardData the data contained in the shard.
     * @param numberOfChallenges the number of challenges.
     */
    public static void addChallenges(Shard shard, byte[] shardData, int numberOfChallenges){
        for(int i = 0; i< numberOfChallenges; i++) {
            String randomChallenge = getRandomChallengeString();
            byte[] challengeBytes = randomChallenge.getBytes(Charsets.UTF_8);

            // trim empty space at the end of shardData.
            shardData = Arrays.copyOf(shardData, (int)shard.getSize());

            // Data to hash = challenge + shard data.
            byte[] dataToHash = Hex.encode(Bytes.concat(challengeBytes, shardData));

            // RMD160(SHA256(RMD160(SHA256(challenge + shard))))
            byte[] tree = EncryptionUtils.rmd160Sha256(EncryptionUtils.rmd160Sha256(dataToHash));

            shard.getChallenges().add(randomChallenge);
            shard.getTree().add(new String(tree));
        }
    }

    /**
     * Creates a random 32 byte string.
     * @return a random 32 byte string.
     */
    public static String getRandomChallengeString(){
        int numChars = 32;

        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numChars){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, numChars);
    }

    /**
     * Take several shards and put them back together into one file.
     * Note this does not decrypt the file.
     * @param shards the shards.
     * @param destination the destination to put the combined file.
     * @throws IOException Problem reading/writing.
     */
    public static void pieceTogetherFile(List<File> shards, File destination) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(destination);
        for(File shard : shards) {
            byte[] shardBytes = readAllBytes(shard.toPath());
            outputStream.write(shardBytes);
        }
        outputStream.close();
    }
}
