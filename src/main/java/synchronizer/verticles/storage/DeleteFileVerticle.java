package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.exceptions.VerticleException;

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
        // Check existence and delete
        vertx.fileSystem().exists(fileToDelete, result -> {
            if (result.succeeded() && result.result()) {
                vertx.fileSystem().deleteBlocking(fileToDelete);
            } else {
                logger.info(String.format("Failed to remove file %s", fileToDelete));
            }
        });
    }

    @Override
    public void stop(){

    }

}
