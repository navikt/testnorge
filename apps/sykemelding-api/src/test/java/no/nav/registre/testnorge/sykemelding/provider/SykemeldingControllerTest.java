package no.nav.registre.testnorge.sykemelding.provider;

import no.nav.registre.testnorge.dto.sykemelding.v1.ArbeidsgiverDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.DetaljerDTO;
import no.nav.registre.testnorge.dto.sykemelding.v1.DiagnoseDTO;
import no.nav.registre.testnorge.sykemelding.service.SykemeldingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;

import static no.nav.registre.testnorge.dto.sykemelding.v1.SykemeldingDTO.SykemeldingDTOBuilder;

@RunWith(MockitoJUnitRunner.class)
public class SykemeldingControllerTest {

    @Mock
    private SykemeldingService sykemeldingService;

    @Test
    public void shouldCreateSykemelding() {
        var sykemeldingRequest = new SykemeldingDTOBuilder()
                .arbeidsgiver(new ArbeidsgiverDTO("Arbeids Giversen", "arbeider", (double) 100))
                .hovedDiagnose(new DiagnoseDTO("diag", "system", "100"))
                .biDiagnoser(new ArrayList<DiagnoseDTO>(Collections.singleton(new DiagnoseDTO("diag", "system", "100"))))
                .detaljer(new DetaljerDTO("tilt", "tilt", true, "hens"))
                .build();

    }

}