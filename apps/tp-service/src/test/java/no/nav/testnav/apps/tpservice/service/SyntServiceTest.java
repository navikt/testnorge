package no.nav.testnav.apps.tpservice.service;

import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.testnav.apps.tpservice.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.testnav.apps.tpservice.consumer.rs.TpSyntConsumer;
import no.nav.testnav.apps.tpservice.database.models.HistorikkComposityKey;
import no.nav.testnav.apps.tpservice.database.models.TForholdYtelseHistorikk;
import no.nav.testnav.apps.tpservice.database.models.TPerson;
import no.nav.testnav.apps.tpservice.database.models.TYtelse;
import no.nav.testnav.apps.tpservice.database.models.TForhold;
import no.nav.testnav.apps.tpservice.database.repository.TForholdRepository;
import no.nav.testnav.apps.tpservice.database.repository.TForholdYtelseHistorikkRepository;
import no.nav.testnav.apps.tpservice.database.repository.TPersonRepository;
import no.nav.testnav.apps.tpservice.database.repository.TYtelseRepository;
import no.nav.testnav.apps.tpservice.provider.rs.request.SyntetiseringsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class SyntServiceTest {

    private static final Integer MIN_AGE = 13;

    @Mock
    private TForholdYtelseHistorikkRepository tForholdYtelseHistorikkRepository;

    @Mock
    private TForholdRepository tForholdRepository;

    @Mock
    private TPersonRepository tPersonRepository;

    @Mock
    private TYtelseRepository tYtelseRepository;

    @Mock
    private TpSyntConsumer tpSyntConsumer;

    @Mock
    private HodejegerenConsumer hodejegerenConsumer;

    @Mock
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @InjectMocks
    private SyntService syntService;

    private SyntetiseringsRequest request = new SyntetiseringsRequest(1L, "q2", 3);

    private TYtelse ytelse1;

    @Before
    public void setUp() {
        var fnrs = new ArrayList<>(Arrays.asList("123", "132", "321"));
        ytelse1 = new TYtelse();
        ytelse1.setYtelseId(3);
        ytelse1.setErGyldig("0");
        ytelse1.setFunkYtelseId("10637556");
        ytelse1.setKYtelseT("UKJENT");
        ytelse1.setEndretAv("");
        ytelse1.setVersjon("2");

        var expectedForhold = new TForhold();
        expectedForhold.setPersonId(1);
        expectedForhold.setForholdId(2);
        expectedForhold.setEndretAv("");

        var person = new TPerson();
        person.setPersonId(1);
        person.setFnrFk("123");

        when(hodejegerenConsumer.getLevende(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(fnrs);
        when(tPersonRepository.save(any())).thenReturn(person);
        when(tForholdRepository.save(any())).thenReturn(expectedForhold);
        when(tYtelseRepository.save(any())).thenReturn(ytelse1);
        when(tForholdYtelseHistorikkRepository.save(any())).thenReturn(new TForholdYtelseHistorikk(new HistorikkComposityKey(2, 3)));
        when(hodejegerenHistorikkConsumer.saveHistory(any())).thenReturn(fnrs);
    }

    /**
     * Scenario: Feil antall ytelser i forhold til fnr
     */
    @Test
    public void syntetiserForMangeForhold() {
        var ytelser = new ArrayList<>(Arrays.asList(
                ytelse1,
                new TYtelse(),
                new TYtelse(),
                new TYtelse(),
                new TYtelse(),
                new TYtelse(),
                new TYtelse(),
                new TYtelse()
        ));

        when(tpSyntConsumer.getSyntYtelser(3)).thenReturn(ytelser);

        syntService.syntetiser(request);

        verify(tForholdRepository, times(3)).save(any());
    }

    /**
     * Scenario: Alt fungerer slik det skal
     */
    @Test
    public void syntetiser() {
        var ytelser = new ArrayList<>(
                Arrays.asList(
                        ytelse1,
                        new TYtelse(),
                        new TYtelse()
                ));

        when(tpSyntConsumer.getSyntYtelser(3)).thenReturn(ytelser);

        syntService.syntetiser(request);

        verify(tForholdRepository, times(3)).save(any());
    }

    /**
     * Scenario: Ikke nok ytelser ble syntetisert
     */
    @Test(expected = HttpServerErrorException.class)
    public void syntetiserForMangeFNR() {
        var fnrs = new ArrayList<>(Arrays.asList(
                "123",
                "132",
                "321",
                "312",
                "213",
                "231"
        ));
        when(hodejegerenConsumer.getLevende(anyLong(), anyString(), anyInt(), eq(MIN_AGE))).thenReturn(fnrs);

        syntService.syntetiser(request);

        verify(tForholdRepository, times(0)).save(any());
    }

    @Test
    public void getForhold() {
        var forhold = new TForhold();
        forhold.setPersonId(1);
        forhold.setForholdId(2);
        forhold.setEndretAv("");

        var forholdListe = new ArrayList<>(Collections.singletonList(forhold));
        when(tForholdRepository.findAll()).thenReturn(forholdListe);//

        var response = syntService.getForhold();

        var tForhold = response.get(0);

        verify(tForholdRepository).findAll();
        assertThat((int) tForhold.getPersonId()).isEqualTo(1);
        assertThat((int) tForhold.getForholdId()).isEqualTo(2);
    }

}
