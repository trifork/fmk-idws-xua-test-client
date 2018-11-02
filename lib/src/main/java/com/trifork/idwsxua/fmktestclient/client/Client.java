package com.trifork.idwsxua.fmktestclient.client;

import com.trifork.idwsxua.fmktestclient.callback.KeystorePasswordCallback;
import com.trifork.idwsxua.fmktestclient.interceptor.XUASTSOutInterceptor;
import com.trifork.idwsxua.fmktestclient.security.TokenProvider;
import com.trifork.idwsxua.fmktestclient.sts.SessionContext;
import com.trifork.idwsxua.fmktestclient.sts.SessionContextHolder;
import com.trifork.idwsxua.fmktestclient.sts.XUASTSClient;
import com.trifork.idwsxua.fmktestclient.sts.client.STSClientWrapper;
import com.trifork.idwsxua.fmktestclient.util.Properties;
import org.apache.cxf.message.Message;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.ws.BindingProvider;
import java.io.StringWriter;
import java.util.Map;

public abstract class Client {

    protected Properties properties;
    protected XUASTSClient stsBootstrap;
    protected final XUASTSClient stsIdentity;
    protected final TokenProvider tokenProvider;

    Client(Properties properties,
           STSClientWrapper stsBootstrap,
           STSClientWrapper stsIdentity,
           TokenProvider tokenProvider) {
        this.properties = properties;
        this.stsBootstrap = stsBootstrap.getClient();
        this.stsIdentity = stsIdentity.getClient();
        this.tokenProvider = tokenProvider;

        SessionContext context = new SessionContext();
        SessionContextHolder.set(context);

        // Add STS interceptors for logging
        final XUASTSOutInterceptor bootstrapSTSOutInterceptor = new XUASTSOutInterceptor("Bootstrap STS");
        this.stsBootstrap.getOutInterceptors().add(bootstrapSTSOutInterceptor);
        final XUASTSOutInterceptor identitySTSOutInterceptor = new XUASTSOutInterceptor("Identity STS");
        this.stsIdentity.getOutInterceptors().add(identitySTSOutInterceptor);

        // Change STS endpoint addresses
        this.stsBootstrap.getRequestContext()
                .put(Message.ENDPOINT_ADDRESS, properties.getStsBootstrap().getEndpoint());
        this.stsIdentity.getRequestContext()
                .put(Message.ENDPOINT_ADDRESS, properties.getStsIdentity().getEndpoint());
    }

    @PostConstruct
    private void configure() {
        // Change WSP endpoint address
        BindingProvider provider = (BindingProvider) getPort();
        provider.getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getWebserviceEndpoint());

        addWSSecurity((BindingProvider) getPort(), stsIdentity);
    }

    protected abstract Object getPort();

    public abstract void callService(String personIdentifier) throws Exception;

    protected String getResponseString(JAXBElement jaxbElement, Marshaller marshaller) throws JAXBException {
        StringWriter sw = new StringWriter();
        marshaller.marshal(jaxbElement, sw);
        return sw.toString();
    }

    private void addWSSecurity(BindingProvider bindingProvider, XUASTSClient stsClient) {
        KeystorePasswordCallback callbackHandler = new KeystorePasswordCallback("Test1234"); // TODO
        Map<String, Object> props = bindingProvider.getRequestContext();
        props.put("ws-security.sts.applies-to", "https://fmk"); // TODO
        props.put("ws-security.sts.client", stsClient);
        props.put("ws-security.encryption.username", "idws xua test wsc (funktionscertifikat)"); // TODO
        props.put("ws-security.signature.properties", "wsc-client.properties");
        props.put("ws-security.encryption.properties", "wsc-client.properties");
        props.put("ws-security.callback-handler", callbackHandler);
        props.put("ws-security.asymmetric.signature.algorithm", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
    }
}
