package no.nav.dolly.provider.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.domain.resultset.entity.team.RsTeamWithBrukere;
import no.nav.dolly.provider.BrukerController;
import no.nav.dolly.service.BrukerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BrukerControllerTest {

    @Mock
    private BrukerService brukerService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private BrukerController controller;

    @BeforeEach
    public void setup() {
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @Test
    void getBrukerByBrukerId() {
        var bruker = RsBrukerAndGruppeId.builder().brukerId("brukerId").build();
        var b = Bruker.builder().build();

        when(brukerService.fetchBrukerOrTeamBruker("brukerId")).thenReturn(b);
        when(mapperFacade.map(b, RsBrukerAndGruppeId.class)).thenReturn(bruker);

        var res = controller.getBrukerBybrukerId("brukerId");

        assertThat(res.getBrukerId(), is("brukerId"));
    }

    @Test
    void getCurrentBruker() {

        var bruker = Bruker.builder().build();
        var rsBruker = new RsBruker();

        when(brukerService.fetchCurrentBrukerWithoutTeam()).thenReturn(bruker);
        when(mapperFacade.map(bruker, RsBruker.class)).thenReturn(rsBruker);

        assertThat(controller.getCurrentBruker(), is(rsBruker));
    }

    @Test
    void getAllBrukere() {
        controller.getAllBrukere();
        verify(brukerService).fetchBrukere();
    }

    @Test
    void fjernFavoritter() {
        var req = new RsBrukerUpdateFavoritterReq();
        req.setGruppeId(1L);
        controller.fjernFavoritt(req);
        verify(brukerService).fjernFavoritt(anyLong());
    }

    @Test
    void leggTilFavoritter() {
        var req = new RsBrukerUpdateFavoritterReq();
        req.setGruppeId(1L);
        controller.leggTilFavoritt(req);
        verify(brukerService).leggTilFavoritt(anyLong());
    }

    @Test
    void getUserTeams() {
        var team = Team.builder().id(1L).build();
        var rsTeam = RsTeamWithBrukere.builder().id(1L).build();

        when(brukerService.fetchTeamsForCurrentBruker()).thenReturn(List.of(team));
        when(mapperFacade.mapAsList(List.of(team), RsTeamWithBrukere.class)).thenReturn(List.of(rsTeam));

        var result = controller.getUserTeams();

        assertThat(result.size(), is(1));
        assertThat(result.getFirst().getId(), is(1L));
    }

    @Test
    void setRepresentererTeam() {
        var bruker = Bruker.builder().id(1L).build();
        var rsBruker = new RsBruker();

        when(brukerService.setRepresentererTeam(1L)).thenReturn(bruker);
        when(mapperFacade.map(bruker, RsBruker.class)).thenReturn(rsBruker);

        var result = controller.setRepresentererTeam(1L);

        assertThat(result, is(rsBruker));
    }

    @Test
    void clearRepresenterendeTeam() {
        var bruker = Bruker.builder().id(1L).build();
        var rsBruker = new RsBruker();

        when(brukerService.setRepresentererTeam(null)).thenReturn(bruker);
        when(mapperFacade.map(bruker, RsBruker.class)).thenReturn(rsBruker);

        var result = controller.clearRepresentererTeam();

        assertThat(result, is(rsBruker));
    }

}