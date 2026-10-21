package no.nav.dolly.synt.meldekort.generator;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Map;

import static java.util.Objects.isNull;
import static no.nav.dolly.synt.meldekort.generator.MeldekortLayout.ANNETFRAVAER;
import static no.nav.dolly.synt.meldekort.generator.MeldekortLayout.ARBEIDET;
import static no.nav.dolly.synt.meldekort.generator.MeldekortLayout.ARBEIDSSOKER;
import static no.nav.dolly.synt.meldekort.generator.MeldekortLayout.DAY_ANNETFRAVAER;
import static no.nav.dolly.synt.meldekort.generator.MeldekortLayout.DAY_KURS;
import static no.nav.dolly.synt.meldekort.generator.MeldekortLayout.DAY_MELDEGRUPPE;
import static no.nav.dolly.synt.meldekort.generator.MeldekortLayout.DAY_SYK;
import static no.nav.dolly.synt.meldekort.generator.MeldekortLayout.DAY_TIMER;
import static no.nav.dolly.synt.meldekort.generator.MeldekortLayout.FORSKUDD;
import static no.nav.dolly.synt.meldekort.generator.MeldekortLayout.KURS;
import static no.nav.dolly.synt.meldekort.generator.MeldekortLayout.SYK;

@UtilityClass
class MeldekortMapper {

    private static final String SIGNATUR = "true";
    private static final String NO_HOURS = "0";

    static GeneratedMeldekort fromRow(MeldekortLayout layout, Map<String, Object> row, MeldekortType meldekortType) {

        var dager = new ArrayList<GeneratedMeldekortDag>(MeldekortLayout.DAYS_PER_MELDEKORT);
        var dayColumns = layout.dayColumns();
        for (int index = 0; index < dayColumns.size(); index++) {
            var columns = dayColumns.get(index);
            dager.add(new GeneratedMeldekortDag(
                    index + 1,
                    timer(row.get(columns.get(DAY_TIMER))),
                    dayValue(row.get(columns.get(DAY_SYK))),
                    dayValue(row.get(columns.get(DAY_ANNETFRAVAER))),
                    dayValue(row.get(columns.get(DAY_KURS))),
                    meldegruppe(columns.containsKey(DAY_MELDEGRUPPE) ? row.get(columns.get(DAY_MELDEGRUPPE)) : null, meldekortType)
            ));
        }
        return new GeneratedMeldekort(
                headValue(row.get(ARBEIDSSOKER)),
                headValue(row.get(ARBEIDET)),
                headValue(row.get(SYK)),
                headValue(row.get(ANNETFRAVAER)),
                headValue(row.get(KURS)),
                headValue(row.get(FORSKUDD)),
                SIGNATUR,
                dager
        );

    }

    private static Object headValue(Object value) {

        if ("J".equals(value)) {
            return true;
        }
        if ("N".equals(value)) {
            return false;
        }
        return value;

    }

    private static Object dayValue(Object value) {

        if ("J".equals(value) || "j".equals(value)) {
            return true;
        }
        if ("N".equals(value) || "n".equals(value)) {
            return false;
        }
        return value;

    }

    private static String timer(Object value) {

        if (isNull(value)) {
            return NO_HOURS;
        }
        if (value instanceof String text) {
            return text.replace(',', '.');
        }
        return PythonText.str(value);

    }

    private static String meldegruppe(Object value, MeldekortType meldekortType) {
        return isNull(value) ? meldekortType.name() : PythonText.upper(value);
    }

}
