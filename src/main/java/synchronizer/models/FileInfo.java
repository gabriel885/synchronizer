package synchronizer.models;

public final class FileInfo {
    long lastModified;
    long length;

    private FileInfo(long lastModified, long length) {
        this.lastModified = lastModified;
        this.length = length;
    }
}