package synchronizer.models.actions;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import synchronizer.exceptions.PathNotFound;

import java.io.File;

// TODO: delete this?
public class RenameAction extends Action {

    private File oldFile, newFile;

    public RenameAction(Future<Void> renameFuture,  File oldFile, File newFile) throws PathNotFound{
        super(ActionType.RENAME);

        this.oldFile = oldFile;
        this.newFile = newFile;

        if (!this.oldFile.exists() || !this.newFile.exists()){
            //TODO: fix this
       //     renameFuture.fail(new PathNotFound(String.format("File %s or %s do not exist",this.oldFile, this.newFile)));
        }
       // renameFuture.complete();

    }

    @Override
    public Buffer bufferize() {
        return Buffer.buffer(toJson());
    }

    @Override
    public String toJson() {
        return "";
    }

    @Override
    public String toString() {
        return null;
    }
}
