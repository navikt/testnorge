package no.nav.dolly.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.dolly.util.EnvironmentsCrossConnect.Type.Q1_AND_Q2;
import static no.nav.dolly.util.EnvironmentsCrossConnect.Type.Q4_TO_Q1;
import static no.nav.dolly.util.EnvironmentsCrossConnect.crossConnect;
import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentsCrossConnectTest {

    @Test
    @DisplayName("Manglende Q4 fører til at Q1 ikke blir lagt til")
    void nothingAddedIfMissingQ4() {
        var environments = List.of("q2", "q3");
        assertThat(crossConnect(environments, Q4_TO_Q1)).containsAll(List.of("q2", "q3"));
    }

    @Test
    @DisplayName("Q4 fører til at Q1 blir lagt til")
    void q1AddedFromQ4() {
        var environments = List.of("q3", "q4");
        assertThat(crossConnect(environments, Q4_TO_Q1)).containsAll(List.of("q1", "q3", "q4"));
    }

    @Test
    @DisplayName(("Q4 fører ikke til at Q1 blir duplisert"))
    void q1NotDuplicatedFromQ4() {
        var environments = List.of("q1", "q3", "q4");
        assertThat(crossConnect(environments, Q4_TO_Q1)).containsAll(List.of("q1", "q3", "q4"));
    }

    @Test
    @DisplayName("Manglende Q1 og manglende Q2 fører til ikke til krysskobling")
    void q1AndQ2AddedFromQ1() {
        var environments = List.of("q3", "q4");
        assertThat(crossConnect(environments, Q1_AND_Q2)).containsAll(List.of("q3", "q4"));
    }

    @Test
    @DisplayName("Q1 fører til at Q2 blir krysskoblet")
    void q2AddedFromQ1() {
        var environments = List.of("q1", "q3");
        assertThat(crossConnect(environments, Q1_AND_Q2)).containsAll(List.of("q1", "q2", "q3"));
    }

    @Test
    @DisplayName("Q2 fører til at Q1 blir krysskoblet")
    void q1AddedFromQ2() {
        var environments = List.of("q2", "q3");
        assertThat(crossConnect(environments, Q1_AND_Q2)).containsAll(List.of("q1", "q2", "q3"));
    }

    @Test
    @DisplayName("Q1 og Q2 blir ikke duplisert")
    void q1Andq2DoesNotDuplicate() {
        var environments = List.of("q1", "q2");
        assertThat(crossConnect(environments, Q1_AND_Q2)).containsAll(List.of("q1", "q2"));
    }

}
