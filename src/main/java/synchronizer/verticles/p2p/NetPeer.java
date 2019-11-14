package synchronizer.verticles.p2p;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// binds NetServer and NetClient together as a NetPeer to listen for incoming socket connections (server)
// and to connect to other peers (client)
public class NetPeer extends AbstractPeer {

    // logger
    private static final Logger logger = LogManager.getLogger(NetPeer.class);

    // peer name (host)
    private String peerName;

    // peer's port (default 2020)
    protected int peerPort;

    // peers to connect to
    // K: peer's host
    // V: peer model
    protected static HashMap<String,Peer> peers = new HashMap<>();

    // peers that are already connected (by host)
    protected static Set<String> connectedPeers = new HashSet<>();

    // tcp server
    protected final NetServer server;

    // tcp server options
    protected final NetServerOptions serverOptions;

    // tcp client
    protected final NetClient client;

    // tcp client options
    protected final NetClientOptions clientOptions;

    /**
     *
     * @param host - name of the peer
     * @param clientOptions - vertx net client options
     * @param serverOptions - vertx net server options
     */
    public NetPeer(String host, int port, Set<Peer> peers,  NetClientOptions clientOptions, NetServerOptions serverOptions) throws Exception{

        // validate peers in network
        for(Peer peer: peers){
            if (!validateHostAndPort(String.format("%s:%d", peer.getHost(), peer.getPort()))){
                throw new ApplicationFailure(String.format("Failed to validate peer %s:%d", host ,port));
            }
        }

        // validate current net peer
        if(!validateHostAndPort(String.format("%s:%d", host, port))){
            throw new ApplicationFailure(String.format("Failed to validate peer %s:%d", host ,port));
        }

        // assign other peers
        for (Peer peer: peers){
            this.peers.put(peer.getHost(),peer);
        }

        // assign current peer credentials
        this.peerName = host;
        this.peerPort = port;

        // set server host and peer (to allow incoming connections)
        this.serverOptions = serverOptions.setHost(host).setPort(port);
        this.clientOptions = clientOptions;

        Vertx vertx = Vertx.vertx();
        this.client = vertx.createNetClient(clientOptions);
        this.server = vertx.createNetServer(serverOptions);
    }


    /**
     * listen to all peers
     * @return reference to net server
     */
    protected final NetServer listen(){
        NetServer server = vertx.createNetServer(serverOptions);
        // dummy handler
        server.connectHandler(handler->{
            //
        });
        return server.listen();
    }


    /**
     * listen to all peers with specific handler
     * @param handler
     * @return
     */
    protected final NetServer listen(ActionHandler handler) {
        NetServer server = vertx.createNetServer(serverOptions);
        return server.connectHandler(handler).listen();
    }

    /**
     * connect listen handlers and listen on port 2020 for incoming connections
     * @param listenHandlers - server listening handlers
     * @return
     */
    protected final Future<Void> listen(Handlers listenHandlers){
        Future<Void> future = Future.future();
        NetServer server = vertx.createNetServer(serverOptions);

        // connect server handlers
        Iterator<ActionHandler> itr = listenHandlers.getHandlersIterator();
        while(itr.hasNext()){
            ActionHandler handler = itr.next();
            // deploy handler
            server.connectHandler(handler);
        }

        // listen on port
        server.listen(res->{
            // this is handler
            if (res.succeeded()){
                logger.debug(String.format("%s is listening for connections from %s", toString(), peers.keySet().toString()));
                future.complete();
            }
            else{
                logger.warn(String.format("%s failed to listen for connections", toString()));
                future.fail(res.cause());
            }
        });

        return future;
    }

    /**
     * connect a handler to a specific peer
     * @param handler
     */
    protected final NetClient connect(Peer peer, ActionHandler handler){
        NetClient client = vertx.createNetClient(clientOptions);
        return client.connect(peer.getPort(), peer.getHost(), handler);
    }
    /**
     * Connect all client handlers to all listening peers in the network
     * @param connectHandlers - client listening handlers
     * @return
     */
    protected final void connect(Handlers connectHandlers){
        // connecting to all peers in the network
        for (Peer peer: peers.values()){
            // register all connect handlers to client
            Iterator<ActionHandler> itr = connectHandlers.getHandlersIterator();
            while(itr.hasNext()){
                ActionHandler handler = itr.next();
                // connect handler
                this.client.connect(peer.getPort(), peer.getHost(), handler);
            }
        }
    }


    /**
     * @return net server
     */
    public NetServer getServer(){
        return server;
    }

    /**
     *
     * @return net client
     */
    public NetClient getClient(){
        return client;
    }

    /**
     * @return peer's name
     */
    public String getHost(){
        return this.peerName;
    }

    /**
     * @return return peer's port
     */
    public int getPort(){
        return this.peerPort;
    }

    /**
     * peer as string
     * @return
     */
    @Override
    public String toString(){
        return String.format("%s:%d",this.peerName, this.peerPort);
    }




}
