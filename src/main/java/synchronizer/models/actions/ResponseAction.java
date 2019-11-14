package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import synchronizer.models.diff.Checksum;

import java.nio.file.Path;

/**
 * response action with requested file
 */
public class ResponseAction extends Action {

    // path of file to response
    private Path fileToResponse;
    // true if path is directory

    private boolean isDir;
    // file's buffer to response
    private Buffer bufferToResponse;

    // checksum of response file
    private String checksum;

    // timestamp of action
    private long unixTime;

    public ResponseAction(Path fileToResponse,boolean isDir, Buffer responseBuffer){
        this.fileToResponse = fileToResponse;
        this.isDir = isDir;
        this.bufferToResponse = responseBuffer;
        this.checksum = Checksum.checksum(fileToResponse);
        this.unixTime = System.currentTimeMillis() / 1000L;
    }

    @Override
    public Buffer bufferize() {
        return Buffer.buffer(toJson());
    }

    /**
     *     {
     *       "type": "RESPONSE",
     *       "path": "/opt/dir/newFile.txt",
     *       "checksum": "f8a6701de14ec3fcfd9f2fe595e9c9ed",
     *       "timestamp": 1572740322,
     *       "buffer": "this is content of requested file"
     *     }
     * @return
     */
    @Override
    public String toJson() {
        return new JsonObject()
                .put("type","RESPONSE")
                .put("path",this.fileToResponse)
                .put("isDir", this.isDir)
                .put("checksum", this.checksum)
                .put("timestamp", this.unixTime)
                .put("buffer",this.bufferToResponse)
                .toString();
    }
}
