package storj.io.restclient.model;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

/**
 * Created by Stephen Nutbrown on 06/07/2016.
 */
public class Shard {

    String _id;
    int index;
    String hash;
    long size;
    List<String> tree;
    List<String> challenges;

    @JsonIgnore
    String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<String> getTree() {
        return tree;
    }

    public void setTree(List<String> tree) {
        this.tree = tree;
    }

    public List<String> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<String> challenges) {
        this.challenges = challenges;
    }

    @Override
    public String toString() {
        return "Shard{" +
                "index=" + index +
                ", hash='" + hash + '\'' +
                ", size=" + size +
                ", tree=" + tree +
                ", challenges=" + challenges +
                '}';
    }
}
