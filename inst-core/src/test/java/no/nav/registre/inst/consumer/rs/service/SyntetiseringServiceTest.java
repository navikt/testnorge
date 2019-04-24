package no.nav.registre.inst.consumer.rs.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
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
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import no.nav.registre.inst.consumer.rs.HodejegerenConsumer;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;
import no.nav.registre.inst.institusjonsforhold.Institusjonsforholdsmelding;
import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.inst.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    @Mock
    private InstSyntetisererenConsumer instSyntetisererenConsumer;

    @Mock
    private Inst2Consumer inst2Consumer;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private Random rand;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallMeldinger = 1;
    private String fnr1 = "01010101010";
    private SyntetiserInstRequest syntetiserInstRequest;
    private List<String> utvalgteIdenter;
    private List<Institusjonsforholdsmelding> meldinger;

    @Before
    public void setUp() {
        syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, antallMeldinger);
        utvalgteIdenter = new ArrayList<>(Arrays.asList(fnr1));
        meldinger = new ArrayList<>();
        meldinger.add(Institusjonsforholdsmelding.builder().build());
    }

    @Test
    public void shouldOppretteInstitusjonsmeldinger() {
        when(instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(antallMeldinger)).thenReturn(meldinger);
        when(hodejegerenConsumer.finnLevendeIdenter(avspillergruppeId)).thenReturn(utvalgteIdenter);
        when(inst2Consumer.leggTilInstitusjonsoppholdIInst2(anyMap(), eq(meldinger.get(0)))).thenReturn(ResponseEntity.ok().body(meldinger.get(0)));

        syntetiseringService.finnSyntetiserteMeldinger(syntetiserInstRequest);

        verify(instSyntetisererenConsumer).hentInstMeldingerFromSyntRest(antallMeldinger);
        verify(hodejegerenConsumer).finnLevendeIdenter(avspillergruppeId);
        verify(inst2Consumer, times(2)).hentTokenTilInst2();
        verify(inst2Consumer).hentInstitusjonsoppholdFraInst2(anyMap(), anyString());
    }

    @Test
    public void shouldFindAndLogEksisterendeInstitusjonsopphold() {
        Logger logger = (Logger) LoggerFactory.getLogger(SyntetiseringService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        when(instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(antallMeldinger)).thenReturn(meldinger);
        when(hodejegerenConsumer.finnLevendeIdenter(avspillergruppeId)).thenReturn(utvalgteIdenter);
        when(inst2Consumer.hentInstitusjonsoppholdFraInst2(anyMap(), anyString())).thenReturn(meldinger);

        syntetiseringService.finnSyntetiserteMeldinger(syntetiserInstRequest);

        verify(instSyntetisererenConsumer).hentInstMeldingerFromSyntRest(antallMeldinger);
        verify(hodejegerenConsumer).finnLevendeIdenter(avspillergruppeId);
        verify(inst2Consumer, times(2)).hentTokenTilInst2();
        verify(inst2Consumer).hentInstitusjonsoppholdFraInst2(anyMap(), anyString());

        assertThat(listAppender.list.get(0).toString(), containsString("Ident " + fnr1 + " har allerede fått opprettet institusjonsforhold."));
    }
}
