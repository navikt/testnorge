package no.nav.registre.inst.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.inst.consumer.rs.Inst2Consumer;
import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;
import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    @Mock
    private IdentService identService;

    @Mock
    private InstSyntetisererenConsumer instSyntetisererenConsumer;

    @Mock
    private Inst2Consumer inst2Consumer;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

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
    private List<Institusjonsopphold> meldinger;
    private String id = "test";

    @Before
    public void setUp() {
        syntetiserInstRequest = new SyntetiserInstRequest(avspillergruppeId, miljoe, antallMeldinger);
        utvalgteIdenter = new ArrayList<>(Collections.singletonList(fnr1));
        meldinger = new ArrayList<>();
        meldinger.add(Institusjonsopphold.builder()
                .tssEksternId("123")
                .startdato(LocalDate.of(2019, 1, 1))
                .faktiskSluttdato(LocalDate.of(2019, 2, 1))
                .build());
        when(identService.hentTokenTilInst2(miljoe)).thenReturn("Bearer 123");
    }

    @Test
    public void shouldOppretteInstitusjonsmeldinger() {
        when(instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(antallMeldinger)).thenReturn(meldinger);
        when(hodejegerenConsumer.getLevende(avspillergruppeId)).thenReturn(utvalgteIdenter);
        when(inst2Consumer.leggTilInstitusjonsoppholdIInst2(anyString(), eq(id), eq(id), eq(miljoe), eq(meldinger.get(0)))).thenReturn(OppholdResponse.builder()
                .status(HttpStatus.OK)
                .build());
        when(inst2Consumer.finnesInstitusjonPaaDato(anyString(), eq(id), eq(id), eq(miljoe), anyString(), any())).thenReturn(HttpStatus.OK);

        syntetiseringService.finnSyntetiserteMeldingerOgLagreIInst2(id, id, miljoe, syntetiserInstRequest);

        verify(instSyntetisererenConsumer).hentInstMeldingerFromSyntRest(antallMeldinger);
        verify(hodejegerenConsumer).getLevende(avspillergruppeId);
        verify(identService).hentTokenTilInst2(miljoe);
        verify(identService).hentInstitusjonsoppholdFraInst2(anyString(), eq(id), eq(id), eq(miljoe), anyString());
        verify(hodejegerenHistorikkConsumer).saveHistory(any());
    }

    @Test
    public void shouldFindAndLogEksisterendeInstitusjonsopphold() {
        Logger logger = (Logger) LoggerFactory.getLogger(SyntetiseringService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        when(instSyntetisererenConsumer.hentInstMeldingerFromSyntRest(antallMeldinger)).thenReturn(meldinger);
        when(hodejegerenConsumer.getLevende(avspillergruppeId)).thenReturn(utvalgteIdenter);
        when(identService.hentInstitusjonsoppholdFraInst2(anyString(), eq(id), eq(id), eq(miljoe), anyString())).thenReturn(meldinger);
        when(inst2Consumer.finnesInstitusjonPaaDato(anyString(), eq(id), eq(id), eq(miljoe), anyString(), any())).thenReturn(HttpStatus.OK);

        syntetiseringService.finnSyntetiserteMeldingerOgLagreIInst2(id, id, miljoe, syntetiserInstRequest);

        verify(instSyntetisererenConsumer).hentInstMeldingerFromSyntRest(antallMeldinger);
        verify(hodejegerenConsumer).getLevende(avspillergruppeId);
        verify(identService).hentTokenTilInst2(miljoe);
        verify(identService).hentInstitusjonsoppholdFraInst2(anyString(), eq(id), eq(id), eq(miljoe), anyString());

        assertThat(listAppender.list.get(0).toString(), containsString("Ident " + fnr1 + " har allerede fått opprettet institusjonsforhold."));
    }
}
