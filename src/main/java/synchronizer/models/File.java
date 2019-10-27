package synchronizer.models;

import io.vertx.core.shareddata.Shareable;
import synchronizer.models.diff.Delta;

import java.util.List;

public class File implements Shareable {
    private int blockSize = 1024; // 1kb
    private String fileName;

    private long lastModified; // modified by local user, unix time
    private long length;

    // file's checksum
    private String checkSum;

    // file deltas
    private List<Delta> deltas;


    // true whenever SyncVerticle checks that global and local map are equal
    private boolean syncCheck = false;

    public File(String fileName){
        this.fileName = fileName;
        this.lastModified = System.currentTimeMillis() / 1000L;
    }

    public File(java.io.File file){
        this(file.getName());
    }

    public void modify(){
        this.lastModified = System.currentTimeMillis() / 1000L;
    }
    // update last file modification
    public void updateLastModification(){

    }


}
