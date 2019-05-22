package no.nav.registre.skd.provider.rs;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.skd.provider.rs.requests.FastMeldingRequest;
import no.nav.registre.skd.provider.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.skd.service.FasteMeldingerService;
import no.nav.registre.skd.service.HodejegerDatabaseService;
import no.nav.registre.skd.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private SyntetiseringService syntetiseringService;

    @Mock
    private HodejegerDatabaseService hodejegerDatabaseService;

    @Mock
    private FasteMeldingerService fasteMeldingerService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private Map<String, Integer> antallMeldingerPerEndringskode;
    private GenereringsOrdreRequest genereringsOrdreRequest;
    private List<FastMeldingRequest> fasteMeldinger;

    @Before
    public void setUp() {
        antallMeldingerPerEndringskode = new HashMap<>();
        genereringsOrdreRequest = new GenereringsOrdreRequest(avspillergruppeId, miljoe, antallMeldingerPerEndringskode);
        fasteMeldinger = new ArrayList<>();
    }

    @Test
    public void shouldGenerereSkdMeldingerOgSendeIdenterTilHodejegeren() {
        syntetiseringController.genererSkdMeldinger(genereringsOrdreRequest);
        verify(hodejegerDatabaseService).sendIdenterMedSkdMeldingerTilHodejegeren(anyList(), eq(miljoe));
        verify(syntetiseringService).puttIdenterIMeldingerOgLagre(genereringsOrdreRequest);
    }

    @Test
    public void shouldStartAvspilling() {
        syntetiseringController.startAvspillingAvTpsfAvspillergruppe(avspillergruppeId, miljoe);

        verify(fasteMeldingerService).startAvspillingAvTpsfAvspillergruppe(avspillergruppeId, miljoe);
    }

    @Test
    public void shouldLeggeTilNyeMeldingerIGruppe() {
        syntetiseringController.leggTilNyeMeldingerIGruppe(avspillergruppeId, fasteMeldinger);

        verify(fasteMeldingerService).opprettMeldingerOgLeggIGruppe(avspillergruppeId, fasteMeldinger);
    }
}