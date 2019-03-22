package no.nav.registre.aareg.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import no.nav.registre.aareg.consumer.rs.AaregSyntetisererenConsumer;
import no.nav.registre.aareg.consumer.rs.AaregstubConsumer;
import no.nav.registre.aareg.consumer.rs.HodejegerenConsumer;
import no.nav.registre.aareg.consumer.rs.responses.ArbeidsforholdsResponse;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    @Mock
    private Random rand;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private AaregSyntetisererenConsumer aaregSyntetisererenConsumer;

    @Mock
    private AaregstubConsumer aaregstubConsumer;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallMeldinger = 2;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private SyntetiserAaregRequest syntetiserAaregRequest;
    private List<String> fnrs;
    private List<ArbeidsforholdsResponse> syntetiserteMeldinger;

    @Before
    public void setUp() {
        syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, antallMeldinger);
        fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        syntetiserteMeldinger = new ArrayList<>();
        when(hodejegerenConsumer.finnLevendeIdenter(avspillergruppeId)).thenReturn(fnrs);
        when(aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(anyList())).thenReturn(syntetiserteMeldinger);
        when(aaregstubConsumer.hentEksisterendeIdenter()).thenReturn(new ArrayList<>());
    }

    @Test
    public void shouldOppretteArbeidshistorikk() {
        when(aaregstubConsumer.sendTilAaregstub(anyList())).thenReturn(new ArrayList<>(fnrs));

        ResponseEntity response = syntetiseringService.opprettArbeidshistorikk(syntetiserAaregRequest);

        verify(aaregstubConsumer).sendTilAaregstub(syntetiserteMeldinger);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldReturnErrorOnPartialResult() {
        when(aaregstubConsumer.sendTilAaregstub(anyList())).thenReturn(new ArrayList<>(Arrays.asList(fnr1)));

        ResponseEntity response = syntetiseringService.opprettArbeidshistorikk(syntetiserAaregRequest);

        verify(aaregstubConsumer).sendTilAaregstub(syntetiserteMeldinger);
        assertThat(response.getStatusCode(), is(HttpStatus.CONFLICT));
        assertThat(response.getBody().toString(), containsString(fnr2));
    }

    @Test
    public void shouldLogOnTooFewAvailableIdents() {
        Logger logger = (Logger) LoggerFactory.getLogger(SyntetiseringService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, 3);

        syntetiseringService.opprettArbeidshistorikk(syntetiserAaregRequest);

        assertThat(listAppender.list.size(), CoreMatchers.is(equalTo(2)));
        assertThat(listAppender.list.get(0).toString(), containsString("Fant ikke nok ledige identer i avspillergruppe. Lager arbeidsforhold på " + antallMeldinger + " identer."));
    }
}
