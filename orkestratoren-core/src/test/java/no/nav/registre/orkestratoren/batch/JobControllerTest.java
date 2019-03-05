package no.nav.registre.orkestratoren.batch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

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
import org.springframework.test.util.ReflectionTestUtils;

import no.nav.registre.orkestratoren.service.AaregSyntPakkenService;
import no.nav.registre.orkestratoren.service.ArenaInntektSyntPakkenService;
import no.nav.registre.orkestratoren.service.EiaSyntPakkenService;
import no.nav.registre.orkestratoren.service.InstSyntPakkenService;
import no.nav.registre.orkestratoren.service.PoppSyntPakkenService;
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

    @InjectMocks
    private JobController jobController;

    private Long avspillergruppeId = 123L;
    private List<String> miljoe;
    private Map<String, Integer> antallMeldingerPerEndringskode;

    @Before
    public void setUp() {
        miljoe = new ArrayList<>(Arrays.asList("t1"));
        ReflectionTestUtils.setField(jobController, "avspillergruppeId", avspillergruppeId);
        ReflectionTestUtils.setField(jobController, "tpsbatchMiljoe", miljoe);
        ReflectionTestUtils.setField(jobController, "eiabatchMiljoe", miljoe);
        ReflectionTestUtils.setField(jobController, "poppbatchMiljoe", miljoe);
        ReflectionTestUtils.setField(jobController, "aaregbatchMiljoe", miljoe);
        ReflectionTestUtils.setField(jobController, "instbatchMiljoe", miljoe);
        antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put("0110", 2);
        ReflectionTestUtils.setField(jobController, "antallMeldingerPerEndringskode", antallMeldingerPerEndringskode);
    }

    @Test
    public void shouldStartTpsBatch() {
        jobController.tpsSyntBatch();
        verify(tpsSyntPakkenService).genererSkdmeldinger(avspillergruppeId, miljoe.get(0), antallMeldingerPerEndringskode);
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
        verify(aaregSyntPakkenService).genererArbeidsforholdsmeldinger(any());
    }

    @Test
    public void shouldStartInstBatch() {
        jobController.instSyntBatch();
        verify(instSyntPakkenService).genererInstitusjonsforhold(any());
    }
}
