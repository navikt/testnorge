package no.nav.testnav.identpool.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdenterAvailServiceTest {

    private static final String IDENT_1 = "11111111111";
    private static final String IDENT_2 = "22222222222";

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private IdentRepository identRepository;

    @Mock
    private IdentGeneratorService identGeneratorService;

    @Mock
    private TpsMessagingConsumer tpsMessagingConsumer;

    @InjectMocks
    private IdenterAvailService identerAvailService;

    private static Ident getIdent(String ident) {
        return Ident.builder()
                .personidentifikator(ident)
                .build();
    }

    @Test
    void happyPathAvailSyntetisk_OK() {

        var request = HentIdenterRequest.builder()
                .antall(1)
                .foedtEtter(LocalDate.of(1960, 1, 1))
                .foedtFoer(LocalDate.of(2000, 12, 31))
                .identtype(Identtype.FNR)
                .kjoenn(Kjoenn.MANN)
                .rekvirertAv("tester")
                .syntetisk(true)
                .build();
        when(mapperFacade.map(request, HentIdenterRequest.class)).thenReturn(request);

        when(identGeneratorService.genererIdenter(request))
                .thenReturn(Set.of(IDENT_1, IDENT_2));
        when(identRepository.findByPersonidentifikatorIn(anySet()))
                .thenReturn(Flux.just(getIdent(IDENT_2)));

        identerAvailService.generateAndCheckIdenter(request, 10)
                .as(StepVerifier::create)
                .assertNext(ident -> assertThat(ident, is(IDENT_1)))
                .verifyComplete();
    }

    @Test
    void happyPathAvailIkkeSyntetisk_OK() {

        var request = HentIdenterRequest.builder()
                .antall(1)
                .foedtEtter(LocalDate.of(1960, 1, 1))
                .foedtFoer(LocalDate.of(2000, 12, 31))
                .identtype(Identtype.FNR)
                .kjoenn(Kjoenn.MANN)
                .rekvirertAv("tester")
                .syntetisk(false)
                .build();
        when(mapperFacade.map(request, HentIdenterRequest.class)).thenReturn(request);

        when(identGeneratorService.genererIdenter(request))
                .thenReturn(Set.of(IDENT_1, IDENT_2));
        when(identRepository.findByPersonidentifikatorIn(anySet()))
                .thenReturn(Flux.just(getIdent(IDENT_2)));
        when(tpsMessagingConsumer.getIdenterProdStatus(anySet()))
                .thenReturn(Flux.just(TpsStatusDTO.builder()
                        .ident(IDENT_1)
                        .inUse(false)
                        .build()));

        identerAvailService.generateAndCheckIdenter(request, 10)
                .as(StepVerifier::create)
                .assertNext(ident -> assertThat(ident, is(IDENT_1)))
                .verifyComplete();
    }
}