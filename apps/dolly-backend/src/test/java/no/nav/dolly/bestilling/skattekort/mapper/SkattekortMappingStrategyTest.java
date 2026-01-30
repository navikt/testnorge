package no.nav.dolly.bestilling.skattekort.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.skattekortservice.v1.ArbeidsgiverSkatt;
import no.nav.testnav.libs.dto.skattekortservice.v1.IdentifikatorForEnhetEllerPerson;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import no.nav.testnav.libs.dto.skattekortservice.v1.SokosSkattekortRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SkattekortMappingStrategyTest {

    private static final String IDENT = "01010112345";
    private static final String ORG_NR = "123456789";
    private static final Integer INNTEKTSAAR = 2026;

    private MapperFacade mapperFacade;

    private SkattekortRequestDTO createSkattekortRequest() {
        return SkattekortRequestDTO.builder()
                .arbeidsgiverSkatt(List.of(
                        ArbeidsgiverSkatt.builder()
                                .arbeidsgiveridentifikator(IdentifikatorForEnhetEllerPerson.builder()
                                        .organisasjonsnummer(ORG_NR)
                                        .build())
                                .arbeidstaker(List.of(
                                        Skattekortmelding.builder()
                                                .inntektsaar(INNTEKTSAAR)
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    private MappingContext createMappingContext(String ident) {
        var context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", ident);
        return context;
    }

    @Nested
    @DisplayName("SkattekortRequestDTO mapping")
    class SokosSkattekortRequestMappingTest {

        @Test
        void shouldMapToFnrAndSkattekort() {
            var source = createSkattekortRequest();
            var context = createMappingContext(IDENT);

            var result = mapperFacade.map(source, SokosSkattekortRequest.class, context);

            assertThat(result.getFnr()).isEqualTo(IDENT);
            assertThat(result.getSkattekort()).isNotNull();
        }

        @Test
        void shouldMapInntektsaar() {
            var source = createSkattekortRequest();
            var context = createMappingContext(IDENT);

            var result = mapperFacade.map(source, SokosSkattekortRequest.class, context);

            assertThat(result.getSkattekort().getInntektsaar()).isEqualTo(INNTEKTSAAR);
        }

        @Test
        void shouldSetFnrFromContext() {
            var source = createSkattekortRequest();
            var context = createMappingContext(IDENT);

            var result = mapperFacade.map(source, SokosSkattekortRequest.class, context);

            assertThat(result.getFnr()).isEqualTo(IDENT);
        }

        @Test
        void shouldNotMapNullValues() {
            var source = SkattekortRequestDTO.builder().build();
            var context = createMappingContext(IDENT);

            var result = mapperFacade.map(source, SokosSkattekortRequest.class, context);

            assertThat(result).isNotNull();
            assertThat(result.getFnr()).isNull();
            assertThat(result.getSkattekort()).isNull();
        }
    }

    @BeforeEach
    void setUp() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new SkattekortMappingStrategy());
    }
}
