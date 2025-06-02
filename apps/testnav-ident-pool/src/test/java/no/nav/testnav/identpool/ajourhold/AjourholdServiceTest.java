package no.nav.testnav.identpool.ajourhold;

import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.repository.IdentRepository;
import no.nav.testnav.identpool.util.PersonidentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
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
        ajourholdService = Mockito.spy(new AjourholdService(identRepository, tpsMessagingConsumer));

        when(identRepository.save(any(Ident.class))).thenAnswer((Answer<Void>) invocationOnMock -> {
            Ident ident = invocationOnMock.getArgument(0);
            entities.add(ident);
            return null;
        });
    }

    @Test
    void genererIdenterForAarHvorIngenErLedige() {
        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenAnswer((Answer<Set<TpsStatusDTO>>) invocationOnMock -> {
            Set<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().map(p -> new TpsStatusDTO(p, false)).collect(Collectors.toSet());
        });
//        ajourholdService.generateForYear(1941, Identtype.FNR, 365 * 4, false);
        verify(identRepository, times(entities.size())).save(any(Ident.class));
        assertThat(entities.size(), is(365 * 4));
        entities.forEach(entity -> assertThat(entity.getIdenttype(), is(Identtype.FNR)));
        entities.forEach(entity -> assertThat(PersonidentUtil.getIdentType(entity.getPersonidentifikator()), is(Identtype.FNR)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.LEDIG)));
    }

    @Test
    void genererIdenterForAarHvorAlleErLedige() {
        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenAnswer((Answer<Set<TpsStatusDTO>>) invocationOnMock -> {
            Set<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().map(p -> new TpsStatusDTO(p, true)).collect(Collectors.toSet());
        });
//        ajourholdService.generateForYear(1941, Identtype.DNR, 0, false);
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
//        ajourholdService.generateForYear(1941, Identtype.BOST, 0, false);
        verify(identRepository, times(entities.size())).save(any(Ident.class));
        assertThat(entities.size(), is(365 * 4));
        entities.forEach(entity -> assertThat(entity.getIdenttype(), is(Identtype.BOST)));
        entities.forEach(entity -> assertThat(PersonidentUtil.getIdentType(entity.getPersonidentifikator()), is(Identtype.BOST)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)));
    }
}
