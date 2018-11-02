package com.trifork.idwsxua.fmktestclient;

import com.trifork.idwsxua.fmktestclient.client.Client;
import com.trifork.idwsxua.fmktestclient.client.MedicineCard_2015_01_01;
import com.trifork.idwsxua.fmktestclient.client.MedicineCard_2015_01_01_E1;
import com.trifork.idwsxua.fmktestclient.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunner implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(ApplicationRunner.class);

    private final ApplicationContext applicationContext;
    private final Properties properties;

    @Autowired
    public ApplicationRunner(ApplicationContext applicationContext, Properties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @Option(name = "-h", aliases = "--help", help = true, usage = "displays help")
    private boolean help = false;

    @Option(name = "-ws", aliases = "--webservice", usage = "webservice endpoint to use")
    private String webserviceEndpoint = "http://wsp-idws-xua:8444/HelloWorld/services/helloworld";

    @Option(name = "-r", aliases = "--repeat", usage = "number of times to repeat the call")
    private int repeat = 2;

    @Option(name = "-m", aliases = "--ms", depends = "-l", usage = "milliseconds to wait between the calls")
    private int ms = 1000;

    @Option(name = "-a", aliases = "--api", usage = "FMK version to use")
    private String apiVersion = "MedicineCard_2015_01_01_E1";

    @Option(name = "-p", aliases = "--personidentifier", usage = "GetMedicineCardRequest: person identifier")
    private String personIdentifier = "1111111111";

    @Override
    public void run(String... args) throws Exception {
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
                return;
            }
        } catch (CmdLineException e) {
            // print error message
            System.err.println(e.getMessage());
            System.err.println();

            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();

            return;
        }

        // Starting app
        logger.info("Starting FMK Test Client...");

        // Update properties class
        properties.setWebserviceEndpoint(webserviceEndpoint);
        properties.setApiVersion(apiVersion);
        properties.setPersonIdentifier(personIdentifier);

        // Create a client
        final Client client;
        switch (apiVersion) {
            case "MedicineCard_2015_01_01":
                client = applicationContext.getBean(MedicineCard_2015_01_01.class);
                break;
            case "MedicineCard_2015_01_01_E1":
                client = applicationContext.getBean(MedicineCard_2015_01_01_E1.class);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized api version");
        }

        for (int i = 1; i <= repeat; i++) {
            logger.info("--- Call count: " + i + " ---");
            client.callService(personIdentifier);
            if (i < repeat) {
                // Don't sleep in last round
                Thread.sleep(ms);
            }
        }
    }


}
