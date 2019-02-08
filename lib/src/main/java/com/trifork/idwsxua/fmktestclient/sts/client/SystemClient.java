package com.trifork.idwsxua.fmktestclient.sts.client;

import com.trifork.idwsxua.fmktestclient.sts.ClaimsCallbackHandler;
import com.trifork.idwsxua.fmktestclient.sts.ClientCallbackHandler;
import com.trifork.idwsxua.fmktestclient.util.XUAProperties;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SystemClient implements STSClientWrapper {

    private final XUASTSClient client;

    @Autowired
    public SystemClient(XUAProperties properties) {
        Bus defaultBus = BusFactory.getDefaultBus();
        client = new XUASTSClient(defaultBus);

        client.setWsdlLocation(properties.getStsSystem().getWsdl());
        client.setServiceName("{http://docs.oasis-open.org/ws-sx/ws-trust/200512/}SecurityTokenService");
        client.setEndpointName("{http://docs.oasis-open.org/ws-sx/ws-trust/200512/}System");
        client.setSendRenewing(false);
        client.setAllowRenewing(false);
        client.setSendKeyType(false);
        client.setRequiresEntropy(false);
        client.setTokenType("http://docs.oasis-open.org/wss/oasis-wss-saml-token-profile-1.1#SAMLV2.0");
        client.setClaimsCallbackHandler(new ClaimsCallbackHandler());
        client.setKeyType("http://docs.oasis-open.org/ws-sx/ws-trust/200512/PublicKey");

        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.put("ws-security.callback-handler", new ClientCallbackHandler());
        propertiesMap.put("ws-security.signature.username", "idws xua test wsc (funktionscertifikat)");
        propertiesMap.put("ws-security.signature.properties", "sts-client.properties");
        propertiesMap.put("ws-security.encryption.username", "sts");
        propertiesMap.put("ws-security.sts.token.properties", "sts-client.properties");
        propertiesMap.put("ws-security.asymmetric.signature.algorithm", "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        client.setProperties(propertiesMap);
    }

    @Override
    public XUASTSClient getClient() {
        return client;
    }
}
