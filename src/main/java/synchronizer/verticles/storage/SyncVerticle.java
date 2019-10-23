package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import synchronizer.models.SharedDataMapAddress;

// responsible for comparing between global path structure and local path structure
// to detect conflicts, adopt global path and request missing files.
// This allows to compensate on missed actions, failed downloads/uploads etc.!
// Deploy on period when no files need to be synced
// Use high priority event bus address
public class SyncVerticle extends AbstractVerticle {

    private SharedDataMapAddress localMap, globalMap;

    public SyncVerticle(SharedDataMapAddress globalPathStructureMap, SharedDataMapAddress localPathStructureMap){
        this.localMap = localPathStructureMap;
        this.globalMap = globalPathStructureMap;
    }


    @Override
    public void start(Future<Void> startFuture) throws Exception{

    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception{

    }
}
