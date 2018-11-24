package com.trifork.idwsxua.fmktestclient;

import com.trifork.idwsxua.fmktestclient.client.MedicineCardClient;
import com.trifork.idwsxua.fmktestclient.client.MedicineCard_2015_01_01;
import com.trifork.idwsxua.fmktestclient.client.MedicineCard_2015_01_01_E1;
import com.trifork.idwsxua.fmktestclient.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.trifork.idwsxua.fmktestclient.client.Client;
import com.trifork.idwsxua.fmktestclient.client.MedicineCard_2015_01_01;
import com.trifork.idwsxua.fmktestclient.client.MedicineCard_2015_01_01_E1;
import com.trifork.idwsxua.fmktestclient.util.Properties;

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

    @Override
    public void run(String... args) throws Exception {

        // Create a MedicineCardClient
        final MedicineCardClient medicineCardClient;

        // Starting app
        logger.info("Starting FMK Test Client...");

        String apiVersion = properties.getApiVersion();
        int repeats = properties.getRepeats();
        int repeatDelayMs = properties.getRepeatDelayMs();
        String personIdentifier = properties.getPersonIdentifier();
        
        // Create a client
        final Client client;

        switch (apiVersion) {
            case "MedicineCard_2015_01_01":
                medicineCardClient = applicationContext.getBean(MedicineCard_2015_01_01.class);
                break;
            case "MedicineCard_2015_01_01_E1":
                medicineCardClient = applicationContext.getBean(MedicineCard_2015_01_01_E1.class);
                break;
            default:
                throw new IllegalArgumentException("Unrecognized api version");
        }

        for (int i = 1; i <= repeats; i++) {
            logger.info("--- Call count: " + i + " ---");
            final String response = medicineCardClient.getMedicineCard(personIdentifier);
            logger.info("WSP response:");
            logger.info(response);
            if (i < repeats) {
                // "<" means do not sleep in last round
                Thread.sleep(ms);
            }
        }
    }

}
