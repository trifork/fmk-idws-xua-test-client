package com.trifork.idwsxua.fmktestclient.sts.callback;

import com.trifork.idwsxua.fmktestclient.sts.SessionContext;
import com.trifork.idwsxua.fmktestclient.sts.SessionContextHolder;
import dk.sds.samlh.model.Validate;
import dk.sds.samlh.model.provideridentifier.ProviderIdentifier;
import dk.sds.samlh.model.resourceid.ResourceId;
import org.apache.cxf.ws.security.trust.claims.ClaimsCallback;

import static dk.sds.samlh.model.AttributeNameConstants.PROVIDER_IDENTIFIER;
import static dk.sds.samlh.model.AttributeNameConstants.RESOURCE_ID;

public class SystemClaimsCallbackHandler extends AbstractClaimsCallbackHandler {

    @Override
    public void addClaims(ClaimsCallback callback) {
        SessionContext context = SessionContextHolder.get();

        try {
            if (context.isIncludeDefaultClaims()) {
                addDefaultClaims(claimsElement);
            }

            final ResourceId resourceId = context.getResourceId();
            if (resourceId != null) {
                addClaim(claimsElement, RESOURCE_ID, resourceId.generate(Validate.YES));
            }

            final ProviderIdentifier providerIdentifier = context.getProviderIdentifier();
            if (providerIdentifier != null) {
                addClaim(claimsElement, PROVIDER_IDENTIFIER, providerIdentifier.generateElement(Validate.YES));
            }

            callback.setClaims(claimsElement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
