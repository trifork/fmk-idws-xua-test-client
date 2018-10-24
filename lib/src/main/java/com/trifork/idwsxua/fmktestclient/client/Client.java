package com.trifork.idwsxua.fmktestclient.client;

import com.trifork.idwsxua.fmktestclient.interceptor.XUASTSOutInterceptor;
import com.trifork.idwsxua.fmktestclient.sts.XUASTSClient;
import dk.dkma.medicinecard.xml_schema._2015._01._01.MedicineCardPortType;
import org.apache.cxf.BusException;
import org.apache.cxf.endpoint.EndpointException;
import org.apache.cxf.message.Message;

import javax.xml.ws.BindingProvider;

public abstract class Client {

    MedicineCardPortType port;
    private XUASTSClient sts;

    Client(MedicineCardPortType port, XUASTSClient sts) {
        this.port = port;
        this.sts = sts;
    }

    public void setWebserviceEndpoint(String webserviceEndpoint) {
        // Change WS endpoint address
        BindingProvider provider = (BindingProvider) this.port;
        provider.getRequestContext()
                .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, webserviceEndpoint);
    }

    public void setSTSEndpoint(String stsEndpoint) throws EndpointException, BusException {
        // Change STS endpoint address
        sts.getClient().getRequestContext().put(Message.ENDPOINT_ADDRESS, stsEndpoint);

        // Add interceptor for logging
        final XUASTSOutInterceptor xuastsOutInterceptor = new XUASTSOutInterceptor();
        sts.getOutInterceptors().add(xuastsOutInterceptor);
    }

    public abstract void callService(String personIdentifier) throws Exception;
}
