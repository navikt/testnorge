package no.nav.dolly.synt.meldekort.generator;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class MeldekortLayoutTest {

    @Test
    void shouldOrderNumberedDayGroupsNumericallyWithExactGroupMatch() {

        var columns = new ArrayList<>(MeldekortLayout.HEAD_COLUMNS);
        var dayColumns = new ArrayList<String>();
        IntStream.rangeClosed(1, 14).forEach(day -> dayColumns.addAll(dayColumns(String.valueOf(day))));
        Collections.reverse(dayColumns);
        columns.addAll(dayColumns);

        var layout = new MeldekortLayout(columns);

        assertThat(layout.dayColumns()).hasSize(14);
        assertThat(layout.dayColumns().stream().map(fields -> fields.get("timer")).toList())
                .containsExactlyElementsOf(IntStream.rangeClosed(1, 14).mapToObj(day -> "TIMER_" + day).toList());
        assertThat(layout.dayColumns().getFirst()).doesNotContainValue("TIMER_10");

    }

    @Test
    void shouldKeepFirstAppearanceOrderForNonNumberedGroups() {

        var groups = List.of("MAN", "TIR", "ONS", "TOR", "FRE", "LOR", "SON", "MANB", "TIRB", "ONSB", "TORB", "FREB", "LORB", "SONB");
        var columns = new ArrayList<>(MeldekortLayout.HEAD_COLUMNS);
        groups.forEach(group -> columns.addAll(dayColumns(group)));

        var layout = new MeldekortLayout(columns);

        assertThat(layout.dayColumns().stream().map(fields -> fields.get("kurs")).toList())
                .containsExactlyElementsOf(groups.stream().map(group -> "KURS_" + group).toList());

    }

    @Test
    void shouldRejectWrongNumberOfDays() {

        var columns = new ArrayList<>(MeldekortLayout.HEAD_COLUMNS);
        IntStream.rangeClosed(1, 13).forEach(day -> columns.addAll(dayColumns("D" + day)));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MeldekortLayout(columns))
                .withMessageContaining("13 day groups");

    }

    @Test
    void shouldRejectDayGroupWithoutRequiredField() {

        var columns = new ArrayList<>(MeldekortLayout.HEAD_COLUMNS);
        IntStream.rangeClosed(1, 14).forEach(day -> columns.addAll(dayColumns("D" + day)));
        columns.remove("KURS_D3");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MeldekortLayout(columns))
                .withMessageContaining("D3 is missing field kurs");

    }

    @Test
    void shouldRejectMissingHeadColumn() {

        var columns = new ArrayList<>(MeldekortLayout.HEAD_COLUMNS);
        columns.remove("FORSKUDD");
        IntStream.rangeClosed(1, 14).forEach(day -> columns.addAll(dayColumns("D" + day)));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MeldekortLayout(columns))
                .withMessageContaining("FORSKUDD");

    }

    private static List<String> dayColumns(String group) {
        return List.of("TIMER_" + group, "SYK_" + group, "ANNETFRAVAER_" + group, "KURS_" + group, "MELDEGRUPPE_" + group);
    }

}
