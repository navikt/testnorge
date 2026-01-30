package no.nav.dolly.bestilling.skattekort.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.skattekortservice.v1.ArbeidsgiverSkatt;
import no.nav.testnav.libs.dto.skattekortservice.v1.IdentifikatorForEnhetEllerPerson;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
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
    class SkattekortRequestDTOMappingTest {

        @Test
        void shouldMapArbeidsgiverSkattToArbeidsgiver() {
            var source = createSkattekortRequest();

            var result = mapperFacade.map(source, no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO.class);

            assertThat(result.getArbeidsgiver()).hasSize(1);
        }

        @Test
        void shouldMapArbeidsgiveridentifikator() {
            var source = createSkattekortRequest();

            var result = mapperFacade.map(source, no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO.class);

            assertThat(result.getArbeidsgiver().get(0).getArbeidsgiveridentifikator().getOrganisasjonsnummer())
                    .isEqualTo(ORG_NR);
        }

        @Test
        void shouldMapArbeidstaker() {
            var source = createSkattekortRequest();

            var result = mapperFacade.map(source, no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO.class);

            assertThat(result.getArbeidsgiver().get(0).getArbeidstaker()).hasSize(1);
            assertThat(result.getArbeidsgiver().get(0).getArbeidstaker().get(0).getInntektsaar())
                    .isEqualTo(INNTEKTSAAR);
        }

        @Test
        void shouldSetArbeidstakeridentifikatorFromContext() {
            var source = createSkattekortRequest();
            var context = createMappingContext(IDENT);

            var result = mapperFacade.map(source, no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO.class, context);

            assertThat(result.getArbeidsgiver().get(0).getArbeidstaker().get(0).getArbeidstakeridentifikator())
                    .isEqualTo(IDENT);
        }

        @Test
        void shouldSetArbeidstakeridentifikatorOnMultipleArbeidstakere() {
            var source = SkattekortRequestDTO.builder()
                    .arbeidsgiverSkatt(List.of(
                            ArbeidsgiverSkatt.builder()
                                    .arbeidsgiveridentifikator(IdentifikatorForEnhetEllerPerson.builder()
                                            .organisasjonsnummer(ORG_NR)
                                            .build())
                                    .arbeidstaker(List.of(
                                            Skattekortmelding.builder().inntektsaar(2025).build(),
                                            Skattekortmelding.builder().inntektsaar(2026).build()
                                    ))
                                    .build()
                    ))
                    .build();
            var context = createMappingContext(IDENT);

            var result = mapperFacade.map(source, no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO.class, context);

            assertThat(result.getArbeidsgiver().get(0).getArbeidstaker()).hasSize(2);
            result.getArbeidsgiver().get(0).getArbeidstaker()
                    .forEach(arbeidstaker -> assertThat(arbeidstaker.getArbeidstakeridentifikator()).isEqualTo(IDENT));
        }

        @Test
        void shouldSetArbeidstakeridentifikatorOnMultipleArbeidsgivere() {
            var source = SkattekortRequestDTO.builder()
                    .arbeidsgiverSkatt(List.of(
                            ArbeidsgiverSkatt.builder()
                                    .arbeidsgiveridentifikator(IdentifikatorForEnhetEllerPerson.builder()
                                            .organisasjonsnummer("111111111")
                                            .build())
                                    .arbeidstaker(List.of(Skattekortmelding.builder().inntektsaar(2025).build()))
                                    .build(),
                            ArbeidsgiverSkatt.builder()
                                    .arbeidsgiveridentifikator(IdentifikatorForEnhetEllerPerson.builder()
                                            .organisasjonsnummer("222222222")
                                            .build())
                                    .arbeidstaker(List.of(Skattekortmelding.builder().inntektsaar(2026).build()))
                                    .build()
                    ))
                    .build();
            var context = createMappingContext(IDENT);

            var result = mapperFacade.map(source, no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO.class, context);

            assertThat(result.getArbeidsgiver()).hasSize(2);
            result.getArbeidsgiver()
                    .forEach(arbeidsgiver -> arbeidsgiver.getArbeidstaker()
                            .forEach(arbeidstaker -> assertThat(arbeidstaker.getArbeidstakeridentifikator()).isEqualTo(IDENT)));
        }

        @Test
        void shouldNotMapNullValues() {
            var source = SkattekortRequestDTO.builder().build();

            var result = mapperFacade.map(source, no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO.class);

            assertThat(result).isNotNull();
            assertThat(result.getArbeidsgiver()).isEmpty();
        }
    }

    @BeforeEach
    void setUp() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new SkattekortMappingStrategy());
    }
}
