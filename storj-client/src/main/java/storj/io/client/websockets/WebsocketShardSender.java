package storj.io.client.websockets;

import com.google.gson.Gson;
import storj.io.restclient.model.AddShardResponse;
import storj.io.restclient.model.Shard;

import javax.websocket.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Created by steve on 12/07/2016.
 */
@ClientEndpoint
public class WebsocketShardSender {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Gson gson = new Gson();
    private Shard shard;
    private AddShardResponse destination;
    private AuthorizationModel authModel;
    private CountDownLatch latch;

    public WebsocketShardSender(Shard shard, AddShardResponse destination, CountDownLatch latch){
        this.shard = shard;
        this.destination = destination;
        this.latch = latch;

        authModel = new AuthorizationModel();
        authModel.setToken(destination.getToken());
        authModel.setOperation(destination.getOperation());
        authModel.setHash(destination.getHash());

    }

    @OnMessage
    public String onMessage(String message, Session session) {
        logger.info("Received ...." + message);
        return message;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        File shardFile = new File(shard.getPath());
        try {
            // send auth as text.
            session.getBasicRemote().sendText(gson.toJson(authModel), true);

            // send shard data as binary - note this will need changing to read in small amounts to save memory.. when it's working.
            ByteBuffer fileBuffer = ByteBuffer.allocate((int)shardFile.length());
            fileBuffer.put(Files.readAllBytes(shardFile.toPath()));
            session.getBasicRemote().sendBinary(fileBuffer, true);

         //   Files.copy(Paths.get(shard.getPath()), session.getBasicRemote().getSendStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        latch.countDown();
    }
}
