package no.nav.identpool.tps.xml;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AksjonKode {

    NONPROD("0"),
    PROD("2");

    private final String kode;
}
