package synchronizer.verticles.p2p;

import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServerOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.Peer;
import synchronizer.verticles.p2p.handlers.Handlers;
import synchronizer.verticles.p2p.handlers.LogActionHandler;
import synchronizer.verticles.p2p.handlers.SendActionHandler;

import java.util.Set;


/**
 * TCP AbstractPeer
 * Connectivity
 * Instability
 * Message routing
 *
 */
// implements Protocol
public class TCPPeer extends NetPeer implements Protocol{

    // logger
    private static final Logger logger = LogManager.getLogger(TCPPeer.class);

    // server handlers (send actions to other peers)
    // default handler must be registered - registering dummy handler
    private Handlers serverHandlers = new Handlers(new LogActionHandler());

    // client handlers (receive actions from other peers)
    // default handler must be registered - registering dummy handler
    private Handlers clientHandlers = new Handlers(event -> {
        // dummy handler
    });

    /**
     * initialize tcp peer
     * @param hostname - peer's host
     * @param port - peer's port it's listening for connections on
     * @param peers - other peers to connect to
     * @throws Exception
     */
    public TCPPeer(String hostname, int port, Set<Peer> peers) throws Exception{
        // default net server and client options
        this(hostname, port, peers,  new NetClientOptions(), new NetServerOptions());
    }

    /**
     * initialize tcp peer with net client options
     * @param hostname
     * @param port
     * @param peers
     * @param clientOptions
     * @throws Exception
     */
    public TCPPeer(String hostname, int port, Set<Peer> peers, NetClientOptions clientOptions) throws Exception {
        this(hostname, port, peers, clientOptions,new NetServerOptions());
    }

    /**
     * initialize tcp peer with net server options
     * @param hostname
     * @param port
     * @param peers
     * @param serverOptions
     * @throws Exception
     */
    public TCPPeer(String hostname, int port, Set<Peer> peers, NetServerOptions serverOptions) throws Exception {
        this(hostname, port, peers, new NetClientOptions(), serverOptions);
    }

    /**
     * NOTE: client-server handlers can be added only before the verticle is deployed!
     * initialize tcp peer with net client and server options
     * @param hostname
     * @param port
     * @param peers
     * @param clientOptions
     * @param serverOptions
     * @throws Exception
     */
    public TCPPeer(String hostname, int port, Set<Peer> peers, NetClientOptions clientOptions, NetServerOptions serverOptions) throws Exception{
        super(hostname, port, peers, clientOptions, serverOptions);
    }

    // TODO: add promise - if something failed re-try on synchronizer.verticles.p2p application leve
    @Override
    public void start(){

        // connect to peers in network
        listen(serverHandlers);
        connect(clientHandlers);

        // log all peers we are listening to
        logger.info(String.format("%s is deployed", this.getHost()));
    }


    @Override
    public void stop(){
        // close tcp server and client
        this.server.close();
        this.client.close();

        logger.debug(String.format("%s is undeployed", this.getHost()));
    }

    /**
     * broadcast all peers an action
     * @param action - json action to transmit
     * @return - ACK/NACK action object
     */
    public void broadcastAction(JsonObject action) {
        logger.info(String.format("broadcasting to all peers %s",action.toString()));
        for (String peerName: peers.keySet()){
            Peer peer = peers.get(peerName);
            connect(peer,new SendActionHandler(action));
        }
    }

    /**
     * send an action to specific peer
     * @param peer
     * @param action
     */
    public void sendAction(Peer peer, JsonObject action){
        connect(peer,new SendActionHandler(action));
    }

}
