package storj.io.restclient.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Created by Stephen Nutbrown on 07/07/2016.
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class BucketEntry {

    String id;
    String hash;
    String bucket;
    String mimetype;
    String filename;
    long size;
    String frame;
    String name;
    String renewal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRenewal() {
        return renewal;
    }

    public void setRenewal(String renewal) {
        this.renewal = renewal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "BucketEntry{" +
                "hash='" + hash + '\'' +
                ", bucket='" + bucket + '\'' +
                ", mimetype='" + mimetype + '\'' +
                ", filename='" + filename + '\'' +
                ", size=" + size +
                '}';
    }
}
