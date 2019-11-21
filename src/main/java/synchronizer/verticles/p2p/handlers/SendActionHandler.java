package synchronizer.verticles.p2p.handlers;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.actions.Action;
import synchronizer.models.actions.ActionType;

// client handler sends actions to the socket
public class SendActionHandler implements ActionHandler<AsyncResult<NetSocket>> {

    // logger
    private static final Logger logger = LogManager.getLogger(SendActionHandler.class);

    // action to send
    private JsonObject action;

    public SendActionHandler(JsonObject action) {
        this.action = action;
    }

    @Override
    public void handle(AsyncResult<NetSocket> event) {

        if (event.succeeded()) { // connection succeded
            // pause if writing queue is full
            if (event.result().writeQueueFull()) {
                logger.info("socket writing queue is full - paused");
                event.result().pause();
                // drain handler and resume
                event.result().drainHandler(done -> {
                    logger.info("socket writing queue is drained - continue");
                    event.result().resume();
                });
            }
            // write action as buffer to socket
            event.result().write(this.action.toBuffer(), handler -> {
                if (handler.succeeded()) {
                    logger.debug(String.format("event %s succeeded writing to socket", this.action.toString()));
                } else {
                    logger.debug(String.format("event %s failed writing to socket!", this.action.toString()));
                }
            });

            event.result().handler(v -> { // receive ACK
                // if ACK wasn't received resend this.action.toBuffer()
                String stringJsonResponse = v.toString();
                ActionType responseActionType = Action.getActionType(new JsonObject(stringJsonResponse));

                if (responseActionType == ActionType.UNKNOWN || responseActionType == ActionType.NACK ){
                    logger.info(String.format("Received %s", v.toString()));
                    // resending buffer
                    event.result().write(this.action.toBuffer());
                }
                else{
                    logger.info(String.format("received ack from server - closing client socket of %s", event.result().localAddress()));
                    event.result().end(); // end handler (close it)
                }
            });

        } else {
            logger.warn(event.cause());
        }
    }
}
