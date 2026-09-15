package no.nav.dolly.service;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.projection.BestillingBrukerFragment;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrukerBestillingerServiceTest {

    private static final String BRUKER_ID = "bruker-id";
    private static final String TEAM_BRUKER_ID = "team-bruker-id";
    private static final Long TEAM_ID = 10L;
    private static final Long TEAM_BRUKER_DATABASE_ID = 20L;

    @Mock
    private BestillingRepository bestillingRepository;

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private GetUserInfo getUserInfo;

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserInfoExtended userInfo;

    @InjectMocks
    private BrukerBestillingerService brukerBestillingerService;

    @Test
    void shouldCountBestillingerForCurrentBruker() {

        var bruker = Bruker.builder()
                .brukerId(BRUKER_ID)
                .build();
        when(getUserInfo.call()).thenReturn(Mono.just(userInfo));
        when(userInfo.id()).thenReturn(BRUKER_ID);
        when(brukerRepository.findByBrukerId(BRUKER_ID)).thenReturn(Mono.just(bruker));
        when(bestillingRepository.findByBrukerIdOrderByIdDesc(BRUKER_ID)).thenReturn(Flux.just(
                bestilling(LocalDate.of(2026, 2, 10), "NYBESTILLING", 2),
                bestilling(LocalDate.of(2026, 2, 11), "NYBESTILLING", 3),
                bestilling(LocalDate.of(2026, 2, 12), "GJENOPPRETTING", 4),
                bestilling(LocalDate.of(2026, 1, 10), "GJENOPPRETTING", 1)));

        StepVerifier.create(brukerBestillingerService.getBestillinger())
                .assertNext(result -> {
                    assertThat(result.getPeriode()).isEqualTo(YearMonth.of(2026, 2));
                    assertThat(result.getAntallNyBestillinger()).isEqualTo(2);
                    assertThat(result.getAntallGjenopprettinger()).isEqualTo(1);
                    assertThat(result.getAntallNyePersoner()).isEqualTo(5);
                })
                .assertNext(result -> {
                    assertThat(result.getPeriode()).isEqualTo(YearMonth.of(2026, 1));
                    assertThat(result.getAntallNyBestillinger()).isZero();
                    assertThat(result.getAntallGjenopprettinger()).isEqualTo(1);
                    assertThat(result.getAntallNyePersoner()).isZero();
                })
                .verifyComplete();

        verify(teamRepository, never()).findById(TEAM_ID);
    }

    @Test
    void shouldCountBestillingerForRepresentedTeam() {

        mockRepresentedTeam();
        when(bestillingRepository.findByBrukerIdOrderByIdDesc(TEAM_BRUKER_ID)).thenReturn(Flux.just(
                bestilling(LocalDate.of(2026, 2, 10), "NYBESTILLING", 4),
                bestilling(LocalDate.of(2026, 2, 11), "GJENOPPRETTING", 1)));

        StepVerifier.create(brukerBestillingerService.getBestillinger())
                .assertNext(result -> {
                    assertThat(result.getPeriode()).isEqualTo(YearMonth.of(2026, 2));
                    assertThat(result.getAntallNyBestillinger()).isEqualTo(1);
                    assertThat(result.getAntallGjenopprettinger()).isEqualTo(1);
                    assertThat(result.getAntallNyePersoner()).isEqualTo(4);
                })
                .verifyComplete();

        verify(bestillingRepository).findByBrukerIdOrderByIdDesc(TEAM_BRUKER_ID);
        verify(bestillingRepository, never()).findByBrukerIdOrderByIdDesc(BRUKER_ID);
    }

    @Test
    void shouldGetDetailedBestillingerForRepresentedTeam() {

        mockRepresentedTeam();
        when(bestillingRepository.findKriterierByBrukerIdOrderByIdDesc(TEAM_BRUKER_ID, "2026-02"))
                .thenReturn(Flux.empty());

        StepVerifier.create(brukerBestillingerService.getBestillingerDetaljert(2026, Month.FEBRUARY))
                .verifyComplete();

        verify(bestillingRepository).findKriterierByBrukerIdOrderByIdDesc(TEAM_BRUKER_ID, "2026-02");
        verify(bestillingRepository, never()).findKriterierByBrukerIdOrderByIdDesc(BRUKER_ID, "2026-02");
    }

    private void mockRepresentedTeam() {

        var bruker = Bruker.builder()
                .brukerId(BRUKER_ID)
                .representererTeam(TEAM_ID)
                .build();
        var team = Team.builder()
                .id(TEAM_ID)
                .brukerId(TEAM_BRUKER_DATABASE_ID)
                .build();
        var teamBruker = Bruker.builder()
                .id(TEAM_BRUKER_DATABASE_ID)
                .brukerId(TEAM_BRUKER_ID)
                .brukertype(Bruker.Brukertype.TEAM)
                .build();

        when(getUserInfo.call()).thenReturn(Mono.just(userInfo));
        when(userInfo.id()).thenReturn(BRUKER_ID);
        when(brukerRepository.findByBrukerId(BRUKER_ID)).thenReturn(Mono.just(bruker));
        when(teamRepository.findById(TEAM_ID)).thenReturn(Mono.just(team));
        when(brukerRepository.findById(TEAM_BRUKER_DATABASE_ID)).thenReturn(Mono.just(teamBruker));
    }

    private static BestillingBrukerFragment bestilling(LocalDate dato, String bestillingtype, int antall) {

        return BestillingBrukerFragment.builder()
                .dato(dato)
                .bestillingtype(bestillingtype)
                .antall(antall)
                .build();
    }
}
