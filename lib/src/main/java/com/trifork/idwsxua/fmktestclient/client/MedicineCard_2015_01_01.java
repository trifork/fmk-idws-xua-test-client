package com.trifork.idwsxua.fmktestclient.client;

import com.trifork.idwsxua.fmktestclient.sts.TokenProvider;
import com.trifork.idwsxua.fmktestclient.sts.client.STSClientWrapper;
import com.trifork.idwsxua.fmktestclient.util.XUAProperties;
import dk.dkma.medicinecard.xml_schema._2015._01._01.GetMedicineCardRequest;
import dk.dkma.medicinecard.xml_schema._2015._01._01.GetMedicineCardResponse;
import dk.dkma.medicinecard.xml_schema._2015._01._01.MedicineCardPortType;
import dk.dkma.medicinecard.xml_schema._2015._01._01.MedicineCardService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;

@Component("MedicineCard_2015_01_01")
public class MedicineCard_2015_01_01 extends AbstractXUABootstrapWebServiceClient {

    private static final Logger logger = LogManager.getLogger(MedicineCard_2015_01_01.class);

    private final MedicineCardPortType port = new MedicineCardService().getMedicineCardPort();
    private final Marshaller marshaller = medicineCardMarshaller();

    @Autowired
    public MedicineCard_2015_01_01(XUAProperties properties,
                                   @Qualifier("bootstrapClient") STSClientWrapper stsBootstrap,
                                   @Qualifier("employeeClient") STSClientWrapper stsIdentity,
                                   TokenProvider tokenProvider) throws JAXBException, IOException {
        super(properties, stsBootstrap, stsIdentity, tokenProvider);
    }

    @Override
    public String callTestAction(String personIdentifier) throws Exception {
        tokenProvider.refreshBootstrapToken(stsBootstrap);

        // then perform a webservice call, which implicitly performs an ActAs call to the STS to get a token for this endpoint
        GetMedicineCardRequest requestType = new GetMedicineCardRequest();
        requestType.setPersonIdentifier(personIdentifier);

        GetMedicineCardResponse response = port.getMedicineCard20150101(requestType);
        return getResponseString(response, marshaller);
    }

    @Override
    protected MedicineCardPortType getPort() {
        return port;
    }

    private Marshaller medicineCardMarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance("dk.dkma.medicinecard.xml_schema._2015._01._01");
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        return marshaller;
    }

}
