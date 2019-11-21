package synchronizer.verticles.storage;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.TestContext;
import junit.framework.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import utils.RandomString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class TestCreateFile extends TestCase {

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

    @After
    void cleanup(){
        logger.info(String.format("cleaning %s",testDir));
        // delete test directory
        if (vertx.fileSystem().existsBlocking(testDir)){
            vertx.fileSystem().deleteRecursiveBlocking(testDir, true);
        }
    }

    @Before
    void perpare(TestContext context){
        logger.info(String.format("creating tests directory %s", testDir));
        // create test directory if missing
        if (!vertx.fileSystem().existsBlocking(testDir)){
            vertx.fileSystem().mkdirsBlocking(testDir);
        }
    }

    @Test
    void testFileCreationOverride(){

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
    void testFileCreation(){
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

    /**
     * test file creation inside a directory that does not exist locally
     */
    @Test
    void testFileCreationOnMissingDirectory(){

        Vertx vertx = Vertx.vertx();
        String testSubDir = genRandomString.nextString();
        Path fileToCreate = Paths.get(testDir,testSubDir,genRandomString.nextString()+".txt");
        Buffer buffer = Buffer.buffer().appendString("new content");
        logger.info(String.format("Test created file %s inside non existing directory %s", fileToCreate.toAbsolutePath().toString(), testSubDir));
        // deploy create verticle and check creations upon deploy complete
        vertx.deployVerticle(new CreateFileVerticle(fileToCreate, true,buffer), handler->{
            // TODO: run assettions only after handlers retured the future
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
    void testDirCreation(){
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
