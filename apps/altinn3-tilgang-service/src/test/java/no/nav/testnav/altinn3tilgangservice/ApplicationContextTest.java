package no.nav.testnav.altinn3tilgangservice;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    public ReactiveJwtDecoder jwtDecoder;

    @MockBean
    public SecretManagerServiceClient secretManagerClient;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
