package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.nio.file.Path;

// represents a delete acition
public class DeleteAction extends Action {

    // file to delete
    private final Path fileToDelete;

    // timestamp action was performed
    private final long unixTime;

    private final boolean isDir;

    public DeleteAction(Path fileToDelete, boolean isDir) {
        super(ActionType.DELETE);
        this.fileToDelete = fileToDelete;
        this.unixTime = System.currentTimeMillis() / 1000L;
        this.isDir = isDir;
    }

    @Override
    public Buffer bufferize() {
        return Buffer.buffer(toJson());
    }


    /**
     * {
     * "type": "DELETE",
     * "path": "/opt/dir/newFile.txt",
     * "timestamp": 1572730328
     * }
     *
     * @return action as json string
     */
    @Override
    public String toJson() {
        return new JsonObject()
                .put("type", "DELETE")
                .put("path", this.fileToDelete.toString())
                .put("timestamp", this.unixTime)
                .put("isDir", this.isDir)
                .toString();
    }

}
