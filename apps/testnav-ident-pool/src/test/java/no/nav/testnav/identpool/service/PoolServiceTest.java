package no.nav.testnav.identpool.service;

import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static no.nav.testnav.identpool.domain.Rekvireringsstatus.I_BRUK;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.LEDIG;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PoolServiceTest {

    private static final String FNR1 = "01010101010";
    private static final String FNR2 = "02020202020";

    @Mock
    private IdentRepository identRepository;

    @Mock
    private DatabaseService databaseService;

    @Mock
    private IdenterAvailService identerAvailService;

    @InjectMocks
    private PoolService poolService;

    @Test
    void rekvirerNyeIdenter_finnesIDatabase() {

        var request = HentIdenterRequest.builder()
                .antall(1)
                .foedtEtter(LocalDate.of(1990, 1, 1))
                .foedtFoer(LocalDate.of(2020, 1, 1))
                .kjoenn(Kjoenn.MANN)
                .identtype(Identtype.FNR)
                .syntetisk(true)
                .build();

        when(databaseService.hentLedigeIdenterFraDatabase(request))
                .thenReturn(Flux.just(
                        prepIdent(FNR1, LEDIG, true),
                        prepIdent(FNR2, LEDIG, true)));

        when(identerAvailService.generateAndCheckIdenter(eq(request), any(Integer.class)))
                .thenReturn(Flux.just(FNR1, FNR2));

        when(identRepository.save(any(Ident.class)))
                .thenReturn(Mono.just(prepIdent(FNR1, LEDIG, true)))
                .thenReturn(Mono.just(prepIdent(FNR2, LEDIG, true)));

        poolService.allocateIdenter(request)
                .as(StepVerifier::create)
                .expectNext(List.of(FNR1))
                .verifyComplete();

        verify(identerAvailService).generateAndCheckIdenter(any(HentIdenterRequest.class), any(Integer.class));
    }

    @Test
    void rekvirerNyeIdenter_maaGenereres() {

        var request = HentIdenterRequest.builder()
                .antall(1)
                .foedtEtter(LocalDate.of(1990, 1, 1))
                .foedtFoer(LocalDate.of(2020, 1, 1))
                .kjoenn(Kjoenn.MANN)
                .identtype(Identtype.FNR)
                .syntetisk(true)
                .build();

        when(databaseService.hentLedigeIdenterFraDatabase(request))
                .thenReturn(Flux.empty());

        when(identerAvailService.generateAndCheckIdenter(eq(request), any(Integer.class)))
                .thenReturn(Flux.just(FNR1, FNR2));

        when(identRepository.save(any(Ident.class)))
                .thenReturn(Mono.just(prepIdent(FNR1, LEDIG, true)))
                .thenReturn(Mono.just(prepIdent(FNR2, LEDIG, true)))
                .thenReturn(Mono.just(prepIdent(FNR1, I_BRUK, true)));

        poolService.allocateIdenter(request)
                .as(StepVerifier::create)
                .expectNext(List.of(FNR1))
                .verifyComplete();
    }

    private static Ident prepIdent(String ident, Rekvireringsstatus status, boolean syntetisk) {

        return Ident.builder()
                .personidentifikator(ident)
                .rekvireringsstatus(status)
                .syntetisk(syntetisk)
                .build();
    }
}