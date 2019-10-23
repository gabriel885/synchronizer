package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;
import synchronizer.models.SharedDataMapAddress;


import java.nio.file.Path;

/**
 * Class responsible for listening incoming actions and
 * deploy local verticles accordingly
 */
public class ActionReceiverVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(ActionReceiverVerticle.class);


    private EventBusAddress address;

    // consume file system actions from event bus
    private MessageConsumer<JsonObject> consumer;

    // object received from event bus
    private JsonObject actionObject;

    /**
     *
     * @param path - local path
     * @param address - event bus address to listen for incoming alternations
     * @param globalMapAddress - SharedData map address for global path structure (what's received)
     */
    public ActionReceiverVerticle(Path path, EventBusAddress address, SharedDataMapAddress globalMapAddress){
        EventBus eb = Vertx.vertx().eventBus();


        this.address = address;
        this.consumer = eb.consumer(address.toString());
        this.consumer.handler(message ->{
            logger.info("Receive from event bus %s" + message.body());
            //JsonObject jsonAction = Json.decodeValue(message.body());

//            Action action = Json.decodeValue(message.body(),Action.class);
//
//
//            // decode action type and deploy verticle ??
//            switch(){
//                case "rename":
//                    // parse old and new names and shoot rename verticle
//                    vertx.deployVerticle(new RenameFileVerticle());
//                    break;
//                case "delete":
//                    break;
//                case "modify":
//                    break;
//            }
        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception{
        // consuming file system action events
        logger.info(String.format("ActionReceiverVerticle consuming actions from: ",this.address.toString()));
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception{
        super.stop(stopFuture);
    }


}
