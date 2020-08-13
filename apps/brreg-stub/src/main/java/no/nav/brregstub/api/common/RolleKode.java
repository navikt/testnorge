package no.nav.brregstub.api.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RolleKode {
    BEST("Bestyrende reder"),
    BOBE("Bostyrer"),
    DAGL("Daglig leder/ adm direktør"),
    DELT("Deltakere"),
    DTPR("Deltaker med delt ansvar"),
    DTSO("Deltaker med fullt ansvar"),
    FFØR("Forretningsfører"),
    INNH("Innehaver"),
    KOMP("Komplementar"),
    KONT("Kontaktperson"),
    LEDE("Styrets leder"),
    MEDL("Styremedlem"),
    NEST("Nestleder"),
    OBS("Observatør"),
    PROK("Prokura"),
    REGN("Regnskapsfører"),
    REPR("Norsk repr. for utenl. enhet"),
    SIGN("Signatur"),
    STYR("Styre"),
    SAM("Sameiere"),
    VARA("Varamedlem");
    private final String beskrivelse;
}
