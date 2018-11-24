package com.trifork.idwsxua.fmktestclient.sts;

import dk.sds.samlh.model.ModelUtil;
import dk.sds.samlh.model.Validate;
import dk.sds.samlh.model.onbehalfof.OnBehalfOf;
import dk.sds.samlh.model.provideridentifier.ProviderIdentifier;
import dk.sds.samlh.model.resourceid.ResourceId;
import org.apache.cxf.ws.security.trust.claims.ClaimsCallback;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

public class ClaimsCallbackHandler implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof ClaimsCallback) {
                ClaimsCallback claimsCallback = (ClaimsCallback) callback;
                createClaims(claimsCallback);
            } else {
                throw new UnsupportedCallbackException(callback, "Unrecognized Callback");
            }
        }
    }

    private static void createClaims(ClaimsCallback callback) {
        SessionContext context = SessionContextHolder.get();
        if (context == null) {
            return;
        }

        try {
            final ResourceId resourceId = context.getResourceId();
            final ProviderIdentifier providerIdentifier = context.getProviderIdentifier();
            final OnBehalfOf onBehalfOf = context.getOnBehalfOf();

            Document doc = ModelUtil.createDocument();
            Element claimsElement = doc.createElementNS("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "wst:Claims");
            claimsElement.setAttributeNS(null, "Dialect", "http://docs.oasis-open.org/wsfed/authorization/200706/authclaims");
            doc.appendChild(claimsElement);

            if (context.isIncludeDefaultClaims()) {
                addClaim(claimsElement, "dk:healthcare:saml:attribute:SystemVersion", "1.5");
                addClaim(claimsElement, "dk:healthcare:saml:attribute:SystemName", "test-client");
                addClaim(claimsElement, "dk:healthcare:saml:attribute:SystemUsingOrganisationName", "Region Test");
                addClaim(claimsElement, "urn:oid:2.5.4.10", "Syfilis klinikken");
            }

            if (resourceId != null) {
                addClaim(claimsElement, "urn:oasis:names:tc:xacml:2.0:resource:resource-id", resourceId.generate(Validate.YES));
            }
            if (providerIdentifier != null) {
                addClaim(claimsElement, "urn:ihe:iti:xua:2017:subject:provider-identifier", providerIdentifier.generateElement(Validate.YES));
            }
            if (onBehalfOf != null) {
                addClaim(claimsElement, "dk:healthcare:saml:attribute:OnBehalfOf", onBehalfOf.generate(Validate.YES));
            }
            if (context.getEducationCode() != null) {
                addClaim(claimsElement, "dk:healthcare:saml:attribute:UserEducationCode", context.getEducationCode());
            }
            if (context.getRole() != null) {
                Element roleElement = context.getRole().generateElement(Validate.YES);
                
                // TODO: When STS is fixed, do this instead:
                // addClaim(claimsElement, "urn:oasis:names:tc:xacml:2.0:subject:role", roleElement);
                addClaim(claimsElement, "urn:oasis:names:tc:xspa:1.0:subject:role", roleElement);
            }

            callback.setClaims(claimsElement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static void addClaim(Element claimsElement, String name, String value) {
        Document doc = claimsElement.getOwnerDocument();
        Element claimType = doc.createElementNS("http://docs.oasis-open.org/wsfed/authorization/200706", "auth:ClaimType");
        claimType.setAttributeNS(null, "Uri", name);
        claimsElement.appendChild(claimType);

        Element claimValue = doc.createElementNS("http://docs.oasis-open.org/wsfed/authorization/200706", "auth:Value");
        claimValue.setTextContent(value);
        claimType.appendChild(claimValue);
    }

    private static void addClaim(Element claimsElement, String name, Element value) {
        Document doc = claimsElement.getOwnerDocument();
        Element claimType = doc.createElementNS("http://docs.oasis-open.org/wsfed/authorization/200706", "auth:ClaimType");
        claimType.setAttributeNS(null, "Uri", name);
        claimsElement.appendChild(claimType);

        Element claimValue = doc.createElementNS("http://docs.oasis-open.org/wsfed/authorization/200706", "auth:Value");
        claimValue.appendChild(claimValue.getOwnerDocument().importNode(value, true));
        claimType.appendChild(claimValue);
    }

}
