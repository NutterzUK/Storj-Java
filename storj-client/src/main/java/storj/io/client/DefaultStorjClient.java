package storj.io.client;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import storj.io.client.encryption.EncryptionUtils;
import storj.io.client.exceptions.StorjException;
import storj.io.client.exceptions.StorjFileException;
import storj.io.client.sharding.ShardingUtils;
import storj.io.client.websockets.WebsocketFileRetriever;
import storj.io.client.websockets.WebsocketShardSender;
import storj.io.restclient.model.AddShardResponse;
import storj.io.restclient.model.Bucket;
import storj.io.restclient.model.BucketEntry;
import storj.io.restclient.model.FilePointer;
import storj.io.restclient.model.Frame;
import storj.io.restclient.model.Operation;
import storj.io.restclient.model.Shard;
import storj.io.restclient.model.Token;
import storj.io.restclient.model.User;
import storj.io.restclient.rest.StorjRestClient;

/**
 * Created by Stephen Nutbrown on 23/07/2016.
 */
public class DefaultStorjClient implements StorjClient {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	StorjConfiguration config;
	StorjRestClient storjRestClient;

	/**
	 * Construct a storj.io.client.DefaultStorjClient client using the config.
	 * 
	 * @param config
	 *            the config for the client.
	 */
	public DefaultStorjClient(StorjConfiguration config) {
		this.config = config;
		storjRestClient = new StorjRestClient(config.getApiRoot(), config.getAuth());
	}

	public void setConfiguration(StorjConfiguration configuration) {
		this.config = configuration;
	}

	public StorjConfiguration getConguration() {
		return config;
	}

	public BucketEntry uploadFile(File inputFile, String bucketId) throws StorjException {

		String encryptionPassword = config.getEncryptionKey();

		// TODO: neaten this.
		File encryptedFile = new File(
				config.getTempDirectoryForShards().getPath() + "/" + inputFile.getName() + ".encrypted");

		Frame frame = null;
		try {

			// Create encrypted file.
			EncryptionUtils.encryptFile(inputFile, encryptedFile, encryptionPassword);

			// Shard the file.
			List<Shard> shards = ShardingUtils.shardFile(encryptedFile, config.getShardSizeInBytes());

			// create a frame.
			frame = storjRestClient.createFrame();

			// upload shards.
			for (Shard shard : shards) {
				AddShardResponse response = storjRestClient.addShardToFrame(frame.getId(), shard, 8);

				String address = "ws://" + response.getFarmer().getAddress() + ":" + response.getFarmer().getPort();
				CountDownLatch latch;
				latch = new CountDownLatch(1);
				try {
					WebsocketShardSender sender = new WebsocketShardSender(new URI(address), shard, response, latch);
					sender.connect();
					latch.await();
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					// clean up shard files..
					File shardFile = new File(shard.getPath());
					shardFile.delete();
				}
			}

		} catch (StorjException storjException) {
			// Not ideal but don't want storj exceptions to be swallowed.
			throw storjException;

		} catch (Exception e) {

		}
		// Create the bucket entry.
		BucketEntry bucketEntry = new BucketEntry();
		// Library for getting mime type (Should work on linux which Files.probe
		// won't always).
		try {
			bucketEntry.setMimetype(Files.probeContentType(inputFile.toPath()));
			bucketEntry.setFilename(inputFile.getName());
			bucketEntry.setFrame(frame.getId());
			bucketEntry.setSize(encryptedFile.length());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// clean up encrypted file
		encryptedFile.delete();

		// store the bucket entry.
		return storjRestClient.storeFile(bucketId, bucketEntry);

	}

	public BucketEntry uploadFile(File file, Bucket bucket) throws Exception {
		return uploadFile(file, bucket.getId());
	}

	public void deleteFile(String bucketId, String fileId) {
		storjRestClient.destroyFileEntry(bucketId, fileId);
	}

	public File downloadFile(BucketEntry bucketEntry, File outputFile) throws StorjFileException {
		return downloadFile(bucketEntry.getBucket(), bucketEntry.getId(), outputFile);
	}

	public File downloadFile(String bucketId, String bucketEntryId, File outputFile) throws StorjFileException {
		File encryptedOutputFile = null;
		try {
			encryptedOutputFile = File.createTempFile("temp", "encrypted");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Token token = storjRestClient.getTokenForBucket(bucketId, Operation.PULL);
		List<FilePointer> pointers = storjRestClient.getFilePointers(bucketId, bucketEntryId, token.getToken());

		// download shards.
		for (FilePointer pointer : pointers) {
			try {
				CountDownLatch latch = new CountDownLatch(1);
				String farmer = "ws://" + pointer.getFarmer().getAddress() + ":" + pointer.getFarmer().getPort();
				WebsocketFileRetriever c = new WebsocketFileRetriever(new URI(farmer), pointer, encryptedOutputFile,
						latch);
				c.connect();
				latch.await();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		try {
			logger.info("Encrypted file: " + encryptedOutputFile.getAbsolutePath());
			EncryptionUtils.decryptFile(encryptedOutputFile, outputFile, config.getEncryptionKey());
			logger.info("Decrypted file: " + outputFile.getAbsolutePath());

		} catch (StorjFileException e) {
			// Not very nice for now but we don't want to eat the
			// StorjFileException as we want the app to recover from it.
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(outputFile.getPath());

		return outputFile;
	}

	public List<Bucket> listBuckets() {
		return storjRestClient.getAllBuckets();
	}

	public Bucket getBucket(String bucketId) {
		return storjRestClient.getBucketById(bucketId);
	}

	public Bucket createBucket(String bucketName) {
		Bucket bucket = new Bucket();
		bucket.setName(bucketName);
		return storjRestClient.createBucket(bucket);
	}

	public void deleteBucket(String buckeId) {
		storjRestClient.deleteBucket(buckeId);
	}

	public void deleteBucket(Bucket bucket) {
		deleteBucket(bucket.getId());
	}

	public void resetPassword(String emailAddress) {
		storjRestClient.resetPassword(emailAddress);
	}

	public void resetPassword(User user) {
		resetPassword(user.getEmail());
	}

	public User createUser(String email, String password) {
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		return storjRestClient.createUser(user);
	}

	public List<BucketEntry> listFiles(String bucketId) {
		return storjRestClient.getFilesInBucket(bucketId);
	}
}
