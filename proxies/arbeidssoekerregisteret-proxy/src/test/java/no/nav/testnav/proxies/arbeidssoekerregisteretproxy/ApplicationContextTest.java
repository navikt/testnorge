package no.nav.testnav.proxies.arbeidssoekerregisteretproxy;

import no.nav.dolly.libs.nais.DollySpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DollySpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @Test
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
