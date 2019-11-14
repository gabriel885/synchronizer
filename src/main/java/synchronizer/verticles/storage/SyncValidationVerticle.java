package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.Checksum;
import synchronizer.models.EventBusAddress;
import synchronizer.models.actions.RequestAction;

import java.nio.file.Path;
import java.nio.file.Paths;

// verticle used to compare between received sync action
// and local path, if some files are missing they are published to
// the event bus
public class SyncValidationVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(SyncValidationVerticle.class);

    // path
    private Path path;

    // event bus
    private EventBus eb;

    // address to publish to
    private EventBusAddress outcomingAddress;

    // produce file system actions to event bus
    private MessageProducer<JsonObject> producer;

    // json object of sync files
    private JsonObject syncAction;

    public SyncValidationVerticle(Path path, EventBusAddress outcomingAddress, JsonObject syncAction){
        this.path = path;
        this.outcomingAddress = outcomingAddress;
        // get all path's strings
        this.syncAction = syncAction;
    }

    @Override
    public void start(){
        // event bus
        this.eb = vertx.eventBus();

        // event bus producer
        this.producer = this.eb.publisher(this.outcomingAddress.toString());

        String path = syncAction.getString("path");
        boolean isDir = syncAction.getBoolean("isDir");
        String checksum = syncAction.getString("checksum");

        logger.info(this.syncAction.toString());

        if (isDir){ // directory doesn't has checksum
            if (!vertx.fileSystem().existsBlocking(path)){
                vertx.fileSystem().mkdirsBlocking(path); // create missing directory
            }
            // directory exists
        }
        else{ // sync file
            if (vertx.fileSystem().existsBlocking(path)){
                if (Checksum.equals(checksum,Checksum.checksum(Paths.get(path)))){ // compare checksums
                    // file is synced!
                }
                else{
                    this.producer.send(new RequestAction(path, false));
                }
            }
            else{ // request missing/old file
                logger.info(String.format("requesting file %s",path));
                this.producer.send(new RequestAction(path, false));
            }
        }


    }
    @Override
    public void stop(){

    }
}
