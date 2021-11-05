package no.nav.dolly.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    public TokenExchange exchange;

    @MockBean
    public JwtDecoder jwtDecoder;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
