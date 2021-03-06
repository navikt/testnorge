package no.nav.identpool.ajourhold;

import io.micrometer.core.instrument.Counter;
import no.nav.identpool.consumers.TpsfConsumer;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.service.IdentGeneratorService;
import no.nav.identpool.service.TpsfService;
import no.nav.identpool.test.mockito.MockitoExtension;
import no.nav.identpool.util.PersonidentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AjourholdServiceTest {

    @Mock
    private TpsfService tpsfService;

    @Mock
    private IdentRepository identRepository;

    @Mock
    private TpsfConsumer tpsfConsumer;

    private AjourholdService ajourholdService;

    private final List<Ident> entities = new ArrayList<>();

    @Mock
    private Counter counter;

    @BeforeEach
    void init() {
        entities.clear();
        ajourholdService = spy(new AjourholdService(identRepository, new IdentGeneratorService(), tpsfService, tpsfConsumer, counter));
        ReflectionTestUtils.setField(ajourholdService, "current", LocalDate.now());

        when(identRepository.save(any(Ident.class))).thenAnswer((Answer<Void>) invocationOnMock -> {
            Ident ident = invocationOnMock.getArgument(0);
            entities.add(ident);
            return null;
        });
    }

    @Test
    void identerBlirGenerertForHvertAar() {
        doNothing().when(ajourholdService).generateForYear(anyInt(), eq(Identtype.FNR), anyInt(), anyBoolean());
        doNothing().when(ajourholdService).generateForYear(anyInt(), eq(Identtype.DNR), anyInt(), anyBoolean());
        doNothing().when(ajourholdService).generateForYear(anyInt(), eq(Identtype.BOST), anyInt(), anyBoolean());
        ajourholdService.checkCriticalAndGenerate();
        int number = 224;
        if (LocalDate.now().getDayOfYear() == 1) {
            number -= 1;
        }
        verify(ajourholdService, times(number)).checkAndGenerateForDate(any(), eq(Identtype.FNR), anyBoolean());
        verify(ajourholdService, times(number)).checkAndGenerateForDate(any(), eq(Identtype.DNR), anyBoolean());
        verify(ajourholdService, times(number)).checkAndGenerateForDate(any(), eq(Identtype.BOST), anyBoolean());
    }

    @Test
    void genererIdenterForAarHvorIngenErLedige() {
        when(tpsfService.checkIdentsInTps(anySet())).thenAnswer((Answer<Set<TpsStatus>>) invocationOnMock -> {
            Set<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().map(p -> new TpsStatus(p, false)).collect(Collectors.toSet());
        });
        ajourholdService.generateForYear(1941, Identtype.FNR, 365 * 4, false);
        verify(identRepository, times(entities.size())).save(any(Ident.class));
        assertThat(entities.size(), is(365 * 4));
        entities.forEach(entity -> assertThat(entity.getIdenttype(),  is(Identtype.FNR)));
        entities.forEach(entity -> assertThat(PersonidentUtil.getIdentType(entity.getPersonidentifikator()), is(Identtype.FNR)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.LEDIG)));
    }

    @Test
    void genererIdenterForAarHvorAlleErLedige() {
        when(tpsfService.checkIdentsInTps(anySet())).thenAnswer((Answer<Set<TpsStatus>>) invocationOnMock -> {
            Set<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().map(p -> new TpsStatus(p, true)).collect(Collectors.toSet());
        });
        ajourholdService.generateForYear(1941, Identtype.DNR, 0, false);
        verify(identRepository, times(entities.size())).save(any(Ident.class));
        assertThat(entities.size(), is(365 * 4));
        entities.forEach(entity -> assertThat(entity.getIdenttype(), is(Identtype.DNR)));
        entities.forEach(entity -> assertThat(PersonidentUtil.getIdentType(entity.getPersonidentifikator()), is(Identtype.DNR)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)));
    }

    @Test
    void genererIdenterForAarHvorAlleErLedigeBOST() {
        when(tpsfService.checkIdentsInTps(anySet())).thenAnswer((Answer<Set<TpsStatus>>) invocationOnMock -> {
            Set<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().map(p -> new TpsStatus(p, true)).collect(Collectors.toSet());
        });
        ajourholdService.generateForYear(1941, Identtype.BOST, 0, false);
        verify(identRepository, times(entities.size())).save(any(Ident.class));
        assertThat(entities.size(), is(365 * 4));
        entities.forEach(entity -> assertThat(entity.getIdenttype(), is(Identtype.BOST)));
        entities.forEach(entity -> assertThat(PersonidentUtil.getIdentType(entity.getPersonidentifikator()), is(Identtype.BOST)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)));
    }

    @Test
    void generererIdenterFraAarTilDatoMidtISammeAar() {
        when(tpsfService.checkIdentsInTps(anySet())).thenAnswer((Answer<Set<TpsStatus>>) invocationOnMock -> {
            Set<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().map(p -> new TpsStatus(p, true)).collect(Collectors.toSet());
        });
        LocalDate dayOfYear = LocalDate.of(1941, 4, 10);
        ReflectionTestUtils.setField(ajourholdService, "current", dayOfYear);
        ajourholdService.generateForYear(1941, Identtype.DNR, 0, false);
        verify(identRepository, times(entities.size())).save(any(Ident.class));
        assertThat(entities.size(), is(dayOfYear.minusDays(1).getDayOfYear() * 4));
        entities.forEach(entity -> assertThat(entity.getIdenttype(), is(Identtype.DNR)));
        entities.forEach(entity -> assertThat(PersonidentUtil.getIdentType(entity.getPersonidentifikator()), is(Identtype.DNR)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)));
    }

    @Test
    void generererIdenterFraAarTilDatoMidtISammeAarBOST() {
        when(tpsfService.checkIdentsInTps(anySet())).thenAnswer((Answer<Set<TpsStatus>>) invocationOnMock -> {
            Set<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().map(p -> new TpsStatus(p, true)).collect(Collectors.toSet());
        });
        LocalDate dayOfYear = LocalDate.of(1941, 4, 10);
        ReflectionTestUtils.setField(ajourholdService, "current", dayOfYear);
        ajourholdService.generateForYear(1941, Identtype.BOST, 0, false);
        verify(identRepository, times(entities.size())).save(any(Ident.class));
        assertThat(entities.size(), is(dayOfYear.minusDays(1).getDayOfYear() * 4));
        entities.forEach(entity -> assertThat(entity.getIdenttype(), is(Identtype.BOST)));
        entities.forEach(entity -> assertThat(PersonidentUtil.getIdentType(entity.getPersonidentifikator()), is(Identtype.BOST)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)));
    }
}
