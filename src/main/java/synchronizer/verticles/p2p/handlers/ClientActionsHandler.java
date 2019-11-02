package synchronizer.verticles.p2p.handlers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import synchronizer.models.EventBusAddress;
import synchronizer.models.actions.Action;
import synchronizer.verticles.p2p.handlers.ActionHandler;

import java.nio.Buffer;

// listen for local event bus outcoming action
// events and send to all peers!
public class ClientActionsHandler extends AbstractVerticle implements ActionHandler<NetSocket> {

    // consume file system actions from event bus
    private MessageConsumer<JsonObject> consumer;

    public ClientActionsHandler(EventBusAddress incomingActions){
        // listen for event bus actions
        EventBus eb = Vertx.vertx().eventBus();
        this.consumer = eb.consumer(incomingActions.toString());

        this.consumer.handler(message->{

            // write message to peers?
        });
    }

    // client
    @Override
    public void handle(NetSocket event) {
        // on received action, transmit to event bus
       // even.
    }
}
