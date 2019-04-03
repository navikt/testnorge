package no.nav.registre.skd.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.skd.consumer.TpsSyntetisererenConsumer;
import no.nav.registre.skd.consumer.TpsfConsumer;
import no.nav.registre.skd.provider.rs.requests.FastMeldingRequest;
import no.nav.registre.skd.skdmelding.RsMeldingstype;

@RunWith(MockitoJUnitRunner.class)
public class FasteMeldingerServiceTest {

    @Mock
    private TpsfConsumer tpsfConsumer;

    @Mock
    private TpsSyntetisererenConsumer tpsSyntetisererenConsumer;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private FasteMeldingerService fasteMeldingerService;

    private Long avspillergruppeId = 10L;
    private String miljoe = "t1";
    private List<Long> avspillergruppeIder;
    private List<FastMeldingRequest> fasteMeldinger;

    @Before
    public void setUp() {
        avspillergruppeIder = new ArrayList<>(Arrays.asList(123L, 234L));
        fasteMeldinger = new ArrayList<>();
    }

    @Test
    public void shouldStartAvspillingAvAvspillergruppe() {
        when(tpsfConsumer.getMeldingIdsFromAvspillergruppe(avspillergruppeId)).thenReturn(avspillergruppeIder);

        fasteMeldingerService.startAvspillingAvTpsfAvspillergruppe(avspillergruppeId, miljoe);

        verify(tpsfConsumer).getMeldingIdsFromAvspillergruppe(avspillergruppeId);
        verify(tpsfConsumer).sendSkdmeldingerToTps(eq(avspillergruppeId), any());
    }

    @Test
    public void shouldOppretteMeldingerOgLeggeIGruppe() {
        List<RsMeldingstype> meldinger = new ArrayList<>();
        when(tpsSyntetisererenConsumer.getSyntetiserteSkdmeldinger(Endringskoder.INNVANDRING.getEndringskode(), 1)).thenReturn(meldinger);

        fasteMeldingerService.opprettMeldingerOgLeggIGruppe(avspillergruppeId, fasteMeldinger);

        verify(validationService).logAndRemoveInvalidMessages(meldinger, Endringskoder.INNVANDRING);
        verify(tpsfConsumer).saveSkdEndringsmeldingerInTPSF(eq(avspillergruppeId), anyList());
    }
}