package synchronizer.app;

import io.vertx.core.net.NetClientOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;
import synchronizer.models.Peer;
import synchronizer.verticles.p2p.ApplyIncomingActionsVerticle;
import synchronizer.verticles.p2p.PublishOutcomingActionsVerticle;
import synchronizer.verticles.p2p.TCPPeer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


// peer to peer application component responsible for connectiong
// peers to network and maintaining connections
public class P2PApplication extends AbstractMultiThreadedApplication {

    // logger
    private static final Logger logger = LogManager.getLogger(P2PApplication.class);

    // reconnect attempts to each non-responding peer
    private static final int reconnectAttemps = 10;

    // reconnect attempt interval
    private static final int reconnectInterval = 5000; // 5 seconds

    // When you deploy another server on the same host and port as an existing server it
    // doesn’t actually try and create a new server listening on the same host/port.
    // Instead it internally maintains just a single server, and, as incoming connections
    // arrive it distributes them in a round-robin fashion to any of the connect handlers.
    // Consequently Vert.x TCP servers can scale over available cores while each instance
    // remains single threaded.
    private static final int tcpInstances = 5;

    // peer's listening port
    private final int port = 2020;

    // synchronizer.verticles.p2p application's peers
    private Set<Peer> peers = new HashSet<>();

    // tcp peer of current host
    private TCPPeer tcpPeer;

    // local monitorable path
    private Path path;

    public P2PApplication(String path,String []devices) throws Exception{

        // peers names
        for (String device: devices) {
            String host = device.split(":")[0];
            int port = Integer.parseInt(device.split(":")[1]);
            peers.add(new Peer(host,port));
        }

        // exclude myself from list of peers
        Iterator<Peer> itr = peers.iterator();
        // log peers
        while (itr.hasNext()){
            Peer other = itr.next();
            if (other.compareTo(new Peer(myIpAddress,port))==1){
                itr.remove();
            }
        }
        this.path = Paths.get(path);
        this.tcpPeer = new TCPPeer(myIpAddress, port, peers, new NetClientOptions().setReconnectAttempts(reconnectAttemps).setReconnectInterval(reconnectInterval));
    }

    /**
     * start synchronizer.verticles.p2p application
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        // deploy tcp peer
        vertx.deployVerticle(tcpPeer, deployResult->{
            if (deployResult.succeeded()){
                // publish local events to all peers
                vertx.deployVerticle(new PublishOutcomingActionsVerticle(this.path ,this.tcpPeer,new EventBusAddress("outcoming.actions")));
                // apply global events locally
                vertx.deployVerticle(new ApplyIncomingActionsVerticle(this.path ,this.tcpPeer,new EventBusAddress("incoming.actions")));
            }else{
                logger.error(deployResult.cause());
            }
        });
    }


    /**
     * kill synchronizer.verticles.p2p application gracefully
     */
    @Override
    public void kill() {
        vertx.close();
        logger.warn("P2PApplication is shutting down...");
        vertx.close();
    }

    /**
     * print host and port of machine the application is running on
     * @return
     */
    @Override
    public String toString(){
        return String.format("%s:%d",myIpAddress, this.port);
    }

}
