package com.trifork.idwsxua.fmktestclient.client;

import com.trifork.idwsxua.fmktestclient.sts.TokenProvider;
import com.trifork.idwsxua.fmktestclient.sts.client.STSClientWrapper;
import com.trifork.idwsxua.fmktestclient.util.XUAProperties;
import dk.dkma.medicinecard.xml_schema._2015._01._01.GetMedicineCardVersionRequest;
import dk.dkma.medicinecard.xml_schema._2015._01._01.GetMedicineCardVersionResponse;
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

@Component("MedicineCard_2015_01_01_SystemUser")
public class MedicineCard_2015_01_01_SystemUser extends AbstractXUASystemUserWebServiceClient {

    private static final Logger logger = LogManager.getLogger(MedicineCard_2015_01_01_SystemUser.class);

    private final MedicineCardPortType port = new MedicineCardService().getMedicineCardPort();
    private final Marshaller marshaller = medicineCardMarshaller();

    @Autowired
    public MedicineCard_2015_01_01_SystemUser(XUAProperties properties,
                                              @Qualifier("systemClient") STSClientWrapper stsSystem,
                                              TokenProvider tokenProvider) throws JAXBException, IOException {
        super(properties, stsSystem, tokenProvider);
    }

    @Override
    public String callTestAction(String personIdentifier) throws Exception {
        tokenProvider.initSystemUserContext();

        // Perform a webservice call, which implicitly performs a call to the STS to get a token for this endpoint
        GetMedicineCardVersionRequest requestType = new GetMedicineCardVersionRequest();
        requestType.setPersonIdentifier(personIdentifier);

        GetMedicineCardVersionResponse response = port.getMedicineCardVersion20150101(requestType);
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
