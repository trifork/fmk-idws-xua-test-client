package com.trifork.idwsxua.fmktestclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.trifork.idwsxua.fmktestclient.util.Properties;

public class CommandLineParser {

    private static final Logger logger = LogManager.getLogger(CommandLineParser.class);

    private Properties properties;

    public CommandLineParser(Properties properties) {
        this.properties = properties;
    }

    @Option(name = "-h", aliases = "--help", help = true, usage = "displays help")
    private boolean help = false;

    @Option(name = "-ws", aliases = "--webservice", usage = "webservice endpoint to use")
    private String webserviceEndpoint;

    @Option(name = "-uc", aliases = "--usercert", usage = "User MOCES certificate")
    private String userCert;

    @Option(name = "-up", aliases = "--usercertpw", usage = "User MOCES certificate password")
    private String userCertPassword;

    @Option(name = "-r", aliases = "--repeat", usage = "number of times to repeat the call")
    private Integer repeat;

    @Option(name = "-m", aliases = "--ms", depends = "-l", usage = "milliseconds to wait between the calls")
    private Integer ms;

    @Option(name = "-a", aliases = "--api", usage = "FMK version to use")
    private String apiVersion;

    @Option(name = "-p", aliases = "--personidentifier", usage = "GetMedicineCardRequest: person identifier")
    private String personIdentifier;

    @Option(name = "-R", aliases = "--role", usage = "GetMedicineCardRequest: user role")
    private String role;

    @Option(name = "-oa", aliases = "--onbehalfofauth", usage = "GetMedicineCardRequest: OnBehalfOf auth no")
    private String onBehalfOfAuth;

    @Option(name = "-oc", aliases = "--onbehalfofcpr", usage = "GetMedicineCardRequest: OnBehalfOf CPR")
    private String onBehalfOfCpr;

    @Option(name = "-e", aliases = "--educationCode", usage = "GetMedicineCardRequest: Education code")
    private String educationCode;

    public Properties parse(String... args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);

        // For arguments without key
        //logger.info("args: " + Arrays.toString(args));

        try {
            // parse the arguments
            parser.parseArgument(args);

            // Print help?
            if (help) {
                // print the list of available options
                parser.printUsage(System.err);
                System.err.println();
                System.exit(1);
            }
        } catch (CmdLineException e) {
            // print error message
            System.err.println(e.getMessage());
            System.err.println();

            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            System.exit(1);
        }

        // Starting app
        logger.info("Starting FMK Test Client...");

        // Update properties class
        if (webserviceEndpoint != null) {
            properties.setWebserviceEndpoint(webserviceEndpoint);
        }
        if (apiVersion != null) {
            properties.setApiVersion(apiVersion);
        }
        if (personIdentifier != null) {
            properties.setPersonIdentifier(personIdentifier);
        }
        if (role != null) {
            properties.setRole(role);
        }
        if (onBehalfOfAuth != null) {
            properties.setOnBehalfOfAuth(onBehalfOfAuth);
        }
        if (onBehalfOfCpr != null) {
            properties.setOnBehalfOfCpr(onBehalfOfCpr);
        }
        if (educationCode != null) {
            properties.setEducationCode(educationCode);
        }
        if (userCert != null) {
            properties.setKeystoreFile(userCert);
        }
        if (userCertPassword != null) {
            properties.setKeystorePassword(userCertPassword);
        }
        
        return properties;
    }
}
