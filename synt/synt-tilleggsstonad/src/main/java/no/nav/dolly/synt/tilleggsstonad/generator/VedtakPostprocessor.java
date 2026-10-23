package no.nav.dolly.synt.tilleggsstonad.generator;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.random.RandomGenerator;

import static java.util.Objects.isNull;
import static no.nav.dolly.synt.tilleggsstonad.generator.HistorySynthesizer.ENDRING;
import static no.nav.dolly.synt.tilleggsstonad.generator.HistorySynthesizer.ORDINAER;
import static no.nav.dolly.synt.tilleggsstonad.generator.HistorySynthesizer.TIL_DATO;
import static no.nav.dolly.synt.tilleggsstonad.generator.HistorySynthesizer.VEDTAKTYPE;

@UtilityClass
class VedtakPostprocessor {

    static final String VEDTAKSPERIODE = "VEDTAKSPERIODE";
    static final String FOM = "FOM";
    static final String TOM = "TOM";

    private static final String FRA_DATO = "FRA_DATO";
    private static final String VEDTAKSTATUS = "VEDTAKSTATUS";
    private static final String AVBRUDDSKODE = "AVBRUDDSKODE";
    private static final String UTFALL = "UTFALL";
    private static final String UTFALL_JA = "JA";
    private static final String VILKAAR = "VILKAAR";
    private static final String KODE = "KODE";
    private static final String STATUS = "STATUS";
    private static final String OVERORDNET = "OVERORDNET";
    private static final String VERDI = "VERDI";
    private static final String RETTIGHET_KODE = "RETTIGHET_KODE";
    private static final String STATUS_JA = "J";
    private static final String STATUS_NEI = "N";
    private static final Set<String> VEDTAKTYPER_SOM_MAA_HA_TIL_DATO = Set.of(ORDINAER, ENDRING);

    private static final Map<String, List<String>> LEGAL_AVBRUDDSKODER = Map.of(
            "MOTAT", List.of("FSOP"),
            "OPPRE", List.of("FSOP"),
            "REGIS", List.of("FSOP", "ESOP", "FFOV"),
            "INNST", List.of("EFOV", "FFAV"));

    private static final List<String> VILKAAR_KODER = List.of(
            "TSTIKKELON", "TSTGSTATUS", "TSTAVSTOBL", "TSTAVSTREI", "TSTAVSTDAG", "TSTBEHTRAN", "TSTBEHOVHJ", "TSTBOUTHJE",
            "TSTHOYBOUT", "TSTMOTBOST", "TSTSAMLDOB", "TSTSAMLDOR", "TSTENSFORS", "TSTFLYTAKT", "TSTFLYTARB", "TSTVEDTAK",
            "TSTIKKDEKR", "TSTIKKDEKF", "TSTGJLEEKT", "TSTFJERDE", "TSTBARNMED", "TSTIKKYTLM", "TSTIKKYTTB", "TSTIKKYTTF",
            "TSTOGANGST", "TSTOGSTPEN", "TSTBOUTFUN", "TSTIKKARBR", "TSTLOVOPPH", "TSTFOLKETR", "TSTDAGPENG", "TSTSYKPENG",
            "TSTTILPENG", "TSTTILFAM", "TSTBOHJFRA", "TSTBOUTAKT", "TSTREGARBS", "TSTREISOPP", "TSTOFFTRAN", "TSTEGENBIL",
            "TSTDROSJE", "TSTUTGFUNK", "TSTFIREHJR", "TSTTIFAMPL", "TSTINGENMG", "TSTBARNSKO", "TSTTILNODV", "TSTBARN",
            "TSTANBKJOR", "TSTULEDIGR", "TSTUTDAKT", "TSTUTGDOKF", "TSTUTGDOKR", "TSTUTGNODF", "TSTUTGNODT", "TSTUTGNODR",
            "TSTUTGSAML", "TSTUTGDAGR", "TSTIKKARBF", "TSTULEDIGF", "TSTANNSTEF", "TSTANNSTER");

    private enum Vilkaarsgruppe {

        REISE(Set.of("TSTOFFTRAN", "TSTEGENBIL", "TSTDROSJE")),
        AVSTAND(Set.of("TSTAVSTDAG", "TSTBEHTRAN")),
        BOLIG(Set.of("TSTBOUTHJE", "TSTHOYBOUT")),
        FLYTTE(Set.of("TSTFLYTAKT", "TSTFLYTARB")),
        TILSYN(Set.of("TSTBARNSKO", "TSTTILNODV", "TSTBARN"));

        private final Set<String> koder;

        Vilkaarsgruppe(Set<String> koder) {
            this.koder = koder;
        }

        static Vilkaarsgruppe of(String kode) {

            for (var gruppe : values()) {
                if (gruppe.koder.contains(kode)) {
                    return gruppe;
                }
            }
            return null;

        }

    }

    static void convertRelativeDates(Map<String, Object> vedtak, String fraDato, String tilDato, TilleggsstonadModel model,
                                     Set<String> differentTimeFormats, boolean brukInnsendtTilDato) {

        var root = model.getRoot();
        var startDato = PythonValues.parseIsoDate(fraDato);
        vedtak.put(root, startDato);
        var deltaColumns = new ArrayList<>(model.getDeltaColumns());

        if (!"".equals(tilDato) && brukInnsendtTilDato) {
            var sluttDato = PythonValues.parseIsoDate(tilDato);
            if (!deltaColumns.remove(TIL_DATO)) {
                throw new IllegalStateException("Modellen %s mangler deltakolonnen %s".formatted(model.getName(), TIL_DATO));
            }
            var scaleFactor = scaleFactor(ChronoUnit.DAYS.between(startDato, sluttDato), actualDelta(required(vedtak, TIL_DATO)));
            for (var column : deltaColumns) {
                var value = required(vedtak, column);
                if (!isNull(value)) {
                    vedtak.put(column, PythonValues.toInt(PythonValues.toInt(PythonValues.toFloat(value)) * scaleFactor));
                }
            }
            vedtak.put(TIL_DATO, PythonValues.formatIsoDate(sluttDato));
        }

        for (var column : deltaColumns) {
            var value = required(vedtak, column);
            if (PythonValues.hasValue(value)) {
                var relativeDate = PythonValues.plusDays(startDato, PythonValues.toInt(PythonValues.toFloat(value)));
                vedtak.put(column, differentTimeFormats.contains(column)
                        ? PythonValues.formatDifferentDate(relativeDate)
                        : PythonValues.formatIsoDate(relativeDate));
            } else {
                vedtak.put(column, null);
            }
        }

        vedtak.put(root, PythonValues.formatIsoDate((LocalDate) vedtak.get(root)));

        if (!brukInnsendtTilDato
                && VEDTAKTYPER_SOM_MAA_HA_TIL_DATO.contains(String.valueOf(required(vedtak, VEDTAKTYPE)))
                && !PythonValues.hasValue(required(vedtak, TIL_DATO))) {
            vedtak.put(TIL_DATO, tilDato);
        }

    }

    static Map<String, Object> structure(Map<String, Object> vedtak, TilleggsstonadType type, RandomGenerator random) {

        vedtak.replaceAll((column, value) -> isNull(value) ? "" : value);
        addAvbruddskode(vedtak, random);
        kodeOverordnetVerdi(vedtak, type.getResultField(), type.getValues());
        vilkaar(vedtak);
        vedtaksperiode(vedtak);
        vedtak.put(RETTIGHET_KODE, type.getRettighetKode());
        if (!isNull(type.getResultItemField())) {
            vedtak.put(type.getResultField(), List.of(sorted(Map.of(type.getResultItemField(), vedtak.get(type.getResultField())))));
        }
        return sorted(vedtak);

    }

    private static long actualDelta(Object tilDato) {

        try {
            return PythonValues.toInt(PythonValues.toFloat(tilDato));
        } catch (NumberFormatException e) {
            return 0;
        }

    }

    private static double scaleFactor(long wantedDelta, long actualDelta) {
        return actualDelta != 0 ? (double) wantedDelta / actualDelta : 1;
    }

    private static void addAvbruddskode(Map<String, Object> vedtak, RandomGenerator random) {

        var status = required(vedtak, VEDTAKSTATUS);
        vedtak.remove(VEDTAKSTATUS);
        var koder = LEGAL_AVBRUDDSKODER.get(String.valueOf(status));
        if (!isNull(koder)) {
            vedtak.put(AVBRUDDSKODE, koder.get(random.nextInt(koder.size())));
        }

    }

    private static void kodeOverordnetVerdi(Map<String, Object> vedtak, String name, List<String> values) {

        var data = new ArrayList<Map<String, Object>>();
        for (var kode : values) {
            String kodenavn;
            String overordnet;
            if (kode.contains("_")) {
                var deler = kode.split("_", -1);
                overordnet = deler[0];
                if (!data.isEmpty() && data.stream().noneMatch(existing -> overordnet.equals(existing.get(KODE)))) {
                    data.add(kode(overordnet, null, null));
                    vedtak.remove(overordnet);
                }
                kodenavn = deler[1];
            } else {
                kodenavn = kode;
                overordnet = null;
            }
            data.add(kode(kodenavn, overordnet, required(vedtak, kode)));
            vedtak.remove(kode);
        }
        vedtak.put(name, data);

    }

    private static Map<String, Object> kode(String kode, String overordnet, Object verdi) {

        var entry = new TreeMap<String, Object>();
        entry.put(KODE, kode);
        entry.put(OVERORDNET, overordnet);
        entry.put(VERDI, verdi);
        return entry;

    }

    private static void vilkaar(Map<String, Object> vedtak) {

        var oppfylteGrupper = EnumSet.noneOf(Vilkaarsgruppe.class);
        var vilkaar = new ArrayList<Map<String, Object>>();
        for (var kode : VILKAAR_KODER) {
            if (!vedtak.containsKey(kode)) {
                continue;
            }
            var status = vedtak.get(kode);
            if (UTFALL_JA.equals(required(vedtak, UTFALL))) {
                var gruppe = Vilkaarsgruppe.of(kode);
                if (!isNull(gruppe)) {
                    if (oppfylteGrupper.contains(gruppe)) {
                        status = STATUS_NEI;
                    }
                    if (STATUS_JA.equals(status)) {
                        oppfylteGrupper.add(gruppe);
                    }
                }
            }
            vilkaar.add(sorted(Map.of(KODE, kode, STATUS, status)));
            vedtak.remove(kode);
        }
        vedtak.put(VILKAAR, vilkaar);

    }

    private static void vedtaksperiode(Map<String, Object> vedtak) {

        var fom = required(vedtak, FRA_DATO);
        vedtak.remove(FRA_DATO);
        var tom = required(vedtak, TIL_DATO);
        vedtak.remove(TIL_DATO);
        var periode = new TreeMap<String, Object>();
        periode.put(FOM, fom);
        periode.put(TOM, tom);
        vedtak.put(VEDTAKSPERIODE, periode);

    }

    private static Object required(Map<String, Object> vedtak, String column) {

        if (!vedtak.containsKey(column)) {
            throw new IllegalStateException("Vedtaket mangler kolonnen " + column);
        }
        return vedtak.get(column);

    }

    private static Map<String, Object> sorted(Map<String, Object> map) {
        return new TreeMap<>(map);
    }

}
