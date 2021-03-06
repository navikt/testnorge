package no.nav.registre.tss.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import wiremock.com.google.common.io.Resources;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.TssSyntetisererenConsumer;
import no.nav.registre.tss.consumer.rs.response.TssMessage;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.domain.Samhandler;
import no.nav.registre.tss.domain.TssType;
import no.nav.registre.tss.provider.rs.request.SyntetiserTssRequest;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    private static final int MIN_ALDER = 25;
    private static final int MAX_ALDER = 70;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private TssSyntetisererenConsumer tssSyntetisererenConsumer;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private int antallNyeIdenter = 2;
    private String fnr1 = "23026325811";
    private String navn1 = "GLOBUS IHERDIG STRAFFET";
    private String fnr2 = "08016325431";
    private String navn2 = "BORD OPPSTEMT ELEKTRONISK";

    @Test
    public void shouldHenteIdenter() throws IOException {
        URL resource = Resources.getResource("status_quo_test.json");
        JsonNode jsonNode = new ObjectMapper().readValue(resource, JsonNode.class);
        JsonNode value1 = jsonNode.findValue(fnr1);
        JsonNode value2 = jsonNode.findValue(fnr2);
        Map<String, JsonNode> identerMedStatusQuo = new HashMap<>();
        identerMedStatusQuo.put(fnr1, value1);
        identerMedStatusQuo.put(fnr2, value2);

        SyntetiserTssRequest syntetiserTssRequest = SyntetiserTssRequest.builder()
                .avspillergruppeId(avspillergruppeId)
                .miljoe(miljoe)
                .antallNyeIdenter(antallNyeIdenter)
                .build();

        when(hodejegerenConsumer.getStatusQuo(avspillergruppeId, miljoe, antallNyeIdenter, MIN_ALDER, MAX_ALDER)).thenReturn(identerMedStatusQuo);

        List<Person> identer = syntetiseringService.hentIdenter(syntetiserTssRequest);

        assertThat(identer.get(0).getFnr(), is(fnr1));
        assertThat(identer.get(0).getNavn(), is(navn1));
        assertThat(identer.get(1).getFnr(), is(fnr2));
        assertThat(identer.get(1).getNavn(), is(navn2));

        verify(hodejegerenConsumer).getStatusQuo(avspillergruppeId, miljoe, antallNyeIdenter, MIN_ALDER, MAX_ALDER);
    }

    @Test
    public void shouldOppretteSyntetiskeRutiner() throws IOException {
        URL resource = Resources.getResource("syntetiske_rutiner_flatfil.json");
        List<String> forventetResultat = new ObjectMapper().readValue(resource, new TypeReference<List<String>>() {
        });

        resource = Resources.getResource("syntetiske_rutiner.json");
        Map<String, List<TssMessage>> syntetiskeMeldinger = new ObjectMapper().readValue(resource, new TypeReference<Map<String, List<TssMessage>>>() {
        });

        Person person1 = new Person(fnr1, navn1);
        Person person2 = new Person(fnr2, navn2);
        List<Person> personer = new ArrayList<>(Arrays.asList(person1, person2));

        LocalDate birthdate = LocalDate.of(1963, 2, 23);
        int alder = Math.toIntExact(ChronoUnit.YEARS.between(birthdate, LocalDate.now()));

        assertThat(person1.getAlder(), is(alder));

        birthdate = LocalDate.of(1963, 1, 8);
        alder = Math.toIntExact(ChronoUnit.YEARS.between(birthdate, LocalDate.now()));

        assertThat(person2.getAlder(), is(alder));

        when(tssSyntetisererenConsumer.hentSyntetiskeTssRutiner(anyList())).thenReturn(syntetiskeMeldinger);


        List<String> syntetiskeRutiner = syntetiseringService.opprettSyntetiskeTssRutiner(personer.stream()
                .map(person -> new Samhandler(person, TssType.LE)).collect(Collectors.toList()));

        assertThat(syntetiskeRutiner.get(0), equalTo(forventetResultat.get(0)));
        assertThat(syntetiskeRutiner.get(1), equalTo(forventetResultat.get(1)));
    }
}