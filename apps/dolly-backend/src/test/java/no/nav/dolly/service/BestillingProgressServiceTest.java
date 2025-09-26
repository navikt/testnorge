package no.nav.dolly.service;

import no.nav.dolly.MockedJwtAuthenticationTokenUtils;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingProgressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BestillingProgressServiceTest {

    @Mock
    private BestillingProgressRepository bestillingProgressRepository;

    @InjectMocks
    private BestillingProgressService progressService;

    @BeforeTestClass
    public void setup() {
        MockedJwtAuthenticationTokenUtils.setJwtAuthenticationToken();
    }

    @Test
    void bestillingProgressKasterExceptionHvisManIkkeFinnerProgress() {

        when(bestillingProgressRepository.findAllByBestillingId(any())).thenReturn(Flux.empty());

        StepVerifier.create(progressService.fetchBestillingProgressByBestillingId(1L))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void hviFetchBestillingProgressFinnerObjectSaaReturnerObjektet() {

        var id = 1L;
        var progress = BestillingProgress.builder().id(id).build();
        when(bestillingProgressRepository.findAllByBestillingId(id)).thenReturn(Flux.just(progress));

        StepVerifier.create(progressService.fetchBestillingProgressByBestillingId(id))
                .expectNext(progress)
                .verifyComplete();
    }
}