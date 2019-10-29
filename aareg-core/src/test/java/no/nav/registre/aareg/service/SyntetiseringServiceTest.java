package no.nav.registre.aareg.service;

import static no.nav.registre.aareg.consumer.ws.AaregWsConsumer.STATUS_OK;
import static no.nav.registre.aareg.testutils.ResourceUtils.getResourceFileContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.aareg.consumer.rs.AaregSyntetisererenConsumer;
import no.nav.registre.aareg.consumer.rs.AaregstubConsumer;
import no.nav.registre.aareg.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.aareg.provider.rs.response.RsAaregResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    private static final int MINIMUM_ALDER = 13;

    @Mock
    private Random rand;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private AaregSyntetisererenConsumer aaregSyntetisererenConsumer;

    @Mock
    private AaregstubConsumer aaregstubConsumer;

    @Mock
    private AaregService aaregService;

    @Mock
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private final Long avspillergruppeId = 123L;
    private final String miljoe = "t1";
    private final int antallMeldinger = 2;
    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";
    private SyntetiserAaregRequest syntetiserAaregRequest;
    private List<String> fnrs;
    private List<RsAaregOpprettRequest> syntetiserteMeldinger;
    private boolean sendAlleEksisterende = true;

    @Before
    public void setUp() throws IOException {
        syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, antallMeldinger);
        fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        syntetiserteMeldinger = new ArrayList<>();

        String resourceFileContent = getResourceFileContent("arbeidsforholdsmelding.json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List list = objectMapper.readValue(resourceFileContent, List.class);
        for (Object o : list) {
            syntetiserteMeldinger.add(objectMapper.convertValue(o, RsAaregOpprettRequest.class));
        }

        when(hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER)).thenReturn(fnrs);
        when(aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(anyList())).thenReturn(syntetiserteMeldinger);
        when(aaregstubConsumer.hentEksisterendeIdenter()).thenReturn(new ArrayList<>());
    }

    @Test
    public void shouldOppretteArbeidshistorikk() {
        Map<String, String> status = new HashMap<>();
        status.put(miljoe, STATUS_OK);
        RsAaregResponse rsAaregResponse = RsAaregResponse.builder()
                .statusPerMiljoe(status)
                .build();

        when(aaregService.opprettArbeidsforhold(any())).thenReturn(rsAaregResponse);
        when(aaregstubConsumer.sendTilAaregstub(anyList())).thenReturn(Collections.singletonList(fnr1));

        ResponseEntity response = syntetiseringService.opprettArbeidshistorikkOgSendTilAaregstub(syntetiserAaregRequest, sendAlleEksisterende);

        verify(aaregstubConsumer).sendTilAaregstub(Collections.singletonList(syntetiserteMeldinger.get(0)));
        verify(aaregstubConsumer).sendTilAaregstub(Collections.singletonList(syntetiserteMeldinger.get(1)));
        verify(hodejegerenHistorikkConsumer, times(2)).saveHistory(any());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
