package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageProducer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.EventBusAddress;
import synchronizer.models.SharedDataMapAddress;
import synchronizer.models.actions.SyncAction;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

        // shared data
        this.sd = vertx.sharedData();
        // event bus
        this.eb = vertx.eventBus();

        // event bus producer
        this.producer = this.eb.publisher(this.outcomingAddress.toString());

        LocalMap<String, JsonObject> localMap = this.sd.getLocalMap(localMapName.toString());
        LocalMap<String, JsonObject> globalMap = this.sd.getLocalMap(globalMapName.toString());

        // send local directrory
        vertx.fileSystem().readDir(this.path.toString(), handler->{
            if (handler.succeeded()){
                List<String> files = handler.result();
                Buffer bufferFiles = Buffer.buffer();
                for (String fileName: files){
                    File file = new File(fileName);
                    boolean isDir = file.isDirectory();
                    //bufferFiles.appendString(new JsonObject().put("path",fileName).put("checksum", Checksum.checksum(file.toPath())).put("isDir",isDir).toString());
                    // broadcast file
                    this.producer.send(new JsonObject(new SyncAction(Paths.get(fileName),isDir).toJson()));
                }

            }
        });

    }

    @Override
    public void stop() throws Exception{
    }
}
