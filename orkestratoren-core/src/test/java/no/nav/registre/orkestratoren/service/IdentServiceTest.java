package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.HodejegerenConsumer;
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

@RunWith(MockitoJUnitRunner.class)
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
    private HodejegerenConsumer hodejegerenConsumer;

    @InjectMocks
    private IdentService identService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private String testdataEier = "orkestratoren";
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private List<String> identer;
    private List<Long> expectedMeldingIder;
    private SletteInstitusjonsoppholdResponse sletteInstitusjonsoppholdResponse;
    private SletteSkattegrunnlagResponse sletteSkattegrunnlagResponse;
    private SletteArbeidsforholdResponse sletteArbeidsforholdResponse;
    private SletteArenaResponse sletteArenaResponse;

    @Before
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
        when(testnorgeAaregConsumer.slettIdenterFraAaregstub(identer)).thenReturn(sletteArbeidsforholdResponse);
        // TODO: Fiks arena og legg inn denne igjen
        // when(arenaConsumer.slettIdenter(miljoe, identer)).thenReturn(sletteArenaResponse);

        var response = identService.slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);

        verify(testnorgeSkdConsumer).slettIdenterFraAvspillerguppe(eq(avspillergruppeId), anyList(), eq(identer));
        verify(testnorgeInstConsumer).slettIdenterFraInst(identer);
        verify(testnorgeSigrunConsumer).slettIdenterFraSigrun(testdataEier, miljoe, identer);
        verify(testnorgeAaregConsumer).slettIdenterFraAaregstub(identer);
        // TODO: Fiks arena og legg inn denne igjen
        // verify(arenaConsumer).slettIdenter(miljoe, identer);

        assertThat(response.getTpsfStatus().getSlettedeMeldingIderFraTpsf(), IsIterableContainingInOrder.contains(expectedMeldingIder.get(0), expectedMeldingIder.get(1)));

        assertThat(response.getInstStatus().getInstStatus().get(0).getPersonident(), equalTo(identer.get(0)));
        assertThat(response.getInstStatus().getInstStatus().get(0).getStatus(), equalTo(HttpStatus.OK));
        assertThat(response.getInstStatus().getInstStatus().get(1).getPersonident(), equalTo(identer.get(1)));
        assertThat(response.getInstStatus().getInstStatus().get(1).getStatus(), equalTo(HttpStatus.OK));

        assertThat(response.getSigrunStatus().getGrunnlagSomBleSlettet().get(0).getPersonidentifikator(), equalTo(fnr1));
        assertThat(response.getSigrunStatus().getGrunnlagSomBleSlettet().get(1).getPersonidentifikator(), equalTo(fnr2));

        assertThat(response.getAaregStatus().getIdentermedArbeidsforholdIdSomBleSlettet().get(fnr1).get(0), equalTo(1L));
        assertThat(response.getAaregStatus().getIdentermedArbeidsforholdIdSomBleSlettet().get(fnr1).get(1), equalTo(2L));

        // TODO: Fiks arena og legg inn denne igjen
        // assertThat(response.getArenaForvalterStatus().getSlettet().get(0), equalTo(fnr1));
        // assertThat(response.getArenaForvalterStatus().getSlettet().get(1), equalTo(fnr2));
        // assertThat(response.getArenaForvalterStatus().getIkkeSlettet().size(), equalTo(0));
    }

    @Test
    public void shouldSynkronisereMedTps() {
        when(hodejegerenConsumer.hentIdenterSomIkkeErITps(avspillergruppeId, miljoe)).thenReturn(identer);
        when(testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(eq(avspillergruppeId), anyList(), eq(identer))).thenReturn(expectedMeldingIder);
        when(testnorgeInstConsumer.slettIdenterFraInst(identer)).thenReturn(SletteInstitusjonsoppholdResponse.builder().build());
        when(testnorgeSigrunConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer)).thenReturn(SletteSkattegrunnlagResponse.builder().build());
        when(testnorgeAaregConsumer.slettIdenterFraAaregstub(identer)).thenReturn(SletteArbeidsforholdResponse.builder().build());
        // TODO: Fiks arena og legg inn denne igjen
        // when(arenaConsumer.slettIdenter(miljoe, identer)).thenReturn(SletteArenaResponse.builder().build());

        var response = identService.synkroniserMedTps(avspillergruppeId, miljoe);

        assertThat(response.getTpsfStatus().getSlettedeMeldingIderFraTpsf(), IsIterableContainingInOrder.contains(expectedMeldingIder.get(0), expectedMeldingIder.get(1)));

        verify(hodejegerenConsumer).hentIdenterSomIkkeErITps(avspillergruppeId, miljoe);
        verify(testnorgeSkdConsumer).slettIdenterFraAvspillerguppe(eq(avspillergruppeId), anyList(), eq(identer));
        verify(testnorgeInstConsumer).slettIdenterFraInst(identer);
        verify(testnorgeSigrunConsumer).slettIdenterFraSigrun(testdataEier, miljoe, identer);
        verify(testnorgeAaregConsumer).slettIdenterFraAaregstub(identer);
        // TODO: Fiks arena og legg inn denne igjen
        // verify(arenaConsumer).slettIdenter(miljoe, identer);
    }

    @Test
    public void shouldFjerneIdenterSomKollidererITps() {
        when(hodejegerenConsumer.hentIdenterSomKollidererITps(avspillergruppeId)).thenReturn(identer);
        when(testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(eq(avspillergruppeId), anyList(), eq(identer))).thenReturn(expectedMeldingIder);
        when(testnorgeInstConsumer.slettIdenterFraInst(identer)).thenReturn(SletteInstitusjonsoppholdResponse.builder().build());
        when(testnorgeSigrunConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer)).thenReturn(SletteSkattegrunnlagResponse.builder().build());
        when(testnorgeAaregConsumer.slettIdenterFraAaregstub(identer)).thenReturn(SletteArbeidsforholdResponse.builder().build());
        // TODO: Fiks arena og legg inn denne igjen
        // when(arenaConsumer.slettIdenter(miljoe, identer)).thenReturn(SletteArenaResponse.builder().build());

        var response = identService.fjernKolliderendeIdenter(avspillergruppeId, miljoe);

        assertThat(response.getTpsfStatus().getSlettedeMeldingIderFraTpsf(), IsIterableContainingInOrder.contains(expectedMeldingIder.get(0), expectedMeldingIder.get(1)));

        verify(hodejegerenConsumer).hentIdenterSomKollidererITps(avspillergruppeId);
        verify(testnorgeSkdConsumer).slettIdenterFraAvspillerguppe(eq(avspillergruppeId), anyList(), eq(identer));
        verify(testnorgeInstConsumer).slettIdenterFraInst(identer);
        verify(testnorgeSigrunConsumer).slettIdenterFraSigrun(testdataEier, miljoe, identer);
        verify(testnorgeAaregConsumer).slettIdenterFraAaregstub(identer);
        // TODO: Fiks arena og legg inn denne igjen
        // verify(arenaConsumer).slettIdenter(miljoe, identer);
    }
}