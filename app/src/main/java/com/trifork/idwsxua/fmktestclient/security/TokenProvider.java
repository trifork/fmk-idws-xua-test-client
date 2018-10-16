package com.trifork.idwsxua.fmktestclient.security;

import com.trifork.idwsxua.fmktestclient.sts.XUASTSClient;
import com.trifork.idwsxua.fmktestclient.util.SAMLTokenBuilder;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

@Component
public class TokenProvider {

    private static Logger logger = LogManager.getLogger(TokenProvider.class);

    private final XUASTSClient stsClient;

    // Can be both a String (SAML XML) or a SecurityToken?
    private static final ThreadLocal<Object> tokenHolder = new ThreadLocal<>();

    private static final ThreadLocal<Assertion> assertionHolder = new ThreadLocal<>();

    @Autowired
    public TokenProvider(XUASTSClient stsClient) {
        this.stsClient = stsClient;
    }

    public static Object getToken() {
        final Object o = tokenHolder.get();
        return o;
    }

    public void refreshBootstrapToken() throws Exception {
        final Object samlOrBootstrapToken = tokenHolder.get();

        SecurityToken securityToken = null;
        if (samlOrBootstrapToken instanceof SecurityToken) {
            securityToken = (SecurityToken) samlOrBootstrapToken;
        }
        if (securityToken == null || securityToken.isExpired()) {
            final String samlToken = getSAMLToken();
            tokenHolder.set(samlToken);

            // perform a call to the STS to exchange the self-signed token into a "bootstrap"-token
            SecurityToken newSecurityToken = stsClient.requestSecurityToken("http://localhost:8080/service/hello");
            tokenHolder.set(newSecurityToken);
        }

    }

    private String getSAMLToken() throws IOException {
        final InputStream keystoreFileInputStream = new ClassPathResource("test-moces.pfx").getInputStream();

        String trustKeystorePassword = "Test1234";

        try {
            KeyStore keystore = getKeystore(keystoreFileInputStream, trustKeystorePassword);
            if (keystore != null) {

                Assertion assertion;

                final Assertion currentAssertion = assertionHolder.get();

                final DateTime now = new DateTime();
                final DateTime notOnOrAfter = currentAssertion == null ? null : currentAssertion.getConditions().getNotOnOrAfter();

                if (notOnOrAfter != null && notOnOrAfter.isBefore(now)) {
                    assertion = currentAssertion;
                } else {
                    assertion = new SAMLTokenBuilder().getAssertion(keystore, keystore.aliases().nextElement(), trustKeystorePassword);
                }

                Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(assertion);
                marshaller.marshall(assertion);

                Element plaintextElement = marshaller.marshall(assertion);
                String assertionString = XMLHelper.nodeToString(plaintextElement);

                return assertionString;
            }
        } catch (Exception ex) {
            logger.error("Failed to connect to backend", ex);
        }

        return null;
    }

    private KeyStore getKeystore(InputStream keystoreFileInputStream, String password) {
        KeyStore ks = null;

        try {
            ks = KeyStore.getInstance("PKCS12");
            ks.load(keystoreFileInputStream, password.toCharArray());
        } catch (GeneralSecurityException | IOException ex) {
            logger.error("Could not load keystore with provided password", ex);
        }

        return ks;
    }
}
