package com.trifork.idwsxua.fmktestclient.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix="fmkclient")
public class Properties {

    private String keystoreFile;
    private String keystorePassword;
    private String privateKeyPassword;
    private String stsWsdl;
    private String webserviceEndpoint;
    private STSBootstrap stsBootstrap = new STSBootstrap();
    private STSIdentity stsIdentity = new STSIdentity();

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

    public String getStsWsdl() {
        return stsWsdl;
    }

    public void setStsWsdl(String stsWsdl) {
        this.stsWsdl = stsWsdl;
    }

    public String getWebserviceEndpoint() {
        return webserviceEndpoint;
    }

    public void setWebserviceEndpoint(String webserviceEndpoint) {
        this.webserviceEndpoint = webserviceEndpoint;
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
}
