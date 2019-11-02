package synchronizer.verticles.p2p;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;
import synchronizer.models.actions.Ack;
import synchronizer.models.actions.Action;
import synchronizer.models.actions.ActionType;
import synchronizer.verticles.p2p.handlers.HandlerVerticle;

import java.util.Iterator;

// listen for local event bus outcoming actions
// and broadcast according to the action types to
// all peers in the network
// This class is a handler and a verticle
public class PublishOutcomingEventsVerticle extends HandlerVerticle<NetSocket> {

    // logger
    private static final Logger logger = LogManager.getLogger(PublishOutcomingEventsVerticle.class);

    // consume file system actions from event bus
    private MessageConsumer<JsonObject> consumer;

    // event bus outcoming address
    private EventBusAddress outcomingAddress;

    // local peer to broadcast from
    private TCPPeer tcpPeer;

    /**
     *
     * @param outcomingAddress - event bus address of outcoming events address
     */
    public PublishOutcomingEventsVerticle(TCPPeer tcpPeer, EventBusAddress outcomingAddress) {
        this.outcomingAddress = outcomingAddress;
        this.tcpPeer = tcpPeer;
    }

    @Override
    public void start(){

        EventBus eb = vertx.eventBus();

        // connect consumer
        this.consumer = eb.consumer(outcomingAddress.toString(), actionReceived->{
            // confirm message
            actionReceived.reply(new Ack());

           ActionType actionType = ActionType.getType(actionReceived.body().getString("type"));

            switch (actionType){
                case DELETE:
                    //event.result().write(new DeleteAction());
                    logger.info("broadcasting delete action to all peers");
                    // tcpPeer.broadcast(new DeleteAction())
                    break;
                case CREATE:
                    logger.info("sending file to all peers");
                    break;
                case MODIFY:
                    logger.info("sending delete file action and sending file agaiin");
                    break;
                case RENAME:
                    logger.info("sending rename action to all peers");
                    break;
                default:
                    logger.warn(String.format("%s received unknown action type from message: %s",this.tcpPeer.getHost(),actionReceived.toString()));
                    break;
            }
        });
    }

    @Override
    public void stop(){

    }

    @Override
    public void handle(NetSocket event) {

    }
}
