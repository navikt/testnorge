package no.nav.registre.orkestratoren.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import no.nav.registre.orkestratoren.batch.v1.JobController;
import no.nav.registre.orkestratoren.service.TestnorgeAaregService;
import no.nav.registre.orkestratoren.service.ArenaService;
import no.nav.registre.orkestratoren.service.TestnorgeInntektService;
import no.nav.registre.orkestratoren.service.TestnorgeInstService;
import no.nav.registre.orkestratoren.service.TestnorgeMedlService;
import no.nav.registre.orkestratoren.service.TestnorgeSamService;
import no.nav.registre.orkestratoren.service.TestnorgeSigrunService;
import no.nav.registre.orkestratoren.service.TestnorgeTpService;

@ExtendWith(MockitoExtension.class)
class JobControllerTest {

    @Mock
    private TestnorgeInntektService testnorgeInntektService;

    @Mock
    private TestnorgeSigrunService testnorgeSigrunService;

    @Mock
    private TestnorgeAaregService testnorgeAaregService;

    @Mock
    private TestnorgeInstService testnorgeInstService;

    @Mock
    private TestnorgeTpService testnorgeTpService;

    @Mock
    private TestnorgeSamService testnorgeSamService;

    @Mock
    private ArenaService arenaService;

    @Mock
    private TestnorgeMedlService testnorgeMedlService;

    @InjectMocks
    private JobController jobController;

    private final Long avspillergruppeId = 123L;

    @BeforeEach
    void setUp() {
        String miljoe = "t1";
        ReflectionTestUtils.setField(jobController, "avspillergruppeId", avspillergruppeId);
        ReflectionTestUtils.setField(jobController, "miljoe", miljoe);
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

}
