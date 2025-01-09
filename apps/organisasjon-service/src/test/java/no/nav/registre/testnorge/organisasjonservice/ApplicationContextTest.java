package no.nav.registre.testnorge.organisasjonservice;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationContextTest {

    @Test
    public void loadAppContext() {
        assertThat(true).isTrue();
    }

}
