/*
 * The MIT License
 *
 * Original work sponsored and donated by National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Copyright (C) 2011 National Board of e-Health (NSI), Denmark (http://www.nsi.dk)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/cxf.xml")
public class StsClientTest {
    /*@Qualifier("bootstrapClient")
    @Autowired
    XUASTSClient xuastsClient;

    @Qualifier("employeeClient")
    @Autowired
    XUASTSClient employeeClient;

    @BeforeClass
    public static void initOpenSaml() throws InitializationException {
        InitializationService.initialize();
    }

    @Before
    public void configureWsClients() throws EndpointException, BusException, KeyStoreException {
        xuastsClient.getClient().getRequestContext().put(Message.ENDPOINT_ADDRESS, "https://test1-cnsp.ekstern-test.nspop.dk:8443/sts3/services/employee/bootstrap");
        employeeClient.getClient().getRequestContext().put(Message.ENDPOINT_ADDRESS, "https://test1-cnsp.ekstern-test.nspop.dk:8443/sts3/services/employee");

        final KeyStore systemKeystore = X509CertUtil.loadKeyStore(X509CertUtil.readResource("wsc.pfx"), "Test1234");
        final X509Certificate systemCertificate = (X509Certificate) systemKeystore.getCertificate("idws xua test wsc (funktionscertifikat)");

        xuastsClient.setUseKeyCertificate(systemCertificate);
        employeeClient.setUseKeyCertificate(systemCertificate);
    }

    @Before
    public void initSessionContext() {
        SessionContext context = new SessionContext();
        SessionContextHolder.set(context);
    }

    public void initPatientContext(String ydernummer, String patientCpr) {
        ResourceId patientContext = buildResourceId(patientCpr);
        SessionContextHolder.get().setResourceId(patientContext);

        ProviderIdentifier providerIdentifier = buildProviderIdentifier(ydernummer);
        SessionContextHolder.get().setProviderIdentifier(providerIdentifier);
    }

    @Test
    public void testAuthorizedWithBasicClaims() throws Exception {
        final KeyStore employeeKeystore = X509CertUtil.getKeyStoreFromResource("MOCES_cpr_gyldig.p12", "Test1234");
        X509Certificate employeeCertificate = (X509Certificate) employeeKeystore.getCertificate("1");
        PrivateKey employeeKey =  (PrivateKey) employeeKeystore.getKey("1", "Test1234".toCharArray());

        final Element actAs = new OpenSaml3TokenBuilder(employeeCertificate, employeeKey).buildToken();
        TokenHolder.selfsigned = actAs;

        final XUASTSOutInterceptor xuastsOutInterceptor = new XUASTSOutInterceptor();
        xuastsClient.getOutInterceptors().add(xuastsOutInterceptor);
        SecurityToken securityToken = xuastsClient.requestSecurityToken("http://sts.sundhedsdatastyrelsen.dk/");


        TokenHolder.bootstrapTokenElement = securityToken.getToken();
        System.out.println(XmlHelper.node2String(securityToken.getToken(), true, true));

        SessionContextHolder.get().setIncludeDefaultClaims(true);
        initPatientContext("541133", "2606767242");

        SecurityToken employeeToken = employeeClient.requestSecurityToken("https://fmk");

        System.out.println(XmlHelper.node2String(employeeToken.getToken(), true, true));
    }

    @Test
    public void testAuthorizedClaimingEducationCode() throws Exception {
        final KeyStore employeeKeystore = X509CertUtil.getKeyStoreFromResource("MOCES_cpr_gyldig.p12", "Test1234");
        X509Certificate employeeCertificate = (X509Certificate) employeeKeystore.getCertificate("1");
        PrivateKey employeeKey =  (PrivateKey) employeeKeystore.getKey("1", "Test1234".toCharArray());


        final Element actAs = new OpenSaml3TokenBuilder(employeeCertificate, employeeKey).buildToken();
        TokenHolder.selfsigned = actAs;
        SecurityToken securityToken = xuastsClient.requestSecurityToken("http://sts.sundhedsdatastyrelsen.dk/");


        TokenHolder.bootstrapTokenElement = securityToken.getToken();
        System.out.println(XmlHelper.node2String(securityToken.getToken(), true, true));

        SessionContextHolder.get().setIncludeDefaultClaims(true);
        initPatientContext("541133", "2606767242");
        SessionContextHolder.get().setEducationCode("7170");
        SecurityToken employeeToken = employeeClient.requestSecurityToken("https://fmk");

        System.out.println(XmlHelper.node2String(employeeToken.getToken(), true, true));
    }

    @Test
    public void testAuthorizedClaimingWrongEducationCode() throws Exception {
        final KeyStore employeeKeystore = X509CertUtil.getKeyStoreFromResource("MOCES_cpr_gyldig.p12", "Test1234");
        X509Certificate employeeCertificate = (X509Certificate) employeeKeystore.getCertificate("1");
        PrivateKey employeeKey =  (PrivateKey) employeeKeystore.getKey("1", "Test1234".toCharArray());


        final Element actAs = new OpenSaml3TokenBuilder(employeeCertificate, employeeKey).buildToken();
        TokenHolder.selfsigned = actAs;
        SecurityToken securityToken = xuastsClient.requestSecurityToken("http://sts.sundhedsdatastyrelsen.dk/");


        TokenHolder.bootstrapTokenElement = securityToken.getToken();
        System.out.println(XmlHelper.node2String(securityToken.getToken(), true, true));

        SessionContextHolder.get().setEducationCode("5150");
        initPatientContext("541133", "2606767242");
        try {
            employeeClient.requestSecurityToken("https://fmk");
            fail();
        } catch (SoapFault e) {
            assertEquals("Ingen autorisationer fundet", e.getReason());
        }
    }

    @Test
    public void testUnauthorizedOnOwnBehalf() throws Exception {
        final KeyStore employeeKeystore = X509CertUtil.getKeyStoreFromResource("LAKESIDE  - (ikke sundhedsfaglig)Grethe Pedersen.p12", "Test1234");
        X509Certificate employeeCertificate = (X509Certificate) employeeKeystore.getCertificate("grethe pedersen");
        PrivateKey employeeKey =  (PrivateKey) employeeKeystore.getKey("grethe pedersen", "Test1234".toCharArray());

        final Element actAs = new OpenSaml3TokenBuilder(employeeCertificate, employeeKey).buildToken();
        TokenHolder.selfsigned = actAs;
        SecurityToken securityToken = xuastsClient.requestSecurityToken("http://sts.sundhedsdatastyrelsen.dk/");
        TokenHolder.bootstrapTokenElement = securityToken.getToken();
        System.out.println(XmlHelper.node2String(securityToken.getToken(), true, true));

        SessionContextHolder.get().setIncludeDefaultClaims(true);
        initPatientContext("541133", "2606767242");

        SecurityToken employeeToken = employeeClient.requestSecurityToken("https://fmk");

        System.out.println(XmlHelper.node2String(employeeToken.getToken(), true, true));
    }

    @Test
    public void testUnauthorizedOnBehalfOf() throws Exception {
        final KeyStore employeeKeystore = X509CertUtil.getKeyStoreFromResource("LAKESIDE  - (ikke sundhedsfaglig)Grethe Pedersen.p12", "Test1234");
        X509Certificate employeeCertificate = (X509Certificate) employeeKeystore.getCertificate("grethe pedersen");
        PrivateKey employeeKey =  (PrivateKey) employeeKeystore.getKey("grethe pedersen", "Test1234".toCharArray());

        final Element actAs = new OpenSaml3TokenBuilder(employeeCertificate, employeeKey).buildToken();
        TokenHolder.selfsigned = actAs;
        SecurityToken securityToken = xuastsClient.requestSecurityToken("http://sts.sundhedsdatastyrelsen.dk/");
        TokenHolder.bootstrapTokenElement = securityToken.getToken();
        System.out.println(XmlHelper.node2String(securityToken.getToken(), true, true));

        OnBehalfOf onBehalfOf = OnBehalfOf
                .builder()
                .authorizationCode("ZXCVB")
                .educationCode("7170")
                .legislation(OnBehalfOf.Legislation.actThroughDelegationByAuthorizedHealthcareProfessional)
                .build();

        SessionContextHolder.get().setIncludeDefaultClaims(true);
        initPatientContext("541133", "2606767242");
        SessionContextHolder.get().setOnBehalfOf(onBehalfOf);

        SecurityToken employeeToken = employeeClient.requestSecurityToken("https://fmk");

        System.out.println(XmlHelper.node2String(employeeToken.getToken(), true, true));
    }

    @Test
    public void testUserWithNoTreatmentRelation() throws Exception {
        final KeyStore employeeKeystore = X509CertUtil.getKeyStoreFromResource("LAKESIDE  - (ikke sundhedsfaglig)Grethe Pedersen.p12", "Test1234");
        X509Certificate employeeCertificate = (X509Certificate) employeeKeystore.getCertificate("grethe pedersen");
        PrivateKey employeeKey =  (PrivateKey) employeeKeystore.getKey("grethe pedersen", "Test1234".toCharArray());

        final Element actAs = new OpenSaml3TokenBuilder(employeeCertificate, employeeKey).buildToken();
        TokenHolder.selfsigned = actAs;
        SecurityToken securityToken = xuastsClient.requestSecurityToken("http://sts.sundhedsdatastyrelsen.dk/");
        TokenHolder.bootstrapTokenElement = securityToken.getToken();
        System.out.println(XmlHelper.node2String(securityToken.getToken(), true, true));

        SessionContextHolder.get().setIncludeDefaultClaims(true);
        initPatientContext("541133", TestUtil.randomCpr());
        SecurityToken employeeToken = employeeClient.requestSecurityToken("https://fmk");

        System.out.println(XmlHelper.node2String(employeeToken.getToken(), true, true));
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
*/
}
