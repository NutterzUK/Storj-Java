package storj.io.client.websockets;

import com.google.gson.Gson;
import storj.io.restclient.model.FilePointer;

import javax.websocket.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Created by steve on 12/07/2016.
 */
@ClientEndpoint
public class WebsocketFileRetriever {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Gson gson = new Gson();
    private FilePointer filePointer;
    private File outputFile;
    private AuthorizationModel authModel;
    private CountDownLatch latch;

    public WebsocketFileRetriever(FilePointer filePointer, File outputFile, CountDownLatch latch){
        this.filePointer = filePointer;
        this.latch = latch;
        this.outputFile = outputFile;
        authModel = new AuthorizationModel();
        authModel.setToken(filePointer.getToken());
        authModel.setOperation(filePointer.getOperation());
        authModel.setHash(filePointer.getHash());
    }

    @OnMessage
    public void onMessage(String s){
        logger.info("Received text... " + s);
    }

    @OnMessage
    public void onMessage(ByteBuffer message) {
        // Why does this never happen?
        logger.info("Received binary" + message);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        logger.info("Opened, sending auth message as text: " + gson.toJson(authModel));
        ByteBuffer buffer = ByteBuffer.allocate(gson.toJson(authModel).getBytes().length);
        buffer.put(gson.toJson(authModel).getBytes());
        try {
            session.getBasicRemote().sendBinary(buffer, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("sent: " + gson.toJson(authModel));
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("Closed Websocket: " + closeReason.getCloseCode() + " " + closeReason.getReasonPhrase());
        //latch.countDown();
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }
}
