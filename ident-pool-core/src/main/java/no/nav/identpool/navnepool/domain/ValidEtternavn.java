package no.nav.identpool.navnepool.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ValidEtternavn {
    ERT("Ert"),
    HEST("Hest"),
    DORULL("Dorull"),
    HATT("Hatt"),
    MASKIN("Maskin"),
    KAFFI("Kaffi"),
    KAKE("Kake"),
    POTET("Potet"),
    KONSOLL("Konsoll"),
    BAEREPOSE("Bærepose"),
    BLYANT("Blyant"),
    PENN("Penn"),
    BOLLE("Bolle"),
    SAKS("Saks"),
    KOPP("Kopp");

    String etternavn;

    public static List<String> getNavnList() {
        return Arrays.stream(ValidEtternavn.values()).map(ValidEtternavn::getEtternavn).collect(Collectors.toList());
    }
}
