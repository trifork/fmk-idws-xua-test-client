package com.trifork.idwsxua.fmktestclient.util;

import com.trifork.idwsxua.fmktestclient.client.ApiVersion;

public interface XUAProperties {
    String getKeystoreFile();

    String getKeystorePassword();

    String getPrivateKeyPassword();

    String getStsWsdl();

    String getWebserviceEndpoint();

    ApiVersion getApiVersion();

    STSBootstrap getStsBootstrap();

    STSIdentity getStsIdentity();

    String getPersonIdentifier();

    int getRepeats();

    int getRepeatDelayMs();

    String getRole();

    String getOnBehalfOfAuth();

    String getOnBehalfOfCpr();

    String getEducationCode();

    class STSBootstrap {
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

    class STSIdentity {
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
