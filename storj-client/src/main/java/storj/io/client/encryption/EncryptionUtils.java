package storj.io.client.encryption;

import com.google.common.hash.Hashing;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;

import java.io.File;

/**
 * Created by steve on 03/08/2016.
 */
public class EncryptionUtils {

    /**
     * Encrypt a file using AES.
     * @param input the input file.
     * @param output where to store the output after encryption.
     * @param key the key used to encrypt (To decrypt this key is required).
     * @throws Exception problem encrypting file.
     */
    public static void encryptFile(File input, File output, String key) throws Exception {
        new AESFiles().encrypt(input, output, key.getBytes());
    }

    /**
     * Decrypt a file using AES.
     * @param input the encrypted input file.
     * @param output where to store the output after decryption.
     * @param key the key used when encryping.
     * @throws Exception problem decrypting file.
     */
    public static void decryptFile(File input, File output, String key) throws Exception {
        new AESFiles().decrypt(input, output, key.getBytes());
    }

    /**
     * Take an input array of bytes and encode it as the storj core does.
     * This firstly sha56s encrypts the file, then hex encodes it.
     * Then, it RIPEMD160s the result and again hex encodes, before returning.
     * @param input a byte array to encrypt.
     * @return the byte[] after encrypting.
     */
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
     * A more efficient version of rmd160sha256 for files, as it uses streaming.
     * @param file the file to hash.
     * @return the file after sha256 and ripemd160.
     * @throws Exception
     */
    public static byte[] getRipemdSha256File(File file) throws Exception{
        // Get sha256 for the file.
        byte[] sha256sBytes = com.google.common.io.Files.hash(file, Hashing.sha256()).asBytes();

        // Updated in v3.0 of storj bridge.
        //sha256sBytes = Hex.encode(sha256sBytes);

        // Get RIPEMD160 for that.
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update (sha256sBytes, 0, sha256sBytes.length);
        byte[] output = new byte[digest.getDigestSize()];
        digest.doFinal (output, 0);
        output = Hex.encode(output);
        return output;
    }

}
