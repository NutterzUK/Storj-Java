package datatransfer;

import storj.io.restclient.model.Operation;

/**
 * Created by steve on 12/07/2016.
 */
public class AuthorizationModel {

    String token;
    String Hash;
    Operation operation;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
