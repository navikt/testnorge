package no.nav.registre.sigrun.provider.rs;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;

import no.nav.registre.sigrun.provider.rs.requests.SyntetiserSigrunRequest;
import no.nav.registre.sigrun.service.SigrunService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private SigrunService sigrunService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    @Test
    public void shouldStartSyntetisering() {
        var identer = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
        var testdataEier = "test";
        var syntetiserPoppRequest = new SyntetiserSigrunRequest(123L, "t1", identer.size());

        when(sigrunService.finnEksisterendeOgNyeIdenter(syntetiserPoppRequest, testdataEier)).thenReturn(identer);
        when(sigrunService.genererPoppmeldingerOgSendTilSigrunStub(identer, testdataEier, syntetiserPoppRequest.getMiljoe())).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        syntetiseringController.startSyntetisering(testdataEier, syntetiserPoppRequest);

        verify(sigrunService).genererPoppmeldingerOgSendTilSigrunStub(identer, testdataEier, syntetiserPoppRequest.getMiljoe());
    }
}
