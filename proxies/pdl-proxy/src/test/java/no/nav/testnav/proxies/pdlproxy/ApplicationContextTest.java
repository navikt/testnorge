package no.nav.testnav.proxies.pdlproxy;

import no.nav.dolly.libs.nais.DollySpringBootTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DollySpringBootTest
class ApplicationContextTest {

    @Test
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
