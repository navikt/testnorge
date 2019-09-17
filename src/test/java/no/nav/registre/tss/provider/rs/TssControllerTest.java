package no.nav.registre.tss.provider.rs;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.provider.rs.requests.SyntetiserTssRequest;
import no.nav.registre.tss.service.TssService;

@RunWith(MockitoJUnitRunner.class)
public class TssControllerTest {

    @Mock
    private TssService tssService;

    @InjectMocks
    private TssController tssController;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private String fnr1 = "29016644901";
    private String fnr2 = "12046846819";

    @Test
    public void shouldOppretteLegerITss() {
        SyntetiserTssRequest syntetiserTssRequest = SyntetiserTssRequest.builder()
                .avspillergruppeId(avspillergruppeId)
                .miljoe(miljoe)
                .antallNyeIdenter(antallNyeIdenter)
                .build();

        Person person1 = new Person(fnr1, "Person1");
        Person person2 = new Person(fnr2, "Person2");
        List<Person> personer = new ArrayList<>(Arrays.asList(person1, person2));

        String syntetiskMelding1 = "SomeMelding";
        String syntetiskMelding2 = "SomeOtherMelding";
        List<String> syntetiskeMeldinger = new ArrayList<>(Arrays.asList(syntetiskMelding1, syntetiskMelding2));

        when(tssService.hentIdenter(syntetiserTssRequest)).thenReturn(personer);
        when(tssService.opprettSyntetiskeTssRutiner(personer)).thenReturn(syntetiskeMeldinger);

        tssController.opprettLegerITss(syntetiserTssRequest);

        verify(tssService).hentIdenter(syntetiserTssRequest);
        verify(tssService).opprettSyntetiskeTssRutiner(personer);
        verify(tssService).sendTilTss(syntetiskeMeldinger);
    }

    @Test
    public void shouldHenteLegerFraTss() throws JMSException {
        tssController.hentLegerFraTss(avspillergruppeId, antallNyeIdenter);

        verify(tssService).sendOgMotta910RutineFraTss(avspillergruppeId, antallNyeIdenter);
    }

    @Test
    public void shouldHenteLegeFraTss() throws JMSException {
        tssController.hentLegeFraTss(fnr1);
        verify(tssService).sendOgMotta910RutineFraTss(fnr1);
    }
}
