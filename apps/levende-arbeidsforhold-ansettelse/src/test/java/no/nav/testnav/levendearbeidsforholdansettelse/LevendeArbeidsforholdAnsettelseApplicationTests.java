package no.nav.testnav.levendearbeidsforholdansettelse;

import no.nav.testnav.libs.DollySpringBootTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DollySpringBootTest
class LevendeArbeidsforholdAnsettelseApplicationTests {

    @Test
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
