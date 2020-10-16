package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import java.util.Collections;
import java.util.List;

import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Permisjon;

public class Permisjoner {
    private final List<Permisjon> list;

    public Permisjoner(List<Permisjon> list) {
        this.list = list != null ? list : Collections.emptyList();
    }

    private int antall(String beskrivelse) {
        return list.stream()
                .filter(value -> value.getBeskrivelse().equals(beskrivelse))
                .toArray()
                .length;
    }

    public int getAntallVelferdspermisjon() {
        return antall("velferdspermisjon");
    }

    public int getAntallPermisjonMedForeldrepenger() {
        return antall("permisjonMedForeldrepenger");
    }

    public int getAntallPermittering() {
        return antall("permittering");
    }

    public int getAntallPermisjon() {
        return antall("permisjon");
    }

    public int getAntallPermisjonVedMilitaertjeneste() {
        return antall("permisjonVedMilitaertjeneste");
    }

    public int getAntallUtdanningspermisjon() {
        return antall("utdanningspermisjon");
    }
}
