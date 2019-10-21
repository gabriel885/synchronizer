package synchronizer.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import synchronizer.models.EventBusAddress;
import synchronizer.models.SharedDataMapAddress;

import java.nio.file.Path;

/**
 * Class responsible for listening incoming actions and
 * deploy local verticles accordingly
 */
public class ActionReceiverVerticle extends AbstractVerticle {

    private EventBusAddress address;

    // produce file system actions to event bus
    private MessageConsumer<JsonObject> consumer;

    // object received from event bus
    private JsonObject actionObject;

    /**
     *
     * @param path
     * @param address
     * @param mapAddress
     */
    public ActionReceiverVerticle(Path path, EventBusAddress address, SharedDataMapAddress mapAddress){
        // WHY INSTANTIATING new vertx() instance?
        EventBus eb = Vertx.vertx().eventBus();


        this.address = address;
        this.consumer = eb.consumer(address.toString());
        this.consumer.handler(message ->{
            System.out.println("Receive from event bus %s" + message.body());
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
    public void start(){
        // consuming file system action events
        System.out.println("Starting ActionReceiverVerticle");
        System.out.println("consuming from to: " + this.address.toString());


    }

    @Override
    public void stop(){
        System.out.println("action listened verticle is stopped");
    }


}
