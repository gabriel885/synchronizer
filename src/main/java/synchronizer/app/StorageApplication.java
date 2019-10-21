package synchronizer.app;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import synchronizer.exceptions.PathNotDirectory;
import synchronizer.exceptions.PathNotFound;
import synchronizer.models.EventBusAddress;
import synchronizer.models.SharedDataMapAddress;
import synchronizer.services.Task;
import synchronizer.verticles.*;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

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



    public StorageApplication(){


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


                vertx.deployVerticle(new ActionReceiverVerticle(path, new EventBusAddress("filesystem.incoming.actions"), new SharedDataMapAddress("global.path.structure")));
                //Thread.sleep(5000);
                vertx.deployVerticle(new ActionSenderVerticle(path, new EventBusAddress("filesystem.outcoming.actions"), new SharedDataMapAddress("local.path.structure")));

                // scan local file system path structure
                vertx.deployVerticle(new LocalFileSystemWalkerVerticle(path));



                // https://www.codota.com/code/java/methods/io.vertx.core.Vertx/setPeriodic
                vertx.setPeriodic(1000, v -> eb.publish("news-feed", "Some news!"));
                vertx.deployVerticle(new Verticle() {
                    @Override
                    public Vertx getVertx() {
                        return null;
                    }

                    @Override
                    public void init(Vertx vertx, Context context) {

                    }

                    @Override
                    public void start(Future<Void> startFuture) throws Exception {
                        EventBus eb = vertx.eventBus();
                        eb.consumer("news-feed", message ->{
                            System.out.println("Consumed dummy: " + message.body());

                            // get updated path structure using shared data
                            SharedData sd = vertx.sharedData();
                            String name = "files";
                            LocalMap<String, synchronizer.models.File> localMap = vertx.sharedData().getLocalMap(name);
                            for (Map.Entry<String, synchronizer.models.File> entry: localMap.entrySet()){
                               //System.out.println(entry.getKey());
                            }

                        });
                    }

                    @Override
                    public void stop(Future<Void> stopFuture) throws Exception {

                    }
                });


            }
        }
        else{
            throw new IllegalArgumentException(String.format("\n Invalid number of arguments.\n %s ",usage));
        }
    }


    @Override
    public void kill(){
        System.out.println("Storage application shutting down...");
        stachosticTasks.shutdownNow();
        sequentTasks.shutdownNow();
        vertx.close();
    }



}
