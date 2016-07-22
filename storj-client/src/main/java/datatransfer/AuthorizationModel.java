package datatransfer;

import storj.io.restclient.model.Operation;

/**
 * Created by Stephen Nutbrown on 12/07/2016.
 */
public class AuthorizationModel {

    String token;
    String hash;
    Operation operation;

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

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
