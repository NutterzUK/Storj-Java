package storj.io.client.websockets;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import storj.io.restclient.model.AddShardResponse;
import storj.io.restclient.model.Shard;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;



public class WebsocketShardSender extends WebSocketClient {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Gson gson = new Gson();
    private Shard shard;
    private AddShardResponse destination;
    private AuthorizationModel authModel;
    private CountDownLatch latch;

    public WebsocketShardSender(URI serverURI, Shard shard, AddShardResponse destination, CountDownLatch latch){
        super(serverURI, new Draft_17(), null, 99999);
        this.shard = shard;
        this.destination = destination;
        this.latch = latch;

        authModel = new AuthorizationModel();
        authModel.setToken(destination.getToken());
        authModel.setOperation(destination.getOperation());
        authModel.setHash(destination.getHash());

    }

    public void onOpen(ServerHandshake serverHandshake) {
        File shardFile = new File(shard.getPath());
        try {
            // send auth as text.
            send(gson.toJson(authModel));
            // send shard data as binary - note this will need changing to read in small amounts to save memory.. when it's working.
            send(Files.readAllBytes(shardFile.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onMessage(String s) {
        logger.info(s);
    }

    public void onClose(int i, String s, boolean b) {
        logger.info("Websocket closed with reason" + i + s + b);
        latch.countDown();
    }

    public void onError(Exception e) {

    }
}
