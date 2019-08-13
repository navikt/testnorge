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
import java.util.List;

import no.nav.registre.sigrun.provider.rs.requests.SyntetiserPoppRequest;
import no.nav.registre.sigrun.service.SigrunService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringControllerTest {

    @Mock
    private SigrunService sigrunService;

    @InjectMocks
    private SyntetiseringController syntetiseringController;

    @Test
    public void shouldStartSyntetisering() {
        List<String> fnrs = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
        SyntetiserPoppRequest syntetiserPoppRequest = new SyntetiserPoppRequest(123L, "t1", fnrs.size());

        String testdataEier = "test";
        when(sigrunService.finnEksisterendeOgNyeIdenter(syntetiserPoppRequest, testdataEier)).thenReturn(fnrs);
        when(sigrunService.genererPoppmeldingerOgSendTilSigrunStub(fnrs, testdataEier, syntetiserPoppRequest.getMiljoe())).thenReturn(ResponseEntity.status(HttpStatus.OK).build());

        syntetiseringController.generatePopp(testdataEier, syntetiserPoppRequest);

        verify(sigrunService).genererPoppmeldingerOgSendTilSigrunStub(fnrs, testdataEier, syntetiserPoppRequest.getMiljoe());
    }
}
