package synchronizer.models.actions;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import synchronizer.models.Checksum;

import java.nio.file.Path;

/** TODO: make class static!!
 *
 */
public class CreateAction extends Action {

    // local file created
    private Path fileToCreate;

    // true if tile is a dir
    private boolean isDir;

    // file buffer
    private Buffer fileBuffer;

    // created file checksum
    private String checksum;

    // timestamp action was performed
    private long unixTime;


    public CreateAction(Path fileToCreate, boolean isDir, Buffer fileBuffer) {
        super(ActionType.CREATE);
        this.fileToCreate = fileToCreate;
        this.isDir = isDir;
        this.checksum = Checksum.checksum(fileToCreate);
        this.unixTime = System.currentTimeMillis() / 1000L;
        this.fileBuffer = fileBuffer;
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
     *     {
     *       "type": "CREATE",
     *       "path": "/opt/dir/newFile.txt",
     *       "isDir": false,
     *       "checksum": "a063e188310b9cf711b0e251a349afc1",
     *       "timestamp": 1572730322,
     *       "buffer" : "new content is added to new file"
     *     }
     * @return
     */
    @Override
    public String toJson() {
        return new JsonObject()
                .put("type","CREATE")
                .put("path",this.fileToCreate.toString())
                .put("checksum",this.checksum)
                .put("timestamp",this.unixTime)
                .put("isDir", this.isDir)
                .put("buffer",this.fileBuffer.toString())
                .toString();
    }

}