package no.nav.dolly.config;

//@Configuration
public class SecurityTestConfig {

    //    public static final String OPEN_AM_ISSUER_URL = "http://openAmIssuerUrl";
    //
    //    @Autowired
    //    private IdpRegistry idpRegistry;
    //
    //    @Bean
    //    @Primary
    //    SimpleGet simpleGetMock() throws Exception {
    //        SimpleGet simpleGet = mock(SimpleGet.class);
    //        mockRsa(issuerOpenAm(), simpleGet);
    //        return simpleGet;
    //    }
    //
    //    private void mockRsa(RsaKey rsaKey, SimpleGet simpleGet) throws IOException {
    //        String jwks = idpRegistry.findByIssuer(rsaKey.getIssuer()).map(Idp::getJwksUrl).orElse(rsaKey.getIssuer());
    //
    //        SimpleResponse response = mock(SimpleResponse.class);
    //        String value = rsaKey.getWebKey().toJson();
    //
    //        when(response.getBody()).thenReturn(format("{\"keys\":[%s]}", value));
    //        when(simpleGet.get(jwks)).thenReturn(response);
    //    }
    //
    //    @Bean
    //    RsaKey issuerOpenAm() throws Exception {
    //        RsaJsonWebKey webKey = RsaJwkGenerator.generateJwk(2048);
    //        webKey.setKeyId("openAm1");
    //        webKey.setAlgorithm("RSA256");
    //        return new RsaKey(OPEN_AM_ISSUER_URL, webKey);
    //    }
}
