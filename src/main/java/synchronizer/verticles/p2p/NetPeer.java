package synchronizer.verticles.p2p;

import io.vertx.core.*;
import io.vertx.core.net.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.exceptions.ApplicationFailure;
import synchronizer.models.Peer;
import synchronizer.verticles.p2p.handlers.*;

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

    // peers to connect to
    // K: peer's host
    // V: peer model
    protected static HashMap<String,Peer> peers = new HashMap<>();

    // peers that are already connected (by host)
    protected static Set<String> connectedPeers = new HashSet<>();

    // tcp server
    protected final NetServer server;

    // tcp client
    protected final NetClient client;

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
        serverOptions.setHost(host).setPort(port);

        Vertx vertx = Vertx.vertx();
        this.client = vertx.createNetClient(clientOptions);
        this.server = vertx.createNetServer(serverOptions);
    }


    /**
     * listen to all peers
     * @return reference to net server
     */
    protected final NetServer listen(){
        return this.server.listen(peerPort);
    }


    /**
     * listen to all peers with specific handler
     * @param handler
     * @return
     */
    protected final NetServer listen(ActionHandler handler) {
        return this.server.listen(handler);
    }

    /**
     * connect to specific peer a handler
     * @return
     */
    protected final NetClient connect(Peer peer, ActionHandler handler){
        return this.client.connect(peer.getPort(), peer.getHost(), handler);
    }

    /**
     * connect listen handlers and listen on port 2020 for incoming connections
     * @param listenHandlers - server listening handlers
     * @return
     */
    protected final Future<Void> listen(Handlers listenHandlers){
        Future<Void> future = Future.future();

        // connect server handlers
        Iterator<ActionHandler> itr = listenHandlers.getHandlersIterator();
        while(itr.hasNext()){
            ActionHandler handler = itr.next();
            // deploy handler
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
     * Connect all client handlers to all listening peers in the network
     * @param connectHandlers - client listening handlers
     * @return
     */
    protected final void connect(Handlers connectHandlers){

        // connecting to all non-connected peers in the network
        for (Peer peer: peers.values()){
            // skip already connected peers
            // TODO: refactor connected peers - why do we need this?
//            if (connectedPeers.contains(peer.getHost())){
//                continue;
//            }
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
     * send file to all peers
     * @param path
     */
    protected final void sendFile(Path path){
        // TODO: how do we know file sent succedded?
        for (String peer: connectedPeers){
            Peer netPeer = peers.get(peer);
            //connect(netPeer, new SendFileHandler(path));
        }
    }

    /**
     * send file to specific peer
     * @param peer
     * @param path
     */
    protected final void sendFileToPeer(Peer peer, Path path){
        Peer netPeer = peers.get(peer.getHost());
       // connect(netPeer,new SendFileHandler(path));
    }

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
