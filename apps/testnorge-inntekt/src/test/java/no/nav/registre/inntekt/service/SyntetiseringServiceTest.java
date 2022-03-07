package no.nav.registre.inntekt.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import no.nav.registre.inntekt.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.inntekt.consumer.rs.SyntInntektConsumer;
import no.nav.registre.inntekt.consumer.rs.InntektstubV2Consumer;
import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;
import no.nav.registre.inntekt.provider.rs.requests.SyntetiseringsRequest;
import no.nav.registre.inntekt.testUtils.InntektGenerator;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;

@ExtendWith(MockitoExtension.class)
class SyntetiseringServiceTest {

    private static final int ALDER = 13;

    @Mock
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @Mock
    private SyntInntektConsumer inntektSyntConsumer;

    @Mock
    private InntektstubV2Consumer inntektstubV2Consumer;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private AaregService aaregService;

    @InjectMocks
    private SyntetiseringService syntetiseringService;

    private List<String> identer = Collections.singletonList("10128400000");
    private SortedMap<String, List<RsInntekt>> inntekter = new TreeMap<>();
    private SyntetiseringsRequest request = new SyntetiseringsRequest(1L, "t1");

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(syntetiseringService, "andelNyeIdenter", 1);
        double beloep = 1490;
        inntekter.put("10128400000", Collections.singletonList(InntektGenerator.genererInntekt(beloep)));
        when(hodejegerenConsumer.getLevende(request.getAvspillergruppeId(), ALDER)).thenReturn(identer);
        when(aaregService.hentIdenterMedArbeidsforhold(anyLong(), anyString())).thenReturn(identer);

    }

    /**
     * Scenario:
     * Ingen feil under hele løpet
     */
    @Test
    void startSyntetiseringTestIngenFeil() {
        when(inntektstubV2Consumer.leggInntekterIInntektstub(inntekter)).thenReturn(new ArrayList<>());
        when(inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(anyMap())).thenReturn(inntekter);

        Map<String, List<RsInntekt>> feil = syntetiseringService.startSyntetisering(request, true);

        assertThat(feil.size(), equalTo(0));
        verify(hodejegerenHistorikkConsumer).saveHistory(any());
    }

    /**
     * Scenario:
     * En inntektsmelding feiler av en eller annen grunn hos inntekt
     */
    @Test
    void startSyntetiseringTestEnFeil() {
        when(inntektstubV2Consumer.leggInntekterIInntektstub(inntekter)).thenReturn(new ArrayList<>());
        double beloep = 1490;
        List<Inntektsinformasjon> feiletInntekt = new ArrayList<>(Collections.singletonList(InntektGenerator.genererFeiletInntektsinformasjon("10128400000", beloep)));
        when(inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(anyMap())).thenReturn(inntekter);
        when(inntektstubV2Consumer.leggInntekterIInntektstub(inntekter)).thenReturn(feiletInntekt);
        Map<String, List<RsInntekt>> feil = syntetiseringService.startSyntetisering(request, true);
        assertThat(feil.size(), equalTo(1));
        assertThat(feil.get("10128400000").get(0).getBeloep(), equalTo(feiletInntekt.get(0).getInntektsliste().get(0).getBeloep()));
    }

    /**
     * Scenario:
     * Får forespørsel om å genere og lagre syntet meldinger men vi får ingen meldinger fra InntektSynt
     */
    @Test
    void startSyntetiseringTestInntektSyntGirIngen() {
        SortedMap<String, List<RsInntekt>> tomSyntInntekt = new TreeMap<>();
        when(inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(anyMap())).thenReturn(tomSyntInntekt);
        Map<String, List<RsInntekt>> feil = syntetiseringService.startSyntetisering(request, true);
        assertThat(feil, equalTo(Collections.EMPTY_MAP));
    }

    @Test
    void startSyntetiseringTestInntektSyntGirEn() {
        SortedMap<String, List<RsInntekt>> tomSyntInntekt = new TreeMap<>();
        tomSyntInntekt.put("10128400000", new ArrayList<>(Collections.singletonList(RsInntekt.builder().build())));
        List<Inntektsinformasjon> feiletInntekt = new ArrayList<>(Collections.singletonList(InntektGenerator.genererFeiletInntektsinformasjon("10128400000", 0)));
        when(inntektSyntConsumer.hentSyntetiserteInntektsmeldinger(anyMap())).thenReturn(tomSyntInntekt);
        when(inntektstubV2Consumer.leggInntekterIInntektstub(anyMap())).thenReturn(feiletInntekt);
        Map<String, List<RsInntekt>> feil = syntetiseringService.startSyntetisering(request, true);
        assertThat(feil.size(), equalTo(1));
    }
}
