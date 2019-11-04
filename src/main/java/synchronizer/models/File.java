package synchronizer.models;

import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;
import synchronizer.models.actions.Action;
import synchronizer.models.diff.Delta;

import java.util.List;

public class File implements Shareable {

    // filename
    private String fileName;

    // origin  modification timestamp (unix time)
    private long timestamp; // modified by local user, unix time

    // file's checksum
    private String checkSum;


    // true whenever SyncVerticle checks that global and local map are equal
    private boolean syncCheck = false;

    public File(String fileName, String checksum, long timestamp){
        this.fileName = fileName;
        this.checkSum = checksum;
        this.timestamp = timestamp;
    }

    // parse json action to a file
    public File(JsonObject action){

        if(action==null || !Action.valid(action)){
            this.checkSum="";
            this.timestamp=0;
            this.fileName="";
        }
        this.fileName = action.getString("path");
        this.checkSum = action.getString("checksum");
        this.timestamp = action.getLong("timestamp");
    }

    public String getFileName(){
        return this.fileName;
    }
    public String getChecksum(){
        return this.checkSum;
    }
    public long getTimeStamp(){
        return this.timestamp;
    }


}
