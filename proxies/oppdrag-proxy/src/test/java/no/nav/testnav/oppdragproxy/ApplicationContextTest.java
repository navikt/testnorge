package no.nav.testnav.oppdragproxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
