package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;

import java.time.LocalDate;
import java.util.UUID;

import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v2.ArbeidsforholdDTO;

public class Arbeidsforhold {
    private final String ident;
    private final String yrke;

    private Arbeidsforhold(String ident, String yrke) {
        this.ident = ident;
        this.yrke = yrke;
    }

    public static Arbeidsforhold from(String ident, String yrke) {
        return new Arbeidsforhold(ident, yrke);
    }

    public String getIdent() {
        return ident;
    }

    public ArbeidsforholdDTO toDefaultDTO() {
        LocalDate startdato = LocalDate.now();
        return ArbeidsforholdDTO
                .builder()
                .typeArbeidsforhold("ordinaertArbeidsforhold")
                .antallTimerPerUke(37.5f)
                .arbeidstidsordning("ikkeSkift")
                .sisteLoennsendringsdato(startdato)
                .stillingsprosent(100.0f)
                .yrke(yrke)
                .startdato(startdato)
                .arbeidsforholdId(UUID.randomUUID().toString())
                .build();
    }

}
