package synchronizer.models.diff;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.verticles.p2p.PublishOutcomingEventsVerticle;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// usage : String checksum = Checksum.checksum("/opt/dir/example.txt");
public abstract class Checksum{

    // logger
    private static final Logger logger = LogManager.getLogger(Checksum.class);

    /**
     * generate checksum of a file
     * @param file
     * @return
     */
    public static String checksum(Path file){
        if (file.toFile().isDirectory()){
            return ""; // directory dont have checksum
        }
        String md5;
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
    public boolean compare(String checksum1, String checksum2){
        return checksum1.equals(checksum2);
    }

}
