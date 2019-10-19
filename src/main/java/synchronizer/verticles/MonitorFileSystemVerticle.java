package synchronizer.verticles;

import io.vertx.core.AbstractVerticle;
import synchronizer.exceptions.PathNotDirectory;
import synchronizer.models.*;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * verticle responsible for listening to File System actions
 * and publish them to event bus
 */
public class MonitorFileSystemVerticle extends AbstractVerticle {

    // path to monitor
    private Path path;

    // represents a path
    private final Map<File, Map<File, FileInfo>> filesMape = new HashMap<>();

    // files to watch
    private final Set<File> filesToWatch = new HashSet<>();

    // how long a scan takes (in milliseconds)
    private long scanPeriod;




    public MonitorFileSystemVerticle(Path path) throws Exception{
        if (!path.toFile().isDirectory()){
            throw new PathNotDirectory(String.format("Path %s is not a directory",path));
        }
        this.path = path;
    }

    @Override
    public void start() {

        // PUBLISHING SHOULD BE BLOCKING!!
        // We need to ensure the actions are executed sequentially!!!!
        //vertx.setPeriodic(2000, v-> vertx.eventBus().publish("filesystem.actions", new ModifyAction("/Users/gabrielmunits/opt/dir/foo.txt")));

        // activate watcher,



    }

    @Override
    public void stop() throws Exception{

        System.out.println("MonitorFileSystem verticle is stopped");
        // TODO: kill all consumers
       // vertx.eventBus().publish("end", "");
    }


}
