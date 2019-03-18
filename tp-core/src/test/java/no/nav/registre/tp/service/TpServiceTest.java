package no.nav.registre.tp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.tp.consumer.rs.HodejegerenConsumer;
import no.nav.registre.tp.consumer.rs.TpSyntConsumer;
import no.nav.registre.tp.database.models.HistorikkComposityKey;
import no.nav.registre.tp.database.models.TForhold;
import no.nav.registre.tp.database.models.TForholdYtelseHistorikk;
import no.nav.registre.tp.database.models.TPerson;
import no.nav.registre.tp.database.models.TYtelse;
import no.nav.registre.tp.database.repository.TForholdRepository;
import no.nav.registre.tp.database.repository.TForholdYtelseHistorikkRepository;
import no.nav.registre.tp.database.repository.TPersonRepository;
import no.nav.registre.tp.database.repository.TYtelseRepository;
import no.nav.registre.tp.provider.rs.request.SyntetiseringsRequest;

@RunWith(SpringJUnit4ClassRunner.class)
public class TpServiceTest {

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

    @InjectMocks
    private TpService tpService;

    private SyntetiseringsRequest request = new SyntetiseringsRequest(1L, "q2", 3);

    @Before
    public void setUp() {
        ArrayList<String> fnrs = new ArrayList<>();
        fnrs.add("123");
        fnrs.add("132");
        fnrs.add("321");
        ArrayList<TYtelse> ytelser = new ArrayList<>();
        ytelser.add(TYtelse.builder()
                .ytelseId(3)
                .erGyldig("0")
                .funkYtelseId("10637556")
                .k_Ytelse_T("UKJENT")
                .endretAv("")
                .versjon("2")
                .build());
        ytelser.add(TYtelse.builder().build());
        ytelser.add(TYtelse.builder().build());
        when(hodejegerenConsumer.getFnrs(any())).thenReturn(fnrs);
        when(tPersonRepository.save(any())).thenReturn(TPerson.builder().personId(1).fnrFk("123").build());
        when(tForholdRepository.save(any())).thenReturn(TForhold.builder().personId(1).forholdId(2).endretAv("").build());
        when(tpSyntConsumer.getYtelser(3)).thenReturn(ytelser);
        when(tYtelseRepository.save(any())).thenReturn(TYtelse.builder()
                .ytelseId(3)
                .erGyldig("0")
                .funkYtelseId("10637556")
                .k_Ytelse_T("UKJENT")
                .endretAv("")
                .versjon("2")
                .build());
        when(tForholdYtelseHistorikkRepository.save(any())).thenReturn(new TForholdYtelseHistorikk(new HistorikkComposityKey(2, 3)));

        ArrayList<TForhold> forhold = new ArrayList<>();
        forhold.add(TForhold.builder().personId(1).forholdId(2).endretAv("").build());
        when(tForholdRepository.findAll()).thenReturn(forhold);
    }

    /**
     * Scenario: Alt fungerer slik det skal
     */
    @Test
    public void syntetiser() {
        tpService.syntetiser(request);
    }

    /**
     * Scenario: Feil antall ytelser i forhold til fnr
     */
    @Test
    public void syntetiserForMangeForhold() {
        ArrayList<TYtelse> ytelser = new ArrayList<>();
        ytelser.add(TYtelse.builder()
                .ytelseId(3)
                .erGyldig("0")
                .funkYtelseId("10637556")
                .k_Ytelse_T("UKJENT")
                .endretAv("")
                .versjon("2")
                .build());
        ytelser.add(TYtelse.builder().build());
        ytelser.add(TYtelse.builder().build());
        ytelser.add(TYtelse.builder().build());
        ytelser.add(TYtelse.builder().build());
        ytelser.add(TYtelse.builder().build());
        ytelser.add(TYtelse.builder().build());
        ytelser.add(TYtelse.builder().build());
        when(tpSyntConsumer.getYtelser(3)).thenReturn(ytelser);
        tpService.syntetiser(request);
    }

    /**
     * Scenario: Ikke nok ytelser ble syntetisert
     */
    @Test(expected = HttpServerErrorException.class)
    public void syntetiserForMangeFNR() {
        ArrayList<String> fnrs = new ArrayList<>();
        fnrs.add("123");
        fnrs.add("132");
        fnrs.add("321");
        fnrs.add("312");
        fnrs.add("213");
        fnrs.add("231");
        when(hodejegerenConsumer.getFnrs(any())).thenReturn(fnrs);

        tpService.syntetiser(request);
    }

    @Test
    public void getForhold() {
        List<TForhold> forhold = tpService.getForhold();
        TForhold tForhold = forhold.get(0);
        assertEquals(1, (int) tForhold.getPersonId());
        assertEquals(2, (int) tForhold.getForholdId());
    }
}