package storj.io.restclient.model;

/**
 * Created by Stephen Nutbrown on 07/07/2016.
 */
public class Token {

    String bucket;
    String expires;
    Operation operation;
    String token;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Token{" +
                "bucket='" + bucket + '\'' +
                ", expires='" + expires + '\'' +
                ", operation=" + operation +
                ", token='" + token + '\'' +
                '}';
    }
}
