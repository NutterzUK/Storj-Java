package sample;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import storj.io.restclient.model.Bucket;

/**
 * Created by steve on 16/08/2016.
 */
public class BucketListCellFactory implements Callback<ListView<Bucket>, ListCell<Bucket>> {

        @Override
        public ListCell<Bucket> call(ListView<Bucket> listview) {
            return new BucketListCell();
        }

}
