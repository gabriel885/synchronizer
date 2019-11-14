package synchronizer.models.diff;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

// usage : String checksum = Checksum.checksum("/opt/dir/example.txt");
public interface  Checksum{

    // logger
    Logger logger = LogManager.getLogger(Checksum.class);

    /**
     * generate checksum of a file
     * @param file
     * @return
     */
    static String checksum(Path file){

        String md5;

        if (file.toFile().isDirectory()){
            return ""; // directory dont have checksum
        }
        try (InputStream is = Files.newInputStream(file)) {
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
            return md5;
        }catch(Exception e){
            logger.warn(String.format("Failed to generate checksum for %s", file.toString()));
        }
        return "";
    }

    /**
     * compare two checksums
     * if (!Checksum.compare("this-is-checksum1","this-is-checksum2")){
     *     logger.info("non equal checksums");
     * }
     * @param checksum1
     * @param checksum2
     * @return
     */
    static boolean equals(String checksum1, String checksum2){
        return checksum1.equals(checksum2);
    }

}
