package synchronizer.verticles.p2p;

import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.*;
import io.vertx.core.streams.ReadStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;
import synchronizer.models.File;
import synchronizer.models.actions.Action;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.*;

// binds NetServer and NetClient together as a NetPeer to listen for incoming socket connections (server)
// and to connect to other peers (client)
public class NetPeer extends AbstractPeer {

    // logger
    private static final Logger logger = LogManager.getLogger(NetPeer.class);

    // peer name (host)
    private String peerName;

    // peer's port (default 2020)
    private int peerPort;

    // all created net peers
    private static List<NetPeer> netPeers = new ArrayList<>();

    // validate peer name is unique
    private static Set<String> peerNames = new HashSet<>();

    // peers that connect to all other peers
    // as soon as 2 peers are listening and connecting to each other they are
    // added to set
    private static Set<String> allConnectedPeers = new HashSet<>();

    // tcp server
    private NetServer server;

    // tcp client
    private NetClient client;


    // default client-server options
    public NetPeer(String host, int port) throws Exception {
        this(host, port, new NetClientOptions(), new NetServerOptions());
    }

    public NetPeer(String host, int port, NetClientOptions clientOptions) throws Exception {
        this(host, port, clientOptions, new NetServerOptions());
    }

    public NetPeer(String host, int port, NetServerOptions serverOptions) throws Exception {
        this(host, port, new NetClientOptions(), serverOptions);
    }


    /**
     *
     * @param host - name of the peer
     * @param clientOptions - vertx net client options
     * @param serverOptions - vertx net server options
     */
    public NetPeer(String host, int port,  NetClientOptions clientOptions, NetServerOptions serverOptions) throws Exception{

        // add this instance to all netPeers
        netPeers.add(this);

        // assert if peer name exists
        if (peerNames.contains(host)){
            logger.error(String.format("Peer name %s already exists", host));
            // don't continue
            return;
        }

        // TODO: Validate host and port!
        // assert peer name is not empty
        if (!host.isEmpty()){
            this.peerName = host;
        }

        this.peerPort = port;

        // TODO: THROW a warning exception if host/port are invalid!!
        // for debugging purposes
        new InetSocketAddress(host,port);

        // add peer name (host) to all peers
        peerNames.add(host);

        // set server host and peer (to allow incoming connections)
        serverOptions.setHost(host).setPort(port);

        Vertx vertx = Vertx.vertx();
        this.client = vertx.createNetClient(clientOptions);
        this.server = vertx.createNetServer(serverOptions);
        logger.info(String.format("Added peer %s:%d to p2p network",host,port));
    }



    /**
     * Listen on port 2020 for incoming connections
     * @return
     */
    // TODO: add List<Handler<NetSocket>> listenHandlers as function argument
    // TODO: listen only on peers in network!!!!!
    // listen(portOfSomePeerInNetwork, peersHost)

    protected final Future<Void> listen(List<Handler<NetSocket>> listenHandlers){
        Future<Void> future = Future.future();

        // connect server handlers
        for (Handler<NetSocket> handler: listenHandlers){
            this.server.connectHandler(handler);
        }

        // list on port
        this.server.listen(this.peerPort,res->{
            // this is handler
            if (res.succeeded()){
                logger.info(String.format("%s is listening for connections", toString()));
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
     * Connect to another peer
     * @param otherPeer - peer to connect to
     * @return
     */
    // TODO: add List<Handler<AsyncResult<NetSocket>>> connectHandlers as function argument
    protected final Future<Void> connect(NetPeer otherPeer){
        Future <Void> future = Future.future();
        String host = otherPeer.getPeerName();
        int port = otherPeer.getPeerPort();

        this.client.connect(port, host, res ->{
            // initial connect
            if (res.succeeded()){
                // connect client handlers
//                for (Handler<AsyncResult<NetSocket>> handler:
//                     connectHandlers) {
//                    this.client.connect(port,host,handler);
//                }
                logger.info("%s connected to %s:%d",this.toString(), host, port);
                future.complete();
            }
            else{
                logger.info("%s failed to connect to %s:%d",this.toString(), host, port);

                future.fail(res.cause());
            }
        });
        return future;
    }

    /**
     * Join to peers network
     * Connecting and listening to that peer.
     * @param peerToJoin
     * @return
     */
    public final Future<Void> join(){
        Future future = Future.future();


//        // connect and listen to all peers in network
//        for (NetPeer peerInNetwork: netPeers){
//            // connect only peer that is not connected to all peers
//            if (!allConnectedPeers.contains(peerInNetwork.getPeerName())){
//                // don't connect myself!
//                if (!peerInNetwork.getPeerName().equals(peerToJoin.peerName)){
//                    logger.info(String.format("joining peer %s to peer %s", peerInNetwork ,peerToJoin.toString()));
//                    // listen
//                    //listen();
//                    // connect this peer with other peer
//                    //connect(peerInNetwork);
//
//                }
//            }
//
//        }
//        // add to connectedPeers
//        allConnectedPeers.add(peerToJoin.peerName);
//
//
//        future.complete();
//        logger.info("succeded to connect to %s", peerToJoin.getPeerName());
        // establish connection with that peer

        return future;
    }

    // TODO: delete this!!
//    /**
//     * join all existing peers to network
//     * @return future indicating all peers joined successfully
//     */
//    public final Future<Void> joinAll(){
//        // all peers futures
//        List<Future> futures = new ArrayList<>();
//
//        // join each peer to the network
//        // each node connects to all other nodes
//        for (NetPeer peer: netPeers){
//            futures.add(this.join(peer));
//        }
//
//        // all futures result
//        Future future = Future.future();
//
//        // ensure all futures succeded
//        CompositeFuture.all(futures).setHandler(done -> {
//            if (done.succeeded()) {
//                future.complete();
//                logger.info("Connected to all peers");
//            } else {
//                logger.info("failed to connect to all peers");
//                future.fail(done.cause());
//            }
//        });
//
//        // return all future
//        return future;
//    }

    /**
     * closes peer's client-server socket connections
     * @return
     */
    protected final Future<Void> close(){
        // server future (client close does not provide future)
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
    public static final void disconnect(String peerName){

        // remove from net Peers
        Iterator<NetPeer> itr = netPeers.iterator();
        while (itr.hasNext()){
            NetPeer nextNetPeer = itr.next();

            // close peer's connections
            nextNetPeer.close();

            if (nextNetPeer.getPeerName().equals(peerName)){
                itr.remove();
            }
        }
        // remove from all connected peers
        allConnectedPeers.remove(peerName);

//        if (peerExists(peerName)){
//            remove(peerName);
//
//            NetPeer tempPeer = netPeers.(peerName);
//
//            // close peer connections
//            tempPeer.close();
//        }
    }

    /**
     * @return peer's name
     */
    public String getPeerName(){
        return this.peerName;
    }

    /**
     * @return return peer's port
     */
    public int getPeerPort(){
        return this.peerPort;
    }

    /**
     * peer ass tring
     * @return
     */
    @Override
    public String toString(){
        return String.format("%s:%d",this.peerName, this.peerPort);
    }

    /**
     * when peer dies remove him from static peerNames collection
     */
    @Override
    public void finalize(){
        // remove peer from peerNames
        this.peerNames.remove(this.peerName);
    }



}
