package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekter;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.dolly.repository.BestillingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InntektServiceTest {

    private static final String IDENT = "12345678901";
    private static final String VIRKSOMHET = "123456789";

    @Mock
    private BestillingRepository bestillingRepository;

    private JsonMapper jsonMapper;
    private InntektService inntektService;

    @BeforeEach
    void setUp() {
        jsonMapper = new JsonMapper();
        inntektService = new InntektService(
                bestillingRepository,
                jsonMapper,
                MapperTestUtils.createMapperFacadeForMappingStrategy());
    }

    @Test
    void shouldExcludeFirstPeriodFromInntekter() {

        var oppdatert = deleteInntekt(
                bestillingMedInntekter("2025-01", 5),
                YearMonth.of(2025, 1));

        assertThat(oppdatert.getInntekter())
                .singleElement()
                .satisfies(inntekt -> {
                    assertThat(inntekt.getStartAarMaaned()).isEqualTo("2025-02");
                    assertThat(inntekt.getAntallMaaneder()).isEqualTo(4);
                    assertThat(inntekt.getVirksomhet()).isEqualTo(VIRKSOMHET);
                });
    }

    @Test
    void shouldExcludeLastPeriodFromInntekter() {

        var oppdatert = deleteInntekt(
                bestillingMedInntekter("2025-01", 5),
                YearMonth.of(2025, 5));

        assertThat(oppdatert.getInntekter())
                .singleElement()
                .satisfies(inntekt -> {
                    assertThat(inntekt.getStartAarMaaned()).isEqualTo("2025-01");
                    assertThat(inntekt.getAntallMaaneder()).isEqualTo(4);
                });
    }

    @Test
    void shouldRemoveSinglePeriodFromInntekter() {

        var oppdatert = deleteInntekt(
                bestillingMedInntekter("2025-01", 1),
                YearMonth.of(2025, 1));

        assertThat(oppdatert.getInntekter()).isEmpty();
    }

    @Test
    void shouldSplitInntekterWhenMiddlePeriodIsExcluded() {

        var oppdatert = deleteInntekt(
                bestillingMedInntekter("2025-01", 5),
                YearMonth.of(2025, 3));

        assertThat(oppdatert.getInntekter())
                .extracting(RsInntekter::getStartAarMaaned, RsInntekter::getAntallMaaneder)
                .containsExactly(
                        tuple("2025-01", 2),
                        tuple("2025-04", 2));
    }

    @Test
    void shouldNotSaveBestillingWhenPeriodIsOutsideInntekter() {

        var bestilling = lagBestilling(bestillingMedInntekter("2025-01", 5));
        when(bestillingRepository.findBestillingerByIdent(IDENT))
                .thenReturn(Flux.just(bestilling));

        StepVerifier.create(inntektService.deleteInntekt(IDENT, YearMonth.of(2025, 6)))
                .verifyComplete();

        verify(bestillingRepository, never()).save(any());
    }

    @ParameterizedTest
    @MethodSource("ugyldigeAntallMaaneder")
    void shouldTreatInvalidAntallMaanederAsSinglePeriod(Integer antallMaaneder) {

        var oppdatert = deleteInntekt(
                bestillingMedInntekter("2025-01", antallMaaneder),
                YearMonth.of(2025, 1));

        assertThat(oppdatert.getInntekter()).isEmpty();
    }

    @Test
    void shouldExcludeFirstPeriodFromInntektstub() {

        var oppdatert = deleteInntekt(
                bestillingMedInntektstub("2025-05", 5),
                YearMonth.of(2025, 1));

        assertThat(oppdatert.getInntektstub().getInntektsinformasjon())
                .singleElement()
                .satisfies(inntekt -> {
                    assertThat(inntekt.getSisteAarMaaned()).isEqualTo("2025-05");
                    assertThat(inntekt.getAntallMaaneder()).isEqualTo(4);
                    assertThat(inntekt.getVirksomhet()).isEqualTo(VIRKSOMHET);
                });
    }

    @Test
    void shouldExcludeLastPeriodFromInntektstub() {

        var oppdatert = deleteInntekt(
                bestillingMedInntektstub("2025-05", 5),
                YearMonth.of(2025, 5));

        assertThat(oppdatert.getInntektstub().getInntektsinformasjon())
                .singleElement()
                .satisfies(inntekt -> {
                    assertThat(inntekt.getSisteAarMaaned()).isEqualTo("2025-04");
                    assertThat(inntekt.getAntallMaaneder()).isEqualTo(4);
                });
    }

    @Test
    void shouldRemoveSinglePeriodFromInntektstub() {

        var oppdatert = deleteInntekt(
                bestillingMedInntektstub("2025-01", 1),
                YearMonth.of(2025, 1));

        assertThat(oppdatert.getInntektstub().getInntektsinformasjon()).isEmpty();
    }

    @Test
    void shouldSplitInntektstubWhenMiddlePeriodIsExcluded() {

        var oppdatert = deleteInntekt(
                bestillingMedInntektstub("2025-05", 5),
                YearMonth.of(2025, 3));

        assertThat(oppdatert.getInntektstub().getInntektsinformasjon())
                .extracting(RsInntektsinformasjon::getSisteAarMaaned, RsInntektsinformasjon::getAntallMaaneder)
                .containsExactly(
                        tuple("2025-02", 2),
                        tuple("2025-05", 2));
    }

    @Test
    void shouldNotSaveBestillingWhenPeriodIsOutsideInntektstub() {

        var bestilling = lagBestilling(bestillingMedInntektstub("2025-05", 5));
        when(bestillingRepository.findBestillingerByIdent(IDENT))
                .thenReturn(Flux.just(bestilling));

        StepVerifier.create(inntektService.deleteInntekt(IDENT, YearMonth.of(2024, 12)))
                .verifyComplete();

        verify(bestillingRepository, never()).save(any());
    }

    private RsDollyBestilling deleteInntekt(RsDollyBestilling dollyBestilling, YearMonth periode) {

        var bestilling = lagBestilling(dollyBestilling);
        var bestillingCaptor = ArgumentCaptor.forClass(Bestilling.class);

        when(bestillingRepository.findBestillingerByIdent(IDENT))
                .thenReturn(Flux.just(bestilling));
        when(bestillingRepository.save(any(Bestilling.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(inntektService.deleteInntekt(IDENT, periode))
                .verifyComplete();

        verify(bestillingRepository).save(bestillingCaptor.capture());
        return jsonMapper.readValue(
                bestillingCaptor.getValue().getBestKriterier(),
                RsDollyBestilling.class);
    }

    private Bestilling lagBestilling(RsDollyBestilling dollyBestilling) {

        return Bestilling.builder()
                .id(1L)
                .bestKriterier(jsonMapper.writeValueAsString(dollyBestilling))
                .build();
    }

    private static RsDollyBestilling bestillingMedInntekter(String startAarMaaned, Integer antallMaaneder) {

        return RsDollyBestilling.builder()
                .inntekter(List.of(RsInntekter.builder()
                        .startAarMaaned(startAarMaaned)
                        .antallMaaneder(antallMaaneder)
                        .virksomhet(VIRKSOMHET)
                        .build()))
                .build();
    }

    private static RsDollyBestilling bestillingMedInntektstub(String sisteAarMaaned, Integer antallMaaneder) {

        return RsDollyBestilling.builder()
                .inntektstub(InntektMultiplierWrapper.builder()
                        .inntektsinformasjon(List.of(RsInntektsinformasjon.builder()
                                .sisteAarMaaned(sisteAarMaaned)
                                .antallMaaneder(antallMaaneder)
                                .virksomhet(VIRKSOMHET)
                                .build()))
                        .build())
                .build();
    }

    private static Stream<Arguments> ugyldigeAntallMaaneder() {

        return Stream.of(
                Arguments.of((Integer) null),
                Arguments.of(0),
                Arguments.of(-1));
    }
}
