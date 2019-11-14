package synchronizer.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.exceptions.PathNotDirectory;
import synchronizer.exceptions.PathNotFound;
import synchronizer.models.EventBusAddress;
import synchronizer.models.SharedDataMapAddress;
import synchronizer.verticles.storage.ActionReceiverVerticle;
import synchronizer.verticles.storage.ActionSenderVerticle;
import synchronizer.verticles.storage.SyncVerticle;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

// StorageApplication component responsible for deploying all
// verticles regarding local file system alternations.
public class StorageApplication extends AbstractMultiThreadedApplication {

    // logger
    private static final Logger logger = LogManager.getLogger(StorageApplication.class);

    // Local path for synchronizer.verticles.storage application to synchronize
    private Path path;


    public StorageApplication(String pathString) throws Exception{
        // parse application arguments and initialize synchronizer.verticles.storage application stuff
        File dirPath = new File(pathString);
        if (!dirPath.exists()){
            throw new PathNotFound(String.format("Path '%s' not found",dirPath));
        }
        if(!dirPath.isDirectory()){
            throw new PathNotDirectory(String.format("Path %s must be a directory",dirPath));
        }
        this.path = dirPath.toPath();
    }

    /**
     * start synchronizer.verticles.storage application
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        logger.warn(String.format("%s: starting Storage application on path %s",myIpAddress, this.path.toString()));

        vertx.deployVerticle(new ActionSenderVerticle(myIpAddress,this.path, new EventBusAddress("outcoming.actions"), new SharedDataMapAddress("local.path")), deployResult1->{
            if (deployResult1.succeeded()){
                // deploy all synchronizer.verticles.storage application verticles
                vertx.deployVerticle(new ActionReceiverVerticle(myIpAddress, this.path, new EventBusAddress("incoming.actions"), new SharedDataMapAddress("local.path")));
            }
        });
        vertx.setPeriodic( ThreadLocalRandom.current().nextInt(7000, 10000 + 1), v->{
            vertx.deployVerticle(new SyncVerticle(this.path, new EventBusAddress("outcoming.actions"), new SharedDataMapAddress("global.path"), new SharedDataMapAddress("local.path")));
        });

    }

    @Override
    public void kill(){
        logger.warn("Storage application shutting down...");
        stachosticTasks.shutdownNow();
        sequentTasks.shutdownNow();
        vertx.close();
    }
}
