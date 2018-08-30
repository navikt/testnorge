package no.nav.identpool.navnepool.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ValidFornavn {
    BLAA("Blå"),
    GUL("Gul"),
    GROENN("Grønn"),
    RASK("Rask"),
    DØLL("Døll"),
    ARTIG("Artig"),
    STOR("Stor"),
    KRIMINELL("Kriminell"),
    LUGUBER("Luguber"),
    SMEKKER("Smekker"),
    GLITRENDE("Glitrende"),
    RAUS("Raus"),
    NATURLIG("Naturlig"),
    SMART("Smart"),
    KREATIV("Kreativ");

    String fornavn;

    public static List<String> getNavnList() {
        return Arrays.stream(ValidFornavn.values()).map(ValidFornavn::getFornavn).collect(Collectors.toList());
    }
}
