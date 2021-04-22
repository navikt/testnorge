package no.nav.registre.testnorge.generersyntameldingservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArbeidsforholdType {

    maritimtArbeidsforhold("maritimt"),
    ordinaertArbeidsforhold("ordinaert");

    private final String path;
}
