package com.trifork.idwsxua.fmktestclient.sts.callback;

import dk.sds.samlh.model.ModelUtil;
import org.apache.cxf.ws.security.trust.claims.ClaimsCallback;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import static dk.sds.samlh.model.AttributeNameConstants.*;

public abstract class AbstractClaimsCallbackHandler implements CallbackHandler {

    Element claimsElement;

    public abstract void addClaims(ClaimsCallback callback);

    AbstractClaimsCallbackHandler() {
        try {
            Document doc = ModelUtil.createDocument();
            claimsElement = doc.createElementNS("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "wst:Claims");
            claimsElement.setAttributeNS(null, "Dialect", "http://docs.oasis-open.org/wsfed/authorization/200706/authclaims");
//            doc.appendChild(claimsElement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof ClaimsCallback) {
                ClaimsCallback claimsCallback = (ClaimsCallback) callback;
                addClaims(claimsCallback);
            } else {
                throw new UnsupportedCallbackException(callback, "Unrecognized Callback");
            }
        }
    }

    static void addDefaultClaims(Element claimsElement) {
        // TODO: Do not hardcode values
        addClaim(claimsElement, SYSTEM_VERSION, "1.0");
        addClaim(claimsElement, SYSTEM_NAME, "fmk-idws-test-client");
        addClaim(claimsElement, SYSTEM_USING_ORGANISATION_NAME, "Trifork");

        // The following attribute is not needed as it will be set by the STS:
        //addClaim(claimsElement, ORGANIZATION_NAME, "Softwareklinikken");
    }

    @SuppressWarnings("Duplicates")
    static void addClaim(Element claimsElement, String name, String value) {
        Document doc = claimsElement.getOwnerDocument();
        Element claimType = doc.createElementNS("http://docs.oasis-open.org/wsfed/authorization/200706", "auth:ClaimType");
        claimType.setAttributeNS(null, "Uri", name);
        claimsElement.appendChild(claimType);

        Element claimValue = doc.createElementNS("http://docs.oasis-open.org/wsfed/authorization/200706", "auth:Value");
        claimValue.setTextContent(value);
        claimType.appendChild(claimValue);
    }

    @SuppressWarnings("Duplicates")
    static void addClaim(Element claimsElement, String name, Element value) {
        Document doc = claimsElement.getOwnerDocument();
        Element claimType = doc.createElementNS("http://docs.oasis-open.org/wsfed/authorization/200706", "auth:ClaimType");
        claimType.setAttributeNS(null, "Uri", name);
        claimsElement.appendChild(claimType);

        Element claimValue = doc.createElementNS("http://docs.oasis-open.org/wsfed/authorization/200706", "auth:Value");
        claimValue.appendChild(claimValue.getOwnerDocument().importNode(value, true));
        claimType.appendChild(claimValue);
    }
}
