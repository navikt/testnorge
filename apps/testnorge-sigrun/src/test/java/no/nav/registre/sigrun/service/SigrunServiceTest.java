package no.nav.registre.sigrun.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.sigrun.domain.PoppSyntetisererenResponse;
import no.nav.registre.sigrun.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.sigrun.consumer.rs.PoppSyntetisererenConsumer;
import no.nav.registre.sigrun.consumer.rs.SigrunStubConsumer;
import no.nav.registre.sigrun.consumer.rs.responses.SigrunSkattegrunnlagResponse;
import no.nav.registre.sigrun.provider.rs.requests.SyntetiserSigrunRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import wiremock.com.google.common.io.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SigrunServiceTest {

    @Mock
    private PoppSyntetisererenConsumer poppSyntetisererenConsumer;

    @Mock
    private SigrunStubConsumer sigrunStubConsumer;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @InjectMocks
    private SigrunService sigrunService;

    private List<PoppSyntetisererenResponse> poppSyntetisererenResponse;
    private String testdataEier = "test";
    private String miljoe = "t1";

    @Before
    public void setUp() throws IOException {
        poppSyntetisererenResponse = Arrays.asList(new ObjectMapper().readValue(Resources.getResource("inntektsmeldinger_test.json"), PoppSyntetisererenResponse[].class));
    }

    @Test
    public void shouldCallPoppSyntetisererenAndSigrunStubConsumer() {
        var fnr1 = "01010101010";
        var fnr2 = "02020202020";
        var identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        when(poppSyntetisererenConsumer.hentPoppMeldingerFromSyntRest(identer)).thenReturn(poppSyntetisererenResponse);
        when(sigrunStubConsumer.sendDataTilSigrunstub(poppSyntetisererenResponse, testdataEier, miljoe)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(identer));

        var actualResponse = sigrunService.genererPoppmeldingerOgSendTilSigrunStub(identer, testdataEier, miljoe);

        verify(poppSyntetisererenConsumer).hentPoppMeldingerFromSyntRest(identer);
        verify(sigrunStubConsumer).sendDataTilSigrunstub(poppSyntetisererenResponse, testdataEier, miljoe);
        verify(hodejegerenHistorikkConsumer).saveHistory(any());
        assertThat(actualResponse.getBody(), equalTo(identer));
    }

    @Test
    public void shouldNotAddIdenticalId() {
        var eksisterendeIdenter = new ArrayList<>(Arrays.asList("01010101010", "02020202020", "03030303030", "04040404040"));
        var nyeIdenter = new ArrayList<>(Arrays.asList("02020202020", "05050505050"));
        var avspillergruppeId = 123L;
        var miljoe = "t1";
        var antallNyeIdenter = 2;

        var syntetiserPoppRequest = new SyntetiserSigrunRequest(avspillergruppeId, miljoe, antallNyeIdenter);

        when(sigrunStubConsumer.hentEksisterendePersonidentifikatorer(miljoe, testdataEier)).thenReturn(eksisterendeIdenter);
        when(hodejegerenConsumer.getLevende(avspillergruppeId, miljoe, antallNyeIdenter, null)).thenReturn(nyeIdenter);

        var resultat = sigrunService.finnEksisterendeOgNyeIdenter(syntetiserPoppRequest, testdataEier);

        assertThat(resultat.size(), is(5));
    }

    @Test
    public void shouldDeleteIdentsFromSigrun() {
        var fnr1 = "01010101010";
        var fnr2 = "02020202020";
        var identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        when(sigrunStubConsumer.hentEksisterendePersonidentifikatorer(miljoe, testdataEier)).thenReturn(identer);
        when(sigrunStubConsumer.hentEksisterendeSkattegrunnlag(fnr1, miljoe)).thenReturn(Collections.singletonList(SigrunSkattegrunnlagResponse.builder()
                .personidentifikator(fnr1)
                .testdataEier(testdataEier)
                .build()));
        when(sigrunStubConsumer.hentEksisterendeSkattegrunnlag(fnr2, miljoe)).thenReturn(Collections.singletonList(SigrunSkattegrunnlagResponse.builder()
                .personidentifikator(fnr2)
                .testdataEier("annenEier")
                .build()));
        when(sigrunStubConsumer.slettEksisterendeSkattegrunnlag(any(), anyString())).thenReturn(ResponseEntity.ok().build());

        var response = sigrunService.slettSkattegrunnlagTilIdenter(identer, testdataEier, miljoe);

        assertThat(response.getGrunnlagSomBleSlettet(), hasSize(1));
        assertThat(response.getGrunnlagSomBleSlettet().get(0).getPersonidentifikator(), equalTo(fnr1));
        assertThat(response.getIdenterMedGrunnlagFraAnnenTestdataEier(), hasSize(1));
        assertThat(response.getIdenterMedGrunnlagFraAnnenTestdataEier().get(0), equalTo(fnr2));
    }
}
