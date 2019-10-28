package synchronizer.verticles.p2p;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import synchronizer.models.File;
import synchronizer.models.actions.Action;

import java.nio.file.Path;
import java.util.List;

// p2p file sync protocol
public interface Protocol {


    /**
     * broadcast an action to all peers
     * @param action
     * @return Future indicating if all peers connected successfully
     */
    Future<Action> broadcast(JsonObject action);

    // send action to peer

    /**
     * send an action to a particular peer
     * @param peer
     * @return ACK/NACK message from peer
     */
    Future<Action> broadcastPeer(NetPeer peer);

    /**
     * send file to specific peer
     * @param peer
     */
    void sendFile(NetPeer peer);

    // requesting all peers for a file in round robin (successor peer)
    Future<File> requestFile(Path path);

    // receiving all peerses actions
    // deploy local storage verticle to compensate differs and update local.path
    //receivePeersActions();

    // receive all peers requests for files
    // deploy sendFile() verticles
    //receivePeersReqeusts();

}
