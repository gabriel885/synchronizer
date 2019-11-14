package synchronizer.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

// usage : String checksum = Checksum.checksum("/opt/dir/example.txt");
public interface  Checksum{

    // logger
    Logger logger = LogManager.getLogger(Checksum.class);

     HashMap<String,String> recentChecksums = new HashMap<>();

     static String get(Path file){
         String checksum;
         return checksum(file);
     }

    /**
     * generate checksum of a file
     * @param file
     * @return
     */
    static String checksum(Path file){
        String md5;

        // check for recent checksums
        if ((md5 =recentChecksums.get(file))!=null){
            return md5;
        }

        if (file.toFile().isDirectory()){
            return ""; // directory dont have checksum
        }
        try (InputStream is = Files.newInputStream(file)) {
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
            recentChecksums.put(file.toString(),md5);
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
