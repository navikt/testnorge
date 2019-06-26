package no.nav.dolly.provider.api;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.util.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsOpenAmRequest;
import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.GruppeRepository;
import no.nav.dolly.service.OpenAmService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class OpenAmControllerTest {

    private static final String IDENT1 = "11111111111";
    private static final String IDENT2 = "22222222222";
    private static final String MILJOE1 = "t0";
    private static final String MILJOE2 = "t1";
    private static final long GRUPPEID = 1L;
    private static final Boolean STATUS = true;
    private static final Long BESTILLING_ID = 1L;

    @Mock
    private OpenAmService openAmService;

    @Mock
    private GruppeRepository gruppeRepository;

    @Mock
    private BestillingRepository bestillingRepository;

    @InjectMocks
    private OpenAmController openAmController;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Testgruppe testgruppe;

    @Mock
    private RsOpenAmResponse openAmResponse;

    private ArgumentCaptor<List<String>> captor;

    @Before
    public void setup() {
        captor = ArgumentCaptor.forClass(List.class);
    }

    @Test
    public void sendIdenterTilOpenAmOk() {
        openAmController.sendIdenterTilOpenAm(RsOpenAmRequest.builder()
                .identer(asList(IDENT1, IDENT2))
                .miljoer(asList(MILJOE1, MILJOE2))
                .build());

        verify(openAmService).opprettIdenter(captor.capture(), eq(MILJOE1));
        verify(openAmService).opprettIdenter(captor.capture(), eq(MILJOE2));
        assertThat(captor.getValue(), containsInAnyOrder(IDENT1, IDENT2));
    }

    @Test
    public void endreOpenAmSentStatusOk() {
        when(gruppeRepository.findById(GRUPPEID)).thenReturn(Optional.of(testgruppe));

        openAmController.oppdaterOpenAmSentStatus(GRUPPEID, STATUS);
        verify(gruppeRepository).findById(GRUPPEID);
        verify(gruppeRepository).save(any(Testgruppe.class));
    }

    @Test
    public void endreOpenAmSentStatusGruppeNotFound() {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage(format("GruppeId %d ble ikke funnet.", GRUPPEID));

        openAmController.oppdaterOpenAmSentStatus(GRUPPEID, STATUS);
        verify(gruppeRepository).findById(GRUPPEID);
        verify(gruppeRepository).save(any(Testgruppe.class));
    }

    @Test
    public void sendBestillingTilOpenAm() {
        when(bestillingRepository.findById(BESTILLING_ID)).thenReturn(Optional.of(
                Bestilling.builder()
                        .miljoer(format("%s,%s", MILJOE1, MILJOE2))
                        .gruppe(Testgruppe.builder()
                                .testidenter(newHashSet(asList(Testident.builder().ident(IDENT1)
                                                .bestillingProgress(singletonList(BestillingProgress.builder().bestillingId(BESTILLING_ID).build()))
                                                .build(),
                                        Testident.builder().ident(IDENT2)
                                                .bestillingProgress(singletonList(BestillingProgress.builder().bestillingId(BESTILLING_ID).build()))
                                                .build())))
                                .build())
                        .build()));
        when(openAmService.opprettIdenter(anyList(), anyString())).thenReturn(openAmResponse);

        openAmController.sendBestillingTilOpenAm(BESTILLING_ID);

        verify(openAmService).opprettIdenter(captor.capture(), eq(MILJOE1));
        verify(openAmService).opprettIdenter(captor.capture(), eq(MILJOE2));
        verify(bestillingRepository).findById(BESTILLING_ID);
        verify(bestillingRepository).save(any(Bestilling.class));
        assertThat(captor.getAllValues().get(0), containsInAnyOrder(IDENT1, IDENT2));
        assertThat(captor.getAllValues().get(1), containsInAnyOrder(IDENT1, IDENT2));
    }
}