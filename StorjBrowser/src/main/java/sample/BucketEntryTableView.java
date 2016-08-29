package sample;

import javafx.beans.property.SimpleStringProperty;
import storj.io.restclient.model.BucketEntry;

/**
 * Created by steve on 16/08/2016.
 */
public class BucketEntryTableView {

    private final SimpleStringProperty fileName;
    private final SimpleStringProperty size;
    private final SimpleStringProperty type;
    private final SimpleStringProperty id;

    public BucketEntryTableView(BucketEntry bucketEntry){
        fileName = new SimpleStringProperty(bucketEntry.getFilename());
        size = new SimpleStringProperty(FileSizeUtils.humanReadableByteCount(bucketEntry.getSize(), true));
        type = new SimpleStringProperty(bucketEntry.getMimetype());
        id = new SimpleStringProperty(bucketEntry.getId());
    }

    public String getId(){
        return id.get();
    }

    public void setId(String id){
        this.id.set(id);
    }

    public String getFileName(){
        return fileName.get();
    }

    public void setFileName(String set){
        fileName.set(set);
    }

    public String getSize(){
        return size.get();
    }

    public void setSize(String set){
        size.set(set);
    }

    public String getType(){
        return type.get();
    }

    public void setType(String set){
        type.set(set);
    }



}
