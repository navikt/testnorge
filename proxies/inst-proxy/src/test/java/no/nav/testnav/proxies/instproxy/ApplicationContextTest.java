package no.nav.testnav.proxies.instproxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

@SpringBootTest
class ApplicationContextTest {

    @MockBean
    public ReactiveJwtDecoder reactiveJwtDecoder;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
