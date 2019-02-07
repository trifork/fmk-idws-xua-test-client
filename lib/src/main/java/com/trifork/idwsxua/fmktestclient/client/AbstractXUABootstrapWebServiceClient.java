package com.trifork.idwsxua.fmktestclient.client;

import com.trifork.idwsxua.fmktestclient.sts.SessionContext;
import com.trifork.idwsxua.fmktestclient.sts.SessionContextHolder;
import com.trifork.idwsxua.fmktestclient.sts.TokenProvider;
import com.trifork.idwsxua.fmktestclient.sts.client.STSClientWrapper;
import com.trifork.idwsxua.fmktestclient.sts.client.XUASTSClient;
import com.trifork.idwsxua.fmktestclient.util.KeystorePasswordCallback;
import com.trifork.idwsxua.fmktestclient.util.XUAProperties;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.MessageInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

public abstract class AbstractXUABootstrapWebServiceClient implements XUAWebServiceClient {

    private static final Logger logger = LogManager.getLogger(AbstractXUABootstrapWebServiceClient.class);

    protected final XUAProperties properties;
    protected final XUASTSClient stsBootstrap;
    protected final XUASTSClient stsIdentity;
    protected final TokenProvider tokenProvider;
    private final java.util.Properties wss4jProperties;

    AbstractXUABootstrapWebServiceClient(XUAProperties properties,
                                         STSClientWrapper stsBootstrap,
                                         STSClientWrapper stsIdentity,
                                         TokenProvider tokenProvider) throws IOException {
        this.properties = properties;
        this.stsBootstrap = stsBootstrap.getClient();
        this.stsIdentity = stsIdentity.getClient();
        this.tokenProvider = tokenProvider;

        wss4jProperties = new java.util.Properties();
        final ClassPathResource classPathResource = new ClassPathResource("sts-client.properties");
        // Auto close stream using try-with-resources
        try (InputStream inputStream = classPathResource.getInputStream()) {
            wss4jProperties.load(inputStream);
        }

        SessionContext context = new SessionContext();
        SessionContextHolder.set(context);

        // Add interceptors to STSes for logging
        this.stsBootstrap.getOutInterceptors().add(new CustomLoggingOutInterceptor("Bootstrap STS"));
        this.stsIdentity.getOutInterceptors().add(new CustomLoggingOutInterceptor("Identity STS"));

        // Change STS endpoint addresses
        this.stsBootstrap.getRequestContext()
                .put(Message.ENDPOINT_ADDRESS, properties.getStsBootstrap().getEndpoint());
        this.stsIdentity.getRequestContext()
                .put(Message.ENDPOINT_ADDRESS, properties.getStsIdentity().getEndpoint());
    }

    protected abstract Object getPort();

    @PostConstruct
    private void configure() {
        // Change WSP endpoint address
        BindingProvider provider = (BindingProvider) getPort();
        provider.getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getWebserviceEndpoint());

        // Add request/response logging interceptors to client
        Client client = ClientProxy.getClient(getPort());
        client.getOutInterceptors().add(new LoggingOutInterceptor());
        client.getOutInterceptors().add(new CustomLoggingOutInterceptor("WSP"));
        client.getInInterceptors().add(new LoggingInInterceptor());

        addWSSecurity((BindingProvider) getPort(), stsIdentity);
    }

    protected String getResponseString(Object jaxbElement, Marshaller marshaller) throws JAXBException {
        StringWriter sw = new StringWriter();
        marshaller.marshal(jaxbElement, sw);
        return sw.toString();
    }

    private void addWSSecurity(BindingProvider bindingProvider, XUASTSClient stsClient) {
        KeystorePasswordCallback callbackHandler = new KeystorePasswordCallback(wss4jProperties.getProperty("org.apache.ws.security.crypto.merlin.keystore.password"));

        Map<String, Object> props = bindingProvider.getRequestContext();
        props.put("ws-security.sts.applies-to", properties.getStsIdentity().getAudience());
        props.put("ws-security.sts.client", stsClient);
        props.put("ws-security.encryption.username", wss4jProperties.getProperty("org.apache.ws.security.crypto.merlin.keystore.alias"));
        props.put("ws-security.signature.properties", "sts-client.properties");
        props.put("ws-security.encryption.properties", "sts-client.properties");
        props.put("ws-security.callback-handler", callbackHandler);
        props.put("ws-security.asymmetric.signature.algorithm", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
    }

    private class CustomLoggingOutInterceptor extends AbstractPhaseInterceptor<Message> {

        private final String webServiceName;

        public CustomLoggingOutInterceptor(String webServiceName) {
            super(Phase.SETUP);
            this.webServiceName = webServiceName;
        }

        public void handleMessage(Message message) {
            final String operation = ((MessageInfo) message.get("org.apache.cxf.service.model.MessageInfo")).getOperation().getName().getLocalPart();
            logger.info(webServiceName + "; Operation: " + operation);
        }

        public void handleFault(Message messageParam) {
        }
    }

}
