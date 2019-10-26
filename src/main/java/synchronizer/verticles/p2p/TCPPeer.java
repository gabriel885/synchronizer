package synchronizer.verticles.p2p;

import io.vertx.core.net.NetClientOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    // port the peer is listening on
    // private final int port;

    /**
     * TCPPeer to connect to
     * @param hostname
     * @param port
     * @throws Exception
     */
    public TCPPeer(String hostname, int port) throws Exception{
        // default net peer options
        super(hostname,port);
    }


    @Override
    public void start(){
        // join

        // connect handlers to peers
        // peers listen
    }

    @Override
    public void stop(){
        // automatically closes all servers and clients that where created on start
    }


}
