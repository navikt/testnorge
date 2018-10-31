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

    @Before
    public void setUp() {
        meldinger = new ArrayList<>();
        meldinger.add(new RsMeldingstype1Felter());

        identer = new ArrayList<>();
        identer.add("01010101010");
        identer.add("02020202020");
        identer.add("03030303030");

        brukteIdenter = new ArrayList<>();

        meldingerPerAarsakskode = new HashMap<>();
    }

    @Test
    public void shouldFindLevendeNordmann() throws IOException {
        aarsakskode = AarsakskoderTrans1.NAVNEENDRING_FOERSTE.getAarsakskode();
        meldingerPerAarsakskode.put(aarsakskode, 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        statusQuo = new HashMap<>();
        statusQuo.put(DATO_DO, "010203");
        statusQuo.put(STATSBORGER, "NORGE");
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq("01010101010"))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(DATO_DO, "");
        statusQuo.put(STATSBORGER, "NORGE");
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq("02020202020"))).thenReturn(statusQuo);

        eksisterendeIdenterService.behandleGenerellAarsak(meldinger, identer, brukteIdenter, aarsakskode, meldingerPerAarsakskode);

        Mockito.verify(aarsakskodeTilFeltnavnMapperService, times(2)).getStatusQuoFraAarsakskode(any(), any());
    }

    @Test
    public void shouldFindUgiftPerson() throws IOException {
        aarsakskode = AarsakskoderTrans1.VIGSEL.getAarsakskode();
        meldingerPerAarsakskode.put(aarsakskode, 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKode());
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq("01010101010"))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq("02020202020"))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKode());
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq("03030303030"))).thenReturn(statusQuo);

        eksisterendeIdenterService.behandleVigsel(meldinger, identer, brukteIdenter, aarsakskode, meldingerPerAarsakskode);

        Mockito.verify(aarsakskodeTilFeltnavnMapperService, times(3)).getStatusQuoFraAarsakskode(any(), any());
    }

    @Test
    public void shouldFindGiftPerson() throws IOException {
        aarsakskode = AarsakskoderTrans1.SKILSMISSE.getAarsakskode();
        meldingerPerAarsakskode.put(aarsakskode, 1);

        when(rand.nextInt(anyInt())).thenReturn(0);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.UGIFT.getSivilstandKode());
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq("01010101010"))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        statusQuo.put(FNR_RELASJON, "03030303030");
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq("02020202020"))).thenReturn(statusQuo);

        statusQuo = new HashMap<>();
        statusQuo.put(SIVILSTAND, KoderForSivilstand.GIFT.getSivilstandKode());
        statusQuo.put(FNR_RELASJON, "02020202020");
        when(aarsakskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(any(), eq("03030303030"))).thenReturn(statusQuo);

        eksisterendeIdenterService.behandleSeperasjonSkilsmisse(meldinger, identer, brukteIdenter, aarsakskode, meldingerPerAarsakskode);

        Mockito.verify(aarsakskodeTilFeltnavnMapperService, times(3)).getStatusQuoFraAarsakskode(any(), any());
    }
}
