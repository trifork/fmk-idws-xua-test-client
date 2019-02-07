package com.trifork.idwsxua.fmktestclient;

import com.trifork.idwsxua.fmktestclient.client.ApiVersion;
import com.trifork.idwsxua.fmktestclient.util.XUAProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@ConfigurationProperties(prefix="fmkclient")
public class Properties implements XUAProperties {

    private static final Logger logger = LogManager.getLogger(Properties.class);

    private String keystoreFile;
    private String keystorePassword;
    private String privateKeyPassword;
    private String webserviceEndpoint;
    private STSBootstrap stsBootstrap = new STSBootstrap();
    private STSIdentity stsIdentity = new STSIdentity();
    private STSSystem stsSystem = new STSSystem();

    private int repeats;

    private int repeatDelayMs;
    private String role;
    private String onBehalfOfAuth;
    private String onBehalfOfCpr;
    private String educationCode;
    private String personIdentifier;
    private ApiVersion apiVersion;

    @Override
    public String getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    @Override
    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    @Override
    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }

    public void setPrivateKeyPassword(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    @Override
    public String getWebserviceEndpoint() {
        return webserviceEndpoint;
    }

    public void setWebserviceEndpoint(String webserviceEndpoint) {
        this.webserviceEndpoint = webserviceEndpoint;
    }

    @Override
    public ApiVersion getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        try {
            this.apiVersion = ApiVersion.valueOf(apiVersion);
        } catch (IllegalArgumentException e) {
            logger.error("API version not valid: [" + apiVersion + "]. Valid API versions are: " + Arrays.toString(ApiVersion.values()));
            System.exit(1);
        }
    }

    @Override
    public STSBootstrap getStsBootstrap() {
        return stsBootstrap;
    }

    public void setStsBootstrap(STSBootstrap stsBootstrap) {
        this.stsBootstrap = stsBootstrap;
    }

    @Override
    public STSIdentity getStsIdentity() {
        return stsIdentity;
    }

    @Override
    public STSSystem getStsSystem() {
        return stsSystem;
    }

    public void setStsIdentity(STSIdentity stsIdentity) {
        this.stsIdentity = stsIdentity;
    }
    
    @Override
    public String getPersonIdentifier() {
        return personIdentifier;
    }

    public void setPersonIdentifier(String personIdentifier) {
        this.personIdentifier = personIdentifier;
    }
    
    @Override
    public int getRepeats() {
        return repeats;
    }
    
    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    @Override
    public int getRepeatDelayMs() {
        return repeatDelayMs;
    }

    public void setRepeatDelayMs(int repeatDelayMs) {
        this.repeatDelayMs = repeatDelayMs;
    }

    @Override
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String getOnBehalfOfAuth() {
        return onBehalfOfAuth;
    }
    
    public void setOnBehalfOfAuth(String onBehalfOfAuth) {
        this.onBehalfOfAuth = onBehalfOfAuth;
    }

    @Override
    public String getOnBehalfOfCpr() {
        return onBehalfOfCpr;
    }
    
    public void setOnBehalfOfCpr(String onBehalfOfCpr) {
        this.onBehalfOfCpr = onBehalfOfCpr;
    }

    @Override
    public String getEducationCode() {
        return educationCode;
    }
    
    public void setEducationCode(String educationCode) {
        this.educationCode = educationCode;
    }

}
