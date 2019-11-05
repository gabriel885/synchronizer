package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
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
        try{
            Files.deleteIfExists(this.fileToCreate);
        }catch(Exception e){
            startFuture.fail(String.format("failed to delete existing file %s", this.fileToCreate.toString()));
        }


        // check if it's a directory
        if (isDir){

            vertx.fileSystem().mkdirBlocking(this.fileToCreate.toString());
            logger.info(String.format("created dir %s", this.fileToCreate.toString()));
        }
        else{ // that's a file
            // check if the file buffer is not empty
            if(fileBuffer==null || fileBuffer.toString().isEmpty()){ // validate buffer
                logger.info(String.format("Failed to create file %s with buffer ", this.fileToCreate, this.fileBuffer.toString()));
                startFuture.fail(String.format("Failed to create file %s with buffer ", this.fileToCreate, this.fileBuffer.toString()));
            }
            // create new file and overwrite origin file
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
