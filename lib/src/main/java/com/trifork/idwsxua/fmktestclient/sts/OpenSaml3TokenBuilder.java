package com.trifork.idwsxua.fmktestclient.sts;

import org.joda.time.DateTime;
import org.opensaml.core.xml.Namespace;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.impl.XSAnyBuilder;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.core.impl.*;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.impl.KeyInfoBuilder;
import org.opensaml.xmlsec.signature.impl.SignatureBuilder;
import org.opensaml.xmlsec.signature.impl.X509CertificateBuilder;
import org.opensaml.xmlsec.signature.impl.X509DataBuilder;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.w3c.dom.Element;

import javax.security.auth.x500.X500Principal;
import javax.xml.namespace.QName;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;

public class OpenSaml3TokenBuilder {

	public static final QName XSI_TYPE_ATTRIBUTE_NAME = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");
	public static final String XS_STRING = "xs:string";

	public static final int ONE_HOUR = 1000 * 60 * 60;
	private final Date created = new Date(System.currentTimeMillis());
    private final X509Certificate certificate;
    private final PrivateKey privateKey;
    private DateTime notBefore = DateTime.now().minusSeconds(3600);
	private DateTime expires = DateTime.now().plusSeconds(3600);
	private String audienceRestriction;

	public OpenSaml3TokenBuilder(X509Certificate certificate, PrivateKey privateKey) {
        this.certificate = certificate;
        this.privateKey = privateKey;

    }

	public void setAudienceRestriction(String audienceRestriction) {
		this.audienceRestriction = Objects.requireNonNull(audienceRestriction, "AudienceRestriction is required");
	}


	public Assertion buildToken() throws CertificateEncodingException, SignatureException, MarshallingException {
		final String nameIdValue = certificate.getSubjectX500Principal().getName(X500Principal.RFC1779, Collections.singletonMap("2.5.4.5", "SERIALNUMBER"));

		Issuer issuer = new IssuerBuilder().buildObject();
		issuer.setValue(nameIdValue); // self-issued

		NameID nameID = new NameIDBuilder().buildObject();
		nameID.setValue(nameIdValue);
		nameID.setFormat("urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName");

		SubjectConfirmation confirmation = new SubjectConfirmationBuilder().buildObject();
		confirmation.setMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer");

		Subject subject = new SubjectBuilder().buildObject();
		subject.setNameID(nameID);
		subject.getSubjectConfirmations().add(confirmation);

		Audience audience = new AudienceBuilder().buildObject();
		audience.setAudienceURI(audienceRestriction);

		AudienceRestriction audienceRestriction = new AudienceRestrictionBuilder().buildObject();
		audienceRestriction.getAudiences().add(audience);

		Conditions conditions = new ConditionsBuilder().buildObject();
		conditions.setNotBefore(notBefore);
		conditions.setNotOnOrAfter(expires);
		conditions.getAudienceRestrictions().add(audienceRestriction);

		Attribute attribute = new AttributeBuilder().buildObject();
		attribute.setName("dk:gov:saml:attribute:AssuranceLevel");
		attribute.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:basic");
		attribute.getAttributeValues().add(createAttributeValue("3"));

		AttributeStatement attributeStatement = new AttributeStatementBuilder().buildObject();
		attributeStatement.getAttributes().add(attribute);

		AuthnContextClassRef authnContextClassRef = new AuthnContextClassRefBuilder().buildObject();
		authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:X509");

		AuthnContext authnContext = new AuthnContextBuilder().buildObject();
		authnContext.setAuthnContextClassRef(authnContextClassRef);

		AuthnStatement authnStatement = new AuthnStatementBuilder().buildObject();
		authnStatement.setAuthnInstant(new DateTime(created.getTime()));
		authnStatement.setAuthnContext(authnContext);


		org.opensaml.xmlsec.signature.X509Certificate cert = new X509CertificateBuilder().buildObject();
		cert.setValue(new String(Base64.getMimeEncoder().encode(certificate.getEncoded()), StandardCharsets.UTF_8));
		X509Data x509Data = new X509DataBuilder().buildObject();
		x509Data.getX509Certificates().add(cert);

		KeyInfo keyInfo = new KeyInfoBuilder().buildObject();
		keyInfo.getX509Datas().add(x509Data);

		Signature signature = new SignatureBuilder().buildObject();
		signature.setSigningCredential(new BasicCredential(this.certificate.getPublicKey(), this.privateKey));
		signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
		signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
		signature.setKeyInfo(keyInfo);

		Assertion assertion = new AssertionBuilder().buildObject();
		assertion.setID(UUID.randomUUID().toString());
		assertion.setIssueInstant(new DateTime(created.getTime()));
		assertion.setIssuer(issuer);
		assertion.setSubject(subject);
		assertion.setConditions(conditions);
		assertion.getAttributeStatements().add(attributeStatement);
		assertion.getAuthnStatements().add(authnStatement);
		assertion.setSignature(signature);

		AssertionMarshaller marshaller = new AssertionMarshaller();
		marshaller.marshall(assertion);

		Signer.signObject(signature);

		return assertion;
	}

	public static XSAny createAttributeValue(String value, String type) {
		XSAnyBuilder builder = new XSAnyBuilder();
		XSAny ep = builder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "AttributeValue", "saml2");
		ep.setTextContent(String.valueOf(value));
		ep.getUnknownAttributes().put(XSI_TYPE_ATTRIBUTE_NAME, type);
		ep.getNamespaceManager().registerNamespaceDeclaration(new Namespace("http://www.w3.org/2001/XMLSchema-instance", "xsi"));
		return ep;
	}

	public static XSAny createAttributeValue(String value) {
		return createAttributeValue(value, XS_STRING);
	}

}
