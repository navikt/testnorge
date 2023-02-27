package no.nav.testnav.apps.tpsmessagingservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @Test
    @DisplayName("Application context should load")
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
