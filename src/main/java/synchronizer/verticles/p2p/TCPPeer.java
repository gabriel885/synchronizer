package synchronizer.verticles.p2p;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.rmi.log.LogHandler;
import synchronizer.models.EventBusAddress;
import synchronizer.models.File;
import synchronizer.models.Peer;
import synchronizer.models.actions.Action;
import synchronizer.verticles.p2p.handlers.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * TCP AbstractPeer
 * Connectivity
 * Instability
 * Message routing
 *
 */
// implements Protocol
public class TCPPeer extends NetPeer{

    // logger
    private static final Logger logger = LogManager.getLogger(TCPPeer.class);

    // server handlers (send actions to other peers)
    // default handler must be registered - registering dummy handler
    private Handlers serverHandlers = new Handlers(event -> {
        // dummy handler
    });

    // client handlers (receive actions from other peers)
    // default handler must be registered - registering dummy handler
    private Handlers clientHandlers = new Handlers(event -> {
        // dummy handler
    });

    // true when tcp peer is deployed
    private boolean running = false;

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

    /**
     * add client handlers
     * @param handler
     */
    public void addClientHandler(ActionHandler handler){
        if (!running){
            this.clientHandlers.add(handler);
        }
        else{
            logger.info("Failed to add clienr handler %s, client already connected...", handler.getClass());
        }
    }

    /**
     * add server handlers
     * @param handler
     */
    public void addServerHandler(ActionHandler handler){
        if (!running){
            this.serverHandlers.add(handler);
        }
        else{
            logger.info("Failed to add server handler %s, server already listening...", handler.getClass());
        }
    }


    @Override
    public void start(){

        running = true;

        // deploy all deployable handlers
        // connect to peers in network
        listen(serverHandlers);
        connect(clientHandlers);

        logger.info(String.format("%s is deployed", this.getHost()));
    }


    @Override
    public void stop(){
        // close tcp server and client
        this.server.close();
        this.client.close();

        logger.info(String.format("%s is undeployed", this.getHost()));
    }

    /**
     * broadcast all peers an action
     * @param action
     * @return - ACK/NACK action object
     */

    public Future<Action> broadcast(Action action) {
        // futures of all peers
        List<Future> peersFutures = new ArrayList<>();

        //
        for (String peerName: connectedPeers){
            Peer peer = peers.get(peerName);
            // TODO: CREATE tcpPeer with broadcast client-server net options
            // TODO: and connect broadcast handlers?
           // connect(new SendActionHander(action.Bufferize()));
        }

       // CompositeFuture.all]

        return null;
    }

    public Future<Action> broadcastPeer(NetPeer peer) {
        return null;
    }

    public void sendFile(NetPeer peer) {

    }

    /**
     * request file from peers using round robin algorithm
     * @param path
     * @return
     */
    public Future<File> requestFile(Path path) {

        return null;
    }


}
