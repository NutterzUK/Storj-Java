package storj.io.restclient.model;

/**
 * Created by steve on 09/07/2016.
 */
public class AddShardResponse {

    String hash;
    String token;
    Operation operation;
    Contact farmer;

    public Contact getFarmer() {
        return farmer;
    }

    public void setFarmer(Contact farmer) {
        this.farmer = farmer;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "AddShardResponse{" +
                "hash='" + hash + '\'' +
                ", token='" + token + '\'' +
                ", operation=" + operation +
                ", farmer=" + farmer +
                '}';
    }
}
