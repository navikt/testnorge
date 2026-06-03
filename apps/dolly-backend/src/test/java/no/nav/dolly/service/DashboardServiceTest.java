package no.nav.dolly.service;

import no.nav.dolly.consumer.teamkatalog.TeamkatalogConsumer;
import no.nav.dolly.consumer.teamkatalog.dto.TeamkatalogDTO;
import no.nav.dolly.domain.dto.DashboardTeamsDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.projection.BestillingerFragment;
import no.nav.dolly.domain.projection.TeamFragment;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    private static final LocalDate DATE_1 = LocalDate.of(2024, 1, 1);
    private static final LocalDate DATE_2 = LocalDate.of(2024, 1, 2);
    private static final String INTERVAL_1 = "2024-01";
    private static final String INTERVAL_2 = "2024-02";

    @Mock
    private BestillingRepository bestillingRepository;

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private TeamkatalogConsumer teamkatalogConsumer;

    @InjectMocks
    private DashboardService dashboardService;

    // ── getPersonerStatus ────────────────────────────────────────────────────

    @Test
    void shouldReturnEmptyWhenNoFragments() {
        when(bestillingRepository.findBestillingerOrderBySistOppdatert()).thenReturn(Flux.empty());

        StepVerifier.create(dashboardService.getPersonerStatus())
                .verifyComplete();
    }

    @Test
    void shouldSumPersonerTotaltForSingleDate() {
        var f1 = fragment(DATE_1, 5L, "NYBESTILLING", "OK", "OK");
        var f2 = fragment(DATE_1, 3L, "NYBESTILLING", "OK", "OK");
        when(bestillingRepository.findBestillingerOrderBySistOppdatert()).thenReturn(Flux.just(f1, f2));

        StepVerifier.create(dashboardService.getPersonerStatus())
                .assertNext(dto -> {
                    assertThat(dto.getDato()).isEqualTo(DATE_1);
                    assertThat(dto.getPersonerTotalt()).isEqualTo(8L);
                })
                .verifyComplete();
    }

    @Test
    void shouldCountNyeByGjenopprettstatusNYBESTILLING() {
        var nybestilling = fragment(DATE_1, 4L, "NYBESTILLING", "OK", "OK");
        var gjenoppretting = fragment(DATE_1, 2L, "GJENOPPRETTING", "OK", "OK");
        when(bestillingRepository.findBestillingerOrderBySistOppdatert()).thenReturn(Flux.just(nybestilling, gjenoppretting));

        StepVerifier.create(dashboardService.getPersonerStatus())
                .assertNext(dto -> {
                    assertThat(dto.getNye()).isEqualTo(4L);
                    assertThat(dto.getGjenopprettede()).isEqualTo(2L);
                })
                .verifyComplete();
    }

    @Test
    void shouldCountPdlFeilByPdlstatusFEIL() {
        var feil = fragment(DATE_1, 3L, "NYBESTILLING", "FEIL", "OK");
        var ok = fragment(DATE_1, 2L, "NYBESTILLING", "OK", "OK");
        when(bestillingRepository.findBestillingerOrderBySistOppdatert()).thenReturn(Flux.just(feil, ok));

        StepVerifier.create(dashboardService.getPersonerStatus())
                .assertNext(dto -> assertThat(dto.getPdlFeil()).isEqualTo(3L))
                .verifyComplete();
    }

    @Test
    void shouldCountAndreFeilByAnnenstatusFEIL() {
        var feil = fragment(DATE_1, 7L, "NYBESTILLING", "OK", "FEIL");
        when(bestillingRepository.findBestillingerOrderBySistOppdatert()).thenReturn(Flux.just(feil));

        StepVerifier.create(dashboardService.getPersonerStatus())
                .assertNext(dto -> assertThat(dto.getAndreFeil()).isEqualTo(7L))
                .verifyComplete();
    }

    @Test
    void shouldGroupFragmentsByDateIntoSeparateDtos() {
        var d1 = fragment(DATE_1, 10L, "NYBESTILLING", "OK", "OK");
        var d2 = fragment(DATE_2, 5L, "NYBESTILLING", "OK", "OK");
        when(bestillingRepository.findBestillingerOrderBySistOppdatert()).thenReturn(Flux.just(d1, d2));

        StepVerifier.create(dashboardService.getPersonerStatus())
                .assertNext(dto -> assertThat(dto.getDato()).isEqualTo(DATE_2))
                .assertNext(dto -> assertThat(dto.getDato()).isEqualTo(DATE_1))
                .verifyComplete();
    }

    @Test
    void shouldSortPersonerStatusByDateDescending() {
        var d1 = fragment(DATE_1, 1L, "NYBESTILLING", "OK", "OK");
        var d2 = fragment(DATE_2, 1L, "NYBESTILLING", "OK", "OK");
        when(bestillingRepository.findBestillingerOrderBySistOppdatert()).thenReturn(Flux.just(d1, d2));

        StepVerifier.create(dashboardService.getPersonerStatus())
                .assertNext(dto -> assertThat(dto.getDato()).isEqualTo(DATE_2))
                .assertNext(dto -> assertThat(dto.getDato()).isEqualTo(DATE_1))
                .verifyComplete();
    }

    @Test
    void shouldProduceZeroCountsWhenNoMatchingStatuses() {
        var f = fragment(DATE_1, 5L, "UKJENT", "UKJENT", "UKJENT");
        when(bestillingRepository.findBestillingerOrderBySistOppdatert()).thenReturn(Flux.just(f));

        StepVerifier.create(dashboardService.getPersonerStatus())
                .assertNext(dto -> {
                    assertThat(dto.getNye()).isZero();
                    assertThat(dto.getGjenopprettede()).isZero();
                    assertThat(dto.getPdlFeil()).isZero();
                    assertThat(dto.getAndreFeil()).isZero();
                })
                .verifyComplete();
    }

    // ── getTeamsStatus ───────────────────────────────────────────────────────

    @Test
    void shouldReturnEmptyTeamsWhenNoFragments() {
        when(brukerRepository.findAll()).thenReturn(Flux.empty());
        when(bestillingRepository.findBestillingerForTeamsOrderBySistOppdatert()).thenReturn(Flux.empty());

        StepVerifier.create(dashboardService.getTeamsStatus())
                .verifyComplete();
    }

    @Test
    void shouldSkipBrukereWithBlankEpost() {
        var brukerUtenEpost = Bruker.builder().epost("").build();
        var brukerMedEpost = Bruker.builder().epost("user@nav.no").build();
        when(brukerRepository.findAll()).thenReturn(Flux.just(brukerUtenEpost, brukerMedEpost));
        when(teamkatalogConsumer.getTeamForEpost(List.of("user@nav.no")))
                .thenReturn(Flux.just(TeamkatalogDTO.builder()
                        .email("user@nav.no")
                        .teamNavn(List.of("Team A"))
                        .build()));
        when(bestillingRepository.findBestillingerForTeamsOrderBySistOppdatert())
                .thenReturn(Flux.just(teamFragment(INTERVAL_1, "user@nav.no")));

        StepVerifier.create(dashboardService.getTeamsStatus())
                .assertNext(dto -> {
                    assertThat(dto.getTeams()).hasSize(1);
                    assertThat(dto.getTeams().getFirst().getTeam()).isEqualTo("Team A");
                })
                .verifyComplete();
    }

    @Test
    void shouldAssignIngenTeamWhenEmailNotInTeamkatalog() {
        var bruker = Bruker.builder().epost("orphan@nav.no").build();
        when(brukerRepository.findAll()).thenReturn(Flux.just(bruker));
        when(teamkatalogConsumer.getTeamForEpost(anyList())).thenReturn(Flux.empty());
        when(bestillingRepository.findBestillingerForTeamsOrderBySistOppdatert())
                .thenReturn(Flux.just(teamFragment(INTERVAL_1, "orphan@nav.no")));

        StepVerifier.create(dashboardService.getTeamsStatus())
                .assertNext(dto -> {
                    assertThat(dto.getTeams()).hasSize(1);
                    assertThat(dto.getTeams().getFirst().getTeam()).isEqualTo("Tilhører ikke noe team");
                    assertThat(dto.getTeams().getFirst().getUnikeBrukere()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    void shouldCountUniqueUsersPerTeam() {
        var bruker1 = Bruker.builder().epost("a@nav.no").build();
        var bruker2 = Bruker.builder().epost("b@nav.no").build();
        when(brukerRepository.findAll()).thenReturn(Flux.just(bruker1, bruker2));
        when(teamkatalogConsumer.getTeamForEpost(anyList())).thenReturn(Flux.just(
                TeamkatalogDTO.builder().email("a@nav.no").teamNavn(List.of("Team X")).build(),
                TeamkatalogDTO.builder().email("b@nav.no").teamNavn(List.of("Team X")).build()
        ));
        when(bestillingRepository.findBestillingerForTeamsOrderBySistOppdatert())
                .thenReturn(Flux.just(
                        teamFragment(INTERVAL_1, "a@nav.no"),
                        teamFragment(INTERVAL_1, "a@nav.no"),
                        teamFragment(INTERVAL_1, "b@nav.no")
                ));

        StepVerifier.create(dashboardService.getTeamsStatus())
                .assertNext(dto -> {
                    assertThat(dto.getTeams()).hasSize(1);
                    assertThat(dto.getTeams().getFirst().getTeam()).isEqualTo("Team X");
                    assertThat(dto.getTeams().getFirst().getUnikeBrukere()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    void shouldAssignUserToMultipleTeams() {
        var bruker = Bruker.builder().epost("multi@nav.no").build();
        when(brukerRepository.findAll()).thenReturn(Flux.just(bruker));
        when(teamkatalogConsumer.getTeamForEpost(anyList())).thenReturn(Flux.just(
                TeamkatalogDTO.builder().email("multi@nav.no").teamNavn(List.of("Team A", "Team B")).build()
        ));
        when(bestillingRepository.findBestillingerForTeamsOrderBySistOppdatert())
                .thenReturn(Flux.just(teamFragment(INTERVAL_1, "multi@nav.no")));

        StepVerifier.create(dashboardService.getTeamsStatus())
                .assertNext(dto -> {
                    var teamNames = dto.getTeams().stream().map(DashboardTeamsDTO.Entry::getTeam).toList();
                    assertThat(teamNames).containsExactlyInAnyOrder("Team A", "Team B");
                })
                .verifyComplete();
    }

    @Test
    void shouldGroupTeamFragmentsByIntervalIntoSeparateDtos() {
        when(brukerRepository.findAll()).thenReturn(Flux.empty());
        when(bestillingRepository.findBestillingerForTeamsOrderBySistOppdatert())
                .thenReturn(Flux.just(
                        teamFragment(INTERVAL_1, "a@nav.no"),
                        teamFragment(INTERVAL_2, "b@nav.no")
                ));

        StepVerifier.create(dashboardService.getTeamsStatus())
                .assertNext(dto -> assertThat(dto.getInterval()).isEqualTo(INTERVAL_2))
                .assertNext(dto -> assertThat(dto.getInterval()).isEqualTo(INTERVAL_1))
                .verifyComplete();
    }

    @Test
    void shouldSortTeamsStatusByIntervalDescending() {
        when(brukerRepository.findAll()).thenReturn(Flux.empty());
        when(bestillingRepository.findBestillingerForTeamsOrderBySistOppdatert())
                .thenReturn(Flux.just(
                        teamFragment(INTERVAL_1, "x@nav.no"),
                        teamFragment(INTERVAL_2, "y@nav.no")
                ));

        var results = dashboardService.getTeamsStatus().collectList().block();
        assertThat(results).isNotNull();
        assertThat(results.get(0).getInterval()).isEqualTo(INTERVAL_2);
        assertThat(results.get(1).getInterval()).isEqualTo(INTERVAL_1);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private static BestillingerFragment fragment(LocalDate dato, Long personer,
                                                  String gjenopprettstatus,
                                                  String pdlstatus,
                                                  String annenstatus) {
        return BestillingerFragment.builder()
                .dato(dato)
                .personer(personer)
                .gjenopprettstatus(gjenopprettstatus)
                .pdlstatus(pdlstatus)
                .annenstatus(annenstatus)
                .build();
    }

    private static TeamFragment teamFragment(String interval, String epost) {
        return TeamFragment.builder()
                .interval(interval)
                .epost(epost)
                .build();
    }
}
