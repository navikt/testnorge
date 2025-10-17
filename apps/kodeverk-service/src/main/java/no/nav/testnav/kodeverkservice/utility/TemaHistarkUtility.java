package no.nav.testnav.kodeverkservice.utility;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkAdjustedDTO;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.GYLDIG_FRA;
import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.GYLDIG_TIL;
import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.TEMAHISTARK;

@UtilityClass
public class TemaHistarkUtility {

    private static final List<Map<String, String>> temaHistark = new ArrayList<>();

    static {
        temaHistark.add(Map.of("AAP", "Arbeidsavklaringspenger"));
        temaHistark.add(Map.of("AAR", "Aa-registeret"));
        temaHistark.add(Map.of("AFP", "Avtalefestet pensjon (AFP)"));
        temaHistark.add(Map.of("AGR", "Ajourhold - Grunnopplysninger"));
        temaHistark.add(Map.of("ALP", "Alderspensjon"));
        temaHistark.add(Map.of("APP", "Avtalefestet pensjon Privat"));
        temaHistark.add(Map.of("BAP", "Barnepensjon"));
        temaHistark.add(Map.of("BAR", "Barnetrygd"));
        temaHistark.add(Map.of("BID", "Bidrag"));
        temaHistark.add(Map.of("BIL", "Bil"));
        temaHistark.add(Map.of("DAG", "Dagpenger"));
        temaHistark.add(Map.of("ENF", "Enslig forsørger"));
        temaHistark.add(Map.of("FAP", "Familiepleier"));
        temaHistark.add(Map.of("FAR", "Farskap"));
        temaHistark.add(Map.of("FEI", "Feilutbetaling(Arenaytelser)"));
        temaHistark.add(Map.of("FOR", "Foreldre- og svangerskapspenger"));
        temaHistark.add(Map.of("FOS", "Forsikring"));
        temaHistark.add(Map.of("GEN", "Generell"));
        temaHistark.add(Map.of("GJP", "Etterlattepensjon/Gjenlevendeytelse"));
        temaHistark.add(Map.of("GRA", "Gravferdsstønad"));
        temaHistark.add(Map.of("GRU", "Grunn- og hjelpestønad"));
        temaHistark.add(Map.of("GYR", "Gammel yrkesskade"));
        temaHistark.add(Map.of("HEL", "Helsetjenester og ortopediske hjelpemidler"));
        temaHistark.add(Map.of("HJE", "Hjelpemidler"));
        temaHistark.add(Map.of("IAR", "Inkluderende arbeidsliv"));
        temaHistark.add(Map.of("IND", "Individstønad"));
        temaHistark.add(Map.of("KON", "Kontantstøtte"));
        temaHistark.add(Map.of("KPE", "Krigspensjon"));
        temaHistark.add(Map.of("KTR", "Kontroll"));
        temaHistark.add(Map.of("MAA", "Maritimt Aa-register"));
        temaHistark.add(Map.of("MED", "Medlemskap"));
        temaHistark.add(Map.of("MOB", "Mobilitetsfremmende stønad"));
        temaHistark.add(Map.of("OMP", "Omsorgspoeng"));
        temaHistark.add(Map.of("OMS", "Omsorgspenger, pleiepenger og opplæringspenger"));
        temaHistark.add(Map.of("OPA", "Oppfølging arbeidsgiver"));
        temaHistark.add(Map.of("OPP", "Oppfølging person"));
        temaHistark.add(Map.of("PER", "Permitteringer og masseoppsigelser"));
        temaHistark.add(Map.of("PFI", "Pensjonstrygden for fiskere"));
        temaHistark.add(Map.of("PNS", "Pensjon Norges Statsbaner"));
        temaHistark.add(Map.of("PRS", "Pensjonstrygden for reindriftssamer"));
        temaHistark.add(Map.of("PSJ", "Pensjonstrygden for sjømenn"));
        temaHistark.add(Map.of("PSK", "Pensjonstrygden for skogsarbeidere"));
        temaHistark.add(Map.of("PSP", "Pensjon Statens Pensjonskasse"));
        temaHistark.add(Map.of("REH", "Rehabilitering"));
        temaHistark.add(Map.of("REK", "Rekruttering og stilling"));
        temaHistark.add(Map.of("RVE", "Rettferdsvederlag"));
        temaHistark.add(Map.of("SAA", "Sanksjon arbeidsgiver"));
        temaHistark.add(Map.of("SAB", "Sanksjon behandler"));
        temaHistark.add(Map.of("SAR", "Sluttrapporter arbeidssøkere ved Arbeidsrådgivningskontoret"));
        temaHistark.add(Map.of("STO", "Regnskap/utbetaling"));
        temaHistark.add(Map.of("SUP", "Supplerende stønad"));
        temaHistark.add(Map.of("SYK", "Sykepenger"));
        temaHistark.add(Map.of("SYM", "Sykmelding"));
        temaHistark.add(Map.of("TAM", "Tiltak - Arbeidsmarkedsopplæring"));
        temaHistark.add(Map.of("TBO", "Tiltak - Bedriftsintern opplæring"));
        temaHistark.add(Map.of("TFP", "Tariffestet pensjon"));
        temaHistark.add(Map.of("TIL", "Tiltak (øvrig, alt som ikke har fått egen kode)"));
        temaHistark.add(Map.of("TPD", "Tiltak - Praksisplass driftstilskudd"));
        temaHistark.add(Map.of("TPF", "Tiltak - Praksisplass med fadderordning"));
        temaHistark.add(Map.of("TRK", "Trekkhåndtering"));
        temaHistark.add(Map.of("TRY", "Trygdeavgift"));
        temaHistark.add(Map.of("TUS", "Tidsbegrenset uførestønad"));
        temaHistark.add(Map.of("UFO", "Uførepensjon"));
        temaHistark.add(Map.of("UFM", "Unntak medlemskap "));
        temaHistark.add(Map.of("VEN", "Ventelønn"));
        temaHistark.add(Map.of("YRA", "Yrkesrettet attføring"));
        temaHistark.add(Map.of("YRK", "Yrkesskade/Menerstatning"));
    }

    public static KodeverkDTO getKodeverk() {

        return KodeverkDTO.builder()
                .kodeverknavn(TEMAHISTARK)
                .kodeverk(temaHistark.stream()
                        .map(Map::entrySet)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .build();
    }

    public static KodeverkAdjustedDTO getKodeverkAdjusted() {

        return KodeverkAdjustedDTO.builder()
                .name(TEMAHISTARK)
                .koder(temaHistark.stream()
                        .map(Map::entrySet)
                        .flatMap(Collection::stream)
                        .map(e -> KodeverkAdjustedDTO.KodeAdjusted.builder()
                                .value(e.getKey())
                                .label(e.getValue())
                                .gyldigFra(GYLDIG_FRA)
                                .gyldigTil(GYLDIG_TIL)
                                .build())
                        .toList())
                .build();
    }
}
