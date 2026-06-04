package no.nav.dolly.service;

import no.nav.dolly.consumer.altinn3.Altinn3TilgangServiceConsumer;
import no.nav.dolly.consumer.altinn3.dto.Altinn3TilgangDTO;
import no.nav.dolly.consumer.brukerservice.BrukerServiceConsumer;
import no.nav.dolly.consumer.brukerservice.dto.BrukerDTO;
import no.nav.dolly.consumer.teamkatalog.TeamkatalogConsumer;
import no.nav.dolly.consumer.teamkatalog.dto.TeamkatalogDTO;
import no.nav.dolly.domain.dto.DashboardDollyTeamsDTO;
import no.nav.dolly.domain.dto.DashboardOrganisasjonerDTO;
import no.nav.dolly.domain.dto.DashboardTeamsDTO;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.projection.BestillingerFragment;
import no.nav.dolly.domain.projection.DollyTeamFragment;
import no.nav.dolly.domain.projection.OrganisasjonFragment;
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
    private Altinn3TilgangServiceConsumer altinn3TilgangServiceConsumer;

    @Mock
    private BrukerServiceConsumer brukerServiceConsumer;

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

    // ── getOrganisasjonerStatus ──────────────────────────────────────────────

    @Test
    void shouldReturnEmptyOrganisasjonerWhenNoFragments() {
        when(altinn3TilgangServiceConsumer.getOrganisasjoner()).thenReturn(Flux.empty());
        when(brukerServiceConsumer.getAlleBrukere()).thenReturn(Flux.empty());
        when(bestillingRepository.findBestillingerForOrganisasjonerOrderBySistOppdatert()).thenReturn(Flux.empty());

        StepVerifier.create(dashboardService.getOrganisasjonerStatus())
                .verifyComplete();
    }

    @Test
    void shouldResolveOrganisasjonNavnFromAltinn3Oppslag() {
        when(altinn3TilgangServiceConsumer.getOrganisasjoner()).thenReturn(Flux.just(
                Altinn3TilgangDTO.builder()
                        .organisasjonsnummer("123456789")
                        .navn("Firma AS")
                        .organisasjonsform("AS")
                        .build()
        ));
        when(brukerServiceConsumer.getAlleBrukere()).thenReturn(Flux.just(
                BrukerDTO.builder().id("bruker1").organisasjonsnummer("123456789").build()
        ));
        when(bestillingRepository.findBestillingerForOrganisasjonerOrderBySistOppdatert())
                .thenReturn(Flux.just(organisasjonFragment(INTERVAL_1, "bruker1")));

        StepVerifier.create(dashboardService.getOrganisasjonerStatus())
                .assertNext(dto -> {
                    assertThat(dto.getOrganisasjoner()).hasSize(1);
                    var entry = dto.getOrganisasjoner().getFirst();
                    assertThat(entry.getOrganisasjonsnummer()).isEqualTo("123456789");
                    assertThat(entry.getNavn()).isEqualTo("Firma AS");
                    assertThat(entry.getOrganisasjonsform()).isEqualTo("AS");
                    assertThat(entry.getUnikeBrukere()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    void shouldFallbackToUkjentWhenOrganisasjonNotInAltinn3() {
        when(altinn3TilgangServiceConsumer.getOrganisasjoner()).thenReturn(Flux.empty());
        when(brukerServiceConsumer.getAlleBrukere()).thenReturn(Flux.just(
                BrukerDTO.builder().id("bruker1").organisasjonsnummer("999999999").build()
        ));
        when(bestillingRepository.findBestillingerForOrganisasjonerOrderBySistOppdatert())
                .thenReturn(Flux.just(organisasjonFragment(INTERVAL_1, "bruker1")));

        StepVerifier.create(dashboardService.getOrganisasjonerStatus())
                .assertNext(dto -> {
                    var entry = dto.getOrganisasjoner().getFirst();
                    assertThat(entry.getNavn()).isEqualTo("Ukjent organisasjon");
                    assertThat(entry.getOrganisasjonsform()).isEqualTo("Ukjent organisasjonsform");
                })
                .verifyComplete();
    }

    @Test
    void shouldCountUniqueUsersPerOrganisasjon() {
        when(altinn3TilgangServiceConsumer.getOrganisasjoner()).thenReturn(Flux.empty());
        when(brukerServiceConsumer.getAlleBrukere()).thenReturn(Flux.just(
                BrukerDTO.builder().id("bruker1").organisasjonsnummer("111111111").build(),
                BrukerDTO.builder().id("bruker2").organisasjonsnummer("111111111").build()
        ));
        when(bestillingRepository.findBestillingerForOrganisasjonerOrderBySistOppdatert())
                .thenReturn(Flux.just(
                        organisasjonFragment(INTERVAL_1, "bruker1"),
                        organisasjonFragment(INTERVAL_1, "bruker1"),
                        organisasjonFragment(INTERVAL_1, "bruker2")
                ));

        StepVerifier.create(dashboardService.getOrganisasjonerStatus())
                .assertNext(dto -> {
                    assertThat(dto.getTotaltUnikeBrukere()).isEqualTo(2);
                    assertThat(dto.getOrganisasjoner().getFirst().getUnikeBrukere()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    void shouldGroupOrganisasjonFragmentsByIntervalIntoSeparateDtos() {
        when(altinn3TilgangServiceConsumer.getOrganisasjoner()).thenReturn(Flux.empty());
        when(brukerServiceConsumer.getAlleBrukere()).thenReturn(Flux.just(
                BrukerDTO.builder().id("bruker1").organisasjonsnummer("111111111").build(),
                BrukerDTO.builder().id("bruker2").organisasjonsnummer("222222222").build()
        ));
        when(bestillingRepository.findBestillingerForOrganisasjonerOrderBySistOppdatert())
                .thenReturn(Flux.just(
                        organisasjonFragment(INTERVAL_1, "bruker1"),
                        organisasjonFragment(INTERVAL_2, "bruker2")
                ));

        StepVerifier.create(dashboardService.getOrganisasjonerStatus())
                .assertNext(dto -> assertThat(dto.getInterval()).isEqualTo(INTERVAL_2))
                .assertNext(dto -> assertThat(dto.getInterval()).isEqualTo(INTERVAL_1))
                .verifyComplete();
    }

    @Test
    void shouldSortOrganisasjonerStatusByIntervalDescending() {
        when(altinn3TilgangServiceConsumer.getOrganisasjoner()).thenReturn(Flux.empty());
        when(brukerServiceConsumer.getAlleBrukere()).thenReturn(Flux.just(
                BrukerDTO.builder().id("bruker1").organisasjonsnummer("111111111").build(),
                BrukerDTO.builder().id("bruker2").organisasjonsnummer("222222222").build()
        ));
        when(bestillingRepository.findBestillingerForOrganisasjonerOrderBySistOppdatert())
                .thenReturn(Flux.just(
                        organisasjonFragment(INTERVAL_1, "bruker1"),
                        organisasjonFragment(INTERVAL_2, "bruker2")
                ));

        var results = dashboardService.getOrganisasjonerStatus().collectList().block();
        assertThat(results).isNotNull();
        assertThat(results.get(0).getInterval()).isEqualTo(INTERVAL_2);
        assertThat(results.get(1).getInterval()).isEqualTo(INTERVAL_1);
    }

    @Test
    void shouldSortOrganisasjonerByNavnAlphabetically() {
        when(altinn3TilgangServiceConsumer.getOrganisasjoner()).thenReturn(Flux.just(
                Altinn3TilgangDTO.builder().organisasjonsnummer("111").navn("Zebra AS").organisasjonsform("AS").build(),
                Altinn3TilgangDTO.builder().organisasjonsnummer("222").navn("Alpha AS").organisasjonsform("AS").build()
        ));
        when(brukerServiceConsumer.getAlleBrukere()).thenReturn(Flux.just(
                BrukerDTO.builder().id("bruker1").organisasjonsnummer("111").build(),
                BrukerDTO.builder().id("bruker2").organisasjonsnummer("222").build()
        ));
        when(bestillingRepository.findBestillingerForOrganisasjonerOrderBySistOppdatert())
                .thenReturn(Flux.just(
                        organisasjonFragment(INTERVAL_1, "bruker1"),
                        organisasjonFragment(INTERVAL_1, "bruker2")
                ));

        StepVerifier.create(dashboardService.getOrganisasjonerStatus())
                .assertNext(dto -> {
                    var names = dto.getOrganisasjoner().stream()
                            .map(DashboardOrganisasjonerDTO.Entry::getNavn)
                            .toList();
                    assertThat(names).containsExactly("Alpha AS", "Zebra AS");
                })
                .verifyComplete();
    }

    // ── getDollyTeamsStatus ──────────────────────────────────────────────────

    @Test
    void shouldReturnEmptyDollyTeamsWhenNoFragments() {
        when(bestillingRepository.findBestillingerForDollyTeamsOrderBySistOppdatert()).thenReturn(Flux.empty());

        StepVerifier.create(dashboardService.getDollyTeamsStatus())
                .verifyComplete();
    }

    @Test
    void shouldParseDollyTeamInformasjonIntoNavnAndBeskrivelse() {
        when(bestillingRepository.findBestillingerForDollyTeamsOrderBySistOppdatert())
                .thenReturn(Flux.just(dollyTeamFragment(INTERVAL_1, "Team A", "En beskrivelse", 3L)));

        StepVerifier.create(dashboardService.getDollyTeamsStatus())
                .assertNext(dto -> {
                    assertThat(dto.getTeams()).hasSize(1);
                    var entry = dto.getTeams().getFirst();
                    assertThat(entry.getNavn()).isEqualTo("Team A");
                    assertThat(entry.getBeskrivelse()).isEqualTo("En beskrivelse");
                    assertThat(entry.getUnikeBrukere()).isEqualTo(3);
                })
                .verifyComplete();
    }

    @Test
    void shouldSumTotaltUnikeBrukereAcrossTeams() {
        when(bestillingRepository.findBestillingerForDollyTeamsOrderBySistOppdatert())
                .thenReturn(Flux.just(
                        dollyTeamFragment(INTERVAL_1, "Team A", "Beskrivelse A", 4L),
                        dollyTeamFragment(INTERVAL_1, "Team B", "Beskrivelse B", 6L)
                ));

        StepVerifier.create(dashboardService.getDollyTeamsStatus())
                .assertNext(dto -> {
                    assertThat(dto.getTotaltAntallTeams()).isEqualTo(2);
                    assertThat(dto.getTotaltUnikeBrukere()).isEqualTo(10);
                })
                .verifyComplete();
    }

    @Test
    void shouldSortDollyTeamsByNavnAlphabetically() {
        when(bestillingRepository.findBestillingerForDollyTeamsOrderBySistOppdatert())
                .thenReturn(Flux.just(
                        dollyTeamFragment(INTERVAL_1, "Zebra", "Z", 1L),
                        dollyTeamFragment(INTERVAL_1, "Alpha", "A", 1L)
                ));

        StepVerifier.create(dashboardService.getDollyTeamsStatus())
                .assertNext(dto -> {
                    var names = dto.getTeams().stream().map(DashboardDollyTeamsDTO.Entry::getNavn).toList();
                    assertThat(names).containsExactly("Alpha", "Zebra");
                })
                .verifyComplete();
    }

    @Test
    void shouldGroupDollyTeamFragmentsByIntervalIntoSeparateDtos() {
        when(bestillingRepository.findBestillingerForDollyTeamsOrderBySistOppdatert())
                .thenReturn(Flux.just(
                        dollyTeamFragment(INTERVAL_1, "Team A", "A", 1L),
                        dollyTeamFragment(INTERVAL_2, "Team B", "B", 1L)
                ));

        StepVerifier.create(dashboardService.getDollyTeamsStatus())
                .assertNext(dto -> assertThat(dto.getInterval()).isEqualTo(INTERVAL_2))
                .assertNext(dto -> assertThat(dto.getInterval()).isEqualTo(INTERVAL_1))
                .verifyComplete();
    }

    @Test
    void shouldSortDollyTeamsStatusByIntervalDescending() {
        when(bestillingRepository.findBestillingerForDollyTeamsOrderBySistOppdatert())
                .thenReturn(Flux.just(
                        dollyTeamFragment(INTERVAL_1, "Team A", "A", 1L),
                        dollyTeamFragment(INTERVAL_2, "Team B", "B", 1L)
                ));

        var results = dashboardService.getDollyTeamsStatus().collectList().block();
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

    private static OrganisasjonFragment organisasjonFragment(String interval, String brukerid) {
        return OrganisasjonFragment.builder()
                .interval(interval)
                .brukerid(brukerid)
                .build();
    }

    private static DollyTeamFragment dollyTeamFragment(String interval, String navn, String beskrivelse, long antall) {
        return DollyTeamFragment.builder()
                .interval(interval)
                .informasjon(navn + "|" + beskrivelse)
                .antall(antall)
                .build();
    }
}
