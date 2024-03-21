package no.nav.testnav.kodeverkservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationContextTest {

    @MockBean
    public JwtDecoder jwtDecoder;

    @Test
    @DisplayName("Application context should load")
    void load_app_context() {
        assertThat(true).isTrue();
    }
}
