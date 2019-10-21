package synchronizer.models.actions;

import io.vertx.core.Future;

import java.io.File;

public class DeleteAction extends Action {
    private File fileToDelete;

    public DeleteAction(Future<Void> deleteFuture, File fileToDelete) {
        this.fileToDelete = fileToDelete;
        if (!this.fileToDelete.exists()){
            // TODO: make it compatible with FUTURE
            //deleteFuture.fail(new PathNotFound(String.format("File %s does not exists", fileToDelete)));
        }

        // future completed
//        deleteFuture.complete();
    }

}
