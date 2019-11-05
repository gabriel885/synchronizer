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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCreateFile {

    // logger
    private static final Logger logger = LogManager.getLogger(TestCreateFile.class);

    // tests directory relative to projects
    private static final String testDir = "tests";

    // random string generator
    private RandomString genRandomString = new RandomString();

    private Vertx vertx;

    public TestCreateFile(){
        this.vertx = Vertx.vertx();
    }

    @Test
    public void testFileCreationOverride(){

        Path fileToCreate = Paths.get(testDir,genRandomString.nextString()+".txt");
        Buffer buffer = Buffer.buffer().appendString("old content");
        logger.info(String.format("Test created file %s", fileToCreate.toAbsolutePath().toString()));
        // deploy create verticle and check creations upon deploy complete
        vertx.deployVerticle(new CreateFileVerticle(fileToCreate, false,buffer), deployResult1->{
            // if file creation succedded
            if (deployResult1.succeeded()) {
                vertx.deployVerticle(new CreateFileVerticle(fileToCreate, false, Buffer.buffer("updated content")), deployResult2->{
                    if (deployResult2.succeeded()){
                        // check if file exists
                        assertTrue(Files.exists(fileToCreate));
                        // check that the file is not a directory
                        assertTrue(!Files.isDirectory(fileToCreate));
                        // check file's data
                        assertEquals("updated content", vertx.fileSystem().readFileBlocking(fileToCreate.toAbsolutePath().toString()).toString());
                    }
                });
            }
        });


    }
    @Test
    public void testFileCreation(){
        Vertx vertx = Vertx.vertx();
        Path fileToCreate = Paths.get(testDir,genRandomString.nextString()+".txt");
        Buffer buffer = Buffer.buffer().appendString("new content");
        logger.info(String.format("Test created file %s", fileToCreate.toAbsolutePath().toString()));
        // deploy create verticle and check creations upon deploy complete
        vertx.deployVerticle(new CreateFileVerticle(fileToCreate, false,buffer), handler->{
            // if file creation succedded
            if (handler.succeeded()) {
                // check if file exists
                assertTrue(Files.exists(fileToCreate));
                // check that the file is not a directory
                assertTrue(!Files.isDirectory(fileToCreate));
                // check file's data
                assertEquals(buffer.toString(), vertx.fileSystem().readFileBlocking(fileToCreate.toAbsolutePath().toString()).toString());
            }
        });


    }
    @Test
    public void testDirCreation(){
        Path fileToCreate = Paths.get(testDir,genRandomString.nextString());
        Buffer buffer = Buffer.buffer(); // dir has empty buffer
        // deploy create verticle and check creations upon deploy complete
        vertx.deployVerticle(new CreateFileVerticle(fileToCreate, true, buffer), handler->{
            if (handler.succeeded()) {
                assertTrue((Files.exists(fileToCreate)));
                assertTrue(Files.isDirectory(fileToCreate));
            }
        });
    }
}
