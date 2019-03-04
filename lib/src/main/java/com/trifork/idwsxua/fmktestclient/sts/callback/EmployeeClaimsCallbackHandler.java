package com.trifork.idwsxua.fmktestclient.sts.callback;

import com.trifork.idwsxua.fmktestclient.sts.SessionContext;
import com.trifork.idwsxua.fmktestclient.sts.SessionContextHolder;
import dk.sds.samlh.model.ModelUtil;
import dk.sds.samlh.model.Validate;
import dk.sds.samlh.model.onbehalfof.OnBehalfOf;
import dk.sds.samlh.model.provideridentifier.ProviderIdentifier;
import dk.sds.samlh.model.resourceid.ResourceId;
import org.apache.cxf.ws.security.trust.claims.ClaimsCallback;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static dk.sds.samlh.model.AttributeNameConstants.*;

public class EmployeeClaimsCallbackHandler extends AbstractClaimsCallbackHandler {

    @Override
    public void addClaims(ClaimsCallback callback) {
        SessionContext context = SessionContextHolder.get();

        try {
            final ResourceId resourceId = context.getResourceId();
            final ProviderIdentifier providerIdentifier = context.getProviderIdentifier();
            final OnBehalfOf onBehalfOf = context.getOnBehalfOf();

            Document doc = ModelUtil.createDocument();
            Element claimsElement = doc.createElementNS("http://docs.oasis-open.org/ws-sx/ws-trust/200512", "wst:Claims");
            claimsElement.setAttributeNS(null, "Dialect", "http://docs.oasis-open.org/wsfed/authorization/200706/authclaims");
            doc.appendChild(claimsElement);

            if (context.isIncludeDefaultClaims()) {
                addDefaultClaims(claimsElement);
            }

            if (resourceId != null) {
                addClaim(claimsElement, RESOURCE_ID, resourceId.generate(Validate.YES));
                if (context.getPurposeOfUse() != null) {
                    addClaim(claimsElement, PURPOSE_OF_USE, context.getPurposeOfUse().generateElement(Validate.YES));
                }
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
                addClaim(claimsElement, ROLE, context.getRole().generateElement(Validate.YES));
            }

            callback.setClaims(claimsElement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
