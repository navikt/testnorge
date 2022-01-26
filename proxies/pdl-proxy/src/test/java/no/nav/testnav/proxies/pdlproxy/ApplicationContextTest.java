package no.nav.testnav.proxies.pdlproxy;

import no.nav.testnav.libs.securitytokenservice.StsOidcTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private StsOidcTokenService stsOidcTokenService;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
