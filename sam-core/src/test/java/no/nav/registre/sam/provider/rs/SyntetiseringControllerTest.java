package no.nav.registre.sam.provider.rs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.sam.provider.rs.requests.SyntetiserSamRequest;
import no.nav.registre.sam.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private SyntetiseringService syntetiseringService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    private SyntetiserSamRequest syntetiserSamRequest;
    private List<String> identer;

    @Before
    public void setUp() {
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var antallMeldinger = 2;
        syntetiserSamRequest = new SyntetiserSamRequest(avspillergruppeId, miljoe, antallMeldinger);
        var fnr1 = "01010101010";
        var fnr2 = "02020202020";
        identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));
    }

    @Test
    public void shouldStartSyntetisering() {
        when(syntetiseringService.finnLevendeIdenter(syntetiserSamRequest)).thenReturn(identer);
        when(syntetiseringService.opprettOgLagreSyntetiserteSamordningsmeldinger(identer)).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        var response = syntetiseringController.genererSamordningsmeldinger(syntetiserSamRequest);

        verify(syntetiseringService).finnLevendeIdenter(syntetiserSamRequest);
        verify(syntetiseringService).opprettOgLagreSyntetiserteSamordningsmeldinger(identer);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}