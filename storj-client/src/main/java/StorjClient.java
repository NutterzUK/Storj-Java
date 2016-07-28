import storj.io.restclient.model.Bucket;
import storj.io.restclient.model.BucketEntry;
import storj.io.restclient.model.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by steve on 23/07/2016.
 */
public interface StorjClient {

    BucketEntry uploadFile(File file, String bucketId) throws Exception;

    BucketEntry uploadFile(File file, Bucket bucket) throws Exception;

    File downloadFile(BucketEntry bucketEntry);

    File downloadFile(String bucketId, String bucketEntryId);

    List<Bucket> getBuckets();

    Bucket getBucket(String bucketId);

    Bucket createBucket(String bucketName);

    void deleteBucket(String buckeId);

    void deleteBucket(Bucket bucket);

    void resetPassword(String emailAddress);

    void resetPassword(User user);

    User createUser(String email, String password);
}
