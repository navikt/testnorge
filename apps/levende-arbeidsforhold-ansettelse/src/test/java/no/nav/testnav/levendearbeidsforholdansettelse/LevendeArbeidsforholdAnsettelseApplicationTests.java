package no.nav.testnav.levendearbeidsforholdansettelse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class LevendeArbeidsforholdAnsettelseApplicationTests {

    @Autowired
    private R2dbcEntityTemplate template;

    @Test
    void load_app_context() {
        assertThat(template).isNotNull();
    }

}
