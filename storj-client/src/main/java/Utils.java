import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Bytes;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;
import storj.io.restclient.model.Shard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.nio.file.Files.readAllBytes;

/**
 * Created by Stephen Nutbrown on 09/07/2016.
 */
public class Utils {


    public static void encryptFile(File input, File output, String key) throws Exception {
        new AESFiles().encrypt(input, output, key.getBytes());
    }

    public static void decryptFile(File input, File output, String key) throws Exception {
        new AESFiles().decrypt(input, output, key.getBytes());
    }

    public static List<Shard> shardFile(File input, int shardSize) throws Exception {
        ArrayList<Shard> shards = new ArrayList<Shard>();

        int numChallenges = 8;

        FileInputStream inputStream = new FileInputStream(input);
        byte[] buffer = new byte[shardSize];
        int length;
        int shardIndex = 0;

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
            shard.setSize(length);
            shard.setHash(new String(getRipemdSha256File(fileShard)));
            shard.setPath(fileShard.getAbsolutePath());

            addChallenges(shard, length, buffer, numChallenges);

            shard.setIndex(shardIndex++);
            shards.add(shard);
        }
        return shards;
    }

    public static void addChallenges(Shard shard, int shardLength, byte[] shardData, int numberOfChallenges){
        for(int i = 0; i< numberOfChallenges; i++) {
            String randomChallenge = getRandomChallengeString();
            byte[] challengeBytes = randomChallenge.getBytes(Charsets.UTF_8);

            // trim empty space at the end of shardData.
            shardData = Arrays.copyOf(shardData, shardLength);

            // Data to hash = challenge + shard data.
            byte[] dataToHash = Hex.encode(Bytes.concat(challengeBytes, shardData));

            // RMD160(SHA256(RMD160(SHA256(challenge + shard))))
            byte[] tree = rmd160Sha256(rmd160Sha256(dataToHash));

            shard.getChallenges().add(randomChallenge);
            shard.getTree().add(new String(tree));
        }
    }

    public static byte[] rmd160Sha256(byte[] input){

        byte[] sha256 = Hashing.sha256().hashBytes(input).asBytes();
        sha256 = Hex.encode(sha256);

        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(sha256, 0, sha256.length);
        byte[] output = new byte[digest.getDigestSize()];
        digest.doFinal (output, 0);

        output = Hex.encode(output);

        return output;
    }


    /**
     * More efficient for files than rmd160sha256 as it uses streaming.
     * @param file the file to hash
     * @return the file after sha256 and ripemd160.
     * @throws Exception
     */
    public static byte[] getRipemdSha256File(File file) throws Exception{
        // Get sha256 for the file.
        byte[] sha256sBytes = com.google.common.io.Files.hash(file, Hashing.sha256()).asBytes();
        sha256sBytes = Hex.encode(sha256sBytes);

        // Get RIPEMD160 for that.
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update (sha256sBytes, 0, sha256sBytes.length);
        byte[] output = new byte[digest.getDigestSize()];
        digest.doFinal (output, 0);
        output = Hex.encode(output);
        return output;
    }

    public static String getRandomChallengeString(){
        int numChars = 32;

        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numChars){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, numChars);
    }

    public static void pieceTogetherFile(List<File> shards, File destination) throws Exception {
        FileOutputStream outputStream = new FileOutputStream(destination);
        for(File shard : shards) {
            FileInputStream inputStream = new FileInputStream(shard);
            byte[] shardBytes = readAllBytes(shard.toPath());
            outputStream.write(shardBytes);
        }
        outputStream.close();
    }
}
