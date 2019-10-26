package synchronizer.verticles.p2p;

import io.vertx.core.*;
import io.vertx.core.net.*;
import io.vertx.core.streams.ReadStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// binds NetServer and NetClient together as a NetPeer to listen for incoming socket connections (server)
// and to connect to other peers (client)
public class NetPeer extends AbstractPeer {

    // logger
    private static final Logger logger = LogManager.getLogger(NetPeer.class);

    // peer name (host)
    private String peerName;

    private final int peerPort;

    // validate peer name is unique
    private static Set<String> peerNames = new HashSet<>();

    // tcp server
    private NetServer server;

    // tcp client
    private NetClient client;

    // TODO: rethink if this should be here!
    // Consume outcoming file system actions from event bus and write to socket
    private EventBusAddress outcomingActions;

    // Produce incoming file system actions received from socket to event bus
    private EventBusAddress incomingActions;


    // default client-server options
    public NetPeer(String host, int port) throws Exception {
        this(host, port, new NetClientOptions(), new NetServerOptions());
    }

    /**
     *
     * @param host - name of the peer
     * @param clientOptions - vertx net client options
     * @param serverOptions - vertx net server options
     */
    public NetPeer(String host, int port,  NetClientOptions clientOptions, NetServerOptions serverOptions) throws Exception{
        // assert if peer name exists
        if (peerNames.contains(host)){
            logger.error(String.format("AbstractPeer name %s already exists", host));
        }

        // TODO: Validate host and port!
        // assert peer name is not empty
        if (!host.isEmpty()){
            this.peerName = host;
        }
        this.peerPort = port;

        // for debugging purposes
        new InetSocketAddress(host,port);


        Vertx vertx = Vertx.vertx();
        this.client = vertx.createNetClient(clientOptions);
        this.server = vertx.createNetServer(serverOptions);
        logger.info(String.format("Added peer %s:%d to network",host,port));
    }

    /**
     * Listen on port
     * @param host
     * @param port
     * @param listenHandlers - list of handlers to connect the server on listen
     */
    protected final Future<Void> listen(String host, int port, List<Handler<NetSocket>> listenHandlers){
        Future<Void> future = Future.future();
        this.server.listen(port, host, res->{
            if (res.succeeded()){
                // connect server handlers
                for(Handler<NetSocket> handler: listenHandlers){
                    this.server.connectHandler(handler);
                }
                future.complete();
            }
            // listening on port failed
            else{
                future.fail(res.cause());
            }
        });
        return future;
    }

    /**
     * Connect to peer with host and port
     * @param host - peer's host to connect to
     * @param port - peer's port to connect to
     * @return
     */
    protected final Future<Void> connect(String host, int port, List<Handler<AsyncResult<NetSocket>>> connectHandlers){
        Future <Void> future = Future.future();
        this.client.connect(port, host, res ->{
            // initial connect
            if (res.succeeded()){
                // connect client handlers
                for (Handler<AsyncResult<NetSocket>> handler:
                     connectHandlers) {
                    this.client.connect(port,host,handler);
                }
                future.complete();
            }
            else{
                future.fail(res.cause());
            }
        });
        return future;
    }

    /**
     * Join a single peer to the known peers network.
     * Connecting and listening to that peer.
     * @param p
     * @return
     */
    public final Future<Void> join(NetPeer p){
        Future future = Future.future();
        // add peer to known peer network

        future.complete();
        // establish connection with that peer

        return future;
    }

    /**
     * join all existing peers to network
     * @return
     */
    public final Future<Void> joinAll(){
        // all peers futures
        List<Future> futures = new ArrayList<>();
        for (NetPeer peer: this.peers.values()){
            futures.add(this.join(peer));

        }
        Future future = Future.future();
        // ensure all futures succeded
        CompositeFuture.all(futures).setHandler(done -> {
            if (done.succeeded()) {
                future.complete();
            } else {
                future.fail(done.cause());
            }
        });

        return future;
    }

    /**
     * closes peer's client-server socket connections
     * @return
     */
    protected final Future<Void> close(){
        Future<Void> serverFuture = Future.future();

        // closing server socket
        this.server.close(res -> {
            if (res.succeeded()){
                serverFuture.complete();
            }
            else{
                serverFuture.fail(res.cause());
            }
        });
        // closing client socket
        this.client.close();
        return serverFuture;
    }

    /**
     *
     * @param peerName - peer to disconnect from the server
     * @return
     */
    public final void disconnect(String peerName){
        if (peerExists(peerName)){
            remove(peerName);

            NetPeer tempPeer = this.peers.remove(peerName);
            // close peer connections
            tempPeer.close();
        }
    }

    /**
     * return peer name
     * @return
     */
    public String getPeerName(){
        return this.peerName;
    }

    @Override

    /**
     * when peer dies remove him from static peerNames collection
     */
    public void finalize(){
        // remove peer from peerNames
        this.peerNames.remove(this.peerName);
    }

}
