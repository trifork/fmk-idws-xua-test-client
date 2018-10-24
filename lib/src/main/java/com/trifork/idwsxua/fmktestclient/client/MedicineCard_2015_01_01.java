package com.trifork.idwsxua.fmktestclient.client;

import com.trifork.idwsxua.fmktestclient.security.TokenProvider;
import com.trifork.idwsxua.fmktestclient.sts.XUASTSClient;
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
public class MedicineCard_2015_01_01 extends Client {

    private static final Logger logger = LogManager.getLogger(MedicineCard_2015_01_01.class);

    private final Marshaller marshaller;
    private final TokenProvider tokenProvider;
    private final XUASTSClient sts;

    @Autowired
    public MedicineCard_2015_01_01(MedicineCardPortType port, Marshaller medicineCardMarshaller, TokenProvider tokenProvider, XUASTSClient sts) {
        super(port, sts);
        this.marshaller = medicineCardMarshaller;
        this.tokenProvider = tokenProvider;
        this.sts = sts;
    }

    @Override
    public void callService(String personIdentifier) throws Exception {
        tokenProvider.refreshBootstrapToken();

        // then perform a webservice call, which implicitly performs an ActAs call to the STS to get a token for this endpoint
        GetMedicineCardRequestType requestType = new GetMedicineCardRequestType();
        requestType.setPersonIdentifier(personIdentifier);
        GetMedicineCardResponseType response = port.getMedicineCard20150101(requestType);

        StringWriter sw = new StringWriter();
        ObjectFactory objectFactory = new ObjectFactory();
        JAXBElement<GetMedicineCardResponseType> je =  objectFactory.createGetMedicineCardResponse(response);
        marshaller.marshal(je, sw);

        logger.info("WSP reponse:");
        logger.info(sw);
    }

}
