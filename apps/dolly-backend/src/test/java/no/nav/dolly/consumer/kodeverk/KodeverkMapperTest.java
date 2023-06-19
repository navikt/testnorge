package no.nav.dolly.consumer.kodeverk;

import no.nav.dolly.consumer.kodeverk.domain.KodeverkBetydningerResponse.Beskrivelse;
import no.nav.dolly.consumer.kodeverk.domain.KodeverkBetydningerResponse.Betydning;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;

public class KodeverkMapperTest {

    private static final String STANDARD_KODE = "1234";
    private static final String STANDARD_KODE_2 = "5678";
    private static final String STANDARD_TERM = "term";
    private static final String STANDARD_TERM_2 = "term2";
    private static final LocalDate STANDARD_GYLDIGFRA = LocalDate.of(1, 1, 1);
    private static final LocalDate STANDARD_GYLDIGTIL = LocalDate.of(1, 1, 2);
    private static final String STANDARD_KODEVERK_NAVN = "navn";
    private static final String KODE_BOKMAAL = "nb";

    private KodeverkMapper kodeverkMapper = new KodeverkMapper();
    private Map<String, List<Betydning>> betydninger = new HashMap<>();
    private Map<String, Beskrivelse> beskrivelser = new HashMap<>();
    private Beskrivelse standardBeskrivelse = Beskrivelse.builder().tekst("tekst").term(STANDARD_TERM).build();
    private Betydning standardBetydning = Betydning.builder().beskrivelser(beskrivelser).gyldigTil(STANDARD_GYLDIGTIL).gyldigFra(STANDARD_GYLDIGFRA).build();

    @BeforeEach
    public void setup() {
        betydninger.put(STANDARD_KODE, List.of(standardBetydning));
        beskrivelser.put(KODE_BOKMAAL, standardBeskrivelse);
        standardBetydning.setBeskrivelser(beskrivelser);
    }

    @Test
    public void mapBetydningToAdjustedKodeverk_gjorOmKodeverkBetydningerTilKodeverkAdjustedMedValueOgLabel() {
        Beskrivelse beskrivelse2 = Beskrivelse.builder().tekst("tekst").term(STANDARD_TERM_2).build();
        Map<String, Beskrivelse> beskrivelser2 = new HashMap<>();
        beskrivelser2.put(KODE_BOKMAAL, beskrivelse2);

        Betydning betydning2 = Betydning.builder().beskrivelser(beskrivelser2).gyldigFra(STANDARD_GYLDIGFRA).gyldigTil(STANDARD_GYLDIGTIL).build();
        betydninger.put(STANDARD_KODE_2, List.of(betydning2));

        KodeverkAdjusted kodeverk = kodeverkMapper.mapBetydningToAdjustedKodeverk("navn", betydninger);

        assertThat(kodeverk.getKoder(), hasItem(allOf(
                hasProperty("value", equalTo(STANDARD_KODE)),
                hasProperty("label", equalTo(STANDARD_TERM)),
                hasProperty("gyldigTil", equalTo(STANDARD_GYLDIGTIL)),
                hasProperty("gyldigFra", equalTo(STANDARD_GYLDIGFRA))
        )));

        assertThat(kodeverk.getKoder(), hasItem(allOf(
                hasProperty("value", equalTo(STANDARD_KODE_2)),
                hasProperty("label", equalTo(STANDARD_TERM_2)),
                hasProperty("gyldigTil", equalTo(STANDARD_GYLDIGTIL)),
                hasProperty("gyldigFra", equalTo(STANDARD_GYLDIGFRA))
        )));

        assertThat(kodeverk.getName(), is("navn"));
    }

    @Test
    public void mapBetydningToAdjustedKodeverk_tomListeAvBetydningerGirKodeverkadjustedMedTomListeAvKoder() {
        KodeverkAdjusted kodeverk = kodeverkMapper.mapBetydningToAdjustedKodeverk(STANDARD_KODEVERK_NAVN, new HashMap<>());

        assertThat(kodeverk.getKoder().isEmpty(), is(true));
        assertThat(kodeverk.getName(), is(STANDARD_KODEVERK_NAVN));
    }

    @Test
    public void mapBetydningToAdjustedKodeverk_nullVerdiForBetydningerGirKodeverkadjustedMedTomListeAvKoder() {
        KodeverkAdjusted kodeverk = kodeverkMapper.mapBetydningToAdjustedKodeverk(STANDARD_KODEVERK_NAVN, null);

        assertThat(kodeverk.getKoder().isEmpty(), is(true));
        assertThat(kodeverk.getName(), is(STANDARD_KODEVERK_NAVN));
    }
}