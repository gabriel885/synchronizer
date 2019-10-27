package synchronizer;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * parse and validate command line arguments
 */
public class CliParser {

    // logger
    private static final Logger logger = LogManager.getLogger(CliParser.class);

    // cli parser
    private CommandLineParser parser;

    // cli
    private CommandLine cmd;

    // cli options
    private Options cliOptions;

    // cli formatter
    private HelpFormatter formatter;

    // path option
    Option pathCliOption = new Option("p","path", true, "path to synchronize with peers. example: /usd/opt/dir");

    // devices option
    Option devicesCliOption = new Option("d","devices", true,"devices with port to connect the p2p network. examples: 10.0.0.1:4321, 10.0.0.2:2019 10.0.0.0.5:1234");


    //String [] devices = cmd.getOptionValues("devices");

    public CliParser(){
        this.parser = new DefaultParser();
        this.formatter = new HelpFormatter();
        this.cliOptions = new Options();

        // path options
        this.pathCliOption.setRequired(true);

        // devices options
        this.devicesCliOption.setRequired(true); // peers to connect the network (must be set)
        this.devicesCliOption.setArgs(this.devicesCliOption.UNLIMITED_VALUES); // unlimited number of devices in p2p network

        // add options to parser
        this.cliOptions.addOption(pathCliOption);
        this.cliOptions.addOption(devicesCliOption);

        // print usage
        this.formatter.printHelp("Synchronizer. A tool to synchronize files between computers",cliOptions);

    }

    /**
     * parse command line arguments
     * @param args
     * @return
     * @throws IllegalArgumentException
     */
    public CommandLine parse(String [] args) throws IllegalArgumentException{
        try{
            cmd = parser.parse(cliOptions, args);
        } catch (Exception e){
            // parse exception IllegalNumberOfArgument equivalent
            logger.error(e);
            throw new IllegalArgumentException(e);
        }

        return cmd;
    }

}
