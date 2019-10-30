package synchronizer.verticles.p2p.handlers;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetSocket;
import synchronizer.models.EventBusAddress;
import synchronizer.models.actions.Action;
import synchronizer.verticles.p2p.handlers.ActionHandler;

// listen for socket's action events
// and transmit locally to event bus for storage application
// to execute actions locally
public class ClientActionsHandler extends AbstractVerticle implements ActionHandler {


    // client consume actions from other peers in the p2p network
    public ClientActionsHandler(EventBusAddress addressToListen){
        // deploy event bus listening verticle
    }

    // client
    @Override
    public void handle(NetSocket event) {
        // on received action, transmit to event bus
       // even.
    }
}
