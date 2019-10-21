package synchronizer.verticles;

import io.vertx.core.AbstractVerticle;

// responsible for comparing between global path structure and local path structure
// This allows to compensate on missed actions, failed downloads/uploads etc.!
// deploy on period when no files need to be synced
public class LocalSyncVerticle extends AbstractVerticle {
    @Override
    public void start(){

    }

    @Override
    public void stop(){

    }
}
