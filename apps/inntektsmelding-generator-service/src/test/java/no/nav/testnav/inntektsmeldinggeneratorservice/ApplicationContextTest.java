package no.nav.testnav.inntektsmeldinggeneratorservice;

import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class ApplicationContextTest {

    @MockBean
    public JwtDecoder jwtDecoder;

    @MockBean
    public MapperFacade mapperFacade;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
