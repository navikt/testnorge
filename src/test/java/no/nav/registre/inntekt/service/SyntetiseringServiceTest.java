package no.nav.registre.inntekt.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.inntekt.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektSyntConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektstubConsumer;
import no.nav.registre.inntekt.domain.RsInntekt;
import no.nav.registre.inntekt.provider.rs.requests.SyntetiseringsRequest;
import no.nav.registre.inntekt.testUtils.InntektGenerator;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringServiceTest {

    private static final int ALDER = 13;

    @Mock
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @Mock
    private InntektSyntConsumer inntektSyntConsumer;

    @Mock
    private InntektstubConsumer inntektstubConsumer;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private List<String> identer = Collections.singletonList("10128400000");
    private Map<String, List<RsInntekt>> inntekter = new HashMap<>();
    private SyntetiseringsRequest request = new SyntetiseringsRequest(1L);

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(syntetiseringService, "andelNyeIdenter", 1);
        double beloep = 1490;
        inntekter.put("10128400000", Collections.singletonList(InntektGenerator.genererInntekt(beloep)));
        when(hodejegerenConsumer.getLevende(request.getAvspillergruppeId(), ALDER)).thenReturn(identer);
        when(inntektstubConsumer.leggInntekterIInntektstub(inntekter)).thenReturn(new HashMap<>());
    }

    /**
     * Scenario:
     * Ingen feil under hele løpet
     */
    @Test
    public void startSyntetiseringTestIngenFeil() {
        when(inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(anyMap())).thenReturn(inntekter);

        Map<String, List<RsInntekt>> feil = syntetiseringService.startSyntetisering(request);

        assertThat(feil.size(), equalTo(0));
        verify(hodejegerenHistorikkConsumer).saveHistory(any());
    }

    /**
     * Scenario:
     * En inntektsmelding feiler av en eller annen grunn hos inntekt
     */
    @Test
    public void startSyntetiseringTestEnFeil() {
        Map<String, List<RsInntekt>> feiletInntekter = new HashMap<>();
        feiletInntekter.put("10128400000", Collections.singletonList(InntektGenerator.genererInntekt(1490)));
        when(inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(anyMap())).thenReturn(inntekter);
        when(inntektstubConsumer.leggInntekterIInntektstub(inntekter)).thenReturn(feiletInntekter);
        Map<String, List<RsInntekt>> feil = syntetiseringService.startSyntetisering(request);
        assertThat(feil.size(), equalTo(1));
        assertThat(feil.get("10128400000"), equalTo(feiletInntekter.get("10128400000")));
    }

    /**
     * Scenario:
     * Får forespørsel om å genere og lagre syntet meldinger men vi får feil fra InntektSynt
     */
    @Test
    public void startSyntetiseringTestInntektSyntFeiler() {
        when(inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(anyMap())).thenReturn(null);
        syntetiseringService.startSyntetisering(request);
        Map<String, List<RsInntekt>> feil = syntetiseringService.startSyntetisering(request);
        assertThat(feil, equalTo(Collections.EMPTY_MAP));
    }

    /**
     * Scenario:
     * Får forespørsel om å genere og lagre syntet meldinger men vi får ingen meldinger fra InntektSynt
     */
    @Test
    public void startSyntetiseringTestInntektSyntGirIngen() {
        Map<String, List<RsInntekt>> tomSyntInntekt = new HashMap<>();
        when(inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(anyMap())).thenReturn(tomSyntInntekt);
        Map<String, List<RsInntekt>> feil = syntetiseringService.startSyntetisering(request);
        assertThat(feil, equalTo(Collections.EMPTY_MAP));
    }

    @Test
    public void startSyntetiseringTestInntektSyntGirEn() {
        Map<String, List<RsInntekt>> tomSyntInntekt = new HashMap<>();
        tomSyntInntekt.put("10128400000", new ArrayList<>(Collections.singletonList(RsInntekt.builder().build())));
        when(inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(anyMap())).thenReturn(tomSyntInntekt);
        when(inntektstubConsumer.leggInntekterIInntektstub(anyMap())).thenReturn(inntekter);
        Map<String, List<RsInntekt>> feil = syntetiseringService.startSyntetisering(request);
        assertThat(feil.size(), equalTo(1));
    }
}
