package synchronizer.models.actions;

import io.vertx.core.Future;

import java.io.File;

public class CreateAction extends Action {
    private java.io.File fileToCreate;


    public CreateAction(Future<Void> createFuture, File fileToCreate) {
        super(ActionType.CREATE);

        this.fileToCreate = fileToCreate;
        if (!this.fileToCreate.exists()){
            //TODO: fix this
            //createFuture.fail(new PathNotFound(String.format("File %s already exists", fileToCreate)));
        }

        // future completed
        //createFuture.complete();
    }

}