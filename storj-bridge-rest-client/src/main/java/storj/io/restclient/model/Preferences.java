package storj.io.restclient.model;

/**
 * Created by steve on 01/06/2017.
 */
public class Preferences {

    private boolean dnt;

    public Preferences(){

    }

    public boolean isDnt() {
        return dnt;
    }

    public void setDnt(boolean dnt) {
        this.dnt = dnt;
    }
}
