package synchronizer.verticles.p2p;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;
import synchronizer.models.Peer;
import synchronizer.models.actions.Ack;
import synchronizer.models.actions.ActionType;
import synchronizer.models.actions.ResponseAction;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// listen for server socket actions
// and publish to local event bus
// published as a verticle!
public class ApplyIncomingActionsVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(ApplyIncomingActionsVerticle.class);

    // monitorable path
    private final Path path;
    // event bus address to publish incoming actions
    private final EventBusAddress incomingAddress;
    // peer the actions are coming from
    private final TCPPeer tcpPeer;
    // consume file system actions from event bus
    private MessageProducer<JsonObject> producer;
    // max number of retries sending to the event bus
    private int MAX_RETRY = 5;
    // attempts made sending to the event bus
    private int attempts = 0;


    // event bus address to publish to
    public ApplyIncomingActionsVerticle(Path path, TCPPeer tcpPeer, EventBusAddress incomingAddress) {
        this.incomingAddress = incomingAddress;
        this.tcpPeer = tcpPeer;
        this.path = path;
    }

    @Override
    public void start() {
        // event bus
        EventBus eb = vertx.eventBus();

        // connect consumer
        this.producer = eb.publisher(incomingAddress.toString());

        // receive actions handler and produce to event bus
        this.tcpPeer.listen(handler -> {
            if (handler instanceof NetSocket) {
                NetSocket socket = (NetSocket) handler; // will fail on runtime if handler is not a net socket

                socket.handler(buffer -> {
                    // ignore empty buffers
                    if (buffer == null || buffer.toString().isEmpty()) {
                        return;
                    }
                    // confirm buffer
                    socket.write(new Ack().bufferize());

                    logger.info(String.format("received buffer %s from %s", buffer.toString(), socket.remoteAddress()));
                    // confirm message
                    JsonObject actionReceived = buffer.toJsonObject();

                    // don't block handler
                    handleAction(new Peer(socket.remoteAddress().host(), this.tcpPeer.peerPort), actionReceived);

                });
            }
        });
    }

    private void handleAction(Peer origin, JsonObject actionReceived) {

        // Append action's path to relative monitorable path
        actionReceived = absolutizePath(actionReceived);

        ActionType actionType = ActionType.getType(actionReceived.getString("type"));

        //File f = new File(actionReceived);

        if (actionType == ActionType.REQUEST) {
            logger.info("getting! response!!!!");
            // send response
            if (actionReceived.containsKey("path")) {
                String path = actionReceived.getString("path");

                vertx.fileSystem().readFile(path, handler -> {
                    if (handler.succeeded()) {
                        Buffer requestedBuffer = handler.result();
                        boolean isDir = Files.isDirectory(Paths.get(path));
                        JsonObject actionResponse = new JsonObject(new ResponseAction(Paths.get(path), isDir, requestedBuffer).toJson());
                        logger.info("Sending response %s", actionResponse.toString());
                        this.tcpPeer.sendAction(origin, actionResponse);
                        logger.info(String.format("Sent response %s  %s", actionResponse.toString(), origin.getHost()));
                    }
                });
            }
        }
        this.producer.send(actionReceived, handler->{
            // if responded with nack - request action
            if (handler.succeeded() && (handler.result().body() instanceof String)){
                String stringJsonResponse = (String) handler.result().body();
                JsonObject jsonResponse = new JsonObject(stringJsonResponse);
                ActionType responseActionType = ActionType.getType(jsonResponse.getString("type"));
                // check action type
                // if nack received send peer nack on message
                if (responseActionType == ActionType.NACK){
                //    this.tcpPeer.sendAction(origin,new JsonObject(new Nack().toJson()));
                    logger.info("received invalid message !");
                }

            }

        });
    }

    /**
     * searches for "path" key in json object and strips absolute path from monitorable path
     *
     * @param action - jsonObject action
     * @return
     */
    private JsonObject absolutizePath(JsonObject action) {
        // type safety
        if (action == null) {
            return null;
        }
        // check key
        if (!action.containsKey("path")) {
            return action;
        }

        String relativeFile = action.getString("path");
        Path absoluteFile = Paths.get(this.path.toString(), relativeFile);
        action.put("path", absoluteFile.toString());
        return action;
    }

    @Override
    public void stop() {

    }
}
