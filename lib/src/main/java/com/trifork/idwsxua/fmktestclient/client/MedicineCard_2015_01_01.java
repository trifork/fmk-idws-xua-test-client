package com.trifork.idwsxua.fmktestclient.client;

import com.trifork.idwsxua.fmktestclient.interceptor.XUASTSOutInterceptor;
import com.trifork.idwsxua.fmktestclient.sts.*;
import com.trifork.idwsxua.fmktestclient.util.X509CertUtil;
import com.trifork.idwsxua.fmktestclient.util.XmlHelper;
import dk.dkma.medicinecard.xml_schema._2015._01._01.GetMedicineCardRequestType;
import dk.dkma.medicinecard.xml_schema._2015._01._01.GetMedicineCardResponseType;
import dk.dkma.medicinecard.xml_schema._2015._01._01.MedicineCardPortType;
import dk.dkma.medicinecard.xml_schema._2015._01._01.ObjectFactory;
import dk.sds.samlh.model.provideridentifier.ProviderIdentifier;
import dk.sds.samlh.model.resourceid.ResourceId;
import org.apache.cxf.message.Message;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@Component
public class MedicineCard_2015_01_01 extends Client {

    private static final Logger logger = LogManager.getLogger(MedicineCard_2015_01_01.class);

    private final Marshaller marshaller;
    private final XUASTSClient sts;
    private XUASTSClient stsBootstrap;

    @Autowired
    public MedicineCard_2015_01_01(MedicineCardPortType port, Marshaller medicineCardMarshaller, @Qualifier("employeeClient") XUASTSClient sts, @Qualifier("bootstrapClient") XUASTSClient stsBootstrap) throws InitializationException {
        super(port, sts);
        this.marshaller = medicineCardMarshaller;
        this.sts = sts;
        this.stsBootstrap = stsBootstrap;

        InitializationService.initialize();

        SessionContext context = new SessionContext();
        SessionContextHolder.set(context);
    }

    @Override
    public void callService(String personIdentifier) throws Exception {
        //tokenProvider.refreshBootstrapToken();
        stsBootstrap.getClient().getRequestContext().put(Message.ENDPOINT_ADDRESS, "https://test1-cnsp.ekstern-test.nspop.dk:8443/sts3/services/employee/bootstrap");
        sts.getClient().getRequestContext().put(Message.ENDPOINT_ADDRESS, "https://test1-cnsp.ekstern-test.nspop.dk:8443/sts3/services/employee");

        final KeyStore systemKeystore = X509CertUtil.loadKeyStore(X509CertUtil.readResource("wsc.pfx"), "Test1234");
        final X509Certificate systemCertificate = (X509Certificate) systemKeystore.getCertificate("idws xua test wsc (funktionscertifikat)");

//        stsBootstrap.setUseKeyCertificate(systemCertificate);
//        sts.setUseKeyCertificate(systemCertificate);


        final KeyStore employeeKeystore = X509CertUtil.getKeyStoreFromResource("MOCES_cpr_gyldig.p12", "Test1234");
        X509Certificate employeeCertificate = (X509Certificate) employeeKeystore.getCertificate("1");
        PrivateKey employeeKey =  (PrivateKey) employeeKeystore.getKey("1", "Test1234".toCharArray());

        final Element actAs = new OpenSaml3TokenBuilder(employeeCertificate, employeeKey).buildToken();
        TokenHolder.selfsigned = actAs;

        final XUASTSOutInterceptor xuastsOutInterceptor = new XUASTSOutInterceptor();
        stsBootstrap.getOutInterceptors().add(xuastsOutInterceptor);
        SecurityToken securityToken = stsBootstrap.requestSecurityToken("http://sts.sundhedsdatastyrelsen.dk/");

        TokenHolder.bst = securityToken.getToken();
        System.out.println(XmlHelper.node2String(securityToken.getToken(), true, true));

        SessionContextHolder.get().setIncludeDefaultClaims(true);
        initPatientContext("541133", "2606767242");


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


    public void initPatientContext(String ydernummer, String patientCpr) {
        ResourceId patientContext = buildResourceId(patientCpr);
        SessionContextHolder.get().setResourceId(patientContext);

        ProviderIdentifier providerIdentifier = buildProviderIdentifier(ydernummer);
        SessionContextHolder.get().setProviderIdentifier(providerIdentifier);
    }

}
