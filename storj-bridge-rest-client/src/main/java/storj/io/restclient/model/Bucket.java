package storj.io.restclient.model;

import java.util.List;

/**
 * Created by Stephen Nutbrown on 07/07/2016.
 */
public class Bucket {

    int storage;
    int transfer;
    String status;
    List<String> pubkeys;
    String user;
    String name;
    String created;
    String id;

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }

    public int getTransfer() {
        return transfer;
    }

    public void setTransfer(int transfer) {
        this.transfer = transfer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getPubkeys() {
        return pubkeys;
    }

    public void setPubkeys(List<String> pubkeys) {
        this.pubkeys = pubkeys;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "storage=" + storage +
                ", transfer=" + transfer +
                ", status='" + status + '\'' +
                ", pubkeys=" + pubkeys +
                ", user='" + user + '\'' +
                ", name='" + name + '\'' +
                ", created='" + created + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
