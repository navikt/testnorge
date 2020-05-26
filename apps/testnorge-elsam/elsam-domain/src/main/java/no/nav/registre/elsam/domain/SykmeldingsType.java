package no.nav.registre.elsam.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum SykmeldingsType {
    AVVENTENDE,
    GRADERT_20,
    GRADERT_40,
    GRADERT_50,
    GRADERT_60,
    GRADERT_80,
    GRADERT_REISETILSKUDD,
    HUNDREPROSENT,
    BEHANDLINGSDAGER,
    BEHANDLINGSDAG,
    REISETILSKUDD;

    private static final Map<String, SykmeldingsType> prosentTilType = Collections.unmodifiableMap(initializeMapping());

    public static SykmeldingsType findSykmeldingsType(Double sykmeldingsprosent) {
        SykmeldingsType sykmeldingsType = prosentTilType.get(String.valueOf(sykmeldingsprosent));
        if (sykmeldingsType == null) {
            throw new RuntimeException("Sykmeldingsprosent kunne ikke mappes til SykmeldingsType. Sykmeldingsprosent: " + sykmeldingsprosent);
        } else {
            return sykmeldingsType;
        }
    }

    private static Map<String, SykmeldingsType> initializeMapping() {
        Map<String, SykmeldingsType> mapping = new HashMap<>();
        mapping.put("20.0", GRADERT_20);
        mapping.put("40.0", GRADERT_40);
        mapping.put("50.0", GRADERT_50);
        mapping.put("60.0", GRADERT_60);
        mapping.put("80.0", GRADERT_80);
        mapping.put("100.0", HUNDREPROSENT);
        return mapping;
    }
}
