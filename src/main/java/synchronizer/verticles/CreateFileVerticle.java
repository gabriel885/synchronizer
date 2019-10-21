package synchronizer.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import synchronizer.exceptions.VerticleException;


public class CreateFileVerticle extends AbstractVerticle {

    private String fileToCreate;

    public CreateFileVerticle(String fileToCreate){
        this.fileToCreate = fileToCreate;
    }

    @Override
    public void start(Future<Void> startFuture){
        // Check existence and delete
        vertx.fileSystem().exists(fileToCreate, result -> {
            if (result.succeeded() && result.result()) {
                // write buffer to file
                Buffer fileData = Buffer.buffer("This is test buffer!");
                vertx.fileSystem().writeFileBlocking(fileToCreate, fileData);
                startFuture.complete();
            } else {
                startFuture.fail(new VerticleException(String.format("File %s failed to create. Reason: %s", fileToCreate, result.cause())));
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception{
        super.stop(stopFuture);
    }

}
