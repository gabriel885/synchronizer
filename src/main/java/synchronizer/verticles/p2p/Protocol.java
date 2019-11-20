package synchronizer.verticles.p2p;

import io.vertx.core.json.JsonObject;
import synchronizer.models.Peer;

// file sync protocol
interface Protocol {

    /**
     * broadcast action to all peers
     *
     * @param action
     */
    void broadcastAction(JsonObject action);

    /**
     * send action to specific peer
     *
     * @param peer
     * @param action
     * @return
     */
    void sendAction(Peer peer, JsonObject action);

}
