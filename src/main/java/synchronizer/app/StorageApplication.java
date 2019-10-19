package synchronizer.app;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.FileSystem;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import synchronizer.exceptions.PathNotDirectory;
import synchronizer.exceptions.PathNotFound;
import synchronizer.models.EventBusAddress;
import synchronizer.models.RenameAction;
import synchronizer.verticles.*;
import synchronizer.utils.RandomString;
import synchronizer.services.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

// StorageApplication responsible for deploying all
// verticles regarding local file system changes.
public class StorageApplication extends MultiThreadedApplication {

    // Local path for storage application to synchronize
    private Path path;

    // Storage application usage
    private static String usage="Synchronizer. " +
            "A tool to synchronize files between computers.\n" +
            "Usage:\n" +
            "  java synchronizer -p <path to monitorable directory>\n";

    // All event bus addresses StorageApplication use
    private Set<EventBusAddress> WatcherEventBusAddresses;



    public StorageApplication(){
        // append all event bus addresses
        WatcherEventBusAddresses = new HashSet<EventBusAddress>() {{
            add(new EventBusAddress("filesystem.actions"));
            // add more event
        }};
    }
    /**
     * -p <path of monitorable directory>
     * @param args
     */
    @Override
    public void start(String[] args) throws Exception {
        if (args.length==2){
            if (args[0].equals("-p")){

                File dirPath = new File(args[1]);

                if (!dirPath.exists()){
                    throw new PathNotFound(String.format("Path '%s' not found",dirPath));
                }
                if(!dirPath.isDirectory()){
                    throw new PathNotDirectory(String.format("Path %s must be a directory",dirPath));
                }
                path = dirPath.toPath();
                System.out.println(String.format("%s: starting storage application on path %s",getIpAddress(), path.toString()));

                // deploy all storage application verticles
                Vertx vertx = Vertx.vertx();
                EventBus eb = vertx.eventBus();

                vertx.deployVerticle(new ActionListenerVerticle(new EventBusAddress("filesystem.actions")));
                Thread.sleep(30000);
                vertx.deployVerticle(new WatcherVerticle(path, new EventBusAddress("filesystem.actions")));


            }
        }
        else{
            throw new IllegalArgumentException(String.format("\n Invalid number of arguments.\n %s ",usage));
        }
    }




    /**
     * Imitate new files creation
     * Usage - new ImitateFileCreations();
     * Used for initial file creation for testing
     */
    private class ImitateFileCreations extends Service{

        private static final int MAX_DELAY = 4; // maximum file creation delay
        private static final int MIN_DELAY = 2; // minimum file creation delay

        private int numFilesToCreate=0;
        // either give a random name or
        private boolean randomName = false;

        private String[] filenames;
        private Random randomSleep = new Random();

        private Path path;

        /**
         * Spawn file creation threads. Sequent execution is NOT guaranteed
         * In order to guarantee sequent execution, must be run in SequentServices pool.
         * File name will be chosen randomly
         * @param numFilesToCreate
         */
        public ImitateFileCreations(Path path, int numFilesToCreate){
            this.path = path;
            this.numFilesToCreate = numFilesToCreate;
            this.randomName = true;
        }

        /**
         * Spawn file creation threads. Sequent execution is NOT guaranteed
         * In order to guarantee sequent execution, must be run in SequentServices pool.
         * @param filenames - filenames to create
         */
        public ImitateFileCreations(Path path, String[] filenames){
            this.path = path;
            this.numFilesToCreate = filenames.length;
            this.randomName = false;
            this.filenames = wrapWithPath(filenames);
        }

        public void run() {

            new Thread(() -> {
               for (int i=0;i<numFilesToCreate;i++){
                   String newFilePath = "";
                   if (randomName){
                       RandomString genRandomString = new RandomString();
                       newFilePath = Paths.get(path.toString(),genRandomString.nextString() + ".txt").toString();
                   }
                   else{
                       newFilePath = filenames[i];
                   }
                   System.out.println(String.format("Creating file %s", newFilePath));
                   File file = new File(newFilePath);

                   try {
                       file.createNewFile();
                       TimeUnit.SECONDS.sleep(randomSleep.nextInt(MAX_DELAY)+MIN_DELAY);
                   } catch (Exception e) {
                       System.out.println(e);
                   }
               }
            }).start();
        }

        /**
         * Wrap filenames with the object's path
         * @param filenames
         * @return
         */
        public String[] wrapWithPath(String[] filenames){
            for(int i=0;i<filenames.length;i++){
                filenames[i] = Paths.get(this.path.toString(),filenames[i]).toString();
            }
            return filenames;
        }
    }
}
