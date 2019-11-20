package synchronizer.verticles.p2p.handlers;

import io.vertx.core.AsyncResult;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// log action handler logs successful client connections
public class LogActionHandler implements ActionHandler<AsyncResult<NetSocket>> {

    // logger
    private static final Logger logger = LogManager.getLogger(LogActionHandler.class);

    @Override
    public void handle(AsyncResult<NetSocket> event) {
        if (event.succeeded()) { // connection succeeded
            logger.info(String.format("%s connected to %s", event.result().localAddress(), event.result().remoteAddress()));
        } else {
            try {
                logger.info(String.format("%s failed to connect to %s", event.result().localAddress(), event.result().remoteAddress()));
            } catch (Exception e) {
                //
            }
        }
    }
}
