package no.nav.registre.ereg.domain;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.ereg.provider.rs.request.NavnRs;

public class Navn extends Flatfil {
    private final String record;

    public Navn(String record) {
        this.record = record;
    }

    public NavnRs toNavnRs() {
        List<String> navneListe = Stream.of(getNavn1(), getNavn2(), getNavn3(), getNavn4(), getNavn5())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return NavnRs
                .builder()
                .navneListe(navneListe)
                .redNavn(getRedNavn())
                .build();
    }

    private String getNavn1() {
        return getValueFromRecord(record, 8, 43);
    }

    private String getNavn2() {
        return getValueFromRecord(record, 43, 78);
    }

    private String getNavn3() {
        return getValueFromRecord(record, 78, 113);
    }

    private String getNavn4() {
        return getValueFromRecord(record, 113, 148);
    }

    private String getNavn5() {
        return getValueFromRecord(record, 148, 183);
    }

    private String getRedNavn() {
        return getValueFromRecord(record, 183, 219);
    }
}
