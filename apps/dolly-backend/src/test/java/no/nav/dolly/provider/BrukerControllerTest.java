package no.nav.dolly.provider;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndClaims;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamBrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.service.BrukerService;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrukerControllerTest {

    private static final String BRUKERID = "123";

    @Mock
    private BrukerService brukerService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private GetUserInfo getUserInfo;

    @Mock
    private UserInfoExtended userInfoExtended;

    @Mock
    private TeamBrukerRepository  teamBrukerRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private BrukerRepository brukerRepository;

    @Mock
    private BrukerFavoritterRepository brukerFavoritterRepository;

    @InjectMocks
    private BrukerController controller;

    @Test
    void getBrukerByBrukerId() {

        var bruker = Bruker.builder()
                .brukerId(BRUKERID)
                .build();

        when(brukerService.fetchOrCreateBruker(BRUKERID)).thenReturn(Mono.just(bruker));
        when(mapperFacade.map(eq(bruker), eq(RsBruker.class), any())).thenReturn(RsBruker.builder()
                .brukerId(BRUKERID)
                .build());
        when(getUserInfo.call()).thenReturn(Mono.just(userInfoExtended));
        when(brukerFavoritterRepository.findByBrukerId(any())).thenReturn(Flux.just(
                BrukerFavoritter.builder()
                        .build()));

        StepVerifier.create(controller.getBrukerBybrukerId(BRUKERID))
                .assertNext(resultat -> assertThat(resultat.getBrukerId(), is(BRUKERID)))
                .verifyComplete();
    }

    @Test
    void getCurrentBruker() {

        var bruker = Bruker.builder().build();
        var brukerAndClaims = RsBrukerAndClaims.builder()
                .brukerId(BRUKERID)
                .build();

        when(brukerService.fetchOrCreateBruker()).thenReturn(Mono.just(bruker));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfoExtended));
        when(mapperFacade.map(eq(bruker), eq(RsBrukerAndClaims.class), any())).thenReturn(brukerAndClaims);
        when(brukerFavoritterRepository.findByBrukerId(any())).thenReturn(Flux.just(BrukerFavoritter.builder().build()));
        when(teamRepository.findById(anyLong())).thenReturn(Mono.just(Team.builder().id(1L).build()));
        when(teamBrukerRepository.findByTeamId(anyLong())).thenReturn(Flux.just(TeamBruker.builder().build()));
        when(brukerRepository.findByIdIn(any())).thenReturn(Flux.just(Bruker.builder().brukernavn("A").build()));

        StepVerifier.create(controller.getCurrentBruker())
                .assertNext(resultat ->
                        assertThat(resultat.getBrukerId(), is(BRUKERID)))
                .verifyComplete();
    }

    @Test
    void getAllBrukere() {

        when(brukerService.fetchBrukere()).thenReturn(Flux.just(Bruker.builder().build()));
        when(brukerFavoritterRepository.findByBrukerId(any())).thenReturn(Flux.just(BrukerFavoritter.builder().build()));
        when(mapperFacade.map(any(Bruker.class), eq(RsBruker.class), any())).thenReturn(RsBruker.builder().build());

        StepVerifier.create(controller.getAllBrukere())
                .assertNext(bruker -> verify(brukerService).fetchBrukere())
                .verifyComplete();
    }

    @Test
    void fjernFavoritter() {

        var req = new RsBrukerUpdateFavoritterReq();
        req.setGruppeId(1L);

        when(brukerService.fjernFavoritt(1L)).thenReturn(Mono.just(Bruker.builder().build()));
        when(mapperFacade.map(any(Bruker.class), eq(RsBruker.class))).thenReturn(RsBruker.builder().build());

        StepVerifier.create(controller.fjernFavoritt(req))
                .assertNext(resultat -> verify(brukerService).fjernFavoritt(anyLong()))
                .verifyComplete();
    }

    @Test
    void leggTilFavoritter() {

        var req = new RsBrukerUpdateFavoritterReq();
        req.setGruppeId(1L);

        when(mapperFacade.map(any(Bruker.class), eq(RsBruker.class))).thenReturn(RsBruker.builder().build());
        when(brukerService.leggTilFavoritt(any())).thenReturn(Mono.just(Bruker.builder().build()));

        StepVerifier.create(controller.leggTilFavoritt(req))
                .assertNext(resultat -> verify(brukerService).leggTilFavoritt(anyLong()))
                .verifyComplete();
    }

    @Test
    void getUserTeams() {
        var team = Team.builder().id(1L).build();
        var rsTeam = RsTeamWithBrukere.builder().id(1L).build();

        when(brukerService.fetchTeamsForCurrentBruker()).thenReturn(Flux.just(team));
        when(mapperFacade.map(team, RsTeamWithBrukere.class)).thenReturn(rsTeam);

        StepVerifier.create(controller.getUserTeams()
                        .collectList())
                .assertNext(result -> {
                    assertThat(result.size(), is(1));
                    assertThat(result.getFirst().getId(), is(1L));
                })
                .verifyComplete();
    }

    @Test
    void setRepresentererTeam() {
        var bruker = Bruker.builder().id(1L).build();
        var rsBruker = new RsBruker();

        when(brukerService.setRepresentererTeam(1L)).thenReturn(Mono.just(bruker));
        when(mapperFacade.map(bruker, RsBruker.class)).thenReturn(rsBruker);

        StepVerifier.create(controller.setRepresentererTeam(1L))
                .assertNext(result -> assertThat(result, is(rsBruker)))
                .verifyComplete();
    }

    @Test
    void clearRepresenterendeTeam() {
        var bruker = Bruker.builder().id(1L).build();
        var rsBruker = new RsBruker();

        when(brukerService.setRepresentererTeam(null)).thenReturn(Mono.just(bruker));
        when(mapperFacade.map(bruker, RsBruker.class)).thenReturn(rsBruker);

        StepVerifier.create(controller.clearRepresentererTeam())
                .assertNext(result -> assertThat(result, is(rsBruker)))
                .verifyComplete();
    }
}