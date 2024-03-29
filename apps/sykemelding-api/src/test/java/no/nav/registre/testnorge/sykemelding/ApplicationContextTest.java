package no.nav.registre.testnorge.sykemelding;

import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    private TokenExchange tokenExchange;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
