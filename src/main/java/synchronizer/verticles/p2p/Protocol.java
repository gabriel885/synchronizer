package synchronizer.verticles.p2p;

import io.vertx.core.json.JsonObject;

// p2p file sync protocol
public interface Protocol {


    /**
     * broadcast action to all peers
     * @param action
     */
    void broadcastAction(JsonObject action);

}
