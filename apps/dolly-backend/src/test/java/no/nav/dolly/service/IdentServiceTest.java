package no.nav.dolly.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.domain.dto.TestidentDTO;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentServiceTest {

    private static final String STANDARD_IDENTER_1 = "en";
    private static final Long GRUPPE_ID = 1L;

    @Mock
    private IdentRepository identRepository;

    @Mock
    private TransaksjonMappingRepository transaksjonMappingRepository;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private BestillingService bestillingService;

    @Mock
    private PersonService personService;

    @InjectMocks
    private IdentService identService;

    @Test
    void saveIdentTilGruppe_saveAvIdentInnholderInputIdentstringOgTestgruppe() {

        var testident = Testident.builder()
                .ident(STANDARD_IDENTER_1)
                .gruppeId(GRUPPE_ID)
                .master(Testident.Master.PDL)
                .build();
        when(identRepository.findByIdent(STANDARD_IDENTER_1)).thenReturn(Mono.empty());
        when(identRepository.save(any())).thenReturn(Mono.just(testident));

        StepVerifier.create(identService.saveIdentTilGruppe(STANDARD_IDENTER_1, 1L, Testident.Master.PDLF, null))
                .expectNext(testident)
                .verifyComplete();
    }

    @Test
    void slettTestident_ok() {

        var testident = Testident.builder()
                .ident(STANDARD_IDENTER_1)
                .gruppeId(GRUPPE_ID)
                .master(Testident.Master.PDL)
                .build();

        when(mapperFacade.map(any(), any())).thenReturn(new TestidentDTO());
        when(identRepository.findByIdent(STANDARD_IDENTER_1)).thenReturn(Mono.just(testident));
        when(transaksjonMappingRepository.deleteAllByIdent(STANDARD_IDENTER_1)).thenReturn(Mono.empty());
        when(bestillingService.slettBestillingByTestIdent(STANDARD_IDENTER_1)).thenReturn(Mono.empty());
        when(identRepository.deleteTestidentByIdent(STANDARD_IDENTER_1)).thenReturn(Mono.empty());
        when(personService.recyclePersoner(any())).thenReturn(Mono.empty());

        StepVerifier.create(identService.slettTestident(STANDARD_IDENTER_1))
                .expectNextCount(0)
                .verifyComplete();

        verify(identRepository).deleteTestidentByIdent(any());
    }
}