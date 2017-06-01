package storj.io.restclient.model;

import com.google.common.hash.Hashing;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Created by Stephen Nutbrown on 03/07/2016.
 */
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class User {

    private String email;
    private String password;
    private String redirect;
    private Long __nonce;
    @JsonIgnore
    private Preferences preferences;
    @JsonIgnore
    private boolean activated;
    private Date created;
    private String id;
    @JsonIgnore
    private String uuid;
    private String pubkey;
    @JsonIgnore
    private boolean isFreeTier;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isIsFreeTier() {
        return isFreeTier;
    }

    public void setIsFreeTier(boolean freeTier) {
        isFreeTier = freeTier;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getPassword(){
        return password;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password){
        this.password = sha256Encrypt(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public Long get__nonce() {
        return __nonce;
    }

    public void set__nonce(Long __nonce) {
        this.__nonce = __nonce;
    }

    private String sha256Encrypt(String input){
        return Hashing.sha256()
                .hashString(input, StandardCharsets.UTF_8)
                .toString();
    }

    @Override
    public String toString() {
        return "User{" +
                "redirect='" + redirect + '\'' +
                ", __nonce=" + __nonce +
                ", activated=" + activated +
                ", created=" + created +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
