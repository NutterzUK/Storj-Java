package sample;


import com.google.gson.annotations.Expose;

/**
 * Created by steve on 21/08/2016.
 */
public class Site {

    @Expose
    String apiRoot;

    @Expose
    String username;

    @Expose
    String siteName;


    public Site(String apiRoot, String username, String key, String siteName) {
        this.apiRoot = apiRoot;
        this.username = username;
        this.siteName = siteName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getApiRoot() {
        return apiRoot;
    }

    public void setApiRoot(String apiRoot) {
        this.apiRoot = apiRoot;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String toString(){
        return siteName;
    }

}
