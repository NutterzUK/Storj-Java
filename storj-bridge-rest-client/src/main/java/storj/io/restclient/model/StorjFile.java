package storj.io.restclient.model;

/**
 * Created by steve on 22/07/2016.
 */
public class StorjFile {

    /**
     * The ID of the frame to finalise.
     */
    String frame;

    /**
     * The mimetype of the file.
     */
    String mimetype;

    /**
     * The filename of the file.
     */
    String filename;

    public StorjFile(String frame, String mimetype, String filename) {
        this.frame = frame;
        this.mimetype = mimetype;
        this.filename = filename;
    }

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
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
}
