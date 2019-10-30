package synchronizer.verticles.p2p.handlers;

import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * logs handler events from socket
 */
public class LogHandler implements ActionHandler {

    // logger
    private Logger logger = LogManager.getLogger(LogHandler.class);

    // host the handler is connected to
    private String identifier;

    public LogHandler(String identifier){
        this.identifier = identifier;
    }

    @Override
    public void handle(NetSocket event) {
        event.handler(buffer->{
           logger.info(String.format("%s %s",identifier,buffer.toString()));
        });
    }
}
