import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import datatransfer.AuthorizationModel;
import org.glassfish.tyrus.client.ClientManager;
import storj.io.restclient.model.AddShardResponse;
import storj.io.restclient.model.Shard;

import javax.websocket.*;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by steve on 12/07/2016.
 */
@ClientEndpoint
public class StorjWebsocketClient{

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Gson gson = new Gson();
    private Shard shard;
    private AddShardResponse destination;
    private AuthorizationModel authModel;

    public StorjWebsocketClient(Shard shard, AddShardResponse destination){
        this.shard = shard;
        this.destination = destination;

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
        logger.info("Connected ... " + session.getId());

        try {
            logger.info("Sending: " + gson.toJson(authModel));
            session.getBasicRemote().sendText(gson.toJson(authModel));
            logger.info("Sent: " + gson.toJson(authModel));
            Files.copy(Paths.get(shard.getPath()), session.getBasicRemote().getSendStream());
            logger.info("Sent file");
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s close because of %s", session.getId(), closeReason));
    }
}
