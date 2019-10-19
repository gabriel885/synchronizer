package synchronizer.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.file.FileSystem;
import synchronizer.exceptions.VerticleException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class RenameFileVerticle extends AbstractVerticle {

    // File system instance
    //private FileSystem fs =  vertx.fileSystem();
    private Path newPath, oldPath;

    public RenameFileVerticle(String oldPath, String newPath){
        this.newPath = Paths.get(newPath);
        this.oldPath = Paths.get(oldPath);
    }

    // called when verticle is deployed
    @Override
    public void start(Future<Void> startFuture){
        try{
            Files.move(this.oldPath, this.newPath);
            System.out.println(String.format("File %s renamed to %s", this.oldPath, this.newPath));
        } catch(Exception e){
            startFuture.fail(new VerticleException(String.format("Failed to rename file %s to %s. Reason: %s", this.oldPath, this.newPath, e.getMessage())));
        }
        startFuture.complete();
    }

    // called when verticle is stopped
    @Override
    public void stop(Future<Void> stopFuture) throws Exception{
        super.stop(stopFuture);
    }
}
