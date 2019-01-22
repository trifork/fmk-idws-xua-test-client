package com.trifork.idwsxua.fmktestclient;

import com.trifork.idwsxua.fmktestclient.client.MedicineCardClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    @Override
    public void run(String... args) throws Exception {

        // Starting app
        logger.info("Starting FMK Test Client...");

        // Create a MedicineCardClient
        String apiVersion = properties.getApiVersion().name();
        final MedicineCardClient medicineCardClient = applicationContext.getBean(apiVersion, MedicineCardClient.class);

        int repeats = properties.getRepeats();
        int repeatDelayMs = properties.getRepeatDelayMs();
        String personIdentifier = properties.getPersonIdentifier();

        for (int i = 1; i <= repeats; i++) {
            logger.info("--- Call count: " + i + " ---");
            final String response = medicineCardClient.getMedicineCard(personIdentifier);
            logger.info("WSP response:");
            logger.info(response);
            if (i < repeats) {
                // "<" means do not sleep in last round
                Thread.sleep(repeatDelayMs);
            }
        }
    }

}
