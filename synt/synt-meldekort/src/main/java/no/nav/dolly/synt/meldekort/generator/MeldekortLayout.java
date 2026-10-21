package no.nav.dolly.synt.meldekort.generator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

class MeldekortLayout {

    static final String MELDEGRUPPE = "MELDEGRUPPE";
    static final String ARBEIDSSOKER = "ARBEIDSSOKER";
    static final String ARBEIDET = "ARBEIDET";
    static final String SYK = "SYK";
    static final String ANNETFRAVAER = "ANNETFRAVAER";
    static final String KURS = "KURS";
    static final String FORSKUDD = "FORSKUDD";
    static final List<String> HEAD_COLUMNS = List.of(MELDEGRUPPE, ARBEIDSSOKER, ARBEIDET, SYK, ANNETFRAVAER, KURS, FORSKUDD);

    static final String DAY_TIMER = "timer";
    static final String DAY_SYK = "syk";
    static final String DAY_ANNETFRAVAER = "annetfravaer";
    static final String DAY_KURS = "kurs";
    static final String DAY_MELDEGRUPPE = "meldegruppe";
    private static final List<String> REQUIRED_DAY_FIELDS = List.of(DAY_TIMER, DAY_SYK, DAY_ANNETFRAVAER, DAY_KURS);

    static final int DAYS_PER_MELDEKORT = 14;
    private static final Pattern NUMBERED_GROUP = Pattern.compile("(\\D*)(\\d+)");

    private final List<Map<String, String>> dayColumns;

    MeldekortLayout(List<String> columnNames) {

        for (var headColumn : HEAD_COLUMNS) {
            if (!columnNames.contains(headColumn)) {
                throw new IllegalArgumentException("Meldekort model is missing column " + headColumn);
            }
        }

        var groups = new LinkedHashMap<String, Map<String, String>>();
        for (var columnName : columnNames) {
            if (HEAD_COLUMNS.contains(columnName)) {
                continue;
            }
            var parts = columnName.split("_");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Day column %s does not follow FIELD_GROUP naming".formatted(columnName));
            }
            var fields = groups.computeIfAbsent(parts[1], ignored -> new LinkedHashMap<>());
            var field = parts[0].toLowerCase(Locale.ROOT);
            if (fields.putIfAbsent(field, columnName) != null) {
                throw new IllegalArgumentException("Day group %s has more than one %s column".formatted(parts[1], field));
            }
        }

        if (groups.size() != DAYS_PER_MELDEKORT) {
            throw new IllegalArgumentException("Meldekort model has %d day groups, expected %d: %s"
                    .formatted(groups.size(), DAYS_PER_MELDEKORT, groups.keySet()));
        }
        groups.forEach((group, fields) -> REQUIRED_DAY_FIELDS.forEach(field -> {
            if (!fields.containsKey(field)) {
                throw new IllegalArgumentException("Day group %s is missing field %s".formatted(group, field));
            }
        }));
        this.dayColumns = List.copyOf(orderedGroups(groups).stream().map(Map::copyOf).toList());

    }

    List<Map<String, String>> dayColumns() {
        return dayColumns;
    }

    private static List<Map<String, String>> orderedGroups(LinkedHashMap<String, Map<String, String>> groups) {

        var keys = new ArrayList<>(groups.keySet());
        var allNumbered = keys.stream().allMatch(key -> NUMBERED_GROUP.matcher(key).matches());
        if (allNumbered) {
            keys.sort(Comparator
                    .comparing((String key) -> numberedPart(key, 1))
                    .thenComparing(key -> new BigInteger(numberedPart(key, 2))));
        }
        return keys.stream().map(groups::get).toList();

    }

    private static String numberedPart(String key, int group) {

        var matcher = NUMBERED_GROUP.matcher(key);
        if (!matcher.matches() || isNull(matcher.group(group))) {
            throw new IllegalStateException("Group %s is not numbered".formatted(key));
        }
        return matcher.group(group);

    }

}
