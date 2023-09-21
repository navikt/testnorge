package no.nav.dolly.budpro;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class BudproServiceApplicationTest {

    @Test
    void contextLoads() {
        assertThat(true)
                .isTrue();
    }

}
