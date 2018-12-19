package no.nav.identpool.ajourhold.service;

import no.nav.identpool.ajourhold.util.IdentDistribusjon;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.service.IdentMQService;
import no.nav.identpool.test.mockito.MockitoExtension;
import no.nav.identpool.util.PersonidentifikatorUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdentDBServiceTest {

    @Mock
    private IdentMQService mqService;

    @Mock
    private IdentRepository identRepository;

    @Mock
    private IdentDistribusjon identDistribusjon;

    private IdentDBService identService;

    private List<Ident> entities = new ArrayList<>();

    @BeforeEach
    void init() {
        entities.clear();
        identService = spy(new IdentDBService(mqService, identRepository, identDistribusjon));
        identService.current = LocalDate.now();
        when(identDistribusjon.antallPersonerPerDagPerAar(anyInt())).thenReturn(3);

        when(identRepository.saveAll(anyIterable())).thenAnswer((Answer<Void>) invocationOnMock -> {
            List<Ident> pins = invocationOnMock.getArgument(0);
            entities.addAll(pins);
            return null;
        });
    }

    //FIXME Skrive om
    @Test
    void identerBlirGenerertForHvertAar() {
        doNothing().when(identService).generateForYear(anyInt(), eq(Identtype.FNR));
        doNothing().when(identService).generateForYear(anyInt(), eq(Identtype.DNR));
        identService.checkCriticalAndGenerate();
        int number = 111;
        if (LocalDate.now().getDayOfYear() == 1) {
            number -= 1;
        }
        verify(identService, times(number)).checkAndGenerateForDate(any(), eq(Identtype.FNR));
        verify(identService, times(number)).checkAndGenerateForDate(any(), eq(Identtype.DNR));
    }

    @Test
    void genererIdenterForAarHvorIngenErLedige() {
        when(mqService.checkIdentsInTps(anyList())).thenAnswer((Answer<Map<String, Boolean>>) invocationOnMock -> {
            List<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().collect(Collectors.toMap(Object::toString, pin -> Boolean.FALSE));
        });
        identService.generateForYear(1941, Identtype.FNR);
        verify(identRepository, times(2)).saveAll(anyIterable());
        assertThat(entities.size(), is(365 * 2 * 3));
        entities.forEach(entity -> assertThat(entity.getIdenttype(), is(Identtype.FNR)));
        entities.forEach(entity -> assertThat(PersonidentifikatorUtil.getPersonidentifikatorType(entity.getPersonidentifikator()), is(Identtype.FNR)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.LEDIG)));
    }

    @Test
    void genererIdenterForAarHvorAlleErLedige() {
        when(mqService.checkIdentsInTps(anyList())).thenAnswer((Answer<Map<String, Boolean>>) invocationOnMock -> {
            List<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().collect(Collectors.toMap(Object::toString, pin -> Boolean.TRUE));
        });
        identService.generateForYear(1941, Identtype.DNR);
        verify(identRepository, times(2)).saveAll(anyIterable());
        assertThat(entities.size(), is(365 * 2 * 3));
        entities.forEach(entity -> assertThat(entity.getIdenttype(), is(Identtype.DNR)));
        entities.forEach(entity -> assertThat(PersonidentifikatorUtil.getPersonidentifikatorType(entity.getPersonidentifikator()), is(Identtype.DNR)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)));

    }

    @Test
    void generererIdenterFraAarTilDatoMidtISammeAar() {
        when(mqService.checkIdentsInTps(anyList())).thenAnswer((Answer<Map<String, Boolean>>) invocationOnMock -> {
            List<String> pins = invocationOnMock.getArgument(0);
            return pins.stream().collect(Collectors.toMap(Object::toString, pin -> Boolean.TRUE));
        });
        LocalDate dayOfYear = LocalDate.of(1941, 4, 10);
        identService.current = dayOfYear;
        identService.generateForYear(1941, Identtype.DNR);
        verify(identRepository, times(2)).saveAll(anyIterable());
        assertThat(entities.size(), is(dayOfYear.minusDays(1).getDayOfYear() * 2 * 3));
        entities.forEach(entity -> assertThat(entity.getIdenttype(), is(Identtype.DNR)));
        entities.forEach(entity -> assertThat(PersonidentifikatorUtil.getPersonidentifikatorType(entity.getPersonidentifikator()), is(Identtype.DNR)));
        entities.forEach(entity -> assertThat(entity.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)));
    }
}
