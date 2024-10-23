package no.nav.dolly.elastic;

import lombok.Getter;

@Getter
public enum ElasticTyper {

    // Forklaring i parantes er det som vises i frontend
    // For at alfabetisk rekkefølge skal beholdes setter dette føring på forkalringen

    AAREG("Arbeidsgiver/arbeidstaker-register (AAREG)"),
    ARBEIDSPLASSENCV("Arbeidsplassen CV"),
    ARENA_AAP("Arena AAP ytelse"),
    ARENA_AAP115("Arena AAP115 rettighet"),
    ARENA_DAGP("Arena dagpenger"),
    BANKKONTO("Bankkontoregister"),
    BANKKONTO_NORGE("Bankkonto i Norge"),
    BANKKONTO_UTLAND("Bankkonto i utlandet"),
    BRREGSTUB("Brønnøysundregistrene (BRREGSTUB)"),
    DOKARKIV("Dokumentarkiv (JOARK)"),
    FULLMAKT("Fullmakt (Representasjon)"),
    HISTARK("Historisk arkiv (HISTARK)"),
    INNTK("Inntektskomponenten/stub (INNTK)"),
    INNTKMELD("Inntektsmelding (ALTINN/JOARK)"),
    INST("Institusjonsopphold (INST2)"),
    KRRSTUB("Kontakt- og reservasjonsregister-stub"),
    MEDL("Medlemskap (MEDL)"),
    PEN_AFP_OFFENTLIG("Pensjon - AFP offentlig"),
    PEN_AP("Pensjon - Alderspensjon (AP)"),
    PEN_INNTEKT("Pensjon - Pensjonsinntekt/opptjening"),
    PEN_PENSJONSAVTALE("Pensjon - Pensjonsavtaler"),
    PEN_TP("Pensjon - Tjenestepensjon (TP)"),
    PEN_UT("Pensjon - Uføretrygd (UT)"),
    SIGRUN_LIGNET("Sigrunstub - Lignet skatteinntekt"),
    SIGRUN_PENSJONSGIVENDE("Sigrunstub - Pensjonsgivende inntekt"),
    SKATTEKORT("Skattekort (SOKOS)"),
    SKJERMING("Skjermingsregisteret"),
    SYKEMELDING("Sykemelding"),
    UDISTUB("Udistub - Utlendingsdirektoratet"),
    YRKESSKADE("Yrkesskade");

    private final String beskrivelse;

    ElasticTyper(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
