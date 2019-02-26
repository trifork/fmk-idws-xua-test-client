package com.trifork.idwsxua.fmktestclient.sts.callback;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

public class WSPasswordCallbackHandler implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks) {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callback;
                if (pc.getUsage() == WSPasswordCallback.DECRYPT || pc.getUsage() == WSPasswordCallback.SIGNATURE) {
                    // TODO: Do not hardcode password
                    pc.setPassword("Test1234");
                }
            }
        }
    }
}
