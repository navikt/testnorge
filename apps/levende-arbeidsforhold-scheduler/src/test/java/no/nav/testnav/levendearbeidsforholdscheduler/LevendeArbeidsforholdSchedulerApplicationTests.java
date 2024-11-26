package no.nav.testnav.levendearbeidsforholdscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LevendeArbeidsforholdSchedulerApplicationTests {

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }

}
