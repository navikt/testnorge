package no.nav.registre.orkestratoren.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.TestnorgeInntektService;
import no.nav.registre.orkestratoren.service.TesnorgeArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeBisysService;
import no.nav.registre.orkestratoren.service.TestnorgeEiaService;
import no.nav.registre.orkestratoren.service.TestnorgeInstService;
import no.nav.registre.orkestratoren.service.TestnorgeMedlService;
import no.nav.registre.orkestratoren.service.TestnorgeSigrunService;
import no.nav.registre.orkestratoren.service.TestnorgeSamService;
import no.nav.registre.orkestratoren.service.TestnorgeTpService;
import no.nav.registre.orkestratoren.service.TestnorgeSkdService;

@RunWith(MockitoJUnitRunner.class)
public class JobControllerTest {

    @Mock
    private TestnorgeSkdService testnorgeSkdService;

    @Mock
    private TestnorgeInntektService testnorgeInntektService;

    @Mock
    private TestnorgeEiaService testnorgeEiaService;

    @Mock
    private TestnorgeSigrunService testnorgeSigrunService;

    @Mock
    private TestnorgeAaregService testnorgeAaregService;

    @Mock
    private TestnorgeInstService testnorgeInstService;

    @Mock
    private TestnorgeBisysService testnorgeBisysService;

    @Mock
    private TestnorgeTpService testnorgeTpService;

    @Mock
    private TestnorgeSamService testnorgeSamService;

    @Mock
    private TesnorgeArenaService tesnorgeArenaService;

    @Mock
    private TestnorgeMedlService testnorgeMedlService;

    @InjectMocks
    private JobController jobController;

    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";
    private List<String> miljoer;
    private Map<String, Integer> antallMeldingerPerEndringskode;
    private Map<Long, String> avspillergruppeIdMedMiljoe;

    @Before
    public void setUp() {
        miljoer = new ArrayList<>(Arrays.asList(miljoe));
        avspillergruppeIdMedMiljoe = new HashMap<>();
        avspillergruppeIdMedMiljoe.put(avspillergruppeId, miljoe);
        ReflectionTestUtils.setField(jobController, "avspillergruppeIdMedMiljoe", avspillergruppeIdMedMiljoe);
        ReflectionTestUtils.setField(jobController, "instbatchAvspillergruppeId", avspillergruppeId);
        ReflectionTestUtils.setField(jobController, "inntektbatchAvspillergruppeId", avspillergruppeId);
        ReflectionTestUtils.setField(jobController, "instbatchMiljoe", miljoer);
        antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put("0110", 2);
        ReflectionTestUtils.setField(jobController, "antallSkdmeldingerPerEndringskode", antallMeldingerPerEndringskode);

        when(testnorgeTpService.genererTp(any())).thenReturn(new ResponseEntity(HttpStatus.OK));
    }

    @Test
    public void shouldStartTpsBatch() {
        jobController.tpsSyntBatch();
        verify(testnorgeSkdService).genererSkdmeldinger(avspillergruppeId, miljoer.get(0), antallMeldingerPerEndringskode);
    }

    @Test
    public void shouldStartNavBatch() {
        jobController.navSyntBatch();
        verify(testnorgeSkdService).genererNavmeldinger(any());
    }

    @Test
    public void shouldStartInntektBatch() {
        jobController.inntektSyntBatch();
        verify(testnorgeInntektService).genererInntektsmeldinger(any());
    }

    @Test
    public void shouldStartEiaBatch() {
        jobController.eiaSyntBatch();
        verify(testnorgeEiaService).genererEiaSykemeldinger(any());
    }

    @Test
    public void shouldStartPoppBatch() {
        jobController.poppSyntBatch();
        verify(testnorgeSigrunService).genererSkattegrunnlag(any(), anyString());
    }

    @Test
    public void shouldStartAaregBatch() {
        jobController.aaregSyntBatch();
        verify(testnorgeAaregService).genererArbeidsforholdsmeldinger(any(), eq(true));
    }

    @Test
    public void shouldStartInstBatch() {
        jobController.instSyntBatch();
        verify(testnorgeInstService).genererInstitusjonsforhold(any());
    }

    @Test
    public void shouldStartBisysBatch() {
        jobController.bisysSyntBatch();
        verify(testnorgeBisysService).genererBistandsmeldinger(any());
    }

    @Test
    public void shouldStartTpBatch() {
        jobController.tpSyntBatch();
        verify(testnorgeTpService).genererTp(any());
    }

    @Test
    public void shouldStartSamBatch() {
        jobController.samSyntBatch();
        verify(testnorgeSamService).genererSamordningsmeldinger(any());
    }

    @Test
    public void shouldStartArenaBatch() {
        jobController.arenaSyntBatch();
        verify(tesnorgeArenaService).opprettArbeidssokereIArena(any());
    }

    @Test
    public void shouldStartMedlBatch() {
        jobController.medlSyntBatch();
        verify(testnorgeMedlService).genererMedlemskap(any());
    }
}
