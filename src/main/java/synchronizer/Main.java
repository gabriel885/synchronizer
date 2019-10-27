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

    // storage application
    static StorageApplication storageApplication;

    // p2p application
    static P2PApplication p2pApplication;

    public static void main(String [] args){

        // cli parser
        CliParser parser = new CliParser();
        // cli command
        CommandLine cmd = parser.parse(args);

        try{
            // storage application
            storageApplication = new StorageApplication(cmd.getOptionValue("path"));
            storageApplication.start();


            // p2p application
            p2pApplication = new P2PApplication(cmd.getOptionValues("devices"));
            p2pApplication.start();

        } catch(Exception e){
            logger.error(new ApplicationFailure(e));
            storageApplication.kill();
            p2pApplication.kill();
        }

        // TODO: erase utils package
        // TODO: create local map of files
        // TODO: docker run (pass arguments to java .jar inside container)
        // TODO: run java program and shell inside a container
        // TODO:

    }
}
