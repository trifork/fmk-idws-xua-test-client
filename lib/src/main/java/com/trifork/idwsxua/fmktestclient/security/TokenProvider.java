package com.trifork.idwsxua.fmktestclient.security;


import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

   /* private static final Logger logger = LogManager.getLogger(TokenProvider.class);

    private final XUASTSClient sts;
    private final Properties configuration;

    private static final ThreadLocal<Object> tokenHolder = new ThreadLocal<>(); // Can be both a String (SAML XML) or a SecurityToken

    private static final ThreadLocal<Assertion> assertionHolder = new ThreadLocal<>();

    @Autowired
    public TokenProvider(XUASTSClient sts, Properties configuration) {
        this.sts = sts;
        this.configuration = configuration;
    }

    public static Object getToken() {
        return tokenHolder.get();
    }

    public void refreshBootstrapToken() {
        final Object samlOrBootstrapToken = tokenHolder.get();

        SecurityToken securityToken = null;
        if (samlOrBootstrapToken instanceof SecurityToken) {
            // Reuse existing Security token
            securityToken = (SecurityToken) samlOrBootstrapToken;
        }
        if (securityToken == null || securityToken.isExpired()) {
            // Security token is null or expired

            final String samlToken = getSAMLToken();
            tokenHolder.set(samlToken);

            // perform a call to the STS to exchange the self-signed token into a "bootstrap"-token
            SecurityToken newSecurityToken = null;
            try {
                logger.info("Requesting new bootstrap token");

                sts.getClient().getRequestContext().put(Message.ENDPOINT_ADDRESS, "https://test1-cnsp.ekstern-test.nspop.dk:8443/sts3/services/employee/bootstrap");

                newSecurityToken = sts.requestSecurityToken("http://localhost:8080/service/hello");

                sts.getClient().getRequestContext().put(Message.ENDPOINT_ADDRESS, "https://test1-cnsp.ekstern-test.nspop.dk:8443/sts3/services/employee");

            } catch (Exception e) {
                logger.error("Error requesting security token", e);
            }
            tokenHolder.set(newSecurityToken);
        }
    }

    private String getSAMLToken() {
        String result = null;

        // Get existing assertion
        final Assertion currentAssertion = assertionHolder.get();

        final DateTime now = new DateTime();
        final DateTime notOnOrAfter = currentAssertion == null ? null : currentAssertion.getConditions().getNotOnOrAfter();

        Assertion assertion;
        try {
            if (notOnOrAfter != null && notOnOrAfter.isBefore(now)) {
                logger.info("Reusing SAML assertion");
                assertion = currentAssertion;
            } else {
                logger.info("Building new SAML assertion");
                assertion = new SAMLTokenBuilder().getAssertion(configuration.getKeystore(), configuration.getKeystore().aliases().nextElement(), configuration.getPrivateKeyPassword());
            }

            Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(assertion);
            marshaller.marshall(assertion);

            Element plaintextElement = marshaller.marshall(assertion);

            result = XMLHelper.nodeToString(plaintextElement);
        } catch (MarshallingException e) {
            logger.error("Mashalling SAML assertion failed", e);
        } catch (KeyStoreException e) {
            logger.error("KeyStore error", e);
        }

        return result;
    }*/
}
