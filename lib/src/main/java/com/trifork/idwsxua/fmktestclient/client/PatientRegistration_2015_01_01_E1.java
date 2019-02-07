package com.trifork.idwsxua.fmktestclient.client;

import com.trifork.idwsxua.fmktestclient.sts.TokenProvider;
import com.trifork.idwsxua.fmktestclient.sts.client.STSClientWrapper;
import com.trifork.idwsxua.fmktestclient.util.XUAProperties;
import dk.dkma.patientregistration.xml_schema._2015._01._01._e1.GetPatientRegistrationRequest;
import dk.dkma.patientregistration.xml_schema._2015._01._01._e1.GetPatientRegistrationResponse;
import dk.dkma.patientregistration.xml_schema._2015._01._01._e1.PatientRelationPortType;
import dk.dkma.patientregistration.xml_schema._2015._01._01._e1.PatientRelationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;

@Component("PatientRegistration_2015_01_01_E1")
public class PatientRegistration_2015_01_01_E1 extends AbstractXUABootstrapWebServiceClient {

    private static final Logger logger = LogManager.getLogger(PatientRegistration_2015_01_01_E1.class);

    private final PatientRelationPortType port = new PatientRelationService().getPatientRelationPort();
    private final Marshaller marshaller;

    @Autowired
    public PatientRegistration_2015_01_01_E1(XUAProperties properties,
                                             @Qualifier("bootstrapClient") STSClientWrapper stsBootstrap,
                                             @Qualifier("employeeClient") STSClientWrapper stsIdentity,
                                             TokenProvider tokenProvider) throws JAXBException, IOException {
        super(properties, stsBootstrap, stsIdentity, tokenProvider);

        JAXBContext jaxbContext = JAXBContext.newInstance("dk.dkma.patientregistration.xml_schema._2015._01._01._e1");
        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    @Override
    public String callTestAction(String personIdentifier) throws Exception {
        tokenProvider.refreshBootstrapToken(stsBootstrap);

        // then perform a webservice call, which implicitly performs an ActAs call to the STS to get a token for this endpoint
        GetPatientRegistrationRequest requestType = new GetPatientRegistrationRequest();
        requestType.setPersonIdentifier(personIdentifier);

        GetPatientRegistrationResponse response = port.getPatientRegistrationIdws20150101(requestType);
        return getResponseString(response, marshaller);
    }

    @Override
    protected PatientRelationPortType getPort() {
        return port;
    }
}
