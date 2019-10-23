package synchronizer.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;
import synchronizer.verticles.p2p.TCPServerVerticle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;


public class P2PApplication extends MultiThreadedApplication {

    // host/port internet address
    protected InetAddress inetAddress;

    // NetServer netServer = vertx.createNetServer();

 //   Future<NetServer> netServerFuture = Future.future(promise -> netServer.listen(promise));


    // logger
    private static final Logger logger = LogManager.getLogger(P2PApplication.class);


    public P2PApplication(){

    }

    @Override
    public void start(String[] args) throws Exception {
        this.inetAddress = inetAddress.getLocalHost();
        vertx.deployVerticle(new TCPServerVerticle(new InetSocketAddress("0.0.0.0",2019),new EventBusAddress("filesystem.outcoming.actions")));

    }

    @Override
    public void kill() {
        logger.warn("P2PApplication is shutting down...");
        vertx.close();
    }


}
