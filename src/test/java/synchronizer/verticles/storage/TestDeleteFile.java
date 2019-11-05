package synchronizer.verticles.storage;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import utils.RandomString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestDeleteFile {

    // logger
    private static final Logger logger = LogManager.getLogger(TestCreateFile.class);
    // tests directory relative to projects
    private static final String testDir = "tests";

    // random string generator
    private RandomString genRandomString = new RandomString();

    @Test
    public void testDirDeletion(){
        Vertx vertx = Vertx.vertx();
        Path fileToCreate = Paths.get(testDir,genRandomString.nextString());
        Buffer buffer = Buffer.buffer(); // dir has empty buffer
        vertx.deployVerticle(new CreateFileVerticle(fileToCreate, true, buffer), deployResult1->{
            if (deployResult1.succeeded()){
                logger.info(deployResult1.result());
                vertx.deployVerticle(new DeleteFileVerticle(fileToCreate.toString()), deployResult2->{
                    if (deployResult2.succeeded()){
                        logger.info("deletion succeeded");
                        assertTrue(!Files.exists(fileToCreate));
                    }
                });
            }
            else{
                logger.error(deployResult1.cause().getMessage());
                assertTrue(false); // create file failed
            }
        });

    }
    @Test
    public void testFileDeletion(){
        Vertx vertx = Vertx.vertx();
        Path fileToCreate = Paths.get(testDir,genRandomString.nextString()+".txt");
        Buffer buffer = Buffer.buffer().appendString("new content");
        logger.info(String.format("Test deletion of created file %s", fileToCreate.toString()));
        vertx.deployVerticle(new CreateFileVerticle(fileToCreate, false,buffer), deployResult1->{
            if (deployResult1.succeeded()){
                vertx.deployVerticle(new DeleteFileVerticle(fileToCreate.toString()), deployResult2->{
                    // check that file was deleted successfully
                    assertTrue(!Files.exists(fileToCreate));
                });
            }
            else{
                logger.error(deployResult1.cause().getMessage());
                assertTrue(false);
            }

        });
    }
}
