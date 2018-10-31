package no.nav.registre.hodejegeren.service;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.*;

import static no.nav.registre.hodejegeren.service.AarsakskodeTilFeltnavnMapperService.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EksisterendeIdenterServiceTest {

    @Mock
    AarsakskodeTilFeltnavnMapperService aarsakskodeTilFeltnavnMapperService;

    @Mock
    Random rand;

    @InjectMocks
    EksisterendeIdenterService eksisterendeIdenterService;

    private List<RsMeldingstype> meldinger;
    private List<String> identer;
    private List<String> brukteIdenter;
    private String aarsakskode;
    private Map<String, Integer> meldingerPerAarsakskode;
    private Map<String, String> statusQuo;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private String fnr3 = "03030303030";

    @Before
    public void setUp() {
        meldinger = new ArrayList<>();
        meldinger.add(new RsMeldingstype1Felter());

        identer = new ArrayList<>();
        identer.add(fnr1);
        identer.add(fnr2);
        identer.add(fnr3);

        brukteIdenter = new ArrayList<>();

        meldingerPerAarsakskode = new HashMap<>();
    }

    @Test
    public void shouldFindLevendeNordmannAndUpdateBrukteIdenter() throws IOException {
        aarsakskode = AarsakskoderTrans1.NAVNEENDRING_FOERSTE.getAarsakskode();
        meldingerPerAarsakskode.put(aarsakskode, 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettLevendeNordmennMock();

        eksisterendeIdenterService.behandleGenerellAarsak(meldinger, identer, brukteIdenter, aarsakskode, meldingerPerAarsakskode);

        Mockito.verify(aarsakskodeTilFeltnavnMapperService, times(2)).getStatusQuoFraAarsakskode(any(), any());
        assertEquals(1, meldinger.size());
        assertEquals(fnr2.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getFodselsdato());

        assertEquals(1, brukteIdenter.size());
        assertEquals(fnr2, brukteIdenter.get(0));
    }

    @Test
    public void shouldFindUgiftPersonAndCreateVigselsmelding() throws IOException {
        aarsakskode = AarsakskoderTrans1.VIGSEL.getAarsakskode();
        meldingerPerAarsakskode.put(aarsakskode, 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettMultipleUgifteIdenterMock();

        eksisterendeIdenterService.behandleVigsel(meldinger, identer, brukteIdenter, aarsakskode, meldingerPerAarsakskode);

        Mockito.verify(aarsakskodeTilFeltnavnMapperService, times(3)).getStatusQuoFraAarsakskode(any(), any());
        assertEquals(2, meldinger.size());
        assertEquals(fnr3.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(0)).getEktefellePartnerFdato());
        assertEquals(fnr1.substring(0, 6), ((RsMeldingstype1Felter) meldinger.get(1)).getEktefellePartnerFdato());
    }


    @Test
    public void shouldFindGiftPersonAndCreateSkilsmissemelding() throws IOException {
        aarsakskode = AarsakskoderTrans1.SKILSMISSE.getAarsakskode();
        meldingerPerAarsakskode.put(aarsakskode, 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettMultipleGifteIdenterMock();

        eksisterendeIdenterService.behandleSeperasjonSkilsmisse(meldinger, identer, brukteIdenter, aarsakskode, meldingerPerAarsakskode);

        Mockito.verify(aarsakskodeTilFeltnavnMapperService, times(3)).getStatusQuoFraAarsakskode(any(), any());
        assertEquals(2, meldinger.size());
    }

    @Test
    public void shouldFindPartnerOfDoedsmeldingIdent() throws IOException {
        aarsakskode = AarsakskoderTrans1.DOEDSMELDING.getAarsakskode();
        meldingerPerAarsakskode.put(aarsakskode, 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        opprettEkteparMock();

        eksisterendeIdenterService.behandleDoedsmelding(meldinger, identer, brukteIdenter, aarsakskode, meldingerPerAarsakskode);

        Mockito.verify(aarsakskodeTilFeltnavnMapperService, times(2)).getStatusQuoFraAarsakskode(any(), any());
        assertEquals(KoderForSivilstand.ENKE_ENKEMANN.getSivilstandKode(), ((RsMeldingstype1Felter) meldinger.get(1)).getSivilstand());
    }

    private void opprettLevendeNordmennMock() throws IOException {
        statusQuo = new HashMap<>();
        statusQuo.put(DATO_DO, "010203");
        statusQuo.put(STATSBORGER, "NORGE");
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(DATO_DO, "");
        statusQuo.put(STATSBORGER, "NORGE");
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(fnr2))).thenReturn(statusQuo);
    }

    private void opprettMultipleUgifteIdenterMock() throws IOException {
        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKode());
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(fnr2))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKode());
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(fnr3))).thenReturn(statusQuo);
    }

    private void opprettMultipleGifteIdenterMock() throws IOException {
        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKode());
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        statusQuo.put(FNR_RELASJON, fnr3);
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(fnr2))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        statusQuo.put(FNR_RELASJON, fnr2);
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(fnr3))).thenReturn(statusQuo);
    }

    private void opprettEkteparMock() throws IOException {
        statusQuo = new HashMap<>();
        statusQuo.put(STATSBORGER, "NORGE");
        statusQuo.put(DATO_DO, "");
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        statusQuo.put(FNR_RELASJON, fnr2);
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(fnr1))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(STATSBORGER, "NORGE");
        statusQuo.put(DATO_DO, "");
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        statusQuo.put(FNR_RELASJON, fnr1);
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq(fnr2))).thenReturn(statusQuo);
    }
}
