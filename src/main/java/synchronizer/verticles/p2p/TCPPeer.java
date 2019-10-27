package synchronizer.verticles.p2p;

import io.vertx.core.net.NetClientOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * TCP AbstractPeer
 * Connectivity
 * Instability
 * Message routing
 *
 */
public class TCPPeer extends NetPeer {
    // logger
    private static final Logger logger = LogManager.getLogger(TCPPeer.class);



    /**
     * TCPPeer connected to p2p network
     * @param hostname - peer's hostname
     * @param port - peer's port
     * @throws Exception
     */
    public TCPPeer(String hostname, int port) throws Exception{
        // default net peer options
        super(hostname,port);

    }


    @Override
    public void start(){
        // join all peers

        // unavailable peers will be tried again to connect after 7 seconds. retry will happen 5 times


        // connect handlers to peers
        // peers listen
        logger.info(String.format("Peer %s is deployed", this.getPeerName()));
    }

    @Override
    public void stop(){
        // automatically closes all servers and clients that where created on start
        logger.info(String.format("Peer %s is undeployed", this.getPeerName()));

    }


}
