package synchronizer.models;

import io.vertx.core.shareddata.Shareable;

public class File implements Shareable {
    private int blockSize = 1024; // 1kb
    private String fileName;

    private long lastModified; // unix time
    private long length;

    public File(String fileName){
        this.fileName = fileName;
        this.lastModified = System.currentTimeMillis() / 1000L;
    }

    // update last file modification
    public void updateLastModification(){
        this.lastModified = System.currentTimeMillis() / 1000L;
    }


}
