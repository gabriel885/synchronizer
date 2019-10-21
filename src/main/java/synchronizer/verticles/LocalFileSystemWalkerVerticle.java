package synchronizer.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import synchronizer.models.File;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// responsible for scanning all inner directories and files inside a path
// and update shared data path structure map
public class LocalFileSystemWalkerVerticle extends AbstractVerticle {

    private Path path;

    public LocalFileSystemWalkerVerticle(Path path){
        this.path = path;
    }

    @Override
    public void start(Future<Void> startFuture) {
        FileSystem fs = vertx.fileSystem();
        SharedData sharedData = vertx.sharedData();

        List<String> files;

        files = fs.readDirBlocking(this.path.toString());

        // Note: File implements io.vertx.core.shareddata.Shareable
        LocalMap<String, File> pathData =  sharedData.getLocalMap("files");

        for (String file: files){
            pathData.put(file,new File(file));
        }

    }

    @Override
    public void stop(Future<Void> stopFuture){

    }

}