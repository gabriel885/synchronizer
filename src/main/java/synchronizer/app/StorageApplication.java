package synchronizer.app;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.exceptions.PathNotDirectory;
import synchronizer.exceptions.PathNotFound;
import synchronizer.models.EventBusAddress;
import synchronizer.models.SharedDataMapAddress;

import synchronizer.tasks.Task;
import synchronizer.verticles.storage.ActionReceiverVerticle;
import synchronizer.verticles.storage.ActionSenderVerticle;

import java.io.File;
import java.nio.file.Path;

// StorageApplication responsible for deploying all
// verticles regarding local file system alternations.
public class StorageApplication extends AbstractMultiThreadedApplication {

    // logger
    private static final Logger logger = LogManager.getLogger(StorageApplication.class);

    // Local path for storage application to synchronize
    private Path path;

    public StorageApplication(String pathString) throws Exception{
        // parse application arguments and initialize storage application stuff
        File dirPath = new File(pathString);
        if (!dirPath.exists()){
            throw new PathNotFound(String.format("Path '%s' not found",dirPath));
        }
        if(!dirPath.isDirectory()){
            throw new PathNotDirectory(String.format("Path %s must be a directory",dirPath));
        }
        this.path = dirPath.toPath();
    }


    // TODO: maybe return a future that indicated succeeded termination?
    // TODO: this future will be "waited" in Main thread
    /**
     * start storage application
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        logger.warn(String.format("%s: starting Storage application on path %s",getIpAddress(), path.toString()));

        // deploy all storage application verticles
        vertx.deployVerticle(new ActionReceiverVerticle(path, new EventBusAddress("filesystem.incoming.actions"), new SharedDataMapAddress("global.path.structure")));

        vertx.deployVerticle(new ActionSenderVerticle(path, new EventBusAddress("filesystem.outcoming.actions"), new SharedDataMapAddress("local.path.structure")));


    }



    @Override
    public void kill(){
        logger.warn("Storage application shutting down...");
        stachosticTasks.shutdownNow();
        sequentTasks.shutdownNow();
        vertx.close();
    }



}
