package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;


public class CreateFileVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(CreateFileVerticle.class);

    // file's path to create
    private Path fileToCreate;

    // files buffer
    private Buffer fileBuffer;

    // true if file to create is a dir
    private boolean isDir;

    // if created file is a directory - buffer is empty
    public CreateFileVerticle(Path fileToCreate,  boolean isDir, Buffer fileBuffer){
        this.fileToCreate = fileToCreate;
        this.fileBuffer = fileBuffer;
        this.isDir = isDir;
    }

    @Override
    public void start(Future<Void> startFuture){

        if (this.fileToCreate == null || this.fileToCreate.toString().isEmpty()){
            logger.info(String.format("Failed to create file %s", this.fileToCreate));
            startFuture.fail(String.format("Failed to create file %s", this.fileToCreate));
        }

        // if file exists - override it
        if (vertx.fileSystem().existsBlocking(this.fileToCreate.toString())){
            vertx.fileSystem().deleteRecursiveBlocking(this.fileToCreate.toString(),true);
        }

        // check if sub directories exist

        // create dir
        if (isDir){
            vertx.fileSystem().mkdirBlocking(this.fileToCreate.toString());
            logger.info(String.format("created dir %s", this.fileToCreate.toString()));
        }
        else{ // create file with buffer
            // create all sub-path if they are missing
            vertx.fileSystem().mkdirsBlocking(this.fileToCreate.getParent().toString());
            vertx.fileSystem().writeFileBlocking(this.fileToCreate.toString(), this.fileBuffer);
            logger.info(String.format("created file %s", this.fileToCreate.toString()));
        }

        startFuture.complete();
    }

    @Override
    public void stop(){
        logger.info("file creation verticle stopped!");
    }

}
