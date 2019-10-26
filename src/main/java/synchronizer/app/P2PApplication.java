package synchronizer.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.verticles.p2p.NetPeer;
import synchronizer.verticles.p2p.TCPPeer;

import java.net.InetAddress;
import java.util.List;


public class P2PApplication extends AbstractMultiThreadedApplication {

    // host/port internet address
    protected InetAddress inetAddress;

    private final int port = 2019;

    private final String host;

    // NetServer netServer = vertx.createNetServer();

 //   Future<NetServer> netServerFuture = Future.future(promise -> netServer.listen(promise));


    // logger
    private static final Logger logger = LogManager.getLogger(P2PApplication.class);

    private List<NetPeer> peers;

    public P2PApplication(String []devices) throws Exception{
        // initialise p2p applicaiton stuff
        this.inetAddress = inetAddress.getLocalHost();
        this.host = this.inetAddress.getHostAddress();
    }

    /**
     * start p2p application
     * @throws Exception
     */
    @Override
    public void start() throws Exception {

        // deploy TCP peers
        vertx.deployVerticle(new TCPPeer(host,port));
        vertx.deployVerticle(new TCPPeer("10.0.0.5",2011));

        logger.warn(String.format("%s: starting P2P application on port %d",inetAddress.getHostAddress(),port));
    }


    @Override
    public void kill() {
        logger.warn("P2PApplication is shutting down...");
        vertx.close();
    }


}
