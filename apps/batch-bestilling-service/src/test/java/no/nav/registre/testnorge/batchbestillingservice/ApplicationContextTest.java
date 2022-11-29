package no.nav.registre.testnorge.batchbestillingservice;

import no.nav.testnav.libs.reactivesecurity.decoder.JwtDecoder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class ApplicationContextTest {

    @MockBean
    public JwtDecoder jwtDecoder;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
