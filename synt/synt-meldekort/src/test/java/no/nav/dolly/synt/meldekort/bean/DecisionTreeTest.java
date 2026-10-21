package no.nav.dolly.synt.meldekort.bean;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class DecisionTreeTest {

    @ParameterizedTest
    @ValueSource(strings = {"ARBS", "GENERIC"})
    void shouldSelectSameLeavesAsSklearn(String fixture) {

        var columns = BeanFixtures.document(fixture).columns().stream()
                .collect(Collectors.toMap(BeanModelDocument.ColumnDocument::name, Function.identity()));
        var cases = BeanFixtures.reference(fixture).get("treeApply");
        assertThat(cases.size()).isPositive();

        for (var testCase : cases) {
            var column = columns.get(testCase.get("column").asString());
            var tree = new DecisionTree(column.synthesis().cart().tree());
            var inputs = testCase.get("inputs");
            var leaves = testCase.get("leaves");
            for (int row = 0; row < inputs.size(); row++) {
                var features = new double[inputs.get(row).size()];
                for (int feature = 0; feature < features.length; feature++) {
                    var value = inputs.get(row).get(feature);
                    features[feature] = value.isNull() ? Double.NaN : value.asDouble();
                }
                assertThat(tree.apply(features))
                        .as("%s row %d", column.name(), row)
                        .isEqualTo(leaves.get(row).asInt());
            }
        }

    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldRouteMissingValuesByMissingGoToLeft(boolean missingGoToLeft) {

        var tree = new DecisionTree(new BeanModelDocument.TreeDocument(
                new int[]{1, -1, -1},
                new int[]{2, -1, -1},
                new int[]{0, -2, -2},
                new double[]{0.5, -2, -2},
                new boolean[]{missingGoToLeft, false, false}
        ));

        assertThat(tree.apply(new double[]{Double.NaN})).isEqualTo(missingGoToLeft ? 1 : 2);
        assertThat(tree.apply(new double[]{0.5})).isEqualTo(1);
        assertThat(tree.apply(new double[]{Math.nextUp(0.5f)})).isEqualTo(2);
        assertThat(tree.apply(new double[]{Math.nextUp(0.5)})).as("float32 cast like sklearn").isEqualTo(1);

    }

    @ParameterizedTest
    @ValueSource(ints = {0, 3})
    void shouldRejectChildrenThatDoNotPointForward(int invalidChild) {

        var document = new BeanModelDocument.TreeDocument(
                new int[]{invalidChild, -1, -1},
                new int[]{2, -1, -1},
                new int[]{0, -2, -2},
                new double[]{0.5, -2, -2},
                null
        );

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new DecisionTree(document));

    }
}
