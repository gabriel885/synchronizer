package synchronizer;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * parse and validate command line arguments
 */
class CliParser {

    // logger
    private static final Logger logger = LogManager.getLogger(CliParser.class);
    // cli parser
    private final CommandLineParser parser;
    // cli options
    private final Options cliOptions;


    public CliParser() {

        this.parser = new DefaultParser();
        // cli formatter
        HelpFormatter formatter = new HelpFormatter();
        this.cliOptions = new Options();

        // path options
        // path option
        Option pathCliOption = new Option("p", "path", true, "path to synchronize with peers. example: /opt/dir");
        pathCliOption.setRequired(true);

        // devices options
        // devices option (peers to connect the synchronizer.verticles.p2p network)
        Option devicesCliOption = new Option("d", "devices", true, "devices with port to connect the synchronizer.verticles.p2p network. examples: 10.0.0.1:4321, 10.0.0.2:2020 10.0.0.0.5:2020. NOTE: port is set to default to 2020");
        devicesCliOption.setRequired(true); // peers to connect the network (must be set)
        devicesCliOption.setArgs(Option.UNLIMITED_VALUES); // unlimited number of devices in synchronizer.verticles.p2p network


        // add options to parser
        this.cliOptions.addOption(pathCliOption);
        this.cliOptions.addOption(devicesCliOption);

        // print usage
        formatter.printHelp("Synchronizer. A tool to synchronize files between computers", cliOptions);

    }

    /**
     * parse command line arguments
     *
     * @param args - program arguments
     * @return command line object
     * @throws IllegalArgumentException - if illegal number of arguments or arguments was provided
     */
    public CommandLine parse(String[] args) throws IllegalArgumentException {
        // cli
        CommandLine cmd;

        try {
            cmd = parser.parse(cliOptions, args);
        } catch (Exception e) {
            // parse exception IllegalNumberOfArgument equivalent
            logger.error(e);
            throw new IllegalArgumentException(e);
        }

        return cmd;
    }

}
