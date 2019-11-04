package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.nio.file.Path;

public class RequestAction extends Action {

    // file requesting to download
    private Path fileToRequest;

    // timestamp action was performed
    private long unixTime;

    public RequestAction(Path requestFile){
        this.fileToRequest = requestFile;
        this.unixTime = System.currentTimeMillis() / 1000L;
    }
    @Override
    public Buffer bufferize() {
        return Buffer.buffer(toJson());
    }

    @Override
    public String toJson() {
        return new JsonObject().put("type","REQUEST").put("path",this.fileToRequest.toString()).put("timestamp",this.unixTime).toString();
    }
}
