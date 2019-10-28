package synchronizer.verticles.p2p;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.File;
import synchronizer.models.actions.Action;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


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

    // all instantiated tcp peers in application
    private static List<TCPPeer> tcpPeers = new ArrayList<>();

    /**
     * TCPPeer connected to p2p network
     * @param hostname - peer's hostname
     * @param port - peer's port
     * @throws Exception
     */
    public TCPPeer(String hostname, int port) throws Exception{
        // default net server and client options
        super(hostname,port);
    }

    /**
     *
     * @param hostname
     * @param port
     * @param clientOptions - net client options
     * @throws Exception
     */
    public TCPPeer(String hostname, int port, NetClientOptions clientOptions) throws Exception {
        super(hostname,port,clientOptions);
    }

    /**
     *
     * @param hostname
     * @param port
     * @param serverOptions - net server options
     * @throws Exception
     */
    public TCPPeer(String hostname, int port, NetServerOptions serverOptions) throws Exception {
        super(hostname,port,serverOptions);
    }

    public TCPPeer(String hostname, int port, NetClientOptions clientOptions, NetServerOptions serverOptions) throws Exception{
        super(hostname, port, clientOptions, serverOptions);
    }

    @Override
    public void start(){
        // connect to all peers
        //joinAll();

        // connect all server listening handlers
        List<Handler<NetSocket>> serverListenHandlers = new ArrayList<>();
        serverListenHandlers.add(new Handler<NetSocket>() {
            @Override
            public void handle(NetSocket event) {
                event.handler(buffer->{

                    logger.info(String.format("%s received buffer %s", toString(),buffer.toString()));
                });

            }
        });

        listen(serverListenHandlers);

        //connect(new NetPeer());


        // after all peers are listening on port 2020
        // iterate all peers and connect to other peers synchronically!!


        // NetClientOptions: unavailable peers will be tried again to connect after 7 seconds. retry will happen 5 times


        // connect handlers to peers
        // peers listen
        logger.info(String.format("Peer %s is deployed", this.getPeerName()));
    }

    @Override
    public void stop(){
        // automatically closes all servers and clients that where created on start
        logger.info(String.format("Peer %s is undeployed", this.getPeerName()));

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
