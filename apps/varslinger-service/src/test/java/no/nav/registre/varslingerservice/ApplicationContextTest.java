package no.nav.registre.varslingerservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    public JwtDecoder jwtDecoder;

    /**
     * Used to test app runtime dependency and properties
     */
    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
