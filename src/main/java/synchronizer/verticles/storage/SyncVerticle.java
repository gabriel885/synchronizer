package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.File;
import synchronizer.models.SharedDataMapAddress;

import java.util.List;
import java.util.Map;

// responsible for comparing between global path structure and local path structure
// to detect conflicts, adopt global path and request missing files.
// This allows to compensate on missed actions, failed downloads/uploads etc.!
// Deploy on period when no files need to be synced
// Use high priority event bus address
public class SyncVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(SyncVerticle.class);


    private SharedData sd;
    private SharedDataMapAddress localMapName, globalMapName;


    public SyncVerticle(SharedDataMapAddress globalPathStructureMap, SharedDataMapAddress localPathStructureMap){
        this.localMapName = localPathStructureMap;
        this.globalMapName = globalPathStructureMap;
        this.sd = vertx.sharedData();
    }


    @Override
    public void start(Future<Void> startFuture) throws Exception{
        LocalMap<String, File> localMap = this.sd.getLocalMap(localMapName.toString());
        LocalMap<String, File> globalMap = this.sd.getLocalMap(globalMapName.toString());

        // compare map keys and file last modification
        for (Map.Entry entry : globalMap.entrySet()){
            // check that key exists
            if (!localMap.containsKey(entry.getKey())){
                logger.info(String.format("Found file %s that does not exists locally",entry.getKey()));
                // request for that file from downloadFileVerticle?
            }
        }

    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception{

    }
}
