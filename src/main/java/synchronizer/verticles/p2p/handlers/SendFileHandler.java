package synchronizer.verticles.p2p.handlers;

import io.vertx.core.AsyncResult;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.verticles.p2p.NetPeer;
import synchronizer.verticles.p2p.TCPPeer;

import java.nio.file.Path;

// handler sends local file path to socket
public class SendFileHandler extends HandlerVerticle<AsyncResult<NetSocket>>{

    // logger
    private static final Logger logger = LogManager.getLogger(SendFileHandler.class);

    // local absolute file path to send
    private String fileToSend;

    // peer to send file from
    private TCPPeer tcpPeer;

    public SendFileHandler(TCPPeer tcpPeer, Path fileToSend){
        this.fileToSend = fileToSend.toAbsolutePath().toString();
        this.tcpPeer = tcpPeer;
    }

    /**
     * when connected to the server
     * @param event
     */
    @Override
    public void handle(AsyncResult<NetSocket> event) {
        // on success connection to peer send file
        if (event.succeeded()){
            event.result().sendFile(fileToSend, res ->{
                if (!res.succeeded()){
                    // try to resend
                    logger.info(String.format("Failed to send file %s", fileToSend));
                }
            });
        }else{
            logger.warn(event.cause());
        }
    }

    @Override
    public void start() {
        // send file
      //  this.tcpPeer.sendFile(??);
    }

    @Override
    public void stop() {

    }
}
