package synchronizer.verticles.p2p;

import io.vertx.core.*;
import io.vertx.core.net.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.exceptions.ApplicationFailure;
import synchronizer.models.Peer;
import synchronizer.verticles.p2p.handlers.ActionHandler;
import synchronizer.verticles.p2p.handlers.client.ClientHandlers;
import synchronizer.verticles.p2p.handlers.server.ServerHandlers;

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

    // peers to connect to
    private static Set<Peer> peers = new HashSet<>();

    // peers that are already connected (by host)
    private static Set<String> connectedPeers = new HashSet<>();

    // tcp server
    private NetServer server;

    // tcp client
    private NetClient client;

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
        this.peers = peers;

        // assign current peer credentials
        this.peerName = host;
        this.peerPort = port;

        // set server host and peer (to allow incoming connections)
        serverOptions.setHost(host).setPort(port);

        Vertx vertx = Vertx.vertx();
        this.client = vertx.createNetClient(clientOptions);
        this.server = vertx.createNetServer(serverOptions);
    }




    /**
     * Listen on port 2020 for incoming connections
     * @param listenHandlers - server listening handlers
     * @return
     */
    protected final Future<Void> listen(ServerHandlers listenHandlers){
        Future<Void> future = Future.future();

        // connect server handlers
        Iterator<ActionHandler> itr = listenHandlers.getHandlersIterator();
        while(itr.hasNext()){
            ActionHandler handler = itr.next();
            logger.info(String.format("%s connected server-handler: %s", toString(),handler.getClass().getSimpleName()));
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
     * Connect to all listening peers in the network
     * @param connectHandlers - client listening handlers
     * @return
     */
    protected final Future<Void> connect(ClientHandlers connectHandlers){
        Future <Void> future = Future.future();

        logger.debug("assigning client handlers");
        //TODO: connect only to non-connected peers
        for (Peer peer: peers){

            // skip already connected peers
            if (connectedPeers.contains(peer.getHost())){
                continue;
            }
            logger.debug("iterating client handlers");
            // register all connect handlers to client
            Iterator<ActionHandler> itr = connectHandlers.getHandlersIterator();
            while(itr.hasNext()){
                ActionHandler handler = itr.next();
                this.client.connect(peer.getPort(), peer.getHost(), res->{
                    if (res.succeeded()){
                        logger.info(String.format("%s connected client-handler: %s to %s", this.getHost(), handler.getClass().getSimpleName(), peer.toString()));
                        future.complete();
                        connectedPeers.add(this.getHost());
                    }
                    else{
                        logger.info(String.format("%s failed to connect client-handler: %s to %s", this.getHost() , handler.getClass().getSimpleName(),peer.toString()));
                        // retry connect attempts according to NetClientOptions
                        future.fail(res.cause());
                    }
                });
            }


        }

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

        // connect and listen()
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
//        Iterator<NetPeer> itr = netPeers.iterator();
//        while (itr.hasNext()){
//            NetPeer nextNetPeer = itr.next();
//
//            // close peer's connections
//            nextNetPeer.close();
//
//            if (nextNetPeer.getPeerName().equals(peerName)){
//                itr.remove();
//            }
//        }
//        // remove from all connected peers
//        allConnectedPeers.remove(peerName);

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
