package com.trifork.idwsxua.fmktestclient.client;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class TestContentTypeOutInterceptor extends AbstractPhaseInterceptor<Message> {

    public TestContentTypeOutInterceptor() {
        super(Phase.SEND);
    }

    @Override
    public void handleMessage(Message message) {
        // Override content type header
        message.put("Content-Type", "application/soap+xml");
    }

    @Override
    public void handleFault(Message messageParam) {
    }
}
