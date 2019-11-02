package synchronizer.app;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServerOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;
import synchronizer.models.Peer;
import synchronizer.verticles.p2p.PublishOutcomingEventsVerticle;
import synchronizer.verticles.p2p.TCPPeer;
import java.net.InetAddress;
import java.util.*;


public class P2PApplication extends AbstractMultiThreadedApplication {

    // logger
    private static final Logger logger = LogManager.getLogger(P2PApplication.class);

    // host/port internet address
    protected InetAddress inetAddress;

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

    private final String myHost;

    // p2p application's peers
    private Set<Peer> peers = new HashSet<>();

    // tcp peer of current host
    private TCPPeer tcpPeer;



    public P2PApplication(String []devices) throws Exception{
        // initialise p2p applicaiton stuff
        this.inetAddress = inetAddress.getLocalHost();
        this.myHost = this.inetAddress.getHostAddress();

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
            if (other.compareTo(new Peer(myHost,port))==1){
                itr.remove();
            }
        }

        // TODO: define default net server options
        tcpPeer = new TCPPeer(myHost, port, peers, new NetClientOptions().setReconnectAttempts(reconnectAttemps).setReconnectInterval(reconnectInterval));

        tcpPeer.addClientHandler(event->{
            logger.info(event.toString());
        });
        // publish outcoming events handler
        //tcpPeer.addClientHandler(new PublishOutcomingEventsHandler(new EventBusAddress("outcoming.actions")));

        // apply incoming events handler
        //tcpPeer.addServerHandler(new ApplyIncomingEventsHandler(new EventBusAddress("incoming.actions")));


    }

    /**
     * start p2p application
     * @throws Exception
     */
    @Override
    public void start() throws Exception {


        vertx.deployVerticle(new PublishOutcomingEventsVerticle(tcpPeer,new EventBusAddress("outcoming.actions")));

        // TODO: set deployment options
        // deploy tcp peer
        vertx.deployVerticle(tcpPeer);


       // logger.warn(String.format("%s: starting P2P application on port %d",inetAddress.getHostAddress(),port));
    }


    /**
     * kill p2p application gracefully
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
        return String.format("%s:%d",this.myHost, this.port);
    }

}
