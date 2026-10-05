package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.inntektstub.InntektstubConsumer;
import no.nav.dolly.bestilling.inntektstub.domain.DeleteMonthDTO;
import no.nav.dolly.bestilling.inntektstub.domain.ResponseDTO;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekter;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.repository.BestillingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InntektServiceTest {

    private static final String IDENT = "12345678901";
    private static final String CRITERIA_JSON = "criteria";
    private static final String UPDATED_CRITERIA_JSON = "updated criteria";

    @Mock
    private BestillingRepository bestillingRepository;

    @Mock
    private InntektstubConsumer inntektstubConsumer;

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private InntektService inntektService;

    @Test
    void shouldRemoveRequestedMonthFromPeriodListAndMultiplier() {

        var period = YearMonth.of(2025, 3);
        var rsInntekter = RsInntekter.builder()
                .perioder(monthsBetween(YearMonth.of(2025, 1), YearMonth.of(2025, 5)))
                .virksomhet("business")
                .build();
        var rsInntektsinformasjon = RsInntektsinformasjon.builder()
                .sisteAarMaaned("2025-05")
                .antallMaaneder(5)
                .virksomhet("business")
                .build();
        var bestillingCriteria = RsDollyBestilling.builder()
                .inntekter(List.of(rsInntekter))
                .inntektstub(InntektMultiplierWrapper.builder()
                        .inntektsinformasjon(List.of(rsInntektsinformasjon))
                        .build())
                .build();
        var bestilling = Bestilling.builder().bestKriterier(CRITERIA_JSON).build();

        when(bestillingRepository.findBestillingerByIdent(IDENT)).thenReturn(Flux.just(bestilling));
        when(jsonMapper.readValue(CRITERIA_JSON, RsDollyBestilling.class)).thenReturn(bestillingCriteria);
        when(jsonMapper.writeValueAsString(any(RsDollyBestilling.class))).thenReturn(UPDATED_CRITERIA_JSON);
        when(bestillingRepository.save(bestilling)).thenReturn(Mono.just(bestilling));
        when(inntektstubConsumer.slettSpesifikkMaaned(any())).thenReturn(Mono.just(new ResponseDTO()));
        when(mapperFacade.map(any(RsInntekter.class), eq(RsInntekter.class)))
                .thenAnswer(invocation -> copyInntekter(invocation.getArgument(0)));
        when(mapperFacade.map(any(RsInntektsinformasjon.class), eq(RsInntektsinformasjon.class)))
                .thenAnswer(invocation -> copyInntektsinformasjon(invocation.getArgument(0)));

        StepVerifier.create(inntektService.deleteInntekt(IDENT, period))
                .verifyComplete();

        var updatedCriteriaCaptor = ArgumentCaptor.forClass(RsDollyBestilling.class);
        verify(jsonMapper).writeValueAsString(updatedCriteriaCaptor.capture());
        var updatedCriteria = updatedCriteriaCaptor.getValue();
        assertThat(updatedCriteria.getInntekter())
                .extracting(RsInntekter::getPerioder)
                .containsExactly(
                        List.of(YearMonth.of(2025, 1), YearMonth.of(2025, 2)),
                        List.of(YearMonth.of(2025, 4), YearMonth.of(2025, 5)));
        assertThat(updatedCriteria.getInntektstub().getInntektsinformasjon())
                .extracting(RsInntektsinformasjon::getSisteAarMaaned)
                .containsExactly("2025-02", "2025-05");
        assertThat(updatedCriteria.getInntektstub().getInntektsinformasjon())
                .extracting(RsInntektsinformasjon::getAntallMaaneder)
                .containsExactly(2, 2);
        assertThat(bestilling.getBestKriterier()).isEqualTo(UPDATED_CRITERIA_JSON);

        var deleteMonthCaptor = ArgumentCaptor.forClass(DeleteMonthDTO.class);
        verify(inntektstubConsumer).slettSpesifikkMaaned(deleteMonthCaptor.capture());
        assertThat(deleteMonthCaptor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(DeleteMonthDTO.builder()
                        .norskIdent(IDENT)
                        .aarMaaned("2025-03")
                        .build());
    }

    @Test
    void shouldDeleteMonthInInntektstubWithoutSavingWhenNoBestillingContainsPeriod() {

        var bestillingCriteria = RsDollyBestilling.builder()
                .inntekter(List.of(RsInntekter.builder()
                        .perioder(List.of(YearMonth.of(2025, 1), YearMonth.of(2025, 2)))
                        .build()))
                .build();
        var bestilling = Bestilling.builder().bestKriterier(CRITERIA_JSON).build();

        when(bestillingRepository.findBestillingerByIdent(IDENT)).thenReturn(Flux.just(bestilling));
        when(jsonMapper.readValue(CRITERIA_JSON, RsDollyBestilling.class)).thenReturn(bestillingCriteria);
        when(inntektstubConsumer.slettSpesifikkMaaned(any())).thenReturn(Mono.just(new ResponseDTO()));

        StepVerifier.create(inntektService.deleteInntekt(IDENT, YearMonth.of(2025, 3)))
                .verifyComplete();

        verify(bestillingRepository, never()).save(any());
        verify(jsonMapper, never()).writeValueAsString(any());
        verify(inntektstubConsumer).slettSpesifikkMaaned(DeleteMonthDTO.builder()
                .norskIdent(IDENT)
                .aarMaaned("2025-03")
                .build());
    }

    private static RsInntekter copyInntekter(RsInntekter source) {

        return RsInntekter.builder()
                .perioder(new ArrayList<>(source.getPerioder()))
                .opplysningspliktig(source.getOpplysningspliktig())
                .virksomhet(source.getVirksomhet())
                .inntektsliste(source.getInntektsliste())
                .fradragsliste(source.getFradragsliste())
                .forskuddstrekksliste(source.getForskuddstrekksliste())
                .rapporteringsdato(source.getRapporteringsdato())
                .versjon(source.getVersjon())
                .build();
    }

    private static RsInntektsinformasjon copyInntektsinformasjon(RsInntektsinformasjon source) {

        return RsInntektsinformasjon.builder()
                .antallMaaneder(source.getAntallMaaneder())
                .sisteAarMaaned(source.getSisteAarMaaned())
                .opplysningspliktig(source.getOpplysningspliktig())
                .virksomhet(source.getVirksomhet())
                .inntektsliste(source.getInntektsliste())
                .fradragsliste(source.getFradragsliste())
                .forskuddstrekksliste(source.getForskuddstrekksliste())
                .rapporteringsdato(source.getRapporteringsdato())
                .versjon(source.getVersjon())
                .build();
    }

    private static List<YearMonth> monthsBetween(YearMonth start, YearMonth end) {

        var months = new ArrayList<YearMonth>();
        for (var month = start; !month.isAfter(end); month = month.plusMonths(1)) {
            months.add(month);
        }
        return months;
    }
}
