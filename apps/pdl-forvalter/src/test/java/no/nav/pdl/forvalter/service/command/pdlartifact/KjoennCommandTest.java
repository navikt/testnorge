package no.nav.pdl.forvalter.service.command.pdlartifact;

import no.nav.pdl.forvalter.domain.PdlKjoenn;
import org.junit.jupiter.api.Test;

import java.util.List;

import static no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn.KVINNE;
import static no.nav.pdl.forvalter.domain.PdlKjoenn.Kjoenn.MANN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class KjoennCommandTest {

    private static final String IDENT_MANN = "12345678901";
    private static final String IDENT_KVINNE = "12345678801";

    @Test
    void whenEmptyAndIdentIsKvinne_thenProvideKjoennIsKvinne() {

        var target = new KjoennCommand(List.of(PdlKjoenn.builder().isNew(true).build()), IDENT_KVINNE).call().get(0);

        assertThat(target.getKjoenn(), is(equalTo(KVINNE)));
    }

    @Test
    void whenEmptyAndIdentIsMann_thenProvideKjoennIsMann() {

        var target = new KjoennCommand(List.of(PdlKjoenn.builder().isNew(true).build()), IDENT_MANN).call().get(0);

        assertThat(target.getKjoenn(), is(equalTo(MANN)));
    }

    @Test
    void whenKjoennIsMannAndIdentIsKvinne_thenProvideKjoennIsMann() {

        var target = new KjoennCommand(List.of(PdlKjoenn.builder()
                .kjoenn(MANN)
                .isNew(true)
                .build()), IDENT_KVINNE).call().get(0);

        assertThat(target.getKjoenn(), is(equalTo(MANN)));
    }

    @Test
    void whenKjoennIsKvinneAndIdentIsMann_thenProvideKjoennIsKvinne() {

        var target = new KjoennCommand(List.of(PdlKjoenn.builder()
                .kjoenn(KVINNE)
                .isNew(true)
                .build()), IDENT_MANN).call().get(0);

        assertThat(target.getKjoenn(), is(equalTo(KVINNE)));
    }
}