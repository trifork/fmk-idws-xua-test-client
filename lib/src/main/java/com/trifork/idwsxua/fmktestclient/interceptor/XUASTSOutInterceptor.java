package com.trifork.idwsxua.fmktestclient.interceptor;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XUASTSOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger logger = LogManager.getLogger(XUASTSOutInterceptor.class);
    private final String stsName;

    public XUASTSOutInterceptor(String stsName) {
        super(Phase.SETUP);
        this.stsName = stsName;
    }

    public void handleMessage(Message message) {
        final String soapAction = (String) message.get("SOAPAction");
        logger.info(stsName + ", SOAPAction: " + soapAction);
    }

    public void handleFault(Message messageParam) {
    }
}
