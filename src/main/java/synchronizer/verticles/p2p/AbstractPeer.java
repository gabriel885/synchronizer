package synchronizer.verticles.p2p;

import io.vertx.core.*;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;

/**
 * represent AbstractPeer in a P2P network
 */
public abstract class AbstractPeer extends AbstractVerticle{

    // logger
    private static final Logger logger = LogManager.getLogger(AbstractPeer.class);


    // TODO: WHAT TO DO WITH THIS??
    // InetAddress
    protected InetSocketAddress inetSocketAddress;

    // Consume file system actions from event bus
    private MessageConsumer<JsonObject> consumer;

    // Produce incoming actions
    private MessageConsumer<JsonObject> producer;

    // AbstractPeer configurations
    private PeerConfig config;

    public AbstractPeer(){

    }

    /**
     * Peer configurations
     * @param config
     */
    public AbstractPeer(PeerConfig config){
        this.config = new PeerConfig();
    }


//    /**
//     *  return true if peer exists in known peers, otherwise return false
//     * @param peerName - peer name to check if exists
//     * @return true if peer exists and it is know
//     */
//    protected boolean peerExists(String peerName){
//        if (netPeers.){
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * add new peer to peers collection but don't join
//     * @param peer
//     */
//    protected final void add(NetPeer peer){
//        this.peers.put(peer.getPeerName(),peer);
//    }
//
//    /**
//     * remove per from peer's collection
//     * @param peer
//     * @return
//     */
//    protected final NetPeer remove(NetPeer peer){
//        return this.peers.remove(peer.getPeerName());
//    }
//
//    /**
//     * remove peer from peer's collection
//     * @param peerName
//     * @return
//     */
//    protected final NetPeer remove(String peerName){
//        return this.peers.remove(peerName);
//    }
//    /**
//     *  return true if peer exists in known peers, otherwise return false
//     * @param peer - NetPeer to check if exists
//     * @return
//     */
//    public boolean peerExists(NetPeer peer){
//        return peerExists(peer.getPeerName());
//    }
//
//    /**
//     * AbstractPeer should return a list of all the peers he knows about
//     * @return
//     */
//    public Collection<NetPeer> getAllRegisteredPeers(){
//        return this.peers.values();
//    }
}
