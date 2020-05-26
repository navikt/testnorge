package no.nav.registre.medl.provider.rs;

import static no.nav.registre.medl.testutils.ResourceUtils.getResourceFileContent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.List;

import no.nav.registre.medl.consumer.rs.response.MedlSyntResponse;
import no.nav.registre.medl.database.model.TMedlemPeriode;
import no.nav.registre.medl.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class OrkestreringControllerTest {

    private final String fnr = "123";
    private final String miljoe = "t1";
    private final MedlSyntResponse medlSynt = new MedlSyntResponse();

    @Mock
    private SyntetiseringService syntetiseringService;

    @InjectMocks
    private OrkestreringController orkestreringController;

    @Test
    public void opprettMedlemskapOK() {
        mockValidResponse();

        var tMedlemPeriodeResponseEntity = orkestreringController.opprettMedlemskap(fnr, miljoe, medlSynt);

        assertSame(HttpStatus.OK, tMedlemPeriodeResponseEntity.getStatusCode());
        assertNotNull(tMedlemPeriodeResponseEntity.getBody());

        var body = tMedlemPeriodeResponseEntity.getBody();

        assertEquals(1L, (long) body.getAktoerId());
        assertEquals(1L, (long) body.getStudieinformasjonId());
        assertEquals(1L, (long) body.getMedlemPeriodeId());
    }

    @Test(expected = HttpServerErrorException.class)
    public void opprettMedlemskapIkkeOK() {
        mockInvalidResponse();
        orkestreringController.opprettMedlemskap(fnr, miljoe, medlSynt);
    }

    private void mockInvalidResponse() {
        when(syntetiseringService.opprettDelvisMelding(medlSynt, fnr, miljoe)).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private void mockValidResponse() {
        var objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<MedlSyntResponse> syntetiserteMeldinger = null;
        try {
            syntetiserteMeldinger = objectMapper.readValue(getResourceFileContent("medlemskapsmelding.json"), new TypeReference<List<MedlSyntResponse>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert syntetiserteMeldinger != null;

        when(syntetiseringService.opprettDelvisMelding(medlSynt, fnr, miljoe)).thenReturn(TMedlemPeriode.builder()
                .aktoerId(1L)
                .studieinformasjonId(1L)
                .medlemPeriodeId(1L)
                .type("PMMEDSKP")
                .versjon("1")
                .build());
    }
}