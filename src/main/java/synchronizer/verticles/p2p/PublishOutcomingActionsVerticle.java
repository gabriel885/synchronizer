package synchronizer.verticles.p2p;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;
import synchronizer.models.actions.Ack;
import synchronizer.models.actions.ActionType;

import java.nio.file.Path;
import java.nio.file.Paths;

// listen for local event bus outcoming actions
// and broadcast according to the action types to
// all peers in the network
// This class is a handler and a verticle
public class PublishOutcomingActionsVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(PublishOutcomingActionsVerticle.class);

    // local monitorable path
    private Path path;

    // event bus outcoming address
    private EventBusAddress outcomingAddress;

    // local peer to broadcast from
    private TCPPeer tcpPeer;


    /**
     * @param outcomingAddress - event bus address of outcoming events address
     */
    public PublishOutcomingActionsVerticle(Path path, TCPPeer tcpPeer, EventBusAddress outcomingAddress) {
        this.outcomingAddress = outcomingAddress;
        this.tcpPeer = tcpPeer;
        this.path = path;

        Buffer buffer = Buffer.buffer();
    }

    @Override
    public void start() {

        // event bus
        EventBus eb = vertx.eventBus();

        // consume file system actions from event bus
        MessageConsumer<JsonObject> consumer = eb.consumer(outcomingAddress.toString(), actionReceived -> {

            // confirm message
            actionReceived.reply(new Ack());

            ActionType actionType = ActionType.getType(actionReceived.body().getString("type"));

            // temp action object for further modifications
            JsonObject action;

            switch (actionType) {
                case DELETE:
                    //event.result().write(new DeleteAction());
                    logger.debug("broadcasting delete action");
                    action = actionReceived.body();
                    // broadcast only relative path!!
                    tcpPeer.broadcastAction(relativizePath(action));
                    break;
                case CREATE:
                    logger.debug("broadcasting create action");
                    action = actionReceived.body();
                    // broadcast only relative path!!
                    tcpPeer.broadcastAction(relativizePath(action));
                    break;
                case MODIFY:
                    logger.debug("broadcasting modify action");
                    action = actionReceived.body();
                    // broadcast only relative path!!
                    tcpPeer.broadcastAction(relativizePath(action));
                    break;
                case REQUEST:
                    logger.info("broadcasting request action");
                    action = actionReceived.body();
                    // broadcast only relative path!!
                    tcpPeer.broadcastAction(relativizePath(action));
                    break;
                default:
                    logger.debug(String.format("%s received unknown action type from message: %s", this.tcpPeer.getHost(), actionReceived.body().toString()));
                    break;
            }
        });
    }

    /**
     * searches for "path" key in json object and strips absolute path from monitorable path
     *
     * @param action
     * @return
     */
    private JsonObject relativizePath(JsonObject action) {
        if (action.getString("path") == null || Paths.get(action.getString("path")) == null) {
            return null;
        }
        Path relativeFile = this.path.relativize(Paths.get(action.getString("path")));
        action.put("path", relativeFile.toString());
        return action;
    }

    @Override
    public void stop() {

    }
}
