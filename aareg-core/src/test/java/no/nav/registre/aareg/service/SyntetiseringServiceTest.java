package no.nav.registre.aareg.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.nav.registre.aareg.consumer.rs.AaregSyntetisererenConsumer;
import no.nav.registre.aareg.consumer.rs.AaregstubConsumer;
import no.nav.registre.aareg.consumer.rs.responses.ArbeidsforholdsResponse;
import no.nav.registre.aareg.consumer.rs.responses.StatusFraAaregstubResponse;
import no.nav.registre.aareg.domain.Arbeidsforhold;
import no.nav.registre.aareg.domain.Arbeidsgiver;
import no.nav.registre.aareg.domain.Arbeidstaker;
import no.nav.registre.aareg.provider.rs.requests.SyntetiserAaregRequest;
import no.nav.registre.testnorge.consumers.HodejegerenConsumer;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    private static int MINIMUM_ALDER = 13;

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
    private Boolean lagreIAareg = false;

    @Before
    public void setUp() {
        syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, antallMeldinger);
        fnrs = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        syntetiserteMeldinger = new ArrayList<>();
        when(hodejegerenConsumer.getLevende(avspillergruppeId, MINIMUM_ALDER)).thenReturn(fnrs);
        when(aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(anyList())).thenReturn(syntetiserteMeldinger);
        when(aaregstubConsumer.hentEksisterendeIdenter()).thenReturn(new ArrayList<>());
    }

    @Test
    public void shouldOppretteArbeidshistorikk() {
        when(aaregstubConsumer.sendTilAaregstub(anyList(), eq(lagreIAareg))).thenReturn(StatusFraAaregstubResponse.builder()
                .identerLagretIStub(Collections.singletonList(fnr1))
                .build());

        ResponseEntity response = syntetiseringService.opprettArbeidshistorikkOgSendTilAaregstub(syntetiserAaregRequest, lagreIAareg);

        verify(aaregstubConsumer).sendTilAaregstub(syntetiserteMeldinger, lagreIAareg);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldReturnErrorOnPartialResult() {
        Map<String, String> identerSomIkkeKunneLagres = new HashMap<>();
        identerSomIkkeKunneLagres.put(fnr2, "Feil, OpprettArbeidsforholdUgyldigInput -> Ugyldig input");
        when(aaregstubConsumer.sendTilAaregstub(anyList(), eq(lagreIAareg))).thenReturn(StatusFraAaregstubResponse.builder()
                .identerSomIkkeKunneLagresIAareg(identerSomIkkeKunneLagres)
                .build());

        ResponseEntity response = syntetiseringService.opprettArbeidshistorikkOgSendTilAaregstub(syntetiserAaregRequest, lagreIAareg);

        verify(aaregstubConsumer).sendTilAaregstub(syntetiserteMeldinger, lagreIAareg);
        assertThat(response.getStatusCode(), is(HttpStatus.CONFLICT));
        assertThat(response.getBody().toString(), containsString(fnr2));
    }

    @Test
    public void shouldLogOnTooFewAvailableIdents() {
        Logger logger = (Logger) LoggerFactory.getLogger(SyntetiseringService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        when(aaregstubConsumer.sendTilAaregstub(anyList(), eq(lagreIAareg))).thenReturn(StatusFraAaregstubResponse.builder().build());

        syntetiserAaregRequest = new SyntetiserAaregRequest(avspillergruppeId, miljoe, 3);

        syntetiseringService.opprettArbeidshistorikkOgSendTilAaregstub(syntetiserAaregRequest, lagreIAareg);

        assertThat(listAppender.list.size(), is(equalTo(2)));
        assertThat(listAppender.list.get(0).toString(), containsString("Fant ikke nok ledige identer i avspillergruppe. Lager arbeidsforhold på " + antallMeldinger + " identer."));
    }

    @Test
    public void shouldSendArbeidsforholdToAareg() {
        String arbeidsforholdId = "123";
        List<ArbeidsforholdsResponse> arbeidsforhold = new ArrayList<>();
        ArbeidsforholdsResponse nyttArbeidsforhold = ArbeidsforholdsResponse.builder()
                .environments(Collections.singletonList(miljoe))
                .arbeidsforhold(Arbeidsforhold.builder()
                        .arbeidsgiver(Arbeidsgiver.builder()
                                .aktoertype("ORG")
                                .orgnummer("111222333")
                                .build())
                        .arbeidstaker(Arbeidstaker.builder()
                                .aktoertype("PERS")
                                .ident(fnr1)
                                .identtype("FNR")
                                .build())
                        .build())
                .build();
        arbeidsforhold.add(nyttArbeidsforhold);

        ArbeidsforholdsResponse syntetisertArbeidsforhold = ArbeidsforholdsResponse.builder()
                .arbeidsforhold(Arbeidsforhold.builder()
                        .arbeidsforholdID(arbeidsforholdId)
                        .arbeidsgiver(Arbeidsgiver.builder()
                                .aktoertype("ORG")
                                .orgnummer("111222333")
                                .build())
                        .arbeidstaker(Arbeidstaker.builder()
                                .aktoertype("PERS")
                                .ident(fnr2)
                                .identtype("FNR")
                                .build())
                        .build())
                .build();

        when(aaregSyntetisererenConsumer.getSyntetiserteArbeidsforholdsmeldinger(anyList())).thenReturn(Collections.singletonList(syntetisertArbeidsforhold));

        syntetiseringService.sendArbeidsforholdTilAareg(arbeidsforhold, true);

        verify(aaregstubConsumer).sendTilAaregstub(arbeidsforhold, true);
    }
}
