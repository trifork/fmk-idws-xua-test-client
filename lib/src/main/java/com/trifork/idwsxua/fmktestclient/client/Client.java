package com.trifork.idwsxua.fmktestclient.client;

import com.trifork.idwsxua.fmktestclient.callback.KeystorePasswordCallback;
import com.trifork.idwsxua.fmktestclient.interceptor.XUASTSOutInterceptor;
import com.trifork.idwsxua.fmktestclient.security.TokenProvider;
import com.trifork.idwsxua.fmktestclient.sts.SessionContext;
import com.trifork.idwsxua.fmktestclient.sts.SessionContextHolder;
import com.trifork.idwsxua.fmktestclient.sts.XUASTSClient;
import com.trifork.idwsxua.fmktestclient.util.Properties;
import org.apache.cxf.message.Message;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;

import javax.xml.ws.BindingProvider;
import java.util.Map;

public abstract class Client {

    final XUASTSClient stsBootstrap;
    final XUASTSClient stsIdentity;
    final TokenProvider tokenProvider;

    Client(Properties properties,
           XUASTSClient stsBootstrap,
           XUASTSClient stsIdentity,
           TokenProvider tokenProvider) throws InitializationException {
        this.stsBootstrap = stsBootstrap;
        this.stsIdentity = stsIdentity;
        this.tokenProvider = tokenProvider;

        // Initialize OpenSAML
        InitializationService.initialize();
        SessionContext context = new SessionContext();
        SessionContextHolder.set(context);

        // Add STS interceptor for logging
        final XUASTSOutInterceptor xuastsOutInterceptor = new XUASTSOutInterceptor();
        this.stsBootstrap.getOutInterceptors().add(xuastsOutInterceptor);

        // Change STS endpoint addresses
        this.stsBootstrap.getRequestContext()
                .put(Message.ENDPOINT_ADDRESS, properties.getStsBootstrap().getEndpoint());
        this.stsIdentity.getRequestContext()
                .put(Message.ENDPOINT_ADDRESS, properties.getStsIdentity().getEndpoint());
    }

    public abstract void callService(String personIdentifier) throws Exception;

    void addWSSecurity(BindingProvider bindingProvider, XUASTSClient stsClient) {
        KeystorePasswordCallback callbackHandler = new KeystorePasswordCallback("Test1234"); // TODO
        Map<String, Object> props = bindingProvider.getRequestContext();
        props.put("ws-security.sts.applies-to", "https://fmk");
        props.put("ws-security.sts.client", stsClient);
        props.put("ws-security.encryption.username", "idws xua test wsc (funktionscertifikat)");
        props.put("ws-security.signature.properties", "wsc-client.properties");
        props.put("ws-security.encryption.properties", "wsc-client.properties");
        props.put("ws-security.callback-handler", callbackHandler);
        props.put("ws-security.asymmetric.signature.algorithm", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
    }
}
