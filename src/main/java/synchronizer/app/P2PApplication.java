package synchronizer.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.verticles.p2p.NetPeer;
import synchronizer.verticles.p2p.TCPPeer;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;


public class P2PApplication extends AbstractMultiThreadedApplication {

    // host/port internet address
    protected InetAddress inetAddress;

    // TODO: SHOULDN'T be hardcoded!!
    private final int port = 2019;

    private final String host;

    // NetServer netServer = vertx.createNetServer();

 //   Future<NetServer> netServerFuture = Future.future(promise -> netServer.listen(promise));


    // logger
    private static final Logger logger = LogManager.getLogger(P2PApplication.class);

    // p2p application's peers
    private List<TCPPeer> peers = new ArrayList<>();

    public P2PApplication(String []devices) throws Exception{
        // initialise p2p applicaiton stuff
        this.inetAddress = inetAddress.getLocalHost();
        this.host = this.inetAddress.getHostAddress();

        // must exist othrwise cli parser will throw an erorr
        for (String device: devices){
            String host = device.split(":")[0];
            int port = Integer.parseInt(device.split(":")[1]);
            // add to list of TCP peers
            this.peers.add(new TCPPeer(host,port));
        }


    }

    /**
     * start p2p application
     * @throws Exception
     */
    @Override
    public void start() throws Exception {

        // deploy TCP peers
        for (TCPPeer peer: peers){
            vertx.deployVerticle(peer);
        }

       // logger.warn(String.format("%s: starting P2P application on port %d",inetAddress.getHostAddress(),port));
    }


    @Override
    public void kill() {
        logger.warn("P2PApplication is shutting down...");
        vertx.close();
    }


}
