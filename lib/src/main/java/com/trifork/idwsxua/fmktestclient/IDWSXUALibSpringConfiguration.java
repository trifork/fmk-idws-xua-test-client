package com.trifork.idwsxua.fmktestclient;

import com.trifork.idwsxua.fmktestclient.callback.KeystorePasswordCallback;
import com.trifork.idwsxua.fmktestclient.sts.ClientCallbackHandler;
import com.trifork.idwsxua.fmktestclient.sts.XUASTSClient;
import dk.dkma.medicinecard.xml_schema._2015._01._01.MedicineCardPortType;
import dk.dkma.medicinecard.xml_schema._2015._01._01.MedicineCardService;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.ws.BindingProvider;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan
@ImportResource({"classpath:cxf.xml"})
public class IDWSXUALibSpringConfiguration {

    private static final Logger logger = LogManager.getLogger(IDWSXUALibSpringConfiguration.class);

    public IDWSXUALibSpringConfiguration() {
        System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");
    }

    @Bean
    public MedicineCardPortType medicineCardPort(@Qualifier("employeeClient") XUASTSClient stsClient) {
        MedicineCardService service = new MedicineCardService();
        MedicineCardPortType port = service.getMedicineCardPort();

        // One way to configure WS Security
//        Map<String,Object> outProps = new HashMap<>();
//        outProps.put(WSHandlerConstants.ACTION, "Signature");
//        outProps.put(WSHandlerConstants.USER, "idws xua test wsc (funktionscertifikat)");
//        outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientCallbackHandler.class.getName());
//        outProps.put(WSHandlerConstants.SIG_PROP_FILE, "wsc-client.properties");
//
//        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
//
//        Client client = ClientProxy.getClient(port);
//        client.getOutInterceptors().add(wssOut);

        // Another way to configure WS Security
        KeystorePasswordCallback callbackHandler = new KeystorePasswordCallback("Test1234");
        Map<String, Object> props = ((BindingProvider) port).getRequestContext();
        props.put("ws-security.sts.applies-to", "https://fmk");
        props.put("ws-security.sts.client", stsClient);
        props.put("ws-security.encryption.username", "idws xua test wsc (funktionscertifikat)");
        props.put("ws-security.signature.properties", "wsc-client.properties");
        props.put("ws-security.encryption.properties", "wsc-client.properties");
        props.put("ws-security.callback-handler", callbackHandler);
        props.put("ws-security.asymmetric.signature.algorithm", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
//        props.put("ws-security.is-bsp-compliant", "false");

        return port;
    }

    @Bean
    public Marshaller medicineCardMarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance("dk.dkma.medicinecard.xml_schema._2015._01._01");
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        return marshaller;
    }
}