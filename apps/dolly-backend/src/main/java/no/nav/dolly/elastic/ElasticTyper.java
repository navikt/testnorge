package no.nav.dolly.elastic;

public enum ElasticTyper {

    AAREG("Arbeidsregister (AAREG)"),
    INST("Institusjonsopphold (INST2)"),
    KRRSTUB("Digital kontaktinformasjon (DKIF)"),
    SIGRUN_LIGNET("Lignet skatteinntekt (Sigrunstub)"),
    SIGRUN_PENSJONSGIVENDE("Pensjonsgivende inntekt (Sigrunstub)"),
    ARENA_BRUKER("Arena bruker"),
    ARENA_AAP("Arena AAP ytelse"),
    ARENA_AAP115("Arena AAP115 rettighet"),
    ARENA_DAGP("Arena dagpenger"),
    UDISTUB("Utlendingsdirektoratet (UDI)"),
    INNTK("Inntektskomponenten (INNTK)"),
    PEN_INNTEKT("Pensjonsopptjening (POPP)"),
    PEN_TP("Tjenestepensjon (TP)"),
    PEN_AP("Alderspensjon (AP)"),
    PEN_UT("Uføretrygd (UT)"),
    INNTKMELD("Inntektsmelding (ALTINN/JOARK)"),
    BRREGSTUB("Brønnøysundregistrene (BRREGSTUB)"),
    DOKARKIV("Dokumentarkiv (JOARK)"),
    MEDL("Medlemskap (MEDL)"),
    HISTARK("Saksmappearkiv (HISTARK)"),
    SYKEMELDING("NAV sykemelding"),
    SKJERMING("Skjermingsregisteret"),
    PDLPERSON("Persondetaljer"),
    BANKKONTO("Bankkontoregister"),
    ARBEIDSPLASSENCV("Arbeidsplassen CV");

    private final String beskrivelse;

    ElasticTyper(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
