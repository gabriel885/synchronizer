package synchronizer.models;

import io.vertx.core.Future;
import synchronizer.exceptions.PathNotFound;

import java.io.File;

public class ModifyAction extends Action {

    private File modifiedFile;

    public ModifyAction(Future<Void> modifyFuture, File modifiedFile){
        this.modifiedFile = modifiedFile;
        //modifyFuture.complete();
    }
}
