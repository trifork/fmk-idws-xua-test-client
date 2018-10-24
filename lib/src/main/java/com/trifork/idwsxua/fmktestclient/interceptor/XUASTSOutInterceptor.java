package com.trifork.idwsxua.fmktestclient.interceptor;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class XUASTSOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger logger = LogManager.getLogger(XUASTSOutInterceptor.class);

    public XUASTSOutInterceptor() {
        super(Phase.SETUP);
    }

    public void handleMessage(Message message) {
        final String action = message.getExchange().getBindingOperationInfo().getName().getLocalPart();
        logger.info("Performing STS action: " + action);
    }

    public void handleFault(Message messageParam) {
    }
}
