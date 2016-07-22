package storj.io.restclient.model;

/**
 * Created by Stephen Nutbrown on 07/07/2016.
 */
public class BucketEntry {

    String hash;
    String bucket;
    String mimetype;
    String filename;
    long size;
    String frame;

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
