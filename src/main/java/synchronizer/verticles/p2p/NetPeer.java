package synchronizer.verticles.p2p;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.exceptions.ApplicationFailure;
import synchronizer.models.Peer;
import synchronizer.verticles.p2p.handlers.ActionHandler;
import synchronizer.verticles.p2p.handlers.Handlers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

// binds NetServer and NetClient together as a NetPeer to listen for incoming socket connections (server)
// and to connect to other peers (client)
class NetPeer extends AbstractVerticle {

    // peers to connect to
    // K: peer's host
    // V: peer model
    static final HashMap<String, Peer> peers = new HashMap<>();
    // logger
    private static final Logger logger = LogManager.getLogger(NetPeer.class);
    // client connection timeout
    private static final int connectionTimeout = 3000;
    // tcp server
    protected  NetServer server;
    // tcp client
    protected  NetClient client;
    // tcp server options
    protected final NetServerOptions serverOptions;
    // tcp client options
    protected final NetClientOptions clientOptions;
    // peer's port (default 2020)
    final int peerPort;
    // peer name (host)
    final String peerName;

    /**
     * @param host          - name of the peer
     * @param clientOptions - vertx net client options
     * @param serverOptions - vertx net server options
     */
    NetPeer(String host, int port, Set<Peer> peers, NetClientOptions clientOptions, NetServerOptions serverOptions) throws Exception {

        // validate peers in network
        for (Peer peer : peers) {
            if (validateHostAndPort(String.format("%s:%d", peer.getHost(), peer.getPort()))) {
                throw new ApplicationFailure(String.format("Failed to validate peer %s:%d", host, port));
            }
        }

        // validate current net peer
        if (validateHostAndPort(String.format("%s:%d", host, port))) {
            throw new ApplicationFailure(String.format("Failed to validate peer %s:%d", host, port));
        }

        // assign other peers
        for (Peer peer : peers) {
            NetPeer.peers.put(peer.getHost(), peer);
        }

        // assign current peer credentials
        this.peerName = host;
        this.peerPort = port;

        // set server host and peer (to allow incoming connections)
        // log server activity
        this.serverOptions = serverOptions.setHost(host).setPort(port).setLogActivity(true);
        this.clientOptions = clientOptions.setConnectTimeout(connectionTimeout);

    }

    /**
     * validate host and port
     *
     * @param string - representing a host and a port (e.g 172.100.100.2:2020)
     * @return true if the host and the port are in correct format
     */
    private static boolean validateHostAndPort(String string) {
        if (string==null || string.isEmpty()){
            return false;
        }
        try {
            // WORKAROUND: add any scheme to make the resulting URI isValid.
            URI uri = new URI("my://" + string); // may throw URISyntaxException
            if (uri.getHost() == null || uri.getPort() == -1) {
                throw new URISyntaxException(uri.toString(),
                        "URI must have host and port parts");
            }
            return false;

        } catch (URISyntaxException ex) {
            // validation failed
            return true;
        }
    }

    /**
     * listen to all peers with specific handler
     *
     * @param handler
     * @return
     */
    final NetServer listen(ActionHandler handler) {
        NetServer server = vertx.createNetServer(serverOptions);
        //noinspection unchecked
        return server.connectHandler(handler).exceptionHandler(t -> {
            logger.warn(t);
        }).listen(v -> { // listening succeeded
            if (v.succeeded()) {
                logger.info(String.format("%s is listening to all connections on port %s",this.peerName ,v.result().actualPort()));
            } else { // listening on port failed
                logger.warn(v.cause());
            }
        });
    }

    /**
     * connect multiple listening handlers to single server instance and listen on port 2020 for incoming connections
     *
     ** NOTE - server cannot assign a handler after listening, therefore for new handlers
     *       a new server instance will be created, afterwards all the handlers will be
     *       registered and the server will be listening (with serverOptions)
     * @param listenHandlers - server listening handlers
     * @return NetServer instance for fluent api
     */
    final NetServer listen(Handlers listenHandlers) {
        Future<Void> future = Future.future();

        NetServer server = vertx.createNetServer(serverOptions);

        // connect server handlers
        Iterator<ActionHandler> itr = listenHandlers.getHandlersIterator();
        while (itr.hasNext()) {
            ActionHandler handler = itr.next();
            // connect handler
            // TODO: check type assignment
            server.connectHandler(handler);
        }

        // listen on port
        server.listen(res -> {
            // this is handler
            if (res.succeeded()) {
                logger.debug(String.format("%s is listening for connections from %s", toString(), peers.keySet().toString()));
                future.complete();
            } else {
                logger.warn(String.format("%s failed to listen for connections", toString()));
                future.fail(res.cause());
            }
        });

        return server;
    }

    /**
     * connect a handler to a specific peer
     * NOTE - client cannot asssign a handler after connection, therefore for new handlers
     * a new client instance will be created
     * @param handler
     */
    final NetClient connect(Peer peer, ActionHandler handler) {
        NetClient client = vertx.createNetClient(clientOptions);
        logger.info(String.format("connecting to peer: %s", peer.toString()));
        return client.connect(peer.getPort(), peer.getHost(), handler);
    }

    /**
     * Connect all client handlers to all listening peers in the network
     *
     * @param connectHandlers - client listening handlers
     * @return
     */
    final void connect(Handlers connectHandlers) {
        // connecting to all peers in the network
        for (Peer peer : peers.values()) {
            // register all connect handlers to client
            Iterator<ActionHandler> itr = connectHandlers.getHandlersIterator();
            while (itr.hasNext()) {
                ActionHandler handler = itr.next();
                // connect handler
                this.client.connect(peer.getPort(), peer.getHost(), handler);
            }
        }
    }

    /**
     * @return net server
     */
    public NetServer getServer() {
        return server;
    }

    /**
     * @return net client
     */
    public NetClient getClient() {
        return client;
    }

    /**
     * @return peer's name
     */
    public String getHost() {
        return this.peerName;
    }

    /**
     * @return return peer's port
     */
    public int getPort() {
        return this.peerPort;
    }

    /**
     * peer as string
     *
     * @return
     */
    @Override
    public String toString() {
        return String.format("%s:%d", this.peerName, this.peerPort);
    }


}
