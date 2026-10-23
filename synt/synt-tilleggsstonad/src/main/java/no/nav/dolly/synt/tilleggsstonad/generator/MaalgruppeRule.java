package no.nav.dolly.synt.tilleggsstonad.generator;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.random.RandomGenerator;

import static java.util.Objects.isNull;

record MaalgruppeRule(
        Mode mode,
        Set<String> avvikletMedDato,
        LocalDate avvikletDato,
        Set<String> avvikletUtenDato,
        List<String> erstatninger
) {

    static final String MAALGRUPPEKODE = "MAALGRUPPEKODE";

    enum Mode {
        RETRY,
        REPLACE,
        NONE
    }

    static MaalgruppeRule retry(Set<String> avvikletMedDato, String avvikletDato, Set<String> avvikletUtenDato) {
        return new MaalgruppeRule(Mode.RETRY, avvikletMedDato, parse(avvikletDato), avvikletUtenDato, List.of());
    }

    static MaalgruppeRule replace(Set<String> avvikletMedDato, String avvikletDato, Set<String> avvikletUtenDato, List<String> erstatninger) {
        return new MaalgruppeRule(Mode.REPLACE, avvikletMedDato, parse(avvikletDato), avvikletUtenDato, erstatninger);
    }

    static MaalgruppeRule none() {
        return new MaalgruppeRule(Mode.NONE, Set.of(), null, Set.of(), List.of());
    }

    boolean isValid(List<Map<String, Object>> vedtaksliste) {

        if (vedtaksliste.isEmpty()) {
            return false;
        }
        for (var vedtak : vedtaksliste) {
            var maalgruppe = maalgruppe(vedtak);
            if (avvikletUtenDato.contains(maalgruppe)) {
                return false;
            }
            if (avvikletMedDato.contains(maalgruppe) && isAfterAvvikletDato(vedtak)) {
                return false;
            }
        }
        return true;

    }

    void replaceInvalid(List<Map<String, Object>> vedtaksliste, RandomGenerator random) {

        var gyldigMaalgruppe = erstatninger.get(random.nextInt(erstatninger.size()));
        for (var vedtak : vedtaksliste) {
            var maalgruppe = maalgruppe(vedtak);
            if (avvikletUtenDato.contains(maalgruppe)
                    || (avvikletMedDato.contains(maalgruppe) && isAfterAvvikletDato(vedtak))) {
                vedtak.put(MAALGRUPPEKODE, gyldigMaalgruppe);
            }
        }

    }

    private static String maalgruppe(Map<String, Object> vedtak) {

        if (!vedtak.containsKey(MAALGRUPPEKODE)) {
            throw new IllegalStateException("Vedtaket mangler " + MAALGRUPPEKODE);
        }
        return String.valueOf(vedtak.get(MAALGRUPPEKODE));

    }

    private boolean isAfterAvvikletDato(Map<String, Object> vedtak) {

        var vedtaksperiode = (Map<?, ?>) vedtak.get(VedtakPostprocessor.VEDTAKSPERIODE);
        var fom = PythonValues.parseIsoDate((String) vedtaksperiode.get(VedtakPostprocessor.FOM));
        return fom.isAfter(avvikletDato);

    }

    private static LocalDate parse(String dato) {
        return isNull(dato) ? null : LocalDate.parse(dato);
    }

}
