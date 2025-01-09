package no.nav.testnav.inntektsmeldinggeneratorservice;

import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class ApplicationContextTest {

    @MockBean
    @SuppressWarnings("unused")
    public MapperFacade mapperFacade;

    @Test
    void load_app_context() {
        assertThat(true).isTrue();
    }

}
