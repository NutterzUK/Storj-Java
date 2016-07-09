package storj.io.restclient.auth;

import com.sun.jersey.api.client.WebResource;

/**
 * Created by Stephen Nutbrown on 03/07/2016.
 */
public interface AuthType {

    public WebResource.Builder setRequiredAuthHeaders (WebResource.Builder builder);

}
