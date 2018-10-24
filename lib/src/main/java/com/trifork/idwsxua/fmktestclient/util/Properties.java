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
@ConfigurationProperties("idwsxua")
public class Properties {

    private static final Logger logger = LogManager.getLogger(Properties.class);

    // Auto mapped
    private String keystoreFile;
    private String keystorePassword;
    private String privateKeyPassword;

    // Programmatically set
    private KeyStore keystore;

    @PostConstruct
    private void init() throws IOException {
        final InputStream keystoreFileInputStream = new ClassPathResource(getKeystoreFile()).getInputStream();
        setKeystore(loadKeystore(keystoreFileInputStream, getKeystorePassword()));
    }

    private KeyStore loadKeystore(InputStream keystoreFileInputStream, String password) {
        KeyStore ks = null;

        try {
            ks = KeyStore.getInstance("PKCS12");
            ks.load(keystoreFileInputStream, password.toCharArray());
        } catch (GeneralSecurityException | IOException ex) {
            logger.error("Could not load keystore with provided password", ex);
        }

        return ks;
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

    public KeyStore getKeystore() {
        return keystore;
    }

    public void setKeystore(KeyStore keystore) {
        this.keystore = keystore;
    }

}
