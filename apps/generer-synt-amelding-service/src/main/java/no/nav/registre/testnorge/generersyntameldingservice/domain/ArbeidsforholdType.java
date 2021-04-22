package no.nav.registre.testnorge.generersyntameldingservice.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArbeidsforholdType {

    MARITIMT("maritimtArbeidsforhold"),
    ORDINAERT("ordinaertArbeidsforhold");

    private static final Map<String, ArbeidsforholdType> lookup = new HashMap<>();

    static {
        for (ArbeidsforholdType a : ArbeidsforholdType.values()) {
            lookup.put(a.getBeskrivelse(), a);
        }
    }

    private final String beskrivelse;

    public static String getPath(String beskrivelse) {
        return lookup.get(beskrivelse).toString().toLowerCase();
    }
}
