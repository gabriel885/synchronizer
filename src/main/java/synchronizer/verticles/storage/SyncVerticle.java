package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;
import synchronizer.models.SharedDataMapAddress;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

// responsible for comparing between global path structure and local path structure
// to detect conflicts, adopt global path - request missing files and make local changes.
// This allows to compensate on missed actions, failed downloads/uploads etc.!
// deploy interval is 2 seconds!
// Use high priority event bus address
public class SyncVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(SyncVerticle.class);

    // path
    private Path path;

    // shared data local map
    private SharedData sd;

    private Deque<Long> timestampStack = new ArrayDeque<Long>();

    // local maps addresses
    private SharedDataMapAddress localMapName, globalMapName;

    // event bus
    private EventBus eb;

    // address to publish to
    private EventBusAddress outcomingAddress;


    // produce file system actions to event bus
    private MessageProducer<JsonObject> producer;

    public SyncVerticle(Path path, EventBusAddress outcomingAddress, SharedDataMapAddress globalPathStructureMap, SharedDataMapAddress localPathStructureMap){
        this.localMapName = localPathStructureMap;
        this.globalMapName = globalPathStructureMap;
        this.path= path;
        this.outcomingAddress = outcomingAddress;
    }

    @Override
    public void start() {

        vertx.deployVerticle(new MyVerticle());

        logger.info("sync verticle deployed!");
        // shared data
        this.sd = vertx.sharedData();
        // event bus
        this.eb = vertx.eventBus();

        // event bus producer
        this.producer = this.eb.publisher(this.outcomingAddress.toString());

        LocalMap<String, JsonObject> localMap = this.sd.getLocalMap(localMapName.toString());
        LocalMap<String, JsonObject> globalMap = this.sd.getLocalMap(globalMapName.toString());

        // compare map keys and file last modification
//        for (Map.Entry entry : globalMap.entrySet()){
//            // check that key exists
//            // or if the checksums of the files differ
//            if (!localMap.containsKey(entry.getKey()) || !Checksum.equals(localMap.get(entry).getChecksum(), globalMap.get(entry).getChecksum())){
//                logger.info(String.format("Found file %s that does not exists locally. Requesting file...",entry.getKey()));
//                JsonObject actionObject = new JsonObject(new RequestAction(entry.getKey().toString()).toJson());
//                // send request action to event bus
//                this.producer.send(actionObject);
//            }
//            else{
//
//            }
//        }

        logger.info("local map:");
        logger.info(localMap.keySet());
    }

    @Override
    public void stop() throws Exception{
    }
}
