/*
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * $HeadURL$
 * $Id$
 */

package com.trifork.idwsxua.fmktestclient.handmaderequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OIOWSTrustRequestDOMBuilder {

    private String audience;
    private Element bootstrapToken;
    private X509Certificate useKey;
    private Map<String, ClaimValue> claims = new HashMap<>();

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public void setActAs(Element actAs) {
        this.bootstrapToken = actAs;
    }

    public void setUseKey(X509Certificate useKey) {
        this.useKey = useKey;
    }

    public void addClaim(String name, String value) {
        claims.put(name, new TextClaimValue(value));
    }

    public void addClaim(String name, Element value) {
        claims.put(name, new ElementClaimValue(value));
    }

    public SOAPMessage createSOAPMessage() throws SOAPException {
        if (audience == null || audience.trim().length() == 0) {
            throw new IllegalStateException("audience" + " is missing.");
        }
        SOAPMessage soapMessage = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL).createMessage();
        final SOAPBody soapBody = soapMessage.getSOAPBody();
        final SOAPElement requestSecurityToken = addTokenRequest(soapBody);
        addActAs(requestSecurityToken);
        addAudience(soapBody.getOwnerDocument(), requestSecurityToken);
        addClaims(requestSecurityToken);
        return soapMessage;
    }

    private SOAPElement addTokenRequest(SOAPElement body) throws SOAPException {
        Document doc = body.getOwnerDocument();
        SOAPElement requestSecurityToken =  body.addChildElement(new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "RequestSecurityToken", "wst"));
        requestSecurityToken.setAttributeNS(null, "Context", "urn:uuid:" + UUID.randomUUID());

        final Element tokenType = doc.createElementNS("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "wst" + ":TokenType");
//        tokenType.setTextContent("http://docs.oasis-open.org/wss/oasis-wss-saml-selfsigned-profile-1.1#SAMLV2.0");
        tokenType.setTextContent("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0");
        requestSecurityToken.appendChild(tokenType);

        final Element requestType = doc.createElementNS("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "wst" + ":RequestType");
        requestType.setTextContent("http://docs.oasis-open.org/ws-sx/ws-trust/200512/Issue");
        requestSecurityToken.appendChild(requestType);

        if (useKey != null) {
            final Element keyType = doc.createElementNS("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "wst:KeyType");
            keyType.setTextContent("http://docs.oasis-open.org/ws-sx/ws-trust/200512/PublicKey");
            requestSecurityToken.appendChild(keyType);

            final Element useKey = doc.createElementNS("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "wst:UseKey");
            final Element bst = doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:BinarySecurityToken");
            bst.setAttributeNS(null, "ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-selfsigned-profile-1.0#X509v3");
            bst.setAttributeNS(null, "EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");

            final String base64Cert;
            try {
                base64Cert = Base64.getMimeEncoder().encodeToString(this.useKey.getEncoded());
            } catch (CertificateEncodingException e) {
                throw new RuntimeException("Unable to get encoded certificate", e);
            }
            bst.setTextContent(base64Cert);
            useKey.appendChild(bst);

            requestSecurityToken.appendChild(useKey);
        }
        return requestSecurityToken;
    }

    private void addActAs(SOAPElement requestSecurityToken) throws SOAPException {
        if (bootstrapToken != null) {
            final QName actAsQname = new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200802", "ActAs", "wst14");
            SOAPElement actAs = requestSecurityToken.addChildElement(actAsQname);
            actAs.appendChild(requestSecurityToken.getOwnerDocument().importNode(bootstrapToken, true));
        }
    }

    private void addAudience(Document doc, SOAPElement requestSecurityToken) {
        final Element appliesTo = doc.createElementNS("http://schemas.xmlsoap.org/ws/2004/09/policy", "wsp:AppliesTo");
        requestSecurityToken.appendChild(appliesTo);

        final Element endpointReference = doc.createElementNS("http://www.w3.org/2005/08/addressing", "wsa:EndpointReference");
        appliesTo.appendChild(endpointReference);

        final Element address = doc.createElementNS("http://www.w3.org/2005/08/addressing", "wsa:Address");
        endpointReference.appendChild(address);
        address.setTextContent(audience);
    }

    private void addClaims(SOAPElement requestSecurityToken) throws SOAPException {
        if (claims.isEmpty()) {
            return;
        }
        SOAPElement claimsElm = requestSecurityToken.addChildElement(new QName("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "Claims", "wst"));
        claimsElm.setAttributeNS(null, "Dialect", "http://docs.oasis-open.org/wsfed/authorization/200706/authclaims");
        for (String claim : claims.keySet()) {
            buildClaimElement(claimsElm, claim, claims.get(claim));
        }
    }

    private void buildClaimElement(SOAPElement claimsElm, String claim, ClaimValue claimValue) throws SOAPException {
        SOAPElement claimElement = claimsElm.addChildElement(new QName("http://docs.oasis-open.org/wsfed/authorization/200706", "ClaimType", "auth"));
        claimElement.setAttributeNS(null, "Uri", claim);

        SOAPElement valueElm = claimElement.addChildElement(new QName("http://docs.oasis-open.org/wsfed/authorization/200706", "Value", "auth"));
        claimValue.appendValueContent(valueElm);
    }

    interface ClaimValue {
        void appendValueContent(Element valueElm);
    }

    class TextClaimValue implements ClaimValue {
        private final String text;

        private TextClaimValue(String text) {
            this.text = text;
        }

        @Override
        public void appendValueContent(Element valueElm) {
            valueElm.setTextContent(text);
        }
    }

    class ElementClaimValue implements ClaimValue {
        private final Element element;

        private ElementClaimValue(Element element) {
            this.element = element;
        }

        @Override
        public void appendValueContent(Element valueElm) {
            Node imported = valueElm.getOwnerDocument().importNode(this.element, true);
            valueElm.appendChild(imported);
        }
    }
}