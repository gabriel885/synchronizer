package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Verticles responsible for removing a file
 */
public class DeleteFileVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(DeleteFileVerticle.class);

    // local file to delete
    private String fileToDelete;

    public DeleteFileVerticle(String fileToDelete){
        this.fileToDelete = fileToDelete;
    }

    public DeleteFileVerticle(File file){
        this.fileToDelete = file.getPath();
    }

    @Override
    public void start(Future<Void> startFuture){
        if (fileToDelete == null || fileToDelete.isEmpty()){
            logger.warn(String.format("received invalid file to delete %s", fileToDelete));
            startFuture.fail(String.format("received invalid file to delete %s", fileToDelete));
            return;
        }
        // Check existence and delete recursively the path
        vertx.fileSystem().exists(fileToDelete, result -> {
            if (result.succeeded()) {
                if (result.result()){ // if file exists delete it
                    vertx.fileSystem().deleteRecursiveBlocking(fileToDelete,true);
                    logger.info(String.format("deleted file %s", fileToDelete));
                }
                else{
                    logger.info(String.format("file %s already deleted", fileToDelete));
                }
                startFuture.complete();
            }
            else{
                startFuture.fail(result.cause());
            }
        });

    }

    @Override
    public void stop(){

    }

}
