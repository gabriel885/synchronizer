package synchronizer.models.actions;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import synchronizer.models.diff.Checksum;

import java.io.File;
import java.nio.file.Path;

/**
 * modify action
 * example:
 * {
 *  "type": {
 *       "MODIFY": {
 *           "path": "/opt/dir/modifiedFile.txt",
 *           "checksum": "abcdef"
 *        }
 *    }
 * }
 */
public class ModifyAction extends Action {

    // modified file
    private Path modifiedFile;
    // checksum
    private String checksum;

    // timestamp action was performed
    private long unixTime;

    public ModifyAction(Path modifiedFile){
        super(ActionType.MODIFY);
        this.modifiedFile = modifiedFile;
        this.checksum = Checksum.checksum(modifiedFile);
        this.unixTime = System.currentTimeMillis() / 1000L;
    }


    @Override
    public Buffer bufferize() {
        return Buffer.buffer(toJson());
    }

    @Override
    public String toJson() {
        return new JsonObject().put("type","MODIFY").put("checksum",this.checksum).put("path",this.modifiedFile.toString()).put("timestamp",this.unixTime).toString();
    }
}
