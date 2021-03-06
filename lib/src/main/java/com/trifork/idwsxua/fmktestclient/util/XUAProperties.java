package com.trifork.idwsxua.fmktestclient.util;

import com.trifork.idwsxua.fmktestclient.client.ApiVersion;

public interface XUAProperties {
    String getKeystoreFile();

    String getKeystorePassword();

    String getPrivateKeyPassword();

    String getWebserviceEndpoint();

    ApiVersion getApiVersion();

    STSBootstrap getStsBootstrap();

    STSIdentity getStsIdentity();

    STSSystem getStsSystem();

    String getPersonIdentifier();

    int getRepeats();

    int getRepeatDelayMs();

    String getRole();

    String getOnBehalfOfAuth();

    String getOnBehalfOfCpr();

    String getEducationCode();

    String getPurposeOfUse();

    class STSBootstrap {
        private String endpoint;
        private String wsdl;
        private String audience;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getWsdl() {
            return wsdl;
        }

        public void setWsdl(String wsdl) {
            this.wsdl = wsdl;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }
    }

    class STSIdentity {
        private String endpoint;
        private String wsdl;
        private String audience;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getWsdl() {
            return wsdl;
        }

        public void setWsdl(String wsdl) {
            this.wsdl = wsdl;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }
    }

    class STSSystem {
        private String endpoint;
        private String wsdl;
        private String audience;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getWsdl() {
            return wsdl;
        }

        public void setWsdl(String wsdl) {
            this.wsdl = wsdl;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }
    }
}
