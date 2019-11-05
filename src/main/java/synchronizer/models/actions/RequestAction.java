package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

/**
 * requesting file action from other peers
 */
public class RequestAction extends Action {

    // file to delete
    private String fileToRequest;

    // timestamp action was performed
    private long unixTime;

    public RequestAction(String fileToRequest){
        this.fileToRequest = fileToRequest;
        this.unixTime = System.currentTimeMillis() / 1000L;
    }

    @Override
    public Buffer bufferize() {
        return Buffer.buffer(toJson());
    }

    /**
     *     {
     *       "type": "REQUEST",
     *       "path": "/opt/dir/newFile.txt",
     *       "timestamp": 1572740322
     *     }
     * @return
     */
    @Override
    public String toJson() {
        return new JsonObject()
                .put("type","REQUEST")
                .put("path",this.fileToRequest)
                .put("timestamp",this.unixTime).toString();
    }
}
