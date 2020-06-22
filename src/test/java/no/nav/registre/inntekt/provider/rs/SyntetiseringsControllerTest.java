package no.nav.registre.inntekt.provider.rs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;
import no.nav.registre.inntekt.provider.rs.requests.SyntetiseringsRequest;
import no.nav.registre.inntekt.service.SyntetiseringService;

@RunWith(MockitoJUnitRunner.class)
public class SyntetiseringsControllerTest {

    private static final Long gruppeId = 1L;

    @Mock
    private SyntetiseringService syntetiseringService;

    @InjectMocks
    private SyntetiseringsController syntetiseringsController;

    private Map<String, List<RsInntekt>> feilet;

    @Before
    public void setUp() {
        feilet = new HashMap<>();
    }

    @Test
    public void syntetiseringOk() {
        when(syntetiseringService.startSyntetisering(any(), anyBoolean())).thenReturn(feilet);
        Map<String, List<RsInntekt>> response = syntetiseringsController.genererSyntetiserteInntektsmeldinger(true, new SyntetiseringsRequest(gruppeId, "t1"));
        assertTrue(response.isEmpty());
        verify(syntetiseringService).startSyntetisering(any(), anyBoolean());
    }

    @Test
    public void syntetiseringFeilet() {
        feilet.put("123", new ArrayList<>());
        when(syntetiseringService.startSyntetisering(any(), anyBoolean())).thenReturn(feilet);
        Map<String, List<RsInntekt>> response = syntetiseringsController.genererSyntetiserteInntektsmeldinger(true, new SyntetiseringsRequest(gruppeId, "t1"));
        assertFalse(response.isEmpty());
        verify(syntetiseringService).startSyntetisering(any(), anyBoolean());
    }
}

