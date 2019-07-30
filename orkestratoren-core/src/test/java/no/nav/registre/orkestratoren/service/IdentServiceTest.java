package no.nav.registre.orkestratoren.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.ArenaConsumer;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteArenaResponse;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.registre.orkestratoren.consumer.rs.InstSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.PoppSyntConsumer;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSkdConsumer;
import no.nav.registre.orkestratoren.consumer.rs.response.SigrunSkattegrunnlagResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteInstitusjonsoppholdResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteSkattegrunnlagResponse;
import no.nav.registre.orkestratoren.provider.rs.responses.SlettedeIdenterResponse;

@RunWith(MockitoJUnitRunner.class)
public class IdentServiceTest {

    @Mock
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    @Mock
    private InstSyntConsumer instSyntConsumer;

    @Mock
    private PoppSyntConsumer poppSyntConsumer;

    @Mock
    private ArenaConsumer arenaConsumer;

    @InjectMocks
    private IdentService identService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private String testdataEier = "test";
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private List<String> identer;
    private List<Long> expectedMeldingIder;
    private SletteInstitusjonsoppholdResponse sletteInstitusjonsoppholdResponse;
    private SletteSkattegrunnlagResponse sletteSkattegrunnlagResponse;
    private List<String> oppholdIdSomBleSlettet;
    private SletteArenaResponse sletteArenaResponse;

    @Before
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList(fnr1, fnr2));
        expectedMeldingIder = new ArrayList<>(Arrays.asList(123L, 456L));

        oppholdIdSomBleSlettet = new ArrayList<>(Arrays.asList("1", "2"));
        Map<String, List<String>> identerMedOppholdIdSomBleSlettet = new HashMap<>();
        identerMedOppholdIdSomBleSlettet.put(fnr1, Collections.singletonList(oppholdIdSomBleSlettet.get(0)));
        identerMedOppholdIdSomBleSlettet.put(fnr2, Collections.singletonList(oppholdIdSomBleSlettet.get(1)));
        sletteInstitusjonsoppholdResponse = SletteInstitusjonsoppholdResponse.builder().identerMedOppholdIdSomBleSlettet(identerMedOppholdIdSomBleSlettet).build();
        sletteArenaResponse = SletteArenaResponse.builder().slettet(identer).ikkeSlettet(new ArrayList<>()).build();

        SigrunSkattegrunnlagResponse skattegrunnlagFnr1 = SigrunSkattegrunnlagResponse.builder().personidentifikator(fnr1).build();
        SigrunSkattegrunnlagResponse skattegrunnlagFnr2 = SigrunSkattegrunnlagResponse.builder().personidentifikator(fnr2).build();
        sletteSkattegrunnlagResponse = SletteSkattegrunnlagResponse.builder().grunnlagSomBleSlettet(Arrays.asList(skattegrunnlagFnr1, skattegrunnlagFnr2)).build();
    }

    @Test
    public void shouldSletteIdenterFraAdaptere() {
        when(testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(avspillergruppeId, identer)).thenReturn(expectedMeldingIder);
        when(instSyntConsumer.slettIdenterFraInst(identer)).thenReturn(sletteInstitusjonsoppholdResponse);
        when(poppSyntConsumer.slettIdenterFraSigrun(testdataEier, miljoe, identer)).thenReturn(sletteSkattegrunnlagResponse);
        when(arenaConsumer.slettIdenter(miljoe, identer)).thenReturn(sletteArenaResponse);

        SlettedeIdenterResponse response = identService.slettIdenterFraAdaptere(avspillergruppeId, miljoe, testdataEier, identer);

        verify(testnorgeSkdConsumer).slettIdenterFraAvspillerguppe(avspillergruppeId, identer);
        verify(instSyntConsumer).slettIdenterFraInst(identer);
        verify(poppSyntConsumer).slettIdenterFraSigrun(testdataEier, miljoe, identer);
        verify(arenaConsumer).slettIdenter(miljoe, identer);

        assertThat(response.getTpsfStatus().getSlettedeMeldingIderFraTpsf(), IsIterableContainingInOrder.contains(expectedMeldingIder.get(0), expectedMeldingIder.get(1)));

        assertThat(response.getInstStatus().getIdenterMedOppholdIdSomBleSlettet().keySet(), IsIterableContainingInAnyOrder.containsInAnyOrder(identer.get(0), identer.get(1)));
        assertThat(response.getInstStatus().getIdenterMedOppholdIdSomBleSlettet().get(identer.get(0)), IsIterableContainingInOrder.contains(oppholdIdSomBleSlettet.get(0)));
        assertThat(response.getInstStatus().getIdenterMedOppholdIdSomBleSlettet().get(identer.get(1)), IsIterableContainingInOrder.contains(oppholdIdSomBleSlettet.get(1)));

        assertThat(response.getSigrunStatus().getGrunnlagSomBleSlettet().get(0).getPersonidentifikator(), equalTo(fnr1));
        assertThat(response.getSigrunStatus().getGrunnlagSomBleSlettet().get(1).getPersonidentifikator(), equalTo(fnr2));

        assertThat(response.getArenaForvalterStatus().getSlettet().get(0), equalTo(fnr1));
        assertThat(response.getArenaForvalterStatus().getSlettet().get(1), equalTo(fnr2));
        assertThat(response.getArenaForvalterStatus().getIkkeSlettet().size(), equalTo(0));
    }
}