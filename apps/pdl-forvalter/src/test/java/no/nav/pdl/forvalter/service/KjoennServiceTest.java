package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO.Kjoenn.KVINNE;
import static no.nav.testnav.libs.data.pdlforvalter.v1.KjoennDTO.Kjoenn.MANN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class KjoennServiceTest {

    private static final String IDENT_MANN = "12345678901";
    private static final String IDENT_KVINNE = "12345678801";
    private static final String IDENT_ID2032 = "22502599902";

    @InjectMocks
    private KjoennService kjoennService;

    @Test
    void whenEmptyAndIdentIsKvinne_thenProvideKjoennIsKvinne() {

        var target = kjoennService.convert(
                PersonDTO.builder()
                        .kjoenn(List.of(KjoennDTO.builder().isNew(true).build()))
                        .ident(IDENT_KVINNE)
                        .build()).getFirst();

        assertThat(target.getKjoenn(), is(equalTo(KVINNE)));
    }

    @Test
    void whenEmptyAndIdentIsMann_thenProvideKjoennIsMann() {

        var target = kjoennService.convert(
                PersonDTO.builder()
                        .kjoenn(List.of(KjoennDTO.builder().isNew(true).build()))
                        .ident(IDENT_MANN)
                        .build()).getFirst();

        assertThat(target.getKjoenn(), is(equalTo(MANN)));
    }

    @Test
    void whenKjoennIsMannAndIdentIsKvinne_thenProvideKjoennIsMann() {

        var target = kjoennService.convert(
                PersonDTO.builder()
                        .kjoenn(List.of(KjoennDTO.builder()
                                .kjoenn(MANN)
                                .isNew(true)
                                .build()))
                        .ident(IDENT_KVINNE)
                        .build()).getFirst();

        assertThat(target.getKjoenn(), is(equalTo(MANN)));
    }

    @Test
    void whenKjoennIsKvinneAndIdentIsMann_thenProvideKjoennIsKvinne() {

        var target = kjoennService.convert(
                PersonDTO.builder()
                        .kjoenn(List.of(KjoennDTO.builder()
                                .kjoenn(KVINNE)
                                .isNew(true)
                                .build()))
                        .ident(IDENT_MANN)
                        .build()).getFirst();

        assertThat(target.getKjoenn(), is(equalTo(KVINNE)));
    }

    @Test
    void whhenIdentIsId2032_thenProvideKjoennFromKjoennUtility() {

        var target = kjoennService.convert(
                PersonDTO.builder()
                        .kjoenn(List.of(KjoennDTO.builder().isNew(true).build()))
                        .ident(IDENT_ID2032)
                        .build()).getFirst();

        assertThat(target.getKjoenn(), is(in(KjoennDTO.Kjoenn.values())));
    }
}