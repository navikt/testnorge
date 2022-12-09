package no.nav.registre.sdforvalter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    public JwtDecoder jwtDecoder;
    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
