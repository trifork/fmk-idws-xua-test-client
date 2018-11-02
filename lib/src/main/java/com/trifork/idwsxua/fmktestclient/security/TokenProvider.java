package com.trifork.idwsxua.fmktestclient.security;


import com.trifork.idwsxua.fmktestclient.interceptor.XUASTSOutInterceptor;
import com.trifork.idwsxua.fmktestclient.sts.OpenSaml3TokenBuilder;
import com.trifork.idwsxua.fmktestclient.sts.SessionContextHolder;
import com.trifork.idwsxua.fmktestclient.sts.TokenHolder;
import com.trifork.idwsxua.fmktestclient.util.Properties;
import com.trifork.idwsxua.fmktestclient.util.X509CertUtil;
import com.trifork.idwsxua.fmktestclient.util.XmlHelper;
import dk.sds.samlh.model.provideridentifier.ProviderIdentifier;
import dk.sds.samlh.model.resourceid.ResourceId;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.trust.STSClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Component
public class TokenProvider {

   private static final Logger logger = LogManager.getLogger(TokenProvider.class);

    private final Properties properties;

    public TokenProvider(Properties properties) {
        this.properties = properties;
    }

    public void refreshBootstrapToken(STSClient stsBootstrap) throws Exception {
        final KeyStore employeeKeystore = X509CertUtil.getKeyStoreFromResource("MOCES_cpr_gyldig.p12", "Test1234"); // TODO
        X509Certificate employeeCertificate = (X509Certificate) employeeKeystore.getCertificate("1");
        PrivateKey employeeKey = (PrivateKey) employeeKeystore.getKey("1", "Test1234".toCharArray());

        final org.w3c.dom.Element actAs = new OpenSaml3TokenBuilder(employeeCertificate, employeeKey).buildToken();
        TokenHolder.selfsigned = actAs;

        SecurityToken securityToken = stsBootstrap.requestSecurityToken("http://sts.sundhedsdatastyrelsen.dk/");

        TokenHolder.bst = securityToken.getToken();
        System.out.println(XmlHelper.node2String(securityToken.getToken(), true, true));

        SessionContextHolder.get().setIncludeDefaultClaims(true);
        initPatientContext("541133", "2606767242");
    }

    private static ProviderIdentifier buildProviderIdentifier(String ydernummer) {
        return ProviderIdentifier.builder()
                .root("1.2.208.176.1.4")
                .xsiType("II")
                .extension(ydernummer + "^description")
                .build();
    }

    private static ResourceId buildResourceId(String cpr) {
        return ResourceId.builder()
                .oid("1.2.208.176.1.2")
                .patientId(cpr)
                .build();
    }

    private void initPatientContext(String ydernummer, String patientCpr) {
        ResourceId patientContext = buildResourceId(patientCpr);
        SessionContextHolder.get().setResourceId(patientContext);

        ProviderIdentifier providerIdentifier = buildProviderIdentifier(ydernummer);
        SessionContextHolder.get().setProviderIdentifier(providerIdentifier);
    }
}
