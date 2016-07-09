package storj.io.restclient.model;

/**
 * Created by Stephen Nutrown on 07/07/2016.
 */
public class FilePointer {
    String hash;
    String token;
    Operation operation;
    String channel;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "FilePointer{" +
                "hash='" + hash + '\'' +
                ", token='" + token + '\'' +
                ", operation=" + operation +
                ", channel='" + channel + '\'' +
                '}';
    }
}
