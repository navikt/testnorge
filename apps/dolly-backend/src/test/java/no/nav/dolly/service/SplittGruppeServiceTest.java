package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SplittGruppeServiceTest {

    private static final Long NY_GRUPPE_ID = 2L;
    private static final Long KILDE_GRUPPE_ID = 1L;
    private static final Long KILDE_BESTILLING_ID = 100L;
    private static final Long NY_BESTILLING_ID = 200L;

    @Mock
    private BestillingRepository bestillingRepository;

    @Mock
    private BestillingProgressRepository bestillingProgressRepository;

    @Mock
    private TransaksjonMappingRepository transaksjonMappingRepository;

    @Mock
    private TestgruppeRepository testgruppeRepository;

    @Mock
    private IdentRepository identRepository;

    @Mock
    private BestillingKontrollRepository bestillingKontrollRepository;

    @Captor
    private ArgumentCaptor<Bestilling> bestillingCaptor;

    @InjectMocks
    private SplittGruppeService splittGruppeService;

    @ParameterizedTest
    @ValueSource(ints = {1, -2})
    void shouldSetAntallIdenterToMovedCountOnCopy(int antallIdenter) {

        var identer = Set.of("A");
        mockFlytting(identer, antallIdenter, List.of("A"), Mono.empty());

        StepVerifier.create(splittGruppeService.flyttIdenterTilDenneGruppe(NY_GRUPPE_ID, identer))
                .verifyComplete();

        var lagredeBestillinger = getLagredeBestillinger();
        assertThat(getKopi(lagredeBestillinger).getAntallIdenter()).isEqualTo(1);
        assertThat(getLagredeKildeBestillinger(lagredeBestillinger)).isEmpty();
    }

    @Test
    void shouldDecrementSourceOnPartialMove() {

        var identer = Set.of("A");
        mockFlytting(identer, 3, List.of("A", "B", "C"), Mono.just(lagKildeBestilling(3)));

        StepVerifier.create(splittGruppeService.flyttIdenterTilDenneGruppe(NY_GRUPPE_ID, identer))
                .verifyComplete();

        var lagredeBestillinger = getLagredeBestillinger();
        assertThat(getKopi(lagredeBestillinger).getAntallIdenter()).isEqualTo(1);
        assertThat(getLagredeKildeBestillinger(lagredeBestillinger))
                .singleElement()
                .extracting(Bestilling::getAntallIdenter)
                .isEqualTo(2);
    }

    @Test
    void shouldFloorSourceAtZero() {

        var identer = Set.of("A", "B");
        mockFlytting(identer, 1, List.of("A", "B"), Mono.just(lagKildeBestilling(1)));

        StepVerifier.create(splittGruppeService.flyttIdenterTilDenneGruppe(NY_GRUPPE_ID, identer))
                .verifyComplete();

        var lagredeBestillinger = getLagredeBestillinger();
        assertThat(getKopi(lagredeBestillinger).getAntallIdenter()).isEqualTo(2);
        assertThat(getLagredeKildeBestillinger(lagredeBestillinger))
                .singleElement()
                .extracting(Bestilling::getAntallIdenter)
                .isEqualTo(0);
    }

    private void mockFlytting(Set<String> identer, int antallIdenter, List<String> progressIdenter,
                              Mono<Bestilling> kildeEtterSletting) {

        when(testgruppeRepository.findById(NY_GRUPPE_ID))
                .thenReturn(Mono.just(Testgruppe.builder().id(NY_GRUPPE_ID).build()));
        when(bestillingRepository.findBestillingerByIdentIn(identer))
                .thenReturn(Flux.just(lagKildeBestilling(antallIdenter)));
        when(bestillingRepository.save(any(Bestilling.class))).thenAnswer(invocation -> {
            Bestilling bestilling = invocation.getArgument(0);
            if (isNull(bestilling.getId())) {
                var lagret = SerializationUtils.clone(bestilling);
                lagret.setId(NY_BESTILLING_ID);
                return Mono.just(lagret);
            }
            return Mono.just(bestilling);
        });
        when(transaksjonMappingRepository.findAllByBestillingId(anyLong())).thenReturn(Flux.empty());
        when(bestillingProgressRepository.findAllByBestillingId(KILDE_BESTILLING_ID))
                .thenReturn(Flux.fromIterable(progressIdenter.stream()
                        .map(ident -> BestillingProgress.builder()
                                .id((long) ident.hashCode())
                                .bestillingId(KILDE_BESTILLING_ID)
                                .ident(ident)
                                .build())
                        .toList()));
        when(bestillingProgressRepository.deleteById(anyLong())).thenReturn(Mono.empty());
        when(bestillingProgressRepository.save(any(BestillingProgress.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(bestillingKontrollRepository.deleteByBestillingWithNoChildren(KILDE_BESTILLING_ID)).thenReturn(Mono.empty());
        when(bestillingRepository.deleteBestillingWithNoChildren(KILDE_BESTILLING_ID)).thenReturn(Mono.empty());
        when(bestillingRepository.findById(KILDE_BESTILLING_ID)).thenReturn(kildeEtterSletting);
        when(identRepository.findByIdentIn(identer)).thenReturn(Flux.empty());
    }

    private static Bestilling lagKildeBestilling(int antallIdenter) {

        return Bestilling.builder()
                .id(KILDE_BESTILLING_ID)
                .versjon(1L)
                .gruppeId(KILDE_GRUPPE_ID)
                .antallIdenter(antallIdenter)
                .ferdig(true)
                .build();
    }

    private List<Bestilling> getLagredeBestillinger() {

        verify(bestillingRepository, atLeastOnce()).save(bestillingCaptor.capture());
        return bestillingCaptor.getAllValues();
    }

    private static Bestilling getKopi(List<Bestilling> lagredeBestillinger) {

        return lagredeBestillinger.stream()
                .filter(bestilling -> NY_BESTILLING_ID.equals(bestilling.getId()))
                .findFirst()
                .orElseThrow();
    }

    private static List<Bestilling> getLagredeKildeBestillinger(List<Bestilling> lagredeBestillinger) {

        return lagredeBestillinger.stream()
                .filter(bestilling -> KILDE_BESTILLING_ID.equals(bestilling.getId()))
                .toList();
    }
}
