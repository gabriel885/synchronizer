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
import synchronizer.models.File;
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
    private Path path;

    // consume file system actions from event bus
    private MessageProducer<JsonObject> producer;

    // event bus
    private EventBus eb;

    // event bus address to publish incoming actions
    private EventBusAddress incomingAddress;

    // max number of retries sending to the event bus
    private int MAX_RETRY=5;

    // attempts made sending to the event bus
    private int attempts=0;

    // peer the actions are coming from
    private TCPPeer tcpPeer;


    // event bus address to publish to
    public ApplyIncomingActionsVerticle(Path path,TCPPeer tcpPeer, EventBusAddress incomingAddress) {
        this.incomingAddress = incomingAddress;
        this.tcpPeer = tcpPeer;
        this.path = path;
    }

    @Override
    public void start(){
        this.eb = vertx.eventBus();
        // connect consumer
        this.producer = eb.publisher(incomingAddress.toString());

//        this.tcpPeer.listen(new ReceiveActionHandler(this.path,vertx.fileSystem(),this.producer));

        // receive actions handler and produce to event bus
        this.tcpPeer.listen(handler->{
            if (handler instanceof NetSocket ){
                NetSocket socket = (NetSocket) handler; // will fail on runtime if handler is not a net socket

                socket.handler(buffer->{
                    // ignore empty buffers
                    if (buffer == null || buffer.toString().isEmpty()){
                        return;
                    }
                    // confirm buffer
                    socket.write(new Ack().bufferize());
                    logger.info(String.format("received buffer %s from %s", buffer.toString(),socket.remoteAddress()));
                    // confirm message
                    JsonObject actionReceived =  buffer.toJsonObject();
                    // don't block handler
                    handleAction(new Peer(socket.remoteAddress().host(),this.tcpPeer.peerPort),actionReceived);
                });
            }
        });
    }

    private void handleAction(Peer origin, JsonObject actionReceived){

        // Append action's path to relative monitorable path
        actionReceived = absolutizePath(actionReceived);


        ActionType actionType = ActionType.getType(actionReceived.getString("type"));

        File f = new File(actionReceived);

        switch(actionType){
            case REQUEST:
                // send response
                Buffer requestedBuffer = vertx.fileSystem().readFileBlocking(f.getFileName());
                boolean isDir = Files.isDirectory(Paths.get(f.getFileName()));
                JsonObject actionResponse = new JsonObject(new ResponseAction(Paths.get(f.getFileName()),isDir,requestedBuffer).toJson());
                this.tcpPeer.sendAction(origin,actionResponse);
                logger.info(String.format("Sent response %s  %s", actionResponse.toString(),origin.getHost()));
                return;
            default:
                break;
        }
        this.producer.send(actionReceived);
    }
    /**
     * searches for "path" key in json object and strips absolute path from monitorable path
     * @param action
     * @return
     */
    private JsonObject absolutizePath(JsonObject action){
        if (action==null || !action.containsKey("path")){
            return null;
        }
        String relativeFile = action.getString("path");
        Path absoluteFile = Paths.get(this.path.toString(),relativeFile);
        action.put("path",absoluteFile.toString());
        return action;
    }

    @Override
    public void stop(){

    }
}
