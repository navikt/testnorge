package no.nav.identpool;

import org.springframework.context.annotation.Configuration;

@Configuration
class SecurityTestConfig {

    static final String NAV_STS_ISSUER_URL = "http://navStsIssuerUrl";

//    @Inject
//    private IdpRegistry idpRegistry;
//
//    @Bean
//    @Primary
//    SimpleGet simpleGetMock() throws Exception {
//        SimpleGet simpleGet = mock(SimpleGet.class);
//        mockRsa(issuerNavSts(), simpleGet);
//        return simpleGet;
//    }
//
//    @Bean
//    RSAKey issuerNavSts() throws Exception {
//        RsaJsonWebKey webKey = RsaJwkGenerator.generateJwk(2048);
//        webKey.setKeyId("navSts1");
//        webKey.setAlgorithm("RSA256");
//        return new RsaKey(NAV_STS_ISSUER_URL, webKey);
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
}
