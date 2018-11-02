package com.trifork.idwsxua.fmktestclient.client;

import com.trifork.idwsxua.fmktestclient.security.TokenProvider;
import com.trifork.idwsxua.fmktestclient.sts.XUASTSClient;
import com.trifork.idwsxua.fmktestclient.util.Properties;
import dk.dkma.medicinecard.xml_schema._2015._01._01._e1.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensaml.core.config.InitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.ws.BindingProvider;
import java.io.StringWriter;

@SuppressWarnings("Duplicates")
@Component
public class MedicineCard_2015_01_01_E1 extends Client {

    private static final Logger logger = LogManager.getLogger(MedicineCard_2015_01_01_E1.class);

    private final MedicineCardPortType port;
    private final Marshaller marshaller;

    @Autowired
    public MedicineCard_2015_01_01_E1(Properties properties,
                                      @Qualifier("bootstrapClient") XUASTSClient stsBootstrap,
                                      @Qualifier("employeeClient") XUASTSClient stsIdentity,
                                      TokenProvider tokenProvider) throws InitializationException, JAXBException {
        super(properties, stsBootstrap, stsIdentity, tokenProvider);
        this.marshaller = medicineCardMarshaller();
        this.port = medicineCardPort(stsIdentity);

        // Change WSP endpoint address
        BindingProvider provider = (BindingProvider) this.port;
        provider.getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, properties.getWebserviceEndpoint());
    }

    @Override
    public void callService(String personIdentifier) throws Exception {
        tokenProvider.refreshBootstrapToken(stsBootstrap);

        // then perform a webservice call, which implicitly performs an ActAs call to the STS to get a token for this endpoint
        GetMedicineCardRequestType requestType = new GetMedicineCardRequestType();
        requestType.setPersonIdentifier(personIdentifier);
        GetMedicineCardResponseType response = port.getMedicineCard20150101E1(requestType);

        StringWriter sw = new StringWriter();
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<GetMedicineCardResponseType> je = objectFactory.createGetMedicineCardResponse(response);
        marshaller.marshal(je, sw);

        logger.info("WSP reponse:");
        logger.info(sw);
    }

    private MedicineCardPortType medicineCardPort(XUASTSClient stsClient) {
        MedicineCardService service = new MedicineCardService();
        MedicineCardPortType port = service.getMedicineCardPort();

        addWSSecurity((BindingProvider) port, stsClient);

        return port;
    }

    private Marshaller medicineCardMarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance("dk.dkma.medicinecard.xml_schema._2015._01._01._e1");
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        return marshaller;
    }

}
