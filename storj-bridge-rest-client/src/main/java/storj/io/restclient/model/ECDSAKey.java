package storj.io.restclient.model;

/**
 * Created by Stephen Nutbrown on 06/07/2016.
 */
public class ECDSAKey {

    /**
     * The ESDA Key value
     */
    String key;

    /**
     * The user ID the key is associated with.
     */
    String userId;

    /**
     * Get the key.
     * @return the ECDSA key value.
     */
    public String getKey() {
        return key;
    }

    /**
     * Set the key
     * @param key the ECDSA key value.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Get the user ID
     * @return the user id.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set the user id.
     * @param userId the user id.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ECDSAKey{" +
                "key='" + key + '\'' +
                ", user='" + userId + '\'' +
                '}';
    }
}
