package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
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
    public void start(){
        if (fileToDelete == null || fileToDelete.isEmpty()){
            logger.warn(String.format("received invalid file to delete %s", fileToDelete));
            return;
        }
        // Check existence and delete
        vertx.fileSystem().exists(fileToDelete, result -> {
            if (result.succeeded() && result.result()) {
                vertx.fileSystem().deleteBlocking(fileToDelete);
                logger.info(String.format("deleted file %s", fileToDelete));
            } else {
                // ignore deletion because file already don't exist
            }
        });
    }

    @Override
    public void stop(){

    }

}
