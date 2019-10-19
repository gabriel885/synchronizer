package synchronizer.models;

import io.vertx.core.Future;
import synchronizer.exceptions.PathNotFound;

import java.io.File;

public class CreateAction extends Action {
    private java.io.File fileToCreate;

    public CreateAction(Future<Void> createFuture, File fileToCreate) {
        this.fileToCreate = fileToCreate;
        if (!this.fileToCreate.exists()){
            //TODO: fix this
            //createFuture.fail(new PathNotFound(String.format("File %s already exists", fileToCreate)));
        }

        // future completed
        //createFuture.complete();
    }

}