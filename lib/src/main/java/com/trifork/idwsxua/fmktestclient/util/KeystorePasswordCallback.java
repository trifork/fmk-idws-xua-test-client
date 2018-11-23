package com.trifork.idwsxua.fmktestclient.util;

import org.apache.wss4j.common.ext.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import java.util.Arrays;

public class KeystorePasswordCallback implements CallbackHandler {

    private String keystorePassword;

    public KeystorePasswordCallback(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    @Override
    public void handle(Callback[] callbacks) {
        Arrays.stream(callbacks).forEach(callback -> {
            if (!(callback instanceof WSPasswordCallback)) {
                throw new RuntimeException("Callback is not instance of WSPasswordCallback");
            }
            WSPasswordCallback pc = (WSPasswordCallback) callback;

            int usage = pc.getUsage();
            if (usage == WSPasswordCallback.DECRYPT || usage == WSPasswordCallback.SIGNATURE) {
                pc.setPassword(keystorePassword);
            }
        });
    }
}
