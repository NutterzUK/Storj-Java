package storj.io.client.websockets;

import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import storj.io.restclient.model.FilePointer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
 * Created by steve on 12/07/2016.
 */
public class WebsocketFileRetriever extends WebSocketClient {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Gson gson = new Gson();
    private FilePointer filePointer;
    private File outputFile;
    private AuthorizationModel authModel;
    private FileChannel channel;
    private CountDownLatch latch;

    public WebsocketFileRetriever(URI serverURI, FilePointer filePointer, File outputFile, CountDownLatch latch){
        super(serverURI, new Draft_17(), null, 99999);
        this.filePointer = filePointer;
        this.outputFile = outputFile;
        authModel = new AuthorizationModel();
        authModel.setToken(filePointer.getToken());
        authModel.setOperation(filePointer.getOperation());
        authModel.setHash(filePointer.getHash());
        this.latch = latch;
    }

    public void onOpen(ServerHandshake serverHandshake) {
        logger.info("Connected to farmer.");
        ByteBuffer buffer = ByteBuffer.allocate(gson.toJson(authModel).getBytes().length);
        buffer.put(gson.toJson(authModel).getBytes());
        send(gson.toJson(authModel));

        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
            channel = new FileOutputStream(outputFile, true).getChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(String s){
        logger.info("Received text... " + s);
    }

    @Override
    public void onMessage(ByteBuffer b){
        logger.info("Received binary... " + b);
        try {
            channel.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        logger.info("Closing connection. " + i + s + b);
        try {
            channel.close();
            latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onError(Exception e) {
        e.printStackTrace();
    }

}
