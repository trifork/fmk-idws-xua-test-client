package com.trifork.idwsxua.fmktestclient.sts;


import com.trifork.idwsxua.fmktestclient.util.X509CertUtil;
import com.trifork.idwsxua.fmktestclient.util.XUAProperties;
import dk.sds.samlh.model.onbehalfof.OnBehalfOf;
import dk.sds.samlh.model.onbehalfof.OnBehalfOf.Legislation;
import dk.sds.samlh.model.provideridentifier.ProviderIdentifier;
import dk.sds.samlh.model.purposeofuse.PurposeOfUse;
import dk.sds.samlh.model.resourceid.ResourceId;
import dk.sds.samlh.model.role.Role;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.impl.AssertionMarshaller;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

@Component
public class TokenProvider {

    private static final Logger logger = LogManager.getLogger(TokenProvider.class);

    private final XUAProperties properties;

    private static final ThreadLocal<Assertion> assertionHolder = new ThreadLocal<>();
    private final PrivateKey employeeKey;
    private final X509Certificate employeeCertificate;

    public TokenProvider(XUAProperties properties) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, InitializationException {
        this.properties = properties;

        // Initialize OpenSAML
        InitializationService.initialize();

        final String keystoreFile = properties.getKeystoreFile();
        final String keystorePassword = properties.getKeystorePassword();
        final String privateKeyPassword = properties.getPrivateKeyPassword();

        // Setup assertion for bootstrap call
        final KeyStore employeeKeystore = X509CertUtil.getKeyStoreFromResource(keystoreFile, keystorePassword);
        String alias = employeeKeystore.aliases().nextElement();
        employeeCertificate = (X509Certificate) employeeKeystore.getCertificate(alias);
        employeeKey = (PrivateKey) employeeKeystore.getKey(alias, privateKeyPassword.toCharArray());
    }

    public void initSystemUserContext() {
        SessionContextHolder.get().setIncludeDefaultClaims(true);
        // TODO: Do not hardcode values
        setPatientContext();
    }

    public void refreshBootstrapToken(STSClient stsBootstrap) throws Exception {
        SecurityToken bootstrapToken = TokenHolder.bootstrapToken;
        if (bootstrapToken == null || bootstrapToken.isExpired()) {
            // Bootstrap token is null or expired

            // Refresh SAML Assertion if needed
            refreshSAMLAssertion();

            // Get new bootstrap token
            SecurityToken newBootstrapToken = stsBootstrap.requestSecurityToken(properties.getStsBootstrap().getAudience());
            TokenHolder.bootstrapToken = newBootstrapToken;
            TokenHolder.bootstrapTokenElement = newBootstrapToken.getToken();

            // Log the security token as pretty XML
            // logger.debug(XmlHelper.node2String(bootstrapToken.getToken(), true, true));

            SessionContextHolder.get().setIncludeDefaultClaims(true);
            setPatientContext();
        }
    }

    private void refreshSAMLAssertion() throws SignatureException, CertificateEncodingException {
        // Get existing assertion
        final Assertion currentAssertion = TokenHolder.assertion;

        final DateTime now = new DateTime();
        final DateTime notOnOrAfter = currentAssertion == null ? null : currentAssertion.getConditions().getNotOnOrAfter();

        try {
            if (notOnOrAfter != null && now.isBefore(notOnOrAfter)) {
                logger.info("Reusing SAML assertion");
            } else {
                logger.info("Building new SAML assertion");
                Assertion assertion = new OpenSaml3TokenBuilder(employeeCertificate, employeeKey).buildToken();
                assertionHolder.set(assertion);

                TokenHolder.assertion = assertion;

                AssertionMarshaller marshaller = new AssertionMarshaller();
                TokenHolder.selfsigned = marshaller.marshall(assertion);
            }

        } catch (MarshallingException e) {
            logger.error("Marshalling SAML assertion failed", e);
        }
    }

    private static ProviderIdentifier buildProviderIdentifier() {
        ProviderIdentifier providerIdentifier = new ProviderIdentifier();

        // TODO: Do not hardcode values
        providerIdentifier.setRoot("1.2.208.176.1.4");
        providerIdentifier.setXsiType("II");
        providerIdentifier.setExtension("541133" + "^description");

        return providerIdentifier;
    }

    private static ResourceId buildResourceId(String cpr) {
        ResourceId resourceId = new ResourceId();

        resourceId.setOid("1.2.208.176.1.2");
        resourceId.setPatientId(cpr);

        return resourceId;
    }

    private static PurposeOfUse buildPurposeOfUse(String code) {
        PurposeOfUse purposeOfUse = new PurposeOfUse();
        //purposeOfUse.setCodeSystemName();
        //purposeOfUse.setDisplayName();
        purposeOfUse.setCodeSystem("urn:oasis:names:tc:xspa:1.0");
        purposeOfUse.setXsiType("CE");
        PurposeOfUse.Code codeType = PurposeOfUse.Code.valueOf(code);
        purposeOfUse.setCode(codeType);

        return purposeOfUse;
    }

    private OnBehalfOf buildOnBehalfOf(String onBehalfOfAuth, String educationCode) {
        OnBehalfOf onBehalfOf = new OnBehalfOf();
        onBehalfOf.setAuthorizationCode(onBehalfOfAuth);
        onBehalfOf.setEducationCode(educationCode);
        onBehalfOf.setLegislation(Legislation.actThroughDelegationByAuthorizedHealthcareProfessional);
        return onBehalfOf;
    }

    private Role buildRole(String roleName) {
        Role role = new Role();
        role.setCode(roleName);

        // Use OID for FMK roles:
        role.setCodeSystem("1.2.208.176.7.2.1");

        // We could also use "Autorisationsregister" roles, but not all FMK roles can be represented!
        // role.setCode("7170");
        // role.setCodeSystem("1.2.208.176.1.3");
        // role.setCodeSystemName("Autorisationsregister");

        role.setDisplayName(roleName);
        role.setXsiType("CE");
        
        return role;
    }

    private void setPatientContext() {
        SessionContext ctx = SessionContextHolder.get();

        ResourceId patientContext = buildResourceId(properties.getPersonIdentifier());
        ctx.setResourceId(patientContext);

        String purposeOfUse = properties.getPurposeOfUse();
        if (purposeOfUse != null) {
            PurposeOfUse purposeOfUseType = buildPurposeOfUse(purposeOfUse);
            ctx.setPurposeOfUse(purposeOfUseType);
        }

        ProviderIdentifier providerIdentifier = buildProviderIdentifier();
        ctx.setProviderIdentifier(providerIdentifier);

        String onBehalfOfAuth = properties.getOnBehalfOfAuth();
        if (onBehalfOfAuth != null) {
            OnBehalfOf onBehalfOf = buildOnBehalfOf(onBehalfOfAuth, properties.getEducationCode());
            ctx.setOnBehalfOf(onBehalfOf);
        } else if (properties.getEducationCode() != null) {
            ctx.setEducationCode(properties.getEducationCode());
        }

        String role = properties.getRole();
        if (role != null) {
            Role roleElement = buildRole(role);
            ctx.setRole(roleElement);
        }
    }


}
