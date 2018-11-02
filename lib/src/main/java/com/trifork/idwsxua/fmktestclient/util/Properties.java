package com.trifork.idwsxua.fmktestclient.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="fmkclient")
public class Properties {

    private static final Logger logger = LogManager.getLogger(Properties.class);

    private String keystoreFile;
    private String keystorePassword;
    private String privateKeyPassword;
    private String webserviceEndpoint;
    private String apiVersion;
    private String personIdentifier;
    private STSBootstrap stsBootstrap = new STSBootstrap();
    private STSIdentity stsIdentity = new STSIdentity();

    public class STSBootstrap {
        private String endpoint;
        private String audience;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }
    }

    public class STSIdentity {
        private String endpoint;
        private String audience;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }
    }

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public void setPrivateKeyPassword(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    public String getWebserviceEndpoint() {
        return webserviceEndpoint;
    }

    public void setWebserviceEndpoint(String webserviceEndpoint) {
        this.webserviceEndpoint = webserviceEndpoint;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getPersonIdentifier() {
        return personIdentifier;
    }

    public void setPersonIdentifier(String personIdentifier) {
        this.personIdentifier = personIdentifier;
    }

    public STSBootstrap getStsBootstrap() {
        return stsBootstrap;
    }

    public void setStsBootstrap(STSBootstrap stsBootstrap) {
        this.stsBootstrap = stsBootstrap;
    }

    public STSIdentity getStsIdentity() {
        return stsIdentity;
    }

    public void setStsIdentity(STSIdentity stsIdentity) {
        this.stsIdentity = stsIdentity;
    }
}
