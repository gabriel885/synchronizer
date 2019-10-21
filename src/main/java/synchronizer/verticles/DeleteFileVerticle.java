package synchronizer.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.file.FileSystem;
import synchronizer.exceptions.VerticleException;

/**
 * Verticles responsible for removing a file
 */
public class DeleteFileVerticle extends AbstractVerticle {

    private String fileToDelete;

    public DeleteFileVerticle(String fileToDelete){
        this.fileToDelete = fileToDelete;
    }

    @Override
    public void start(Future<Void> startFuture){
        // Check existence and delete
        vertx.fileSystem().exists(fileToDelete, result -> {
            if (result.succeeded() && result.result()) {
                vertx.fileSystem().delete(fileToDelete, r -> {
                    System.out.println(String.format("File %s deleted", fileToDelete));
                    startFuture.complete();
                });
            } else {
                startFuture.fail(new VerticleException(String.format("File %s failed to delete. Reason: %s", fileToDelete, result.cause())));
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception{
        super.stop(stopFuture);
    }

}
