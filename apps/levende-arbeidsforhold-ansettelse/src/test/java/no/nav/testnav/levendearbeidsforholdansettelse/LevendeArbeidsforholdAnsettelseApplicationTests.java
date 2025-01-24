package no.nav.testnav.levendearbeidsforholdansettelse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LevendeArbeidsforholdAnsettelseApplicationTests {

    @Test
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
