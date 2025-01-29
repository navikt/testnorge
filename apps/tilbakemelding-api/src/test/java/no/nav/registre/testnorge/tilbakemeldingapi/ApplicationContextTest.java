package no.nav.registre.testnorge.tilbakemeldingapi;

import no.nav.testnav.libs.DollySpringBootTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DollySpringBootTest
class ApplicationContextTest {

    @Test
    void loadAppContext() {
        assertThat(true).isTrue();
    }

}
