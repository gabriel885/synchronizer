package synchronizer.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

// usage : String checksum = Checksum.checksum("/opt/dir/example.txt");
public interface Checksum {

    // logger
    Logger logger = LogManager.getLogger(Checksum.class);

    // none checksum for objects that checksum can't be genrated for them
    String NONE = "NONE";

    static String get(Path file) {
        String checksum;
        return checksum(file);
    }

    /**
     * Generate checksum of a file
     *
     * @param file - from which to calculate the checksuom
     * @return md5 checksum as string
     */
    static String checksum(Path file) {
        String md5;

        // directory doesn't have checksums
        if (file.toFile().isDirectory()) {
            return NONE; // directory dont have checksum
        }

        // missing files can't have checksums
        if (!file.toFile().exists()){
            return NONE;
        }

        try (InputStream is = Files.newInputStream(file)) {
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
            return md5;
        } catch (Exception e) {
            logger.warn(String.format("Failed to generate checksum for %s", file.toString()));
        }
        return NONE;
    }

    /**
     * compare two checksums
     * if (!Checksum.compare("this-is-checksum1","this-is-checksum2")){
     * logger.info("non equal checksums");
     * }
     *
     * @param checksum1 - checksum of file 1
     * @param checksum2 - checksum of file 2
     * @return true if the checksums are equal
     */
    static boolean equals(String checksum1, String checksum2) {
        return checksum1.equals(checksum2);
    }

}
