package com.trifork.idwsxua.fmktestclient.client;

import com.trifork.idwsxua.fmktestclient.sts.TokenProvider;
import com.trifork.idwsxua.fmktestclient.sts.client.STSClientWrapper;
import com.trifork.idwsxua.fmktestclient.util.XUAProperties;
import dk.dkma.effectuationordering.xml_schema._2015._01._01.EffectuationOrderingPortType;
import dk.dkma.effectuationordering.xml_schema._2015._01._01.EffectuationOrderingService;
import dk.dkma.effectuationordering.xml_schema._2015._01._01.GetOrderedEffectuationsRequest;
import dk.dkma.effectuationordering.xml_schema._2015._01._01.GetOrderedEffectuationsResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;

@Component("EffectuationOrdering_2015_01_01")
public class EffectuationOrdering_2015_01_01 extends XUAWebServiceClient {

    private static final Logger logger = LogManager.getLogger(EffectuationOrdering_2015_01_01.class);

    private final EffectuationOrderingPortType port = new EffectuationOrderingService().getEffectuationOrderingPort();
    private final Marshaller marshaller;

    @Autowired
    public EffectuationOrdering_2015_01_01(XUAProperties properties,
                                           @Qualifier("bootstrapClient") STSClientWrapper stsBootstrap,
                                           @Qualifier("employeeClient") STSClientWrapper stsIdentity,
                                           TokenProvider tokenProvider) throws JAXBException, IOException {
        super(properties, stsBootstrap, stsIdentity, tokenProvider);

        JAXBContext jaxbContext = JAXBContext.newInstance("dk.dkma.effectuationordering.xml_schema._2015._01._01");
        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    @Override
    public String callTestAction(String personIdentifier) throws Exception {
        tokenProvider.refreshBootstrapToken(stsBootstrap);

        // then perform a webservice call, which implicitly performs an ActAs call to the STS to get a token for this endpoint
        GetOrderedEffectuationsRequest request = new GetOrderedEffectuationsRequest();
        request.setPersonIdentifier(personIdentifier);

        GetOrderedEffectuationsResponse response = port.getOrderedEffectuationsIdws20150101(request);
        return getResponseString(response, marshaller);
    }

    @Override
    protected EffectuationOrderingPortType getPort() {
        return port;
    }
}
