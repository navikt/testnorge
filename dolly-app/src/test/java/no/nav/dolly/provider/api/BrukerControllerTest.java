package no.nav.dolly.provider.api;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsBrukerTeamAndGruppeIDs;
import no.nav.dolly.domain.resultset.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.service.BrukerService;
import no.nav.freg.security.oidc.auth.common.OidcTokenAuthentication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContextHolder;

@RunWith(MockitoJUnitRunner.class)
public class BrukerControllerTest {

    @Mock
    private BrukerService brukerService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private BrukerController controller;

    @Test
    public void getBrukerByNavIdent() {
        RsBrukerTeamAndGruppeIDs bruker = new RsBrukerTeamAndGruppeIDs();
        bruker.setNavIdent("navident");
        Bruker b = new Bruker();

        when(brukerService.fetchBruker("navident")).thenReturn(b);
        when(mapperFacade.map(b, RsBrukerTeamAndGruppeIDs.class)).thenReturn(bruker);

        RsBrukerTeamAndGruppeIDs res = controller.getBrukerByNavIdent("navident");

        assertThat(res.getNavIdent(), is("navident"));
    }

    @Test
    public void getCurrentBruker() {
        String standardPrincipal = "test";
        OidcTokenAuthentication token = new OidcTokenAuthentication(standardPrincipal, null, null, null);
        SecurityContextHolder.getContext().setAuthentication(token);

        Bruker b = new Bruker();
        RsBruker rsBruker = new RsBruker();

        when(brukerService.fetchOrCreateBruker(standardPrincipal)).thenReturn(b);
        when(mapperFacade.map(b, RsBruker.class)).thenReturn(rsBruker);

        assertThat(controller.getCurrentBruker(), is(rsBruker));

    }

    @Test
    public void getAllBrukere() {
        controller.getAllBrukere();
        verify(brukerService).fetchBrukere();
    }

    @Test
    public void fjernFavoritter() {
        RsBrukerUpdateFavoritterReq req = new RsBrukerUpdateFavoritterReq();
        req.setGruppeId(1l);
        controller.fjernFavoritt(req);
        verify(brukerService).fjernFavoritt(anyLong());
    }

    @Test
    public void leggTilFavoritter() {
        RsBrukerUpdateFavoritterReq req = new RsBrukerUpdateFavoritterReq();
        req.setGruppeId(1l);
        controller.leggTilFavoritt(req);
        verify(brukerService).leggTilFavoritt(anyLong());
    }
}