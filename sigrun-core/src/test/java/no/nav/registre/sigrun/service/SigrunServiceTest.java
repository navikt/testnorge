package no.nav.registre.sigrun.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.sigrun.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sigrun.consumer.rs.PoppSyntetisererenConsumer;
import no.nav.registre.sigrun.consumer.rs.SigrunStubConsumer;
import no.nav.registre.sigrun.provider.rs.requests.SyntetiserPoppRequest;

@RunWith(MockitoJUnitRunner.class)
public class SigrunServiceTest {

    @Mock
    private PoppSyntetisererenConsumer poppSyntetisererenConsumer;

    @Mock
    private SigrunStubConsumer sigrunStubConsumer;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @InjectMocks
    private SigrunService sigrunService;

    private List poppSyntetisererenResponse;
    private String testdataEier = "test";
    private String miljoe = "t1";

    @Before
    public void setUp() throws IOException {
        URL jsonContent = Resources.getResource("inntektsmeldinger_test.json");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonContent);
        poppSyntetisererenResponse = objectMapper.treeToValue(jsonNode, List.class);
    }

    @Test
    public void shouldCallPoppSyntetisererenAndSigrunStubConsumer() {
        String fnr1 = "01010101010";
        String fnr2 = "02020202020";
        List<String> fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2));

        when(poppSyntetisererenConsumer.hentPoppMeldingerFromSyntRest(fnrs)).thenReturn(poppSyntetisererenResponse);
        when(sigrunStubConsumer.sendDataTilSigrunstub(poppSyntetisererenResponse, testdataEier, miljoe)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(HttpStatus.OK));

        ResponseEntity actualResponse = sigrunService.genererPoppmeldingerOgSendTilSigrunStub(fnrs, testdataEier, miljoe);

        verify(poppSyntetisererenConsumer).hentPoppMeldingerFromSyntRest(fnrs);
        verify(sigrunStubConsumer).sendDataTilSigrunstub(poppSyntetisererenResponse, testdataEier, miljoe);
        verify(hodejegerenConsumer).saveHistory(any());
        assertThat(actualResponse.getBody(), equalTo(HttpStatus.OK));
    }

    @Test
    public void shouldNotAddIdenticalId() {
        List<String> eksisterendeIdenter = new ArrayList<>(Arrays.asList("01010101010", "02020202020", "03030303030", "04040404040"));
        List<String> nyeIdenter = new ArrayList<>(Arrays.asList("02020202020", "05050505050"));

        SyntetiserPoppRequest syntetiserPoppRequest = new SyntetiserPoppRequest(123L, "t1", 2);

        when(sigrunStubConsumer.hentEksisterendePersonidentifikatorer(miljoe)).thenReturn(eksisterendeIdenter);
        when(hodejegerenConsumer.finnLevendeIdenter(syntetiserPoppRequest)).thenReturn(nyeIdenter);

        List<String> resultat = sigrunService.finnEksisterendeOgNyeIdenter(syntetiserPoppRequest);

        assertThat(resultat.size(), is(5));
    }
}
