package synchronizer.models;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.Shareable;
import synchronizer.models.actions.Action;

class File implements Shareable {

    // filename
    private final String fileName;

    // origin  modification timestamp (unix time)
    private final long timestamp; // modified by local user, unix time

    // file's checksum
    private final String checkSum;

    // file's buffer
    private final Buffer buffer;

    public File(String fileName, boolean isDir, String checksum, long timestamp, Buffer buffer) {
        this.fileName = fileName;
        // true if file is a directory
        this.checkSum = checksum;
        this.timestamp = timestamp;
        this.buffer = buffer;
    }

    // parse json action to a file
    public File(JsonObject action) {
        if (action == null || Action.isValid(action)) {
            this.checkSum = "";
            this.timestamp = 0;
            this.fileName = "";
            this.buffer = Buffer.buffer();
            return;
        }
        if (action.getString("path") == null) {
            this.fileName = "";
        } else {
            this.fileName = action.getString("path");
        }
        if (action.getString("checksum") == null) {
            this.checkSum = "";
        } else {
            this.checkSum = action.getString("checksum");
        }
        if (action.getLong("timestamp") == null) {
            this.timestamp = 0;
        } else {
            this.timestamp = action.getLong("timestamp");
        }
        if (action.getString("buffer") == null) {
            this.buffer = Buffer.buffer();
        } else {
            this.buffer = Buffer.buffer(action.getString("buffer"));
        }
    }

    private String getFileName() {
        return this.fileName;
    }

    private String getChecksum() {
        if (this.checkSum == null) {
            return "";
        }
        return this.checkSum;
    }

    private long getTimeStamp() {
        return this.timestamp;
    }

    private Buffer getBuffer() {
        return this.buffer;
    }

    @Override
    public String toString() {
        return String.format("path:%s\n checksum:%s\n timestamp:%d\n buffer:%s\n", this.getFileName(), this.getChecksum(), this.getTimeStamp(), this.getBuffer().toString());
    }

}
