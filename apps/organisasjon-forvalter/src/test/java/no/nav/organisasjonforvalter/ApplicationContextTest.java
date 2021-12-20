package no.nav.organisasjonforvalter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationContextTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
