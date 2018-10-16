package com.trifork.idwsxua.fmktestclient.util;

import dk.itst.oiosaml.sp.util.AttributeUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.*;
import org.opensaml.saml2.core.impl.*;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.security.BasicSecurityConfiguration;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.signature.*;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.signature.impl.X509CertificateBuilder;
import org.opensaml.xml.signature.impl.X509DataBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.UUID;

public class SAMLTokenBuilder {

    private Logger logger = LogManager.getLogger(SAMLTokenBuilder.class);

    public SAMLTokenBuilder() throws Exception {
        DefaultBootstrap.bootstrap();

        BasicSecurityConfiguration config = (BasicSecurityConfiguration) Configuration.getGlobalSecurityConfiguration();
        config.setSignatureReferenceDigestMethod(SignatureConstants.ALGO_ID_DIGEST_SHA256);
    }

    public Assertion getAssertion(KeyStore ks, String alias, String password) {
        try {
            Issuer issuer = new IssuerBuilder().buildObject();
            issuer.setValue("CN=Henrik Jensen"); // self-issued

            NameID nameID = new NameIDBuilder().buildObject();
            nameID.setValue("CN=Henrik Jensen");
            nameID.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName");

            SubjectConfirmation confirmation = new SubjectConfirmationBuilder().buildObject();
            confirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer");

            Subject subject = new SubjectBuilder().buildObject();
            subject.setNameID(nameID);
            subject.getSubjectConfirmations().add(confirmation);

            Audience audience = new AudienceBuilder().buildObject();
            audience.setAudienceURI("http://sts.sundhedsdatastyrelsen.dk/");

            AudienceRestriction audienceRestriction = new AudienceRestrictionBuilder().buildObject();
            audienceRestriction.getAudiences().add(audience);

            Conditions conditions = new ConditionsBuilder().buildObject();
            conditions.setNotBefore(new DateTime().minus(5 * 1000));
            conditions.setNotOnOrAfter(new DateTime().plus(60 * 60 * 1000));
            conditions.getAudienceRestrictions().add(audienceRestriction);

            Attribute attribute = new AttributeBuilder().buildObject();
            attribute.setName("dk:gov:saml:attribute:AssuranceLevel");
            attribute.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:basic");
            attribute.getAttributeValues().add(AttributeUtil.createAttributeValue("2"));

            AttributeStatement attributeStatement = new AttributeStatementBuilder().buildObject();
            attributeStatement.getAttributes().add(attribute);

            AuthnContextClassRef authnContextClassRef = new AuthnContextClassRefBuilder().buildObject();
            authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:X509");

            AuthnContext authnContext = new AuthnContextBuilder().buildObject();
            authnContext.setAuthnContextClassRef(authnContextClassRef);

            AuthnStatement authnStatement = new AuthnStatementBuilder().buildObject();
            authnStatement.setAuthnInstant(new DateTime());
            authnStatement.setAuthnContext(authnContext);

            X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
            Credential signingCredential = SecurityHelper.getSimpleCredential(certificate, privateKey);

            org.opensaml.xml.signature.X509Certificate cert = new X509CertificateBuilder().buildObject();
            cert.setValue(Base64.encodeBase64String(certificate.getEncoded()));
            X509Data x509Data = new X509DataBuilder().buildObject();
            x509Data.getX509Certificates().add(cert);

            KeyInfo keyInfo = new KeyInfoBuilder().buildObject();
            keyInfo.getX509Datas().add(x509Data);

            Signature signature = new SignatureBuilder().buildObject();
            signature.setSigningCredential(signingCredential);
            signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
            signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
            signature.setKeyInfo(keyInfo);

            Assertion assertion = new AssertionBuilder().buildObject();
            assertion.setID(UUID.randomUUID().toString());
            assertion.setIssueInstant(new DateTime());
            assertion.setIssuer(issuer);
            assertion.setSubject(subject);
            assertion.setConditions(conditions);
            assertion.getAttributeStatements().add(attributeStatement);
            assertion.getAuthnStatements().add(authnStatement);
            assertion.setSignature(signature);

            Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(assertion);
            marshaller.marshall(assertion);

            Signer.signObject(signature);

            logToken(assertion);

            return assertion;
        } catch (Exception ex) {
            logger.error("Failed to generate self-signed token", ex);
        }

        return null;
    }

    private void logToken(Assertion assertion) throws Exception {
        AssertionMarshaller marshaller = new AssertionMarshaller();
        Element plaintextElement = marshaller.marshall(assertion);
        String assertionString = XMLHelper.prettyPrintXML(plaintextElement);

        logger.info("**** start self-signed assertion ****");
        logger.info(assertionString);
        logger.info("**** end self-signed assertion ****");
    }
}
