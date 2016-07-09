package storj.io.restclient.model;

import java.util.List;

/**
 * Represents a file staging frame.
 * Created by Stephen Nutbrown on 06/07/2016.
 */
public class Frame {

    String user;
    String created;
    String id;
    List<Shard> shards;


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public List<Shard> getShards() {
        return shards;
    }

    public void setShards(List<Shard> shards) {
        this.shards = shards;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "created='" + created + '\'' +
                ", id='" + id + '\'' +
                ", shards=" + shards +
                '}';
    }
}
