package com.trifork.idwsxua.fmktestclient.sts;

import static dk.sds.samlh.model.AttributeNameConstants.*;
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
                addClaim(claimsElement, SYSTEM_VERSION, "1.0");
                addClaim(claimsElement, SYSTEM_NAME, "fmk-idws-test-client");
                addClaim(claimsElement, SYSTEM_USING_ORGANISATION_NAME, "Trifork");
                addClaim(claimsElement, ORGANIZATION_NAME, "Softwareklinikken");
            }

            if (resourceId != null) {
                addClaim(claimsElement, RESOURCE_ID, resourceId.generate(Validate.YES));
            }
            if (providerIdentifier != null) {
                addClaim(claimsElement, PROVIDER_IDENTIFIER, providerIdentifier.generateElement(Validate.YES));
            }
            if (onBehalfOf != null) {
                addClaim(claimsElement, ON_BEHALF_OF, onBehalfOf.generate(Validate.YES));
            }
            if (context.getEducationCode() != null) {
                addClaim(claimsElement, USER_EDUCATION_CODE, context.getEducationCode());
            }
            if (context.getRole() != null) {
                Element roleElement = context.getRole().generateElement(Validate.YES);
                
                addClaim(claimsElement, ROLE, roleElement);

                // TODO: Remove this claim when STS is fixed to use the correct claim name for role above:
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
