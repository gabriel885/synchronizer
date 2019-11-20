package synchronizer;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import synchronizer.app.AbstractMultiThreadedApplication;
import synchronizer.app.P2PApplication;
import synchronizer.app.StorageApplication;
import synchronizer.exceptions.ApplicationFailure;

class Main {

    // logger
    private static final Logger logger = LogManager.getLogger(Main.class);

    // storage application
    private static AbstractMultiThreadedApplication storageApplication;

    // p2p application
    private static AbstractMultiThreadedApplication p2pApplication;

    public static void main(String[] args) {

        // cli parser
        CliParser cliParser = new CliParser();

        // cli command
        CommandLine cmd = cliParser.parse(args);

        try {
            // run storage application with path arguments
            storageApplication = new StorageApplication(cmd.getOptionValue("path"));
            storageApplication.start();

            // run p2p application with paht and devices arguments
            p2pApplication = new P2PApplication(cmd.getOptionValue("path"), cmd.getOptionValues("devices"));
            p2pApplication.start();

        } catch (Exception e) {
            logger.error(new ApplicationFailure(e));
            storageApplication.kill();
            p2pApplication.kill();
        }

    }
}
