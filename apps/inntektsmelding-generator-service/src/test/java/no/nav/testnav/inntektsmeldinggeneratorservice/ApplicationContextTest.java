package no.nav.testnav.inntektsmeldinggeneratorservice;

import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class ApplicationContextTest {

    @MockitoBean
    public JwtDecoder jwtDecoder;

    @MockitoBean
    public MapperFacade mapperFacade;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
