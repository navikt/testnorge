package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeAaregConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeArenaConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeInstConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSigrunConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.consumer.rs.response.InstitusjonsoppholdResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SigrunSkattegrunnlagResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteArbeidsforholdResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteArenaResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteInstitusjonsoppholdResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteSkattegrunnlagResponse;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@ExtendWith(MockitoExtension.class)
public class IdentServiceTest {

    @Mock
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    @Mock
    private TestnorgeInstConsumer testnorgeInstConsumer;

    @Mock
    private TestnorgeSigrunConsumer testnorgeSigrunConsumer;

    @Mock
    private TestnorgeAaregConsumer testnorgeAaregConsumer;

    @Mock
    private TestnorgeArenaConsumer testnorgeArenaConsumer;

    @Mock
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @InjectMocks
    private IdentService identService;

    private final Long avspillergruppeId = 123L;
    private final String miljoe = "t1";
    private final String testdataEier = "orkestratoren";
    private final String fnr1 = "01010101010";
    private final String fnr2 = "02020202020";
    private List<String> identer;
    private List<Long> expectedMeldingIder;
    private SletteInstitusjonsoppholdResponse sletteInstitusjonsoppholdResponse;
    private SletteSkattegrunnlagResponse sletteSkattegrunnlagResponse;
    private SletteArbeidsforholdResponse sletteArbeidsforholdResponse;
    private SletteArenaResponse sletteArenaResponse;

    @BeforeEach
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        expectedMeldingIder = new ArrayList<>(Arrays.asList(123L, 456L));

        var responses = new ArrayList<>(Arrays.asList(
                InstitusjonsoppholdResponse.builder()
                        .personident(fnr1)
                        .status(HttpStatus.OK)
                        .build(),
                InstitusjonsoppholdResponse.builder()
                        .personident(fnr2)
                        .status(HttpStatus.OK)
                        .build()));
        sletteInstitusjonsoppholdResponse = SletteInstitusjonsoppholdResponse.builder().instStatus(responses).build();

        sletteArenaResponse = SletteArenaResponse.builder().slettet(identer).ikkeSlettet(new ArrayList<>()).build();

        var skattegrunnlagFnr1 = SigrunSkattegrunnlagResponse.builder().personidentifikator(fnr1).build();
        var skattegrunnlagFnr2 = SigrunSkattegrunnlagResponse.builder().personidentifikator(fnr2).build();
        sletteSkattegrunnlagResponse = SletteSkattegrunnlagResponse.builder().grunnlagSomBleSlettet(Arrays.asList(skattegrunnlagFnr1, skattegrunnlagFnr2)).build();

        Map<String, List<Long>> identerMedArbeidsforholdSomBleSlettet = new HashMap<>();
        identerMedArbeidsforholdSomBleSlettet.put(fnr1, Arrays.asList(1L, 2L));
        sletteArbeidsforholdResponse = SletteArbeidsforholdResponse.builder().identermedArbeidsforholdIdSomBleSlettet(identerMedArbeidsforholdSomBleSlettet).build();
    }

    @Test
    public void shouldSletteIdenterFraAdaptere() {
        when(testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(eq(avspillergruppeId), anyList(), eq(identer))).thenReturn(expectedMeldingIder);
        when(testnorgeInstConsumer.slettIdenterFraInst(identer)).thenReturn(sletteInstitusjonsoppholdResponse);
        when(testnorgeSigrunConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer)).thenReturn(sletteSkattegrunnlagResponse);
        when(testnorgeSigrunConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer)).thenReturn(sletteSkattegrunnlagResponse);
        // TODO: Fiks arena og legg inn denne igjen
        // when(arenaConsumer.slettIdenter(miljoe, identer)).thenReturn(sletteArenaResponse);

        var response = identService.slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);

        verify(testnorgeSkdConsumer).slettIdenterFraAvspillerguppe(eq(avspillergruppeId), anyList(), eq(identer));
        verify(testnorgeInstConsumer).slettIdenterFraInst(identer);
        verify(testnorgeSigrunConsumer).slettIdenterFraSigrun(testdataEier, miljoe, identer);
        // TODO: Fiks arena og legg inn denne igjen
        // verify(arenaConsumer).slettIdenter(miljoe, identer);

        assertThat(response.getTpsfStatus().getSlettedeMeldingIderFraTpsf(), IsIterableContainingInOrder.contains(expectedMeldingIder.get(0), expectedMeldingIder.get(1)));

        assertThat(response.getInstStatus().getInstStatus().get(0).getPersonident(), equalTo(identer.get(0)));
        assertThat(response.getInstStatus().getInstStatus().get(0).getStatus(), equalTo(HttpStatus.OK));
        assertThat(response.getInstStatus().getInstStatus().get(1).getPersonident(), equalTo(identer.get(1)));
        assertThat(response.getInstStatus().getInstStatus().get(1).getStatus(), equalTo(HttpStatus.OK));

        assertThat(response.getSigrunStatus().getGrunnlagSomBleSlettet().get(0).getPersonidentifikator(), equalTo(fnr1));
        assertThat(response.getSigrunStatus().getGrunnlagSomBleSlettet().get(1).getPersonidentifikator(), equalTo(fnr2));

        // TODO: Fiks arena og legg inn denne igjen
        // assertThat(response.getArenaForvalterStatus().getSlettet().get(0), equalTo(fnr1));
        // assertThat(response.getArenaForvalterStatus().getSlettet().get(1), equalTo(fnr2));
        // assertThat(response.getArenaForvalterStatus().getIkkeSlettet().size(), equalTo(0));
    }

    @Test
    public void shouldSynkronisereMedTps() {
        when(hodejegerenConsumer.getIdenterSomIkkeErITps(avspillergruppeId, miljoe)).thenReturn(identer);
        when(testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(eq(avspillergruppeId), anyList(), eq(identer))).thenReturn(expectedMeldingIder);
        when(testnorgeInstConsumer.slettIdenterFraInst(identer)).thenReturn(SletteInstitusjonsoppholdResponse.builder().build());
        when(testnorgeSigrunConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer)).thenReturn(SletteSkattegrunnlagResponse.builder().build());
        // TODO: Fiks arena og legg inn denne igjen
        // when(arenaConsumer.slettIdenter(miljoe, identer)).thenReturn(SletteArenaResponse.builder().build());

        var response = identService.synkroniserMedTps(avspillergruppeId, miljoe);

        assertThat(response.getTpsfStatus().getSlettedeMeldingIderFraTpsf(), IsIterableContainingInOrder.contains(expectedMeldingIder.get(0), expectedMeldingIder.get(1)));

        verify(hodejegerenConsumer).getIdenterSomIkkeErITps(avspillergruppeId, miljoe);
        verify(testnorgeSkdConsumer).slettIdenterFraAvspillerguppe(eq(avspillergruppeId), anyList(), eq(identer));
        verify(testnorgeInstConsumer).slettIdenterFraInst(identer);
        verify(testnorgeSigrunConsumer).slettIdenterFraSigrun(testdataEier, miljoe, identer);
        // TODO: Fiks arena og legg inn denne igjen
        // verify(arenaConsumer).slettIdenter(miljoe, identer);
    }

    @Test
    public void shouldFjerneIdenterSomKollidererITps() {
        when(hodejegerenConsumer.getIdenterSomKolliderer(avspillergruppeId)).thenReturn(identer);
        when(testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(eq(avspillergruppeId), anyList(), eq(identer))).thenReturn(expectedMeldingIder);
        when(testnorgeInstConsumer.slettIdenterFraInst(identer)).thenReturn(SletteInstitusjonsoppholdResponse.builder().build());
        when(testnorgeSigrunConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer)).thenReturn(SletteSkattegrunnlagResponse.builder().build());
        // TODO: Fiks arena og legg inn denne igjen
        // when(arenaConsumer.slettIdenter(miljoe, identer)).thenReturn(SletteArenaResponse.builder().build());

        var response = identService.fjernKolliderendeIdenter(avspillergruppeId, miljoe);

        assertThat(response.getTpsfStatus().getSlettedeMeldingIderFraTpsf(), IsIterableContainingInOrder.contains(expectedMeldingIder.get(0), expectedMeldingIder.get(1)));

        verify(hodejegerenConsumer).getIdenterSomKolliderer(avspillergruppeId);
        verify(testnorgeSkdConsumer).slettIdenterFraAvspillerguppe(eq(avspillergruppeId), anyList(), eq(identer));
        verify(testnorgeInstConsumer).slettIdenterFraInst(identer);
        verify(testnorgeSigrunConsumer).slettIdenterFraSigrun(testdataEier, miljoe, identer);
        // TODO: Fiks arena og legg inn denne igjen
        // verify(arenaConsumer).slettIdenter(miljoe, identer);
    }
}