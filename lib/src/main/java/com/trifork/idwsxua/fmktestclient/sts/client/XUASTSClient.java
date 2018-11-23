package com.trifork.idwsxua.fmktestclient.sts.client;

import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.Bus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.rt.security.claims.ClaimCollection;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.staxutils.W3CDOMStreamWriter;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.cxf.ws.security.trust.claims.ClaimsCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import javax.security.auth.callback.Callback;
import javax.xml.stream.XMLStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;

public class XUASTSClient extends STSClient {

    private static final Logger logger = LogManager.getLogger(XUASTSClient.class);

    public XUASTSClient(Bus b) {
        super(b);

        getOutInterceptors().add(new LoggingOutInterceptor());
        getInInterceptors().add(new LoggingInInterceptor());
    }

    @Override
    protected void writeElementsForRSTPublicKey(W3CDOMStreamWriter writer, X509Certificate cert) throws Exception {
        writer.writeStartElement("wst", "UseKey", namespace);
        writer.writeStartElement("wsse", "BinarySecurityToken", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        //writer.writeAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
        writer.writeAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-selfsigned-profile-1.0#X509v3");
        writer.writeAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
        writer.getCurrentNode().setTextContent(new String(Base64.encodeBase64(cert.getEncoded()), StandardCharsets.UTF_8));

        writer.writeEndElement();
        writer.writeEndElement();
    }

    protected void addClaims(XMLStreamWriter writer) throws Exception {
        Object claimsToSerialize = this.claims;
        if (claimsToSerialize == null && this.claimsCallbackHandler != null) {
            ClaimsCallback callback = new ClaimsCallback(this.message);
            this.claimsCallbackHandler.handle(new Callback[]{callback});
            claimsToSerialize = callback.getClaims();
        }

        if (claimsToSerialize instanceof Element) {
            StaxUtils.copy((Element) claimsToSerialize, writer);
        } else if (claimsToSerialize instanceof ClaimCollection) {
            // original implementation used this.claims instead
            // this is a bug fix as claims would still be null after callback
            ClaimCollection claimCollection = (ClaimCollection) claimsToSerialize;
            claimCollection.serialize(writer, "wst", this.namespace);
        }

    }
}
