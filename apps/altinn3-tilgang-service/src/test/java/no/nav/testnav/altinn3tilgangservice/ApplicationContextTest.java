package no.nav.testnav.altinn3tilgangservice;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockitoBean
    @SuppressWarnings("unused")
    public ReactiveJwtDecoder jwtDecoder;

    @MockitoBean
    @SuppressWarnings("unused")
    public SecretManagerServiceClient secretManagerClient;

    @Test
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
