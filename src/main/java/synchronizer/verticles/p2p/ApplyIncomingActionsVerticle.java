package synchronizer.verticles.p2p;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.nio.ch.Net;
import synchronizer.models.EventBusAddress;
import synchronizer.models.File;
import synchronizer.models.actions.Action;
import synchronizer.models.actions.ActionType;
import synchronizer.models.actions.Nack;
import synchronizer.models.actions.RequestAction;
import synchronizer.models.diff.Checksum;

import java.nio.Buffer;
import java.nio.file.Paths;

// listen for server socket actions
// and publish to local event bus
// published as a verticle!
public class ApplyIncomingActionsVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(ApplyIncomingActionsVerticle.class);

    // consume file system actions from event bus
    private MessageProducer<JsonObject> producer;

    // event bus
    private EventBus eb;

    // event bus address to publish incoming actions
    private EventBusAddress incomingAddress;

    // peer the actions are coming from
    private TCPPeer tcpPeer;

    // event bus address to publish to
    public ApplyIncomingActionsVerticle(TCPPeer tcpPeer, EventBusAddress incomingAddress) {
        this.incomingAddress = incomingAddress;
        this.tcpPeer = tcpPeer;
    }

    @Override
    public void start(){
        this.eb = vertx.eventBus();
        // connect consumer
        this.producer = eb.publisher(incomingAddress.toString());

        // receive actions handler and produce to event bus
        this.tcpPeer.listen(handler->{
            if (handler instanceof NetSocket){
                NetSocket socket = (NetSocket) handler; // will fail on runtime if handler is not a net socket

                socket.handler(buffer->{
                    logger.info(String.format("received buffer %s from %s", buffer.toString(),socket.remoteAddress()));

                    // confirm message
                    JsonObject actionReceived =  buffer.toJsonObject();

                    // validate incoming json response
                    if (!Action.valid(actionReceived)){
                        logger.warn(String.format("Received unknown action %s from %s", buffer.toString(),socket.remoteAddress()));
                        socket.write(new Nack().bufferize());
                        return;
                    }

                    ActionType actionType = ActionType.getType(actionReceived.getString("type"));

                    File f = new File(actionReceived);
                    switch(actionType){
                        case CREATE:
                            // broadcast request action
                            this.tcpPeer.broadcastAction(new RequestAction(Paths.get(f.getFileName())));
                        case MODIFY: // request modification and pass to event bus
                            // this.tcpPeer.requestModification(Checksum.
                            // String oldChecksum = Checksum.checksum(Paths.get(f.getFileName()));
                            // this.tcpPeer.requestModification(f.getFileName(),oldChecksum);
                        case REQUEST: //
                            socket.sendFile(f.getFileName());
                            return;
                    }

                    this.producer.send(actionReceived);
                });
            }
        });
//        // receive file buffers
//        this.tcpPeer.listen(handler->{
//           if (handler instanceof NetSocket){
//               NetSocket socket = (NetSocket) handler; // will fail on runtime if handler is not a net socket
//               socket.handler(buffer->{
//                   logger.info(buffer);
//               });
//           }
//        });
    }

    @Override
    public void stop(){

    }
}
