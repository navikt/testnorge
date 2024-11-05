package no.nav.testnav.altinn3tilgangservice;

import ma.glasnost.orika.MapperFacade;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationContextTest {

    @MockBean
    public ReactiveJwtDecoder jwtDecoder;

    @MockBean
    public MapperFacade mapperFacade;

    @MockBean
    public Flyway flyway;

    @Test
    @SuppressWarnings("java:S2699")
    void load_app_context() {
    }
}
