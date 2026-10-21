package no.nav.dolly.synt.meldekort.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class BeanSynthesizer {

    static final int FORMAT_VERSION = 1;
    private static final String NAN_LABEL = "NaN";

    private final String meldegruppe;
    private final String[] names;
    private final List<Object>[] labels;
    private final List<Object>[] excludedRaw;
    private final List<String>[] excludedOutput;
    private final SynthesizedColumn[] synthesized;
    private final int[] visitSequence;

    @SuppressWarnings("unchecked")
    BeanSynthesizer(BeanModelDocument document) {

        if (isNull(document) || document.formatVersion() != FORMAT_VERSION) {
            throw new IllegalArgumentException("Unsupported bean model format version, expected " + FORMAT_VERSION);
        }
        if (isNull(document.columns()) || document.columns().isEmpty()) {
            throw new IllegalArgumentException("Bean model has no columns");
        }
        var columnCount = document.columns().size();
        this.meldegruppe = document.meldegruppe();
        this.names = new String[columnCount];
        this.labels = new List[columnCount];
        this.excludedRaw = new List[columnCount];
        this.excludedOutput = new List[columnCount];
        this.synthesized = new SynthesizedColumn[columnCount];

        for (int index = 0; index < columnCount; index++) {
            var column = document.columns().get(index);
            names[index] = column.name();
            labels[index] = column.labels();
            if (nonNull(column.excluded())) {
                excludedRaw[index] = validatedExcluded(column);
                excludedOutput[index] = column.excluded().output();
            } else if (nonNull(column.synthesis())) {
                synthesized[index] = new SynthesizedColumn(column, document.strictBoundaries());
            } else {
                throw new IllegalArgumentException("Column %s is neither excluded nor synthesized".formatted(column.name()));
            }
        }
        this.visitSequence = validatedVisitSequence(document.visitSequence());

    }

    public String meldegruppe() {
        return meldegruppe;
    }

    public List<String> columnNames() {
        return List.of(names);
    }

    public Map<String, Object> synthesizeRow(int rowIndex, RandomGenerator random) {

        var columnCount = names.length;
        var raw = new Object[columnCount];
        var missing = new boolean[columnCount];
        for (int index = 0; index < columnCount; index++) {
            if (nonNull(excludedRaw[index])) {
                raw[index] = excludedRaw[index].get(rowIndex % excludedRaw[index].size());
            }
        }
        for (var index : visitSequence) {
            var sample = synthesized[index].sample(raw, random);
            raw[index] = sample.value();
            missing[index] = sample.missing();
        }

        var row = new LinkedHashMap<String, Object>(columnCount * 2);
        for (int index = 0; index < columnCount; index++) {
            row.put(names[index], outputValue(index, rowIndex, raw[index], missing[index]));
        }
        return Collections.unmodifiableMap(row);

    }

    private Object outputValue(int index, int rowIndex, Object raw, boolean missing) {

        if (nonNull(excludedOutput[index])) {
            return excludedOutput[index].get(rowIndex % excludedOutput[index].size());
        }
        if (missing) {
            return null;
        }
        if (isNull(labels[index])) {
            return raw;
        }
        var label = labels[index].get(labelIndex(index, raw));
        return NAN_LABEL.equals(label) ? null : label;

    }

    private int labelIndex(int index, Object raw) {

        var code = BeanValues.toDouble(raw);
        if (code != Math.rint(code) || code < 0 || code >= labels[index].size()) {
            throw new IllegalStateException("Column %s produced label code %s outside of %d labels"
                    .formatted(names[index], raw, labels[index].size()));
        }
        return (int) code;

    }

    private List<Object> validatedExcluded(BeanModelDocument.ColumnDocument column) {

        var excluded = column.excluded();
        if (isNull(excluded.raw()) || excluded.raw().isEmpty() || isNull(excluded.output())
                || excluded.output().size() != excluded.raw().size()) {
            throw new IllegalArgumentException("Excluded column %s must have the same number of raw and output values"
                    .formatted(column.name()));
        }
        return excluded.raw();

    }

    private int[] validatedVisitSequence(List<Integer> sequence) {

        if (isNull(sequence)) {
            throw new IllegalArgumentException("Bean model has no visit sequence");
        }
        var available = new HashSet<Integer>();
        for (int index = 0; index < names.length; index++) {
            if (nonNull(excludedRaw[index])) {
                available.add(index);
            }
        }
        var visited = new ArrayList<Integer>();
        for (var index : sequence) {
            if (isNull(index) || index < 0 || index >= names.length || isNull(synthesized[index]) || available.contains(index)) {
                throw new IllegalArgumentException("Visit sequence contains invalid or repeated column " + index);
            }
            for (var dependency : synthesized[index].dependsOn()) {
                if (!available.contains(dependency)) {
                    throw new IllegalArgumentException("Column %s depends on column %d before it is available"
                            .formatted(names[index], dependency));
                }
            }
            available.add(index);
            visited.add(index);
        }
        for (int index = 0; index < names.length; index++) {
            if (nonNull(synthesized[index]) && !available.contains(index)) {
                throw new IllegalArgumentException("Column %s is not in the visit sequence".formatted(names[index]));
            }
        }
        return visited.stream().mapToInt(Integer::intValue).toArray();

    }

}
