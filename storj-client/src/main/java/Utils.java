import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import storj.io.restclient.model.Shard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.nio.file.Files.readAllBytes;

/**
 * Created by steve on 09/07/2016.
 */
public class Utils {


    public static void encryptFile(File input, File output, String key) throws Exception {
        new AESFiles().encrypt(input, output, key.getBytes());
    }

    public static void decryptFile(File input, File output, String key) throws Exception {
        new AESFiles().decrypt(input, output, key.getBytes());
    }

    public static List<Shard> shardFile(File input, int shardSize) throws Exception {
        int shardIndex = 0;
        ArrayList<Shard> shards = new ArrayList<Shard>();
        FileInputStream inputStream = new FileInputStream(input);
        byte[] buffer = new byte[shardSize];

        int length;
        //copy the file content in bytes
        while ((length = inputStream.read(buffer)) > 0){

            // Write the file
            File fileShard = File.createTempFile("shard",".shard");
            FileOutputStream out = new FileOutputStream(fileShard);
            out.write(buffer, 0, length);
            out.close();

            // create shard object pointing to the file..
            Shard shard = new Shard();
            shard.setSize(length);
            shard.setHash(com.google.common.io.Files.hash(fileShard, Hashing.sha256()).toString());

            shard.setChallenges(new ArrayList<String>());
            shard.getChallenges().add(getRandomChallengeString());


            shard.setTree(new ArrayList<String>());

            String tree = shard.getHash() + shard.getChallenges().get(0);
            tree = Hashing.sha256().hashString(tree, Charsets.UTF_8).toString();
            shard.getTree().add(tree);

            shard.setIndex(shardIndex++);
            shard.setPath(fileShard.getAbsolutePath());
            shards.add(shard);
        }
        return shards;
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
