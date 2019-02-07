package no.nav.registre.sigrun.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
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

import no.nav.registre.sigrun.consumer.rs.PoppSyntetisererenConsumer;
import no.nav.registre.sigrun.consumer.rs.SigrunStubConsumer;

@RunWith(MockitoJUnitRunner.class)
public class PoppServiceTest {

    @Mock
    private PoppSyntetisererenConsumer poppSyntetisererenConsumer;

    @Mock
    private SigrunStubConsumer sigrunStubConsumer;

    @InjectMocks
    private PoppService poppService;

    private List poppSyntetisererenResponse;
    private String testdataEier = "test";

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

        when(poppSyntetisererenConsumer.getPoppMeldingerFromSyntRest(fnrs)).thenReturn(poppSyntetisererenResponse);
        when(sigrunStubConsumer.sendDataToSigrunstub(poppSyntetisererenResponse, testdataEier)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(HttpStatus.OK));

        ResponseEntity actualResponse = poppService.getPoppMeldinger(fnrs, testdataEier);

        verify(poppSyntetisererenConsumer).getPoppMeldingerFromSyntRest(fnrs);
        verify(sigrunStubConsumer).sendDataToSigrunstub(poppSyntetisererenResponse, testdataEier);
        assertThat(actualResponse.getBody(), equalTo(HttpStatus.OK));
    }
}
