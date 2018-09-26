package no.nav.identpool;

import static no.nav.identpool.SecurityTestConfig.NAV_STS_ISSUER_URL;

import java.time.LocalDateTime;
import org.jose4j.jwt.JwtClaims;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.freg.security.test.oidc.tools.JwtClaimsBuilder;
import no.nav.freg.security.test.oidc.tools.OidcTestService;
import no.nav.identpool.ident.ajourhold.mq.factory.ConnectionStrategyFactory;
import no.nav.identpool.ident.repository.IdentRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ComponentTestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ComponentTestbase {
    protected static final String IDENT_V1_BASEURL = "/api/v1/identifikator";
    protected static final String FINNESHOSSKATT_V1_BASEURL = "/api/v1/finneshosskatt";

    @Autowired
    protected IdentRepository identRepository;
    @Autowired
    protected TestRestTemplate testRestTemplate;
    @Autowired
    private OidcTestService oidcTestService;

    protected HttpEntity lagHttpEntity(boolean withOidc) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
        if (withOidc) {
            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + oidcTestService.createOidc(getJwtClaims()));
        }
        return new HttpEntity(httpHeaders);
    }

    private JwtClaims getJwtClaims() {
        return new JwtClaimsBuilder()
                .subject("sub")
                .audience("aud")
                .expiry(LocalDateTime.now().plusMinutes(10))
                .validFrom(LocalDateTime.now().minusMinutes(5))
                .azp("azp")
                .issuer(NAV_STS_ISSUER_URL)
                .build();
    }

}
