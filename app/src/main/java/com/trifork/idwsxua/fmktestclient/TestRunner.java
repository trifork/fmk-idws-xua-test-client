package com.trifork.idwsxua.fmktestclient;

import com.trifork.idwsxua.fmktestclient.security.TokenProvider;
import dk.dkma.medicinecard.xml_schema._2015._01._01.GetMedicineCardRequestType;
import dk.dkma.medicinecard.xml_schema._2015._01._01.GetMedicineCardResponseType;
import dk.dkma.medicinecard.xml_schema._2015._01._01.MedicineCardPortType;
import dk.dkma.medicinecard.xml_schema._2015._01._01.ObjectFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Component
public class TestRunner {

    private static Logger logger = LogManager.getLogger(TestRunner.class);

    private final MedicineCardPortType port;
    private final Marshaller mashaller;
    private final TokenProvider tokenProvider;

    @Autowired
    public TestRunner(MedicineCardPortType port, Marshaller medicineCardMarshaller, TokenProvider tokenProvider) {
        this.port = port;
        this.mashaller = medicineCardMarshaller;
        this.tokenProvider = tokenProvider;
    }
    void callService(String personIdentifier) throws Exception {
        tokenProvider.refreshBootstrapToken();

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

}
