package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import synchronizer.models.Checksum;

import java.nio.file.Path;

/**
 * modify action
 * example:
 * {
 * "type": {
 * "MODIFY": {
 * "path": "/opt/dir/modifiedFile.txt",
 * "checksum": "abcdef"
 * }
 * }
 * }
 */
public class ModifyAction extends Action {

    // modified file
    private final Path modifiedFile;

    // true if file is a directory
    private final boolean isDir;

    // file buffer
    private final Buffer newFileBuffer;

    // checksum
    private final String checksum;

    // timestamp action was performed
    private final long unixTime;

    /**
     * if path is a directory - pass an empty buffer (e.g Buffer.buff())
     * @param modifiedFile
     * @param isDir
     * @param newFileBuffer
     */
    public ModifyAction(Path modifiedFile, boolean isDir, Buffer newFileBuffer) {
        super(ActionType.MODIFY);
        this.modifiedFile = modifiedFile;
        this.isDir = isDir;
        this.checksum = Checksum.checksum(modifiedFile);
        this.unixTime = System.currentTimeMillis() / 1000L;
        this.newFileBuffer = newFileBuffer;
    }


    @Override
    public Buffer bufferize() {
        return Buffer.buffer(toJson());
    }

    /**
     * {
     * "type": "MODIFY",
     * "checksum": "edfdcfd4e646fe736caa2825226bf33f",
     * "path": "/opt/dir/newFile.txt",
     * "timestamp": 1572730328,
     * "buffer" : "this is the modifications that was made in file"
     * }
     *
     * @return action json object as string
     */
    @Override
    public String toJson() {
        return new JsonObject()
                .put("type", "MODIFY")
                .put("checksum", this.checksum)
                .put("path", this.modifiedFile.toString())
                .put("timestamp", this.unixTime)
                .put("isDir", this.isDir)
                .put("buffer", this.newFileBuffer.toString())
                .toString();
    }
}
