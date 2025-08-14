package no.nav.testnav.kodeverkservice.utility;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkAdjustedDTO;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.GYLDIG_FRA;
import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.GYLDIG_TIL;
import static no.nav.testnav.kodeverkservice.utility.CommonKeysAndUtils.VERGEMAAL_FYLKESEMBETER;

@UtilityClass
public class VergemaalFylkesembeterUtility {

    private static final List<Map<String, String>> fylkesembeter = List.of(

            Map.of("FMAV", "Statsforvalter i Agder"),
            Map.of("FMIN", "Statsforvalter i Innlandet"),
            Map.of("FMMR", "Statsforvalter i Møre og Romsdal"),
            Map.of("FMNO", "Statsforvalter i Nordland"),
            Map.of("FMOV", "Statsforvalter i Østfold, Buskerud, Oslo og Akershus"),
            Map.of("FMRO", "Statsforvalter i Rogaland"),
            Map.of("FMTF", "Statsforvalter i Troms og Finnmark"),
            Map.of("FMTL", "Statsforvalter i Trøndelag"),
            Map.of("FMVL", "Statsforvaltar i Vestland"),
            Map.of("FMVT", "Statsforvalter i Vestfold og Telemark")
    );

    public static KodeverkDTO getKodeverk() {

        return KodeverkDTO.builder()
                .kodeverknavn(VERGEMAAL_FYLKESEMBETER)
                .kodeverk(fylkesembeter.stream()
                        .map(Map::entrySet)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .build();
    }

    public static KodeverkAdjustedDTO getKodeverkAdjusted() {

        return KodeverkAdjustedDTO.builder()
                .name(VERGEMAAL_FYLKESEMBETER)
                .koder(fylkesembeter.stream()
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
