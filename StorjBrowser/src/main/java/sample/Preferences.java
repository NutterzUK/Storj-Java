package sample;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by steve on 21/08/2016.
 */
public class Preferences {

    @Expose
    ArrayList<Site> sites = new ArrayList<>();

    public ArrayList<Site> getSites() {
        return sites;
    }

    public void setSites(ArrayList<Site> sites) {
        this.sites = sites;
    }
}
