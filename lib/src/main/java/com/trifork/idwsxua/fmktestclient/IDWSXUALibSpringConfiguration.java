package com.trifork.idwsxua.fmktestclient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan
@ImportResource({"classpath:cxf.xml"})
public class IDWSXUALibSpringConfiguration {

    private static final Logger logger = LogManager.getLogger(IDWSXUALibSpringConfiguration.class);

    public IDWSXUALibSpringConfiguration() {
        System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");
    }
}
