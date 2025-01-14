package no.nav.registre.testnav.ameldingservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockitoBean
    public ReactiveJwtDecoder reactiveJwtDecoder;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
