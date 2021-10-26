package no.nav.registre.orkestratoren.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.batch.v1.JobController;
import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.TestnorgeArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeBisysService;
import no.nav.registre.orkestratoren.service.TestnorgeFrikortService;
import no.nav.registre.orkestratoren.service.TestnorgeInntektService;
import no.nav.registre.orkestratoren.service.TestnorgeInstService;
import no.nav.registre.orkestratoren.service.TestnorgeMedlService;
import no.nav.registre.orkestratoren.service.TestnorgeSamService;
import no.nav.registre.orkestratoren.service.TestnorgeSigrunService;
import no.nav.registre.orkestratoren.service.TestnorgeSkdService;
import no.nav.registre.orkestratoren.service.TestnorgeTpService;

@ExtendWith(MockitoExtension.class)
public class JobControllerTest {

    @Mock
    private TestnorgeSkdService testnorgeSkdService;

    @Mock
    private TestnorgeInntektService testnorgeInntektService;

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
    private TestnorgeArenaService testnorgeArenaService;

    @Mock
    private TestnorgeMedlService testnorgeMedlService;

    @Mock
    private TestnorgeFrikortService testnorgeFrikortService;

    @InjectMocks
    private JobController jobController;

    private final Long avspillergruppeId = 123L;
    private List<String> miljoer;
    private Map<String, Integer> antallMeldingerPerEndringskode;

    @BeforeEach
    void setUp() {
        String miljoe = "t1";
        miljoer = new ArrayList<>(Collections.singletonList(miljoe));
        Map<Long, String> avspillergruppeIdMedMiljoe = new HashMap<>();
        avspillergruppeIdMedMiljoe.put(avspillergruppeId, miljoe);
        ReflectionTestUtils.setField(jobController, "avspillergruppeIdMedMiljoe", avspillergruppeIdMedMiljoe);
        antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put("0110", 2);
        ReflectionTestUtils.setField(jobController, "antallSkdmeldingerPerEndringskode", antallMeldingerPerEndringskode);
    }

    @Test
    void shouldStartTpsBatch() {
        jobController.tpsSyntBatch();
        verify(testnorgeSkdService).genererSkdmeldinger(avspillergruppeId, miljoer.get(0), antallMeldingerPerEndringskode);
    }

    @Test
    @Disabled("Koden er kommentert ut")
    void shouldStartNavBatch() {
        jobController.navSyntBatch();
        verify(testnorgeSkdService).genererNavmeldinger(any());
    }

    @Test
    void shouldStartInntektBatch() {
        jobController.inntektSyntBatch();
        verify(testnorgeInntektService).genererInntektsmeldinger(any());
    }

    @Test
    void shouldStartPoppBatch() {
        jobController.poppSyntBatch();
        verify(testnorgeSigrunService).genererSkattegrunnlag(any(), anyString());
    }

    @Test
    void shouldStartAaregBatch() {
        jobController.aaregSyntBatch();
        verify(testnorgeAaregService).genererArbeidsforholdsmeldinger(any(), eq(true));
    }

    @Test
    void shouldStartInstBatch() {
        jobController.instSyntBatch();
        verify(testnorgeInstService).genererInstitusjonsforhold(any());
    }

    @Test
    void shouldStartBisysBatch() {
        jobController.bisysSyntBatch();
        verify(testnorgeBisysService).genererBistandsmeldinger(any());
    }

    @Test
    void shouldStartTpBatch() {
        when(testnorgeTpService.genererTp(any())).thenReturn(new ResponseEntity(HttpStatus.OK));
        jobController.tpSyntBatch();
        verify(testnorgeTpService).genererTp(any());
    }

    @Test
    void shouldStartSamBatch() {
        jobController.samSyntBatch();
        verify(testnorgeSamService).genererSamordningsmeldinger(any());
    }

    @Test
    void shouldStartMedlBatch() {
        jobController.medlSyntBatch();
        verify(testnorgeMedlService).genererMedlemskap(any());
    }

    @Test
    void shouldStartFrikortBatch() {
        jobController.frikortSyntBatch();
        verify(testnorgeFrikortService).genererFrikortEgenmeldinger(any());
    }
}
