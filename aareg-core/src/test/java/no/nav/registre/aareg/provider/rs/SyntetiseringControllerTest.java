package no.nav.registre.aareg.provider.rs;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.aareg.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private SyntetiseringService syntetiseringService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    private SyntetiserAaregRequest syntetiserAaregRequest;
    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallMeldinger = 2;
    private String melding1 = "En melding";
    private String melding2 = "En annen melding";

    @Before
    public void setUp() {
        syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, antallMeldinger);
    }

    @Test
    public void shouldStartSyntetisering() {
        List<String> meldinger = new ArrayList<>();
        meldinger.add(melding1);
        meldinger.add(melding2);

        when(syntetiseringService.hentArbeidshistorikk(syntetiserAaregRequest)).thenReturn(meldinger);

        List<String> result = syntetiseringController.genererArbeidsforholdsmeldinger(syntetiserAaregRequest);

        verify(syntetiseringService).hentArbeidshistorikk(syntetiserAaregRequest);

        MatcherAssert.assertThat(result, hasItem(melding1));
        MatcherAssert.assertThat(result, hasItem(melding2));
    }
}
