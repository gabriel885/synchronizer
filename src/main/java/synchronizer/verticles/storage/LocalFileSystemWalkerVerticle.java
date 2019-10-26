package synchronizer.verticles.storage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.file.FileSystem;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.models.File;

import java.nio.file.Path;
import java.util.List;

// responsible for scanning all inner directories and files inside a path
// and update shared data path structure map
public class LocalFileSystemWalkerVerticle extends AbstractVerticle {

    // logger
    private static final Logger logger = LogManager.getLogger(LocalFileSystemWalkerVerticle.class);

    private Path path;

    public LocalFileSystemWalkerVerticle(Path path){
        this.path = path;
    }

    // TODO: BAD!!
    @Override
    public void start(Future<Void> startFuture) {
        FileSystem fs = vertx.fileSystem();
        SharedData sharedData = vertx.sharedData();

        List<String> files;

        files = fs.readDirBlocking(this.path.toString());

        // Note: File implements io.vertx.core.shareddata.Shareable
        LocalMap<String, synchronizer.models.File> pathMap =  sharedData.getLocalMap("local.path.structure");
        pathMap.clear();

        for (String file: files){
            pathMap.put(file,new File(file));
        }


        logger.info("Periodic Scan:");

        StringBuilder mapAsString = new StringBuilder();
        mapAsString.append("\n");
        for (String fileName: pathMap.keySet()){
            mapAsString.append(fileName+"\n");
        }
        logger.info(mapAsString.toString());
        startFuture.complete();
    }

    @Override
    public void stop(Future<Void> stopFuture){
        stopFuture.complete();

    }

}