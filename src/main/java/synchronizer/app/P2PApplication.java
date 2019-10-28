package synchronizer.app;

import io.vertx.core.net.NetClientOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.Peer;
import synchronizer.verticles.p2p.NetPeer;
import synchronizer.verticles.p2p.TCPPeer;

import java.net.InetAddress;
import java.util.*;


public class P2PApplication extends AbstractMultiThreadedApplication {

    // logger
    private static final Logger logger = LogManager.getLogger(P2PApplication.class);

    // host/port internet address
    protected InetAddress inetAddress;

    // reconnect attempts to each non-responding peer
    private static final int reconnectAttemps = 5;

    // reconnect attempt interval
    private static final int reconnectInterval = 5000; // 5 seconds

    // peer's listening port
    private final int port = 2020;

    private final String myHost;

    // p2p application's peers
    private Set<Peer> peers = new HashSet<>();

    // applications tcp peers
    //private List<TCPPeer> tcpPeers = new ArrayList<>();
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
       // peers.remove(new Peer(this.myHost,this.port).toString());

//        // instantiate tcp peers
//        for (String device: devices){
//            String host = device.split(":")[0];
//            int port = Integer.parseInt(device.split(":")[1]);
//            // exlude this peer
//            // add to list of TCP peers
//            //this.tcpPeers.add(new TCPPeer(host,port,new NetClientOptions().setReconnectAttempts(5).setReconnectInterval(5000)));
//
//        }
        logger.info(String.format("list of my peers %s",Arrays.asList(peers)));

        tcpPeer = new TCPPeer(myHost,port,new NetClientOptions().setReconnectAttempts(reconnectAttemps).setReconnectInterval(reconnectInterval));


    }

    /**
     * start p2p application
     * @throws Exception
     */
    @Override
    public void start() throws Exception {

        // deploy tcp peer
        vertx.deployVerticle(tcpPeer);


       // logger.warn(String.format("%s: starting P2P application on port %d",inetAddress.getHostAddress(),port));
    }


    @Override
    public void kill() {
        logger.warn("P2PApplication is shutting down...");
        vertx.close();
    }


}
