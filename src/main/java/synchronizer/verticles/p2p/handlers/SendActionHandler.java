package synchronizer.verticles.p2p.handlers;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.actions.Action;

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
        if (event.succeeded()){
            logger.info(String.format("writing event %s to socket!", this.action.toBuffer()));
            event.result().write(this.action.toBuffer());
        }
        else{
            logger.warn(event.cause());
        }
    }
}
