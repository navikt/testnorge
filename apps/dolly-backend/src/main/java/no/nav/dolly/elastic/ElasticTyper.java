package no.nav.dolly.elastic;

public enum ElasticTyper {

    AAREG("Arbeidsregister (AAREG)"),
    INST("Institusjonsopphold (INST2)"),
    KRRSTUB("Digital kontaktinformasjon (DKIF)"),
    SIGRUN_LIGNET("Lignet skatteinntekt (Sigrunstub)"),
    SIGRUN_PENSJONSGIVENDE("Pensjonsgivende inntekt (Sigrunstub)"),
    ARENA_AAP("Arena AAP ytelse"),
    ARENA_AAP115("Arena AAP115 rettighet"),
    ARENA_DAGP("Arena dagpenger"),
    UDISTUB("Utlendingsdirektoratet (UDI)"),
    INNTK("Inntektskomponenten (INNTK)"),
    PEN_INNTEKT("Pensjonsopptjening (POPP)"),
    PEN_TP("Tjenestepensjon (TP)"),
    PEN_AP("Alderspensjon (AP)"),
    PEN_UT("Uføretrygd (UT)"),
    PEN_AFP_OFFENTLIG("AFP offentlig (PEN"),
    PEN_PENSJONSAVTALE("Pensjonsavtaler (PEN)"),
    INNTKMELD("Inntektsmelding (ALTINN/JOARK)"),
    BRREGSTUB("Brønnøysundregistrene (BRREGSTUB)"),
    DOKARKIV("Dokumentarkiv (JOARK)"),
    FULLMAKT("Fullmakt (Representasjon)"),
    MEDL("Medlemskap (MEDL)"),
    HISTARK("Saksmappearkiv (HISTARK)"),
    SYKEMELDING("NAV sykemelding"),
    SKJERMING("Skjermingsregisteret"),
    BANKKONTO("Bankkontoregister"),
    BANKKONTO_NORGE("Norsk bankkonto"),
    BANKKONTO_UTLAND("Utenlandsk bankkonto"),
    ARBEIDSPLASSENCV("Arbeidsplassen CV"),
    SKATTEKORT("SOKOS"),
    YRKESSKADE("Yrkesskade");

    private final String beskrivelse;

    ElasticTyper(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
