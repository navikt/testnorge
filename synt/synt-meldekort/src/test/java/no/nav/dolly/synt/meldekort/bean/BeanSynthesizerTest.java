package no.nav.dolly.synt.meldekort.bean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.within;

class BeanSynthesizerTest {

    private static final int ROWS = 20_000;
    private static final double FREQUENCY_TOLERANCE = 0.02;

    @ParameterizedTest
    @ValueSource(strings = {"ARBS", "GENERIC"})
    void shouldMatchPythonColumnStatistics(String fixture) {

        var rows = synthesize(fixture);
        var columns = BeanFixtures.reference(fixture).get("statistics").get("columns");

        for (var entry : columns.properties()) {
            var name = entry.getKey();
            var expected = entry.getValue();
            var values = rows.stream().map(row -> row.get(name)).toList();
            if (expected.has("frequencies")) {
                assertFrequencies(name, frequencies(values, "null"), expected.get("frequencies"));
            } else {
                assertContinuous(name, values, expected);
            }
        }

    }

    @ParameterizedTest
    @ValueSource(strings = {"ARBS", "GENERIC"})
    void shouldMatchPythonConditionalStatistics(String fixture) {

        var rows = synthesize(fixture);
        var conditionals = BeanFixtures.reference(fixture).get("statistics").get("conditionals");
        assertThat(conditionals.size()).isPositive();

        for (var conditional : conditionals) {
            var column = conditional.get("column").asString();
            var given = conditional.get("given").asString();
            var pairs = rows.stream()
                    .map(row -> (Object) (pandasText(row.get(given)) + "|" + pandasText(row.get(column))))
                    .toList();
            assertFrequencies(column + " given " + given, frequencies(pairs, "nan"), conditional.get("frequencies"));
        }

    }

    @Test
    void shouldRespectDecimalsBoundariesAndIntegerTypes() {

        var rows = synthesize("GENERIC");

        assertThat(rows).allSatisfy(row -> {
            if (row.get("B") instanceof Double value) {
                assertThat(value).isBetween(5.0, 25.0);
                assertThat(Math.rint(value * 10) / 10).isEqualTo(value);
            } else {
                assertThat(row.get("B")).isNull();
            }
            if (row.get("C") instanceof Long value) {
                assertThat(value).isBetween(40L, 260L);
            } else {
                assertThat(row.get("C")).isNull();
            }
        });

    }

    @Test
    void shouldRepeatExcludedValuesByRowIndex() {

        var synthesizer = BeanFixtures.synthesizer("GENERIC");
        var random = new SplittableRandom(1);

        assertThat(synthesizer.synthesizeRow(3, random).get("E"))
                .isEqualTo(synthesizer.synthesizeRow(53, random).get("E"));
        assertThat(synthesizer.synthesizeRow(0, random).get("E"))
                .isEqualTo(synthesizer.synthesizeRow(50, random).get("E"));

    }

    @Test
    void shouldRejectUnsupportedFormatVersion() {

        var document = BeanFixtures.document("GENERIC");
        var unsupported = new BeanModelDocument(2, document.meldegruppe(), document.strictBoundaries(),
                document.visitSequence(), document.columns());

        assertThatIllegalArgumentException().isThrownBy(() -> new BeanSynthesizer(unsupported));

    }

    @Test
    void shouldRejectVisitSequenceWhereDependencyIsVisitedLater() {

        var document = BeanFixtures.document("GENERIC");
        var reversed = new ArrayList<>(document.visitSequence());
        Collections.reverse(reversed);
        var invalid = new BeanModelDocument(document.formatVersion(), document.meldegruppe(), document.strictBoundaries(),
                reversed, document.columns());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new BeanSynthesizer(invalid))
                .withMessageContaining("before it is available");

    }

    private static List<Map<String, Object>> synthesize(String fixture) {

        var synthesizer = BeanFixtures.synthesizer(fixture);
        var random = new SplittableRandom(20261014L);
        var rows = new ArrayList<Map<String, Object>>(ROWS);
        for (int index = 0; index < ROWS; index++) {
            rows.add(synthesizer.synthesizeRow(index, random));
        }
        return rows;

    }

    private static void assertFrequencies(String description, Map<String, Double> actual, JsonNode expected) {

        var expectedFrequencies = new HashMap<String, Double>();
        expected.properties().forEach(entry -> expectedFrequencies.put(entry.getKey(), entry.getValue().asDouble()));

        assertThat(actual.keySet()).as(description).isSubsetOf(expectedFrequencies.keySet());
        expectedFrequencies.forEach((value, frequency) -> assertThat(actual.getOrDefault(value, 0.0))
                .as("%s = %s", description, value)
                .isCloseTo(frequency, within(FREQUENCY_TOLERANCE)));

    }

    private static void assertContinuous(String name, List<Object> values, JsonNode expected) {

        var present = values.stream()
                .filter(value -> !isNull(value))
                .mapToDouble(value -> ((Number) value).doubleValue())
                .toArray();
        var mean = Arrays.stream(present).average().orElseThrow();
        var variance = Arrays.stream(present).map(value -> (value - mean) * (value - mean)).sum() / (present.length - 1);
        var expectedStd = expected.get("std").asDouble();

        assertThat(mean).as("%s mean", name).isCloseTo(expected.get("mean").asDouble(), within(0.05 * expectedStd));
        assertThat(Math.sqrt(variance)).as("%s std", name).isCloseTo(expectedStd, within(0.05 * expectedStd));
        assertThat(1.0 - (double) present.length / values.size()).as("%s missing rate", name)
                .isCloseTo(expected.get("missingRate").asDouble(), within(FREQUENCY_TOLERANCE));

    }

    private static Map<String, Double> frequencies(List<Object> values, String nullText) {

        var counts = new HashMap<String, Double>();
        for (var value : values) {
            counts.merge(isNull(value) ? nullText : String.valueOf(value), 1.0, Double::sum);
        }
        counts.replaceAll((ignored, count) -> count / values.size());
        return counts;

    }

    private static String pandasText(Object value) {
        return isNull(value) ? "nan" : String.valueOf(value);
    }

}
