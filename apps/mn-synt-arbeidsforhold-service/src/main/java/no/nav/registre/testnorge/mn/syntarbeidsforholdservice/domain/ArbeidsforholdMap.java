package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;


public class ArbeidsforholdMap {
    private final String ident;
    private final Map<LocalDate, Arbeidsforhold> map;

    public ArbeidsforholdMap(String ident, Map<LocalDate, Arbeidsforhold> map) {
        this.ident = ident;
        this.map = map;
    }

    public boolean isNewArbeidsforhold(LocalDate date) {
        var arbeidsforhold = getArbeidsforhold(date);
        return arbeidsforhold.getStartdato().getMonthValue() == date.getMonthValue()
                && arbeidsforhold.getStartdato().getYear() == date.getYear();
    }

    public Arbeidsforhold getArbeidsforhold(LocalDate date) {
        return Optional.ofNullable(map.get(date))
                .orElseThrow(() -> new RuntimeException("Finner ikke arbeidsforhold for dato=" + date));
    }

    public boolean contains(LocalDate date) {
        return Optional.ofNullable(map.get(date)).isPresent();
    }


    public String getIdent() {
        return ident;
    }
}
