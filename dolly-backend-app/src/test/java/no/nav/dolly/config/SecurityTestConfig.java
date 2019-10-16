package no.nav.dolly.config;

import static java.lang.String.format;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import no.nav.freg.security.oidc.idp.Idp;
import no.nav.freg.security.oidc.idp.registry.IdpRegistry;
import no.nav.freg.security.test.oidc.tools.RsaKey;
import org.jose4j.http.SimpleGet;
import org.jose4j.http.SimpleResponse;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@Configuration
public class SecurityTestConfig {

    public static final String OPEN_AM_ISSUER_URL = "http://openAmIssuerUrl";

    @Autowired
    private IdpRegistry idpRegistry;

    @Bean
    @Primary
    SimpleGet simpleGetMock() throws Exception {
        SimpleGet simpleGet = mock(SimpleGet.class);
        mockRsa(issuerOpenAm(), simpleGet);
        return simpleGet;
    }

    private void mockRsa(RsaKey rsaKey, SimpleGet simpleGet) throws IOException {
        String jwks = idpRegistry.findByIssuer(rsaKey.getIssuer()).map(Idp::getJwksUrl).orElse(rsaKey.getIssuer());

        SimpleResponse response = mock(SimpleResponse.class);
        String value = rsaKey.getWebKey().toJson();

        when(response.getBody()).thenReturn(format("{\"keys\":[%s]}", value));
        when(simpleGet.get(jwks)).thenReturn(response);
    }

    @Bean
    RsaKey issuerOpenAm() throws Exception {
        RsaJsonWebKey webKey = RsaJwkGenerator.generateJwk(2048);
        webKey.setKeyId("openAm1");
        webKey.setAlgorithm("RSA256");
        return new RsaKey(OPEN_AM_ISSUER_URL, webKey);
    }
}
