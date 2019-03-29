package no.nav.registre.skd.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.skd.consumer.TpsfConsumer;

@RunWith(MockitoJUnitRunner.class)
public class AvspillerServiceTest {

    @Mock
    private TpsfConsumer tpsfConsumer;

    @InjectMocks
    private AvspillerService avspillerService;

    private Long avspillergruppeId = 10L;
    private String miljoe = "t1";
    private List<Long> avspillergruppeIder;

    @Before
    public void setUp() {
        avspillergruppeIder = new ArrayList<>(Arrays.asList(123L, 234L));
    }

    @Test
    public void shouldStartAvspillingAvAvspillergruppe() {
        when(tpsfConsumer.getMeldingIdsFromAvspillergruppe(avspillergruppeId)).thenReturn(avspillergruppeIder);

        avspillerService.startAvspillingAvTpsfAvspillergruppe(avspillergruppeId, miljoe);

        verify(tpsfConsumer).getMeldingIdsFromAvspillergruppe(avspillergruppeId);
        verify(tpsfConsumer).sendSkdmeldingerToTps(eq(avspillergruppeId), any());
    }
}