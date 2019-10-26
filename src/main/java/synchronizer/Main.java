package synchronizer;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.app.P2PApplication;
import synchronizer.app.StorageApplication;
import synchronizer.exceptions.ApplicationFailure;

public class Main {

    // logger
    private static final Logger logger = LogManager.getLogger(Main.class);


    public static void main(String [] args){

        CliParser parser = new CliParser();

        CommandLine cmd = parser.parse(args);

        try{
            // storage application
            StorageApplication storageApplication = new StorageApplication(cmd.getOptionValue("path"));
            storageApplication.start();

            // p2p application
            P2PApplication p2p = new P2PApplication(cmd.getOptionValues("devices"));
            p2p.start();

        } catch(Exception e){
            logger.error(new ApplicationFailure(e));
        }

        // p2p application
        //P2PApplication p2p = new P2PApplication();

//        try{
//            storageApplication.start(args);
//            p2p.start(args);
//        } catch(Exception e){
//            storageApplication.kill();
//            p2p.kill();
//            logger.error(new ApplicationFailure(e));
//        }


//        storageApplication.kill();
//        p2p.kill();
//
//
//
//
//        logger.warn("Finished running application");
    }
}
