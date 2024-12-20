package no.nav.testnav.apps.endringsmeldingfrontend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
        assertThat(true).isTrue();
    }
}
