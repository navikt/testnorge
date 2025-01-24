package no.nav.testnav.inntektsmeldinggeneratorservice;

import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ApplicationContextTest {

    @MockitoBean
    @SuppressWarnings("unused")
    public MapperFacade mapperFacade;

    @Test
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
