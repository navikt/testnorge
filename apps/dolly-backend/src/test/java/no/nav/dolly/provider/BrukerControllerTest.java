package no.nav.dolly.provider;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BrukerService;
import no.nav.dolly.service.TeamService;
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
import static org.mockito.ArgumentMatchers.anyList;
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
    private TestgruppeRepository testgruppeRepository;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private GetUserInfo getUserInfo;

    @Mock
    private UserInfoExtended userInfoExtended;

    @Mock
    private TeamService teamService;

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

        when(brukerService.fetchBrukerWithoutTeam(BRUKERID)).thenReturn(Mono.just(bruker));
        when(mapperFacade.map(eq(bruker), eq(RsBruker.class), any())).thenReturn(RsBruker.builder()
                .brukerId(BRUKERID)
                .build());
        when(brukerFavoritterRepository.findByBrukerId(any())).thenReturn(Flux.just(
                BrukerFavoritter.builder().brukerId(1L).gruppeId(1L).build()));
        when(testgruppeRepository.findByIdIn(anyList())).thenReturn(Flux.just(Testgruppe.builder().id(1L).build()));
        when(brukerRepository.findAll()).thenReturn(Flux.just(Bruker.builder().id(1L).build()));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfoExtended));

        StepVerifier.create(controller.getBrukerBybrukerId(BRUKERID))
                .assertNext(resultat -> assertThat(resultat.getBrukerId(), is(BRUKERID)))
                .verifyComplete();
    }

    @Test
    void getCurrentBruker() {

        var bruker = Bruker.builder()
                .brukerId(BRUKERID)
                .id(1L)
                .brukernavn("brukernavn")
                .brukertype(Bruker.Brukertype.AZURE)
                .build();
        var brukerAndClaims = RsBruker.builder()
                .brukerId(BRUKERID)
                .build();

        when(brukerService.fetchBrukerWithoutTeam()).thenReturn(Mono.just(bruker));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfoExtended));
        when(mapperFacade.map(eq(bruker), eq(RsBruker.class), any())).thenReturn(brukerAndClaims);
        when(brukerFavoritterRepository.findByBrukerId(any())).thenReturn(Flux.just(
                BrukerFavoritter.builder().brukerId(1L).gruppeId(1L).build()));
        when(testgruppeRepository.findByIdIn(anyList())).thenReturn(Flux.just(Testgruppe.builder().id(1L).build()));
        when(brukerRepository.findAll()).thenReturn(Flux.just(Bruker.builder().id(1L).build()));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfoExtended));

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

        when(brukerService.fjernFavoritt(1L)).thenReturn(Mono.just(Bruker.builder().id(1L).build()));
        when(mapperFacade.map(any(Bruker.class), eq(RsBruker.class), any(MappingContext.class)))
                .thenReturn(RsBruker.builder().build());
        when(brukerFavoritterRepository.findByBrukerId(any())).thenReturn(Flux.just(
                BrukerFavoritter.builder().brukerId(1L).gruppeId(1L).build()));
        when(testgruppeRepository.findByIdIn(anyList())).thenReturn(Flux.just(Testgruppe.builder().id(1L).build()));
        when(brukerRepository.findAll()).thenReturn(Flux.just(Bruker.builder().id(1L).build()));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfoExtended));

        StepVerifier.create(controller.fjernFavoritt(req))
                .assertNext(resultat -> verify(brukerService).fjernFavoritt(anyLong()))
                .verifyComplete();
    }

    @Test
    void leggTilFavoritter() {

        var req = new RsBrukerUpdateFavoritterReq();
        req.setGruppeId(1L);

        when(mapperFacade.map(any(Bruker.class), eq(RsBruker.class), any(MappingContext.class)))
                .thenReturn(RsBruker.builder().build());
        when(brukerService.leggTilFavoritt(any())).thenReturn(Mono.just(Bruker.builder().id(1L).build()));
        when(brukerFavoritterRepository.findByBrukerId(any())).thenReturn(Flux.just(
                BrukerFavoritter.builder().brukerId(1L).gruppeId(1L).build()));
        when(testgruppeRepository.findByIdIn(anyList())).thenReturn(Flux.just(Testgruppe.builder().id(1L).build()));
        when(brukerRepository.findAll()).thenReturn(Flux.just(Bruker.builder().id(1L).build()));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfoExtended));

        StepVerifier.create(controller.leggTilFavoritt(req))
                .assertNext(resultat -> verify(brukerService).leggTilFavoritt(anyLong()))
                .verifyComplete();
    }

    @Test
    void getUserTeams() {

        var team = Team.builder().id(1L).brukerId(1L).build();
        var rsTeam = RsTeamWithBrukere.builder().id(1L).build();

        when(mapperFacade.map(eq(team), eq(RsTeamWithBrukere.class), any(MappingContext.class))).thenReturn(rsTeam);
        when(teamService.fetchTeam(any())).thenReturn(Flux.just(team));
        when(brukerRepository.findById(anyLong())).thenReturn(Mono.just(Bruker.builder().id(1L).build()));

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
        when(mapperFacade.map(eq(bruker), eq(RsBruker.class), any(MappingContext.class))).thenReturn(rsBruker);
        when(brukerFavoritterRepository.findByBrukerId(any())).thenReturn(Flux.just(
                BrukerFavoritter.builder().brukerId(1L).gruppeId(1L).build()));
        when(testgruppeRepository.findByIdIn(anyList())).thenReturn(Flux.just(Testgruppe.builder().id(1L).build()));
        when(brukerRepository.findAll()).thenReturn(Flux.just(Bruker.builder().id(1L).build()));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfoExtended));

        StepVerifier.create(controller.setRepresentererTeam(1L))
                .assertNext(result -> assertThat(result, is(rsBruker)))
                .verifyComplete();
    }

    @Test
    void clearRepresenterendeTeam() {
        var bruker = Bruker.builder().id(1L).build();
        var rsBruker = new RsBruker();

        when(brukerService.setRepresentererTeam(null)).thenReturn(Mono.just(bruker));
        when(mapperFacade.map(eq(bruker), eq(RsBruker.class), any(MappingContext.class))).thenReturn(rsBruker);
        when(brukerFavoritterRepository.findByBrukerId(any())).thenReturn(Flux.just(
                BrukerFavoritter.builder().brukerId(1L).gruppeId(1L).build()));
        when(testgruppeRepository.findByIdIn(anyList())).thenReturn(Flux.just(Testgruppe.builder().id(1L).build()));
        when(brukerRepository.findAll()).thenReturn(Flux.just(Bruker.builder().id(1L).build()));
        when(getUserInfo.call()).thenReturn(Mono.just(userInfoExtended));

        StepVerifier.create(controller.clearRepresentererTeam())
                .assertNext(result -> assertThat(result, is(rsBruker)))
                .verifyComplete();
    }
}