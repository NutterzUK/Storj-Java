import com.google.common.io.Files;
import org.apache.commons.lang3.ObjectUtils;
import storj.io.restclient.auth.AuthType;
import storj.io.restclient.auth.BasicAuthType;
import storj.io.restclient.auth.NoAuthType;

import java.io.File;
import java.util.logging.Logger;

/**
 * Created by steve on 23/07/2016.
 */
public class StorjConfiguration {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private AuthType auth = new NoAuthType();

    private String encryptionKey;

    private int shardSizeInBytes =  1024*1024*8;

    private int retryRestAttempts = 4;

    private File tempDirectoryForShards = Files.createTempDir();

    private String apiRoot = "https://api.storj.io";

    /**
     * Create an unauthenticated storj client configuration.
     * @param encryptionKey the encryption key to use.
     */
    public StorjConfiguration(String encryptionKey){
        if(encryptionKey == null){
            throw new RuntimeException("Encryption key required for encrypting and decrypting files.");
        }
        this.encryptionKey = encryptionKey;
    }

    /**
     * Create a storj client configuration with a predefined authentication type.
     * @param encryptionKey the encrption key to use.
     * @param auth the auth type.
     */
    public StorjConfiguration(String encryptionKey, AuthType auth){
        this(encryptionKey);
        this.auth = auth;
    }

    /**
     * Create a storj client configuration with basic authentication.
     * @param encryptionKey the encryption key to use.
     * @param username the username.
     * @param password the password
     */
    public StorjConfiguration(String encryptionKey, String username, String password){
        this(encryptionKey);
        setAuth(new BasicAuthType(username, password));
    }

    public AuthType getAuth() {
        if(auth instanceof NoAuthType){
            logger.info("No authentication strategy has been specified, only endpoints which do not require auth will work.");
        }
        return auth;
    }

    public void setAuth(AuthType auth) {
        this.auth = auth;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public String getApiRoot() {
        return apiRoot;
    }

    public void setApiRoot(String apiRoot) {
        this.apiRoot = apiRoot;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public int getShardSizeInBytes() {
        return shardSizeInBytes;
    }

    public void setShardSizeInBytes(int shardSizeInBytes) {
        this.shardSizeInBytes = shardSizeInBytes;
    }

    public int getRetryRestAttempts() {
        return retryRestAttempts;
    }

    public void setRetryRestAttempts(int retryRestAttempts) {
        this.retryRestAttempts = retryRestAttempts;
    }

    public File getTempDirectoryForShards() {
        return tempDirectoryForShards;
    }

    public void setTempDirectoryForShards(File tempDirectoryForShards) {
        if(tempDirectoryForShards == null || !tempDirectoryForShards.isDirectory() || !tempDirectoryForShards.canWrite()){
            throw new RuntimeException("Temp directory must be readable, writable and a directory not a file.");
        }
        this.tempDirectoryForShards = tempDirectoryForShards;
    }
}
