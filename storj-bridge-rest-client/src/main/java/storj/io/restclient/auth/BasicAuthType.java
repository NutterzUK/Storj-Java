package storj.io.restclient.auth;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.sun.jersey.api.client.WebResource;

import javax.ws.rs.core.HttpHeaders;
import java.nio.charset.StandardCharsets;

/**
 * Created by Stephen Nutbrown on 03/07/2016.
 */
public class BasicAuthType implements AuthType{

    private String authorizationHeaderValue;

    public BasicAuthType(String username, String password){
        authorizationHeaderValue = "basic " + base64Encode(username + ":" + sha256Encrypt(password));
    }

    public WebResource.Builder setRequiredAuthHeaders(WebResource.Builder builder){
        return builder.header(HttpHeaders.AUTHORIZATION, authorizationHeaderValue);
    }

    private String sha256Encrypt(String input){
        return Hashing.sha256()
                .hashString(input, StandardCharsets.UTF_8)
                .toString();
    }

    private String base64Encode(String input){
        return BaseEncoding.base64().encode(input.getBytes(StandardCharsets.UTF_8));
    }
}
