package no.nav.dolly.provider.api;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsOpenAmResponse;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.service.OpenAmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpenAmControllerTest {

    private static final String IDENT1 = "11111111111";
    private static final String IDENT2 = "22222222222";
    private static final String MILJOE1 = "t0";
    private static final String MILJOE2 = "t1";
    private static final Long BESTILLING_ID = 1L;

    @Mock
    private OpenAmService openAmService;

    @Mock
    private BestillingRepository bestillingRepository;

    @InjectMocks
    private OpenAmController openAmController;

    @Mock
    private RsOpenAmResponse openAmResponse;

    private ArgumentCaptor<List<String>> captor;

    @BeforeEach
    public void setup() {
        captor = ArgumentCaptor.forClass(List.class);
    }

    @Test
    public void sendBestillingTilOpenAm() {
        when(bestillingRepository.findById(BESTILLING_ID)).thenReturn(Optional.of(
                Bestilling.builder()
                        .miljoer(format("%s,%s", MILJOE1, MILJOE2))
                        .gruppe(Testgruppe.builder()
                                .testidenter(List.of(Testident.builder().ident(IDENT1)
                                                .bestillingProgress(singletonList(BestillingProgress.builder()
                                                        .bestilling(Bestilling.builder().id(BESTILLING_ID).build())
                                                        .build()))
                                                .build(),
                                        Testident.builder().ident(IDENT2)
                                                .bestillingProgress(singletonList(BestillingProgress.builder()
                                                        .bestilling(Bestilling.builder().id(BESTILLING_ID).build())
                                                        .build()))
                                                .build()))
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