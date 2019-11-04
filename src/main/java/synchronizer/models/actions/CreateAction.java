package synchronizer.models.actions;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import synchronizer.models.diff.Checksum;

import java.io.File;
import java.nio.file.Path;

/** TODO: make class static!!
 *
 */
public class CreateAction extends Action {

    // local file created
    private Path fileToCreate;

    // created file checksum
    private String checksum;

    // timestamp action was performed
    private long unixTime;

    public CreateAction(Path fileToCreate) {
        super(ActionType.CREATE);
        this.fileToCreate = fileToCreate;
        this.checksum = Checksum.checksum(fileToCreate);
        this.unixTime = System.currentTimeMillis() / 1000L;
    }

    /**
     * return created file path
     * @return local created file
     */
    public String getCreatedFile(){
        return this.fileToCreate.toString();
    }

    /**
     * return file's checksum
     * @return
     */
    public String getChecksum(){
        return this.checksum;
    }

    /**
     * return create action as buffer
     * @return
     */
    @Override
    public Buffer bufferize() {
        return Buffer.buffer(toJson());
    }

    /**
     * return create actions as json string
     * @return
     */
    @Override
    public String toString() {
        return null;
    }

    /**
     * convert create action to object
     * @return
     */
    @Override
    public String toJson() {
        return new JsonObject().put("type","CREATE").put("path",this.fileToCreate.toString()).put("checksum",this.checksum).put("timestamp",this.unixTime).toString();
    }

}