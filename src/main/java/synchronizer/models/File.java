package synchronizer.models;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;
import synchronizer.models.actions.Action;

public class File implements Shareable {

    // filename
    private String fileName;

    // origin  modification timestamp (unix time)
    private long timestamp; // modified by local user, unix time

    // file's checksum
    private String checkSum;

    // file's buffer
    private Buffer buffer;

    // true whenever SyncVerticle checks that global and local map are equal
    private boolean syncCheck = false;

    public File(String fileName, String checksum, long timestamp, Buffer buffer){
        this.fileName = fileName;
        this.checkSum = checksum;
        this.timestamp = timestamp;
        this.buffer = buffer;
    }

    // parse json action to a file
    public File(JsonObject action){
        if(action==null || !Action.valid(action)){
            this.checkSum="";
            this.timestamp=0;
            this.fileName="";
            this.buffer = Buffer.buffer();
            return;
        }
        if (action.getString("path") == null){
            this.fileName = "";
        }
        else{
            this.fileName = action.getString("path");
        }
        if (action.getString("checksum") == null){
            this.checkSum = "";
        }
        else{
            this.checkSum = action.getString("checksum");
        }
        if (action.getLong("timestamp") == null){
            this.timestamp = 0;
        }
        else{
            this.timestamp = action.getLong("timestamp");
        }
        if (action.getString("buffer") == null){
            this.buffer = Buffer.buffer();
        }
        else{
            this.buffer = Buffer.buffer(action.getString("buffer"));
        }
    }

    public String getFileName(){
        return this.fileName;
    }
    public String getChecksum(){
        if (this.checkSum==null){
            return "";
        }
        return this.checkSum;
    }
    public long getTimeStamp(){
        return this.timestamp;
    }
    public Buffer getBuffer() { return this.buffer; }

    @Override
    public String toString(){
        return String.format("path:%s\n checksum:%s\n timestamp:%d\n buffer:%s\n",this.getFileName(),this.getChecksum(),this.getTimeStamp(),this.getBuffer().toString());
    }

}
