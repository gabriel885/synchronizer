package synchronizer.verticles;

import io.vertx.core.AbstractVerticle;

// responsible for comparing between received file structure
// and sent file structure
// This allows to compensate on missed actions!
// deploy on period when no files need to be synceed
public class LocalSyncVerticle extends AbstractVerticle {
    @Override
    public void start(){

    }

    @Override
    public void stop(){

    }
}
