package synchronizer.verticles.p2p.handlers;

import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * handlers logs events from socket
 */
public class LogHandler implements ActionHandler {

    // logger
    private Logger logger = LogManager.getLogger(LogHandler.class);

    @Override
    public void handle(NetSocket event) {
        event.handler(buffer->{
           logger.info(buffer.toString());
        });
    }
}
