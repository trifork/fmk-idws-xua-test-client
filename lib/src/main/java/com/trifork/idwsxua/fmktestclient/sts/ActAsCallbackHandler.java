package com.trifork.idwsxua.fmktestclient.sts;

import org.apache.cxf.ws.security.trust.delegation.DelegationCallback;
import org.w3c.dom.Element;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

public abstract class ActAsCallbackHandler implements CallbackHandler {
    @Override
    public void handle(Callback[] callbacks) {
        for (Callback callback : callbacks) {
            if (callback instanceof DelegationCallback) {
                DelegationCallback pc = (DelegationCallback) callback;
                pc.setToken(getToken());
            }
        }
    }

    protected abstract Element getToken();
}
