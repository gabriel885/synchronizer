package synchronizer.verticles.p2p;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;

// listen for server socket actions
// and publish to local event bus
// published as a verticle!
public class ApplyIncomingEventsVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(ApplyIncomingEventsVerticle.class);

    // consume file system actions from event bus
    private MessageProducer<JsonObject> producer;

    // event bus
    private EventBus eb;

    // event bus address to publish incoming actions
    private EventBusAddress incomingAddress;

    // peer the actions are coming from
    private TCPPeer tcpPeer;

    // event bus address to publish to
    public ApplyIncomingEventsVerticle(TCPPeer tcpPeer, EventBusAddress incomingAddress) {
        this.incomingAddress = incomingAddress;
        this.tcpPeer = tcpPeer;
    }

    @Override
    public void start(){
        this.eb = vertx.eventBus();
        // connect consumer
        this.producer = eb.publisher(incomingAddress.toString());

        // listen for tcpPeer actions
        // create JsonObject actions and publish to event bus "incoming.actions"
        // update global.path shared data map
    }

    @Override
    public void stop(){

    }
}
