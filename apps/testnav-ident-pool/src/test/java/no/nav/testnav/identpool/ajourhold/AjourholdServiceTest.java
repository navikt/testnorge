package no.nav.testnav.identpool.ajourhold;

import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.repository.IdentRepository;
import no.nav.testnav.identpool.service.IdentGeneratorService;
import no.nav.testnav.identpool.util.PersonidentUtil;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AjourholdServiceTest {

    private final List<Ident> entities = new ArrayList<>();

    @Mock
    private TpsMessagingConsumer tpsMessagingConsumer;
    @Mock
    private IdentRepository identRepository;

    private AjourholdService ajourholdService;

    @BeforeEach
    void init() {
        entities.clear();
        ajourholdService = Mockito.spy(new AjourholdService(new IdentGeneratorService(), identRepository, tpsMessagingConsumer));

//        when(identRepository.save(any(Ident.class))).thenAnswer((Answer<Void>) invocationOnMock -> {
//            Ident ident = invocationOnMock.getArgument(0);
//            entities.add(ident);
//            return null;
//        });
    }

    @Test
    void genererIdenterForAarHvorIngenErLedige() {

        var year = LocalDate.now().getYear() - 50; // 50 years ago
        when(identRepository.findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(
                LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31),
                Identtype.FNR, Rekvireringsstatus.LEDIG, false)).thenReturn(Flux.empty());
        when(identRepository.existsByPersonidentifikator(anyString())).thenReturn(Mono.just(true));
        ajourholdService.checkAndGenerateForYear(year, Identtype.FNR, false)
                .as(StepVerifier::create)
                .assertNext(status -> status.equals("Ingen ledige identer for år: " + year + " og identtype: FNR"))
                .verifyComplete();
    }

    @Test
    void genererIdenterForAarHvorAlleErLedige() {
        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenAnswer((Answer<Set<TpsStatusDTO>>) invocationOnMock -> {
            Set<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().map(p -> new TpsStatusDTO(p, true)).collect(Collectors.toSet());
        });
        ajourholdService.checkAndGenerateForYear(1941, Identtype.DNR, false);
        verify(identRepository, times(entities.size())).save(any(Ident.class));
        assertThat(entities.size(), is(365 * 4));
        entities.forEach(entity -> assertThat(entity.getIdenttype(), is(Identtype.DNR)));
        entities.forEach(entity -> assertThat(PersonidentUtil.getIdentType(entity.getPersonidentifikator()), is(Identtype.DNR)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)));
    }

    @Test
    void genererIdenterForAarHvorAlleErLedigeBOST() {
        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenAnswer((Answer<Set<TpsStatusDTO>>) invocationOnMock -> {
            Set<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().map(p -> new TpsStatusDTO(p, true)).collect(Collectors.toSet());
        });
        ajourholdService.checkAndGenerateForYear(1941, Identtype.BOST, false);
        verify(identRepository, times(entities.size())).save(any(Ident.class));
        assertThat(entities.size(), is(365 * 4));
        entities.forEach(entity -> assertThat(entity.getIdenttype(), is(Identtype.BOST)));
        entities.forEach(entity -> assertThat(PersonidentUtil.getIdentType(entity.getPersonidentifikator()), is(Identtype.BOST)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)));
    }
}
