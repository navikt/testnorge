package no.nav.testnav.levendearbeidsforholdscheduler;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Profile("test")
class LevendeArbeidsforholdSchedulerApplicationTests {

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }

}
