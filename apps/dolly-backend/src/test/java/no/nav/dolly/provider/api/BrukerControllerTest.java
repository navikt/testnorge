package no.nav.dolly.provider.api;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerUpdateFavoritterReq;
import no.nav.dolly.service.BrukerService;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BrukerControllerTest {

    private final static String BRUKERID = "123";
    private final static String BRUKERNAVN = "BRUKER";
    private final static String EPOST = "@@@@";

    @Mock
    private BrukerService brukerService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private BrukerController controller;

    @Test
    public void getBrukerByBrukerId() {
        RsBrukerAndGruppeId bruker = RsBrukerAndGruppeId.builder().brukerId("brukerId").build();
        Bruker b = Bruker.builder().build();

        when(brukerService.fetchBruker("brukerId")).thenReturn(b);
        when(mapperFacade.map(b, RsBrukerAndGruppeId.class)).thenReturn(bruker);

        RsBrukerAndGruppeId res = controller.getBrukerBybrukerId("brukerId");

        assertThat(res.getBrukerId(), is("brukerId"));
    }

    @Test
    public void getCurrentBruker() {

        Bruker bruker = Bruker.builder().build();
        RsBruker rsBruker = new RsBruker();

        when(brukerService.fetchOrCreateBruker(BRUKERID)).thenReturn(bruker);
        when(mapperFacade.map(bruker, RsBruker.class)).thenReturn(rsBruker);

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

    @BeforeEach
    public void setup() {
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(Jwt.withTokenValue("test")
                .claim("oid", BRUKERID)
                .claim("name", BRUKERNAVN)
                .claim("epost", EPOST)
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON)
                .build()));
    }
}