package com.trifork.idwsxua.fmktestclient.sts;

import dk.sds.samlh.model.onbehalfof.OnBehalfOf;
import dk.sds.samlh.model.provideridentifier.ProviderIdentifier;
import dk.sds.samlh.model.resourceid.ResourceId;
import dk.sds.samlh.model.role.Role;

public class SessionContext {
	private ResourceId resourceId;
	private ProviderIdentifier providerIdentifier;
	private OnBehalfOf onBehalfOf;
	private String educationCode;
    private boolean includeDefaultClaims;
    private Role role;


    public ResourceId getResourceId() {
		return resourceId;
	}

	public void setResourceId(ResourceId resourceId) {
		this.resourceId = resourceId;
	}

	public ProviderIdentifier getProviderIdentifier() {
		return providerIdentifier;
	}

	public void setProviderIdentifier(ProviderIdentifier providerIdentifier) {
		this.providerIdentifier = providerIdentifier;
	}

    public OnBehalfOf getOnBehalfOf() {
        return onBehalfOf;
    }

    public void setOnBehalfOf(OnBehalfOf onBehalfOf) {
        this.onBehalfOf = onBehalfOf;
    }

    public String getEducationCode() {
        return educationCode;
    }

    public void setEducationCode(String educationCode) {
        this.educationCode = educationCode;
    }

    public boolean isIncludeDefaultClaims() {
        return includeDefaultClaims;
    }

    public void setIncludeDefaultClaims(boolean includeDefaultClaims) {
        this.includeDefaultClaims = includeDefaultClaims;
    }

    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
}
