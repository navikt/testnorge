package no.nav.registre.ereg.domain;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.ereg.provider.rs.request.AdresseRs;


public class Adresse extends Flatfil {
    private final String record;

    public Adresse(String record) {
        this.record = record;
    }

    public AdresseRs toAdresseRs() {
        List<String> adresser = Stream.of(getPostadr1(), getPostadr2(), getPostadr3(), getLinjenummer(), getVegadresseid())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return AdresseRs.builder()
                .postnr(getPostnr())
                .poststed(getPoststed())
                .kommunenr(getKommunenr())
                .landkode(getLandkode())
                .adresser(adresser)
                .build();
    }

    private String getPostnr() {
        return getValueFromRecord(record, 8, 17);
    }

    private String getLandkode() {
        return getValueFromRecord(record, 17, 20);
    }

    private String getKommunenr() {
        return getValueFromRecord(record, 20, 29);
    }

    private String getPoststed() {
        return getValueFromRecord(record, 29, 64);
    }

    private String getPostadr1() {
        return getValueFromRecord(record, 64, 99);
    }

    private String getPostadr2() {
        return getValueFromRecord(record, 99, 134);
    }

    private String getPostadr3() {
        return getValueFromRecord(record, 134, 169);
    }

    private String getLinjenummer() {
        return getValueFromRecord(record, 169, 170);
    }

    private String getVegadresseid() {
        return getValueFromRecord(record, 170, 185);
    }
}
