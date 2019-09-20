package no.nav.registre.tss.provider.rs;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.provider.rs.requests.SyntetiserTssRequest;
import no.nav.registre.tss.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringsControllerTest {

    @Mock
    private SyntetiseringService syntetiseringService;

    @InjectMocks
    private SyntetiseringsController syntetiseringsController;

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

        when(syntetiseringService.hentIdenter(syntetiserTssRequest)).thenReturn(personer);
        when(syntetiseringService.opprettSyntetiskeTssRutiner(personer)).thenReturn(syntetiskeMeldinger);

        syntetiseringsController.opprettLegerITss(syntetiserTssRequest);

        verify(syntetiseringService).hentIdenter(syntetiserTssRequest);
        verify(syntetiseringService).opprettSyntetiskeTssRutiner(personer);
        verify(syntetiseringService).sendTilTss(syntetiskeMeldinger, miljoe);
    }
}
