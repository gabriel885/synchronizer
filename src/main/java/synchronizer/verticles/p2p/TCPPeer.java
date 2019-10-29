package synchronizer.verticles.p2p;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServerOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.File;
import synchronizer.models.Peer;
import synchronizer.models.actions.Action;
import synchronizer.verticles.p2p.handlers.LogHandler;
import synchronizer.verticles.p2p.handlers.client.ClientHandlers;
import synchronizer.verticles.p2p.handlers.server.ServerHandlers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * TCP AbstractPeer
 * Connectivity
 * Instability
 * Message routing
 *
 */
public class TCPPeer extends NetPeer implements Protocol {

    // logger
    private static final Logger logger = LogManager.getLogger(TCPPeer.class);

    // server handlers (send actions to other peers)
    private ServerHandlers serverHandlers = new ServerHandlers(new LogHandler());

    // client handlers (receive actions from other peers)
    private ClientHandlers clientHandlers = new ClientHandlers(new LogHandler());

    // all instantiated tcp peers in application
    private static List<TCPPeer> tcpPeers = new ArrayList<>();


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

    @Override
    public void start(){

        listen(serverHandlers);

        connect(clientHandlers);

        logger.info(String.format("Peer %s is deployed", this.getHost()));
    }


    @Override
    public void stop(){
        // automatically closes all servers and clients that where created on start
        logger.info(String.format("Peer %s is undeployed", this.getHost()));

    }

    /**
     * broadcast all peers an action
     * @param action
     * @return - ACK/NACK action object
     */
    @Override
    public Future<Action> broadcast(JsonObject action) {
        // composite future to all peers
        // CompositeFuture.all()

        return null;
    }

    @Override
    public Future<Action> broadcastPeer(NetPeer peer) {
        return null;
    }

    @Override
    public void sendFile(NetPeer peer) {

    }

    @Override
    public Future<File> requestFile(Path path) {
        return null;
    }


}
