package com.trifork.idwsxua.fmktestclient;

import com.trifork.idwsxua.fmktestclient.client.Client;
import com.trifork.idwsxua.fmktestclient.client.MedicineCard_2015_01_01;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRunner implements CommandLineRunner {

    private static final Logger logger = LogManager.getLogger(ApplicationRunner.class);

    private final Client client;

    @Autowired
    public ApplicationRunner(MedicineCard_2015_01_01 client) {
        this.client = client;
    }

    @Option(name = "-h", aliases = "--help", help = true, usage = "displays help")
    private boolean help;

    @Option(name = "-ws", aliases = "--webservice", usage = "webservice endpoint to use")
    private String webserviceEndpoint = "http://wsp-idws-xua:8444/HelloWorld/services/helloworld";

    @Option(name = "-s", aliases = "--sts", usage = "sts endpoint to use")
    private String stsEndpoint = "http://sts-idws-xua:8181/service/sts";

    @Option(name = "-r", aliases = "--repeat", usage = "number of times to repeat the call")
    private int repeat = 2;

    @Option(name = "-m", aliases = "--ms", depends = "-l", usage = "milliseconds to wait between the calls")
    private int ms = 1000;

    @Option(name = "-p", aliases = "--personidentifier", usage = "person identifier to make GetMedicineCard request for")
    private String personIdentifier = "1111111111";

    @Override
    public void run(String... args) throws Exception {
        CmdLineParser parser = new CmdLineParser(this);

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

        // Change STS endpoint address
        client.setSTSEndpoint(stsEndpoint);

        // Change WS endpoint address
        client.setWebserviceEndpoint(webserviceEndpoint);

        for (int i = 1; i <= repeat; i++) {
            logger.info("--- Call count: " + i + " ---");
            client.callService(personIdentifier);
            if(i < repeat) {
                // Don't sleep in last round
                Thread.sleep(ms);
            }
        }

    }
}
