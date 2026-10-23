package no.nav.dolly.synt.tilleggsstonad.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum TilleggsstonadType {

    BOUTGIFT("boutgifter", HistorySynthesis.STANDARD,
            "BOUTGIFTER", null, "TSOBOUTG",
            Values.BOUTGIFTER, Values.KRPER_OG_AKDAT_DATOFORMAT,
            MaalgruppeRule.retry(Set.of(), null, Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"))),
    BOUTGIFTER_ARBEIDSSOKERE("boutgifter_arbeidssokere", HistorySynthesis.STANDARD,
            "BOUTGIFTER", null, "TSRBOUTG",
            Values.BOUTGIFTER, Values.KRPER_OG_AKDAT_DATOFORMAT,
            MaalgruppeRule.replace(Set.of("MOTTILTPEN"), "2019-12-31", Set.of("GJENEKARBS", "ENSFORARBS", "MOTDAGPEN", "ARBSOKERE"),
                    List.of("NEDSARBEVN", "GJENEKUTD", "ENSFORUTD", "TIDLFAMPL"))),
    DAGLIG_REISE("daglig_reise", HistorySynthesis.STANDARD_WITH_KERNEL_DENSITY,
            "DAGLIG_REISE", null, "TSODAGREIS",
            Values.DAGLIG_REISE, Values.KRPER_DATOFORMAT,
            MaalgruppeRule.retry(Set.of(), null, Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"))),
    DAGLIG_REISE_ARBEIDSSOKER("daglig_reise_arbeidssoker", HistorySynthesis.STANDARD,
            "DAGLIG_REISE", null, "TSRDAGREIS",
            Values.DAGLIG_REISE, Values.KRPER_DATOFORMAT,
            MaalgruppeRule.retry(Set.of(), null, Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTDAGPEN"))),
    LAEREMIDLER("laeremidler", HistorySynthesis.STANDARD,
            "LAEREMIDLER", null, "TSOLMIDLER",
            Values.LAEREMIDLER, Values.KRPER_OG_AKDAT_DATOFORMAT,
            MaalgruppeRule.retry(Set.of(), null, Set.of("ENSFORARBS", "GJENEKARBS", "ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"))),
    LAEREMIDLER_ARBEIDSSOKERE("laeremidler_arbeidssokere", HistorySynthesis.STANDARD,
            "LAEREMIDLER", null, "TSRLMIDLER",
            Values.LAEREMIDLER, Values.KRPER_DATOFORMAT,
            MaalgruppeRule.replace(Set.of("MOTTILTPEN"), "2019-12-31", Set.of("ENSFORARBS", "GJENEKARBS", "MOTDAGPEN", "ARBSOKERE"),
                    List.of("NEDSARBEVN", "GJENEKUTD", "ENSFORUTD", "TIDLFAMPL"))),
    FLYTTING("flytting", HistorySynthesis.STANDARD,
            "FLYTTING", null, "TSOFLYTT",
            Values.FLYTTING, Set.of(),
            MaalgruppeRule.retry(Set.of("ENSFORARBS", "ENSFORUTD"), "2017-12-31", Set.of("ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"))),
    FLYTTING_ARBEIDSSOKERE("flytting_arbeidssokere", HistorySynthesis.STANDARD,
            "FLYTTING", null, "TSRFLYTT",
            Values.FLYTTING, Set.of(),
            MaalgruppeRule.retry(Set.of("MOTDAGPEN", "MOTTILTPEN"), "2019-12-31", Set.of("MOTDAGPEN"))),
    REISE_AKTIVITET_OG_HJEMREISER("reise_aktivitet_og_hjemreiser", HistorySynthesis.STANDARD,
            "REISE_VED_OPPSTART_OG_AVSLUTNING_AV_AKTIVITET_OG_HJEMREISE", null, "TSOREISAKT",
            Values.REISE_AKTIVITET, Values.KRPER_DATOFORMAT,
            MaalgruppeRule.retry(Set.of(), null, Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"))),
    REISE_AKTIVITET_OG_HJEMREISER_ARBEIDSSOKERE("reise_aktivitet_og_hjemreiser_arbeidssokere", HistorySynthesis.STANDARD,
            "REISE_VED_OPPSTART_OG_AVSLUTNING_AV_AKTIVITET_OG_HJEMREISE", null, "TSRREISAKT",
            Values.REISE_AKTIVITET, Set.of(),
            MaalgruppeRule.retry(Set.of(), null, Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTDAGPEN"))),
    REISESTONAD_TIL_ARBEIDSSOKERE("reisestonad_til_arbeidssokere", HistorySynthesis.ONLY_O,
            "REISESTONAD_ARBEIDSSOKER", null, "TSOREISARB",
            Values.REISESTONAD, Set.of(),
            MaalgruppeRule.retry(Set.of(), null, Set.of("ENSFORUTD", "MOTDAGPEN"))),
    REISE_TIL_OBLIGATORISK_SAMLING("reise_til_obligatorisk_samling", HistorySynthesis.STANDARD,
            "REISE_OBLIGATORISK_SAMLING", null, "TSOREISOBL",
            Values.REISE_OBLIGATORISK_SAMLING, Set.of(),
            MaalgruppeRule.retry(Set.of(), null, Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"))),
    REISE_TIL_OBLIGATORISK_SAMLING_ARBEIDSSOKERE("reise_til_obligatorisk_samling_arbeidssokere", HistorySynthesis.STANDARD,
            "REISE_OBLIGATORISK_SAMLING", null, "TSRREISOBL",
            Values.REISE_OBLIGATORISK_SAMLING, Set.of(),
            MaalgruppeRule.retry(Set.of(), null, Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTDAGPEN"))),
    TILSYN_FAMILIEMEDLEMMER("tilsyn_familiemedlemmer", HistorySynthesis.ONLY_O_AND_S,
            "TILSYN_FAMILIEMEDLEMMER", "FAMILIEMEDLEM", "TSOTILFAM",
            Values.TILSYN_FAMILIEMEDLEMMER, Set.of(),
            MaalgruppeRule.none()),
    TILSYN_FAMILIEMEDLEMMER_ARBEIDSSOKERE("tilsyn_familiemedlemmer_arbeidssokere", HistorySynthesis.ONLY_O,
            "TILSYN_FAMILIEMEDLEMMER", "FAMILIEMEDLEM", "TSRTILFAM",
            Values.TILSYN_FAMILIEMEDLEMMER, Set.of(),
            MaalgruppeRule.replace(Set.of("MOTTILTPEN"), "2019-12-31", Set.of("ARBSOKERE", "MOTDAGPEN"),
                    List.of("NEDSARBEVN", "GJENEKUTD", "GJENEKARBS", "ENSFORUTD", "ENSFORARBS", "TIDLFAMPL"))),
    TILSYN_BARN("tilsyn_barn", HistorySynthesis.STANDARD,
            "TILSYN_BARN", "BARN", "TSOTILBARN",
            Values.TILSYN_BARN, Set.of(),
            MaalgruppeRule.retry(Set.of("ENSFORARBS", "ENSFORUTD"), "2017-12-31", Set.of("ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"))),
    TILSYN_BARN_ARBEIDSSOKER("tilsyn_barn_arbeidssoker", HistorySynthesis.STANDARD,
            "TILSYN_BARN", "BARN", "TSRTILBARN",
            Values.TILSYN_BARN, Set.of(),
            MaalgruppeRule.replace(Set.of("MOTTILTPEN"), "2019-12-31", Set.of("ARBSOKERE", "MOTDAGPEN"),
                    List.of("NEDSARBEVN", "GJENEKUTD", "GJENEKARBS", "ENSFORUTD", "ENSFORARBS", "TIDLFAMPL")));

    private final String modelName;
    private final HistorySynthesis historySynthesis;
    private final String resultField;
    private final String resultItemField;
    private final String rettighetKode;
    private final List<String> values;
    private final Set<String> differentTimeFormats;
    private final MaalgruppeRule maalgruppeRule;

    enum HistorySynthesis {
        STANDARD,
        STANDARD_WITH_KERNEL_DENSITY,
        ONLY_O,
        ONLY_O_AND_S
    }

    private static final class Values {

        private static final Set<String> KRPER_DATOFORMAT = Set.of("KRPER_KRFRA", "KRPER_KRTIL");
        private static final Set<String> KRPER_OG_AKDAT_DATOFORMAT = Set.of("KRPER_KRFRA", "KRPER_KRTIL", "AKDAT_SAFRA", "AKDAT_SATIL");

        private static final List<String> BOUTGIFTER = List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "AKDAT", "AKDAT_SAFRA",
                "AKDAT_SATIL", "BOFAS", "BOSAM", "BOUAK", "BOUTG", "UTGHJ", "BFUTG", "BEGRU", "MBBOS", "BOMND", "LAND");
        private static final List<String> DAGLIG_REISE = List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "ADAKT", "AVBOS", "BEGRU",
                "OFFTR", "BERRT", "BEGRN", "BILBE", "UTGBP", "UTGPA", "DAUTG", "FREKV", "BIBBN", "UTDRO", "LAND");
        private static final List<String> FLYTTING = List.of("FLDAT", "FLAKT", "FLSTI", "NSTIT", "TIADR", "KOFLY", "ANTKM", "FLAN1",
                "FLAN2", "BELO1", "BELO2", "LAND");
        private static final List<String> LAEREMIDLER = List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "NISKO", "PRUAK", "FAUTG",
                "BEGRU", "UDEKK", "LAND");
        private static final List<String> REISE_AKTIVITET = List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "ADAKT", "KMBOS", "AKTVA",
                "BAUFE", "BAUFM", "BEGHJ", "OFFTR", "BELPR", "BEGRN", "BILBE", "UTGBP", "BIBBN", "UTDRO", "LAND");
        private static final List<String> REISE_OBLIGATORISK_SAMLING = List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "DAAKT",
                "DAAKT_SAFRA", "DAAKT_SATIL", "ADAKT", "AVBOS", "OFFTR", "BELPR", "BEGRN", "BILBE", "UTGBP", "BIBBN", "UTDRO", "LAND");
        private static final List<String> REISESTONAD = List.of("KRPER", "BEGRU", "RETIL", "KMBOS", "UDEKK", "FO8UK", "TB8UK",
                "OFFTR", "BELPR", "BEGRN", "BILBE", "UTGBP", "BIBBN", "UTDRO", "LAND");
        private static final List<String> TILSYN_BARN = List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "BAUTG", "TILAV", "BA4SK", "LAND");
        private static final List<String> TILSYN_FAMILIEMEDLEMMER = List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "ANFNR", "TILAV",
                "FANAV", "BEGRU", "LAND");

    }

}
