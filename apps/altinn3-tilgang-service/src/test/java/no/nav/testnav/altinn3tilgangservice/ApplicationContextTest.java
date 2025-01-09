package no.nav.testnav.altinn3tilgangservice;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    @SuppressWarnings("unused")
    public SecretManagerServiceClient secretManagerClient;

    @Test
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
