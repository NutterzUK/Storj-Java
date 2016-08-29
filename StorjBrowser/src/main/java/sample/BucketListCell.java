package sample;

import javafx.scene.control.ListCell;
import storj.io.restclient.model.Bucket;

/**
 * Created by steve on 16/08/2016.
 */
public class BucketListCell extends ListCell<Bucket> {

    @Override
    public void updateItem(Bucket item, boolean empty) {
        super.updateItem(item, empty);
        // Format for bucket.
        if (!(item == null || empty)) {
            this.setText(item.getName());
        }
        setGraphic(null);
    }
}
