package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;


public class CreateFileVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(CreateFileVerticle.class);


    // file's path to create
    private String fileToCreate;

    // files buffer
    private Buffer fileBuffer;

    // if created file is a directory - buffer is empty
    public CreateFileVerticle(String fileToCreate, Buffer fileBuffer){
        this.fileToCreate = fileToCreate;
        this.fileBuffer = fileBuffer;
    }

    @Override
    public void start(){
        if (this.fileToCreate == null || this.fileToCreate.isEmpty()){
            logger.info(String.format("Failed to create file %s with buffer ", this.fileToCreate, this.fileBuffer.toString()));
            return;
        }
        // TODO: CHECK IF THERE IS NOT EXTENTION - than the file is a directory
        if (Paths.get(this.fileToCreate).toFile().isDirectory()){
            vertx.fileSystem().mkdirBlocking(this.fileToCreate,"666");
            logger.info("created dir %s", this.fileToCreate);
        }
        else{
            if(fileBuffer==null || fileBuffer.toString().isEmpty()){ // validate buffer
                logger.info(String.format("Failed to create file %s with buffer ", this.fileToCreate, this.fileBuffer.toString()));
                return;
            }
            // create new file and overwrite origin file
            vertx.fileSystem().writeFileBlocking(this.fileToCreate, this.fileBuffer);
            logger.info(String.format("created file %s", this.fileToCreate));
        }
    }

    @Override
    public void stop(){

    }

}
