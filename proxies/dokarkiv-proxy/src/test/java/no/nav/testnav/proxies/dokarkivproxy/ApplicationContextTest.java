package no.nav.testnav.proxies.dokarkivproxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    @SuppressWarnings("unused")
    public ReactiveJwtDecoder reactiveJwtDecoder;

    @Test
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
