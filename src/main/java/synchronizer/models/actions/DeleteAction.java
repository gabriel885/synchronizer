package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.nio.file.Path;

// represents a delete acition
public class DeleteAction extends Action {

    // file to delete
    private Path fileToDelete;

    // timestamp action was performed
    private long unixTime;

    public DeleteAction(Path fileToDelete) {
        super(ActionType.DELETE);

        this.fileToDelete = fileToDelete;
        this.unixTime = System.currentTimeMillis() / 1000L;


        if (!this.fileToDelete.toFile().exists()){
            // TODO: make it compatible with FUTURE
            //deleteFuture.fail(new PathNotFound(String.format("File %s does not exists", fileToDelete)));
        }

        // future completed
//        deleteFuture.complete();
    }

    @Override
    public Buffer bufferize() {
        return Buffer.buffer(toJson());
    }


    @Override
    public String toJson() {
        return new JsonObject().put("type","DELETE").put("path",this.fileToDelete.toString()).put("timestamp",this.unixTime).toString();
    }

}
