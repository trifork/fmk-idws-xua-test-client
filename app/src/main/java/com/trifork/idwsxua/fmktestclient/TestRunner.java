package com.trifork.idwsxua.fmktestclient;

import com.trifork.idwsxua.fmktestclient.security.TokenProvider;
import com.trifork.idwsxua.fmktestclient.sts.XUASTSClient;
import com.trifork.idwsxua.fmktestclient.util.SAMLTokenBuilder;
import dk.dkma.medicinecard.xml_schema._2015._01._01.GetMedicineCardRequestType;
import dk.dkma.medicinecard.xml_schema._2015._01._01.GetMedicineCardResponseType;
import dk.dkma.medicinecard.xml_schema._2015._01._01.MedicineCardPortType;
import dk.dkma.medicinecard.xml_schema._2015._01._01.ObjectFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

@Component
public class TestRunner {

    private static Logger logger = LogManager.getLogger(TestRunner.class);

    private final XUASTSClient stsClient;
    private final MedicineCardPortType port;
    private final Marshaller mashaller;

    @Autowired
    public TestRunner(XUASTSClient stsClient, MedicineCardPortType port, Marshaller medicineCardMarshaller) {
        this.stsClient = stsClient;
        this.port = port;
        this.mashaller = medicineCardMarshaller;
    }

    private void bootstrapCall() throws Exception {
        final String token = getSAMLToken();

        TokenProvider.setToken(new String(Base64.decodeBase64(token), Charset.forName("UTF-8")));

        // perform a call to the STS to exchange the self-signed token into a "bootstrap"-token
        SecurityToken securityToken = stsClient.requestSecurityToken("http://localhost:8080/service/hello");

        TokenProvider.setToken(securityToken.getToken());
    }

    void callService(String personIdentifier) throws Exception {
        bootstrapCall();

        // then perform a webservice call, which implicitly performs an ActAs call to the STS to get a token for this endpoint
        GetMedicineCardRequestType requestType = new GetMedicineCardRequestType();
        requestType.setPersonIdentifier(personIdentifier);
        GetMedicineCardResponseType response = port.getMedicineCard20150101(requestType);

        StringWriter sw = new StringWriter();
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<GetMedicineCardResponseType> je =  objectFactory.createGetMedicineCardResponse(response);
        mashaller.marshal(je, sw);

        System.out.println("Reponse:");
        System.out.println(sw);
    }

    private String getSAMLToken() throws IOException {
        final InputStream keystoreFileInputStream = new ClassPathResource("test-moces.pfx").getInputStream();

        String trustKeystorePassword = "Test1234";

        try {
            KeyStore keystore = getKeystore(keystoreFileInputStream, trustKeystorePassword);
            if (keystore != null) {
                String token = new SAMLTokenBuilder().getToken(keystore, keystore.aliases().nextElement(), trustKeystorePassword);

                return token;
            }
        } catch (Exception ex) {
            logger.error("Failed to connect to backend", ex);
        }

        return null;
    }

    private KeyStore getKeystore(InputStream keystoreFileInputStream, String password) {
        KeyStore ks = null;

        try {
            ks = KeyStore.getInstance("PKCS12");
            ks.load(keystoreFileInputStream, password.toCharArray());
        } catch (GeneralSecurityException | IOException ex) {
            logger.error("Could not load keystore with provided password", ex);
        }

        return ks;
    }

}
