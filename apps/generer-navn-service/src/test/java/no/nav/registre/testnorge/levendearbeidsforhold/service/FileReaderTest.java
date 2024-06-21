package no.nav.registre.testnorge.levendearbeidsforhold.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class FileReaderTest {

    @Test
    void testThatContentsAreCorrectlyRead() {
        var contents = FileReader.readLinesFromResources("static/test.csv");
        assertThat(contents)
                .hasSize(3)
                .containsExactly(
                        "one",
                        "two",
                        "three"
                );
    }

    @Test
    void testThatNonexistingResourceCausesException() {
        assertThatThrownBy(() -> FileReader.readLinesFromResources("static/does-not-exist.csv"))
                .isInstanceOf(IOException.class)
                .hasMessage("Unable to find file static/does-not-exist.csv");
    }

}
