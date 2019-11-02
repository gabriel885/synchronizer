package synchronizer.verticles.p2p.handlers;

import io.vertx.core.AsyncResult;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.actions.Action;

public class SendActionHandler implements ActionHandler<AsyncResult<NetSocket>> {

    // logger
    private static final Logger logger = LogManager.getLogger(SendActionHandler.class);


    private Action action;

    public SendActionHandler(Action action){
        this.action = action;
    }

    @Override
    public void handle(AsyncResult<NetSocket> event) {
        if (event.succeeded()){
            //event.result().write(action).;
        }
        else{
            logger.warn(event.cause());
        }
    }
}
