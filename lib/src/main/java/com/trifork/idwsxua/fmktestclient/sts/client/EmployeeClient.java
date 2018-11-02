package com.trifork.idwsxua.fmktestclient.sts.client;

import com.trifork.idwsxua.fmktestclient.sts.*;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmployeeClient implements STSClientWrapper {

    private final XUASTSClient client;

    public EmployeeClient() {
        Bus defaultBus = BusFactory.getDefaultBus();
        client = new XUASTSClient(defaultBus);

        client.setWsdlLocation("https://test1-cnsp.ekstern-test.nspop.dk:8443/sts3/services/employee?wsdl");
        client.setServiceName("{http://docs.oasis-open.org/ws-sx/ws-trust/200512/}SecurityTokenService");
        client.setEndpointName("{http://docs.oasis-open.org/ws-sx/ws-trust/200512/}Employee");
        client.setSendRenewing(false);
        client.setAllowRenewing(false);
        client.setSendKeyType(false);
        client.setRequiresEntropy(false);
        client.setTokenType("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0");
        client.setActAs(new ActAsBootstrapCallbackHandler());

        client.setClaimsCallbackHandler(new ClaimsCallbackHandler());

        client.setKeyType("http://docs.oasis-open.org/ws-sx/ws-trust/200512/PublicKey");

        Map<String, Object> properties = new HashMap<>();
        properties.put("ws-security.callback-handler", new ClientCallbackHandler());
        properties.put("ws-security.signature.username", "idws xua test wsc (funktionscertifikat)");
        properties.put("ws-security.signature.properties", "sts-client.properties");
        properties.put("ws-security.encryption.username", "sts");
        properties.put("ws-security.sts.token.properties", "sts-client.properties");
        properties.put("ws-security.asymmetric.signature.algorithm", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        client.setProperties(properties);
    }

    @Override
    public XUASTSClient getClient() {
        return client;
    }
}
