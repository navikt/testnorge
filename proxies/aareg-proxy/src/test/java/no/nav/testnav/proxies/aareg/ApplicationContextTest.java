package no.nav.testnav.proxies.aareg;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled
@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    @SuppressWarnings("unused")
    private JwtDecoder jwtDecoder;

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }
}
