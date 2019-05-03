package no.nav.registre.orkestratoren.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import no.nav.registre.orkestratoren.service.AaregSyntPakkenService;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.BisysSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.InstSyntPakkenService;
import no.nav.registre.orkestratoren.service.PoppSyntPakkenService;
import no.nav.registre.orkestratoren.service.SamSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpSyntPakkenService;
import no.nav.registre.orkestratoren.service.TpsSyntPakkenService;

@RunWith(MockitoJUnitRunner.class)
public class JobControllerTest {

    @Mock
    private TpsSyntPakkenService tpsSyntPakkenService;

    @Mock
    private ArenaInntektSyntPakkenService arenaInntektSyntPakkenService;

    @Mock
    private EiaSyntPakkenService eiaSyntPakkenService;

    @Mock
    private PoppSyntPakkenService poppSyntPakkenService;

    @Mock
    private AaregSyntPakkenService aaregSyntPakkenService;

    @Mock
    private InstSyntPakkenService instSyntPakkenService;

    @Mock
    private BisysSyntPakkenService bisysSyntPakkenService;

    @Mock
    private TpSyntPakkenService tpSyntPakkenService;

    @Mock
    private SamSyntPakkenService samSyntPakkenService;

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
        ReflectionTestUtils.setField(jobController, "antallMeldingerPerEndringskode", antallMeldingerPerEndringskode);

        when(tpSyntPakkenService.genererTp(any())).thenReturn(new ResponseEntity(HttpStatus.OK));
    }

    @Test
    public void shouldStartTpsBatch() {
        jobController.tpsSyntBatch();
        verify(tpsSyntPakkenService).genererSkdmeldinger(avspillergruppeId, miljoer.get(0), antallMeldingerPerEndringskode);
    }

    @Test
    public void shouldStartArenaInntektBatch() {
        jobController.arenaInntektSyntBatch();
        verify(arenaInntektSyntPakkenService).genererInntektsmeldinger(any());
    }

    @Test
    public void shouldStartEiaBatch() {
        jobController.eiaSyntBatch();
        verify(eiaSyntPakkenService).genererEiaSykemeldinger(any());
    }

    @Test
    public void shouldStartPoppBatch() {
        jobController.poppSyntBatch();
        verify(poppSyntPakkenService).genererSkattegrunnlag(any(), anyString());
    }

    @Test
    public void shouldStartAaregBatch() {
        jobController.aaregSyntBatch();
        verify(aaregSyntPakkenService).genererArbeidsforholdsmeldinger(any(), eq(true));
    }

    @Test
    public void shouldStartInstBatch() {
        jobController.instSyntBatch();
        verify(instSyntPakkenService).genererInstitusjonsforhold(any());
    }

    @Test
    public void shouldStartBisysBatch() {
        jobController.bisysSyntBatch();
        verify(bisysSyntPakkenService).genererBistandsmeldinger(any());
    }

    @Test
    public void shouldStartTpBatch() {
        jobController.tpSyntBatch();
        verify(tpSyntPakkenService).genererTp(any());
    }

    @Test
    public void shouldStartSamBatch() {
        jobController.samSyntBatch();
        verify(samSyntPakkenService).genererSamordningsmeldinger(any());
    }
}
