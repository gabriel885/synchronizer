package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import synchronizer.models.Checksum;

import java.nio.file.Path;

public class SyncAction extends Action {

    private Path path;

    private boolean isDir;
    // timestamp action was performed
    private long unixTime;

    private Buffer bufferFiles;

    // file's checksum
    private String checksum;


    public SyncAction(Path path, boolean isDir){
        super(ActionType.SYNC);
        this.path = path;
        this.isDir = isDir;
        this.checksum = Checksum.checksum(path);
        this.unixTime = System.currentTimeMillis() / 1000L;
    }

    @Override
    public Buffer bufferize() {
        return Buffer.buffer(toJson());
    }


    @Override
    public String toJson() {
        return new JsonObject()
                .put("type","SYNC")
                .put("path", this.path.toString())
                .put("checksum", this.checksum)
                .put("isDir", this.isDir)
                .put("timestamp",this.unixTime).toString();
    }
}
