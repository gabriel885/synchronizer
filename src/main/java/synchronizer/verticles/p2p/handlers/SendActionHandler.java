package synchronizer.verticles.p2p.handlers;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SendActionHandler implements ActionHandler<AsyncResult<NetSocket>> {

    // logger
    private static final Logger logger = LogManager.getLogger(SendActionHandler.class);

    // action to send
    private JsonObject action;

    public SendActionHandler(JsonObject action){
        this.action = action;
    }

    @Override
    public void handle(AsyncResult<NetSocket> event) {
        if (event.succeeded()){ // connection succeded
            // pause if writing queue is full
            if (event.result().writeQueueFull()) {
                logger.info("socket writing queue is full - paused");
                event.result().pause();
                event.result().drainHandler(done -> {
                    logger.info("socket writing queue is drained - continue");
                    event.result().resume();
                });
            }
            event.result().write(this.action.toBuffer(), handler->{
                if (handler.succeeded()){
                    logger.debug(String.format("event %s succeeded writing to socket", this.action.toString()));
                }
                else{
                    logger.debug(String.format("event %s failed writing to socket!", this.action.toString()));
                }
            });
            //event.result().end(); // end handler
        }
        else{
            logger.warn(event.cause());
        }
    }
}
