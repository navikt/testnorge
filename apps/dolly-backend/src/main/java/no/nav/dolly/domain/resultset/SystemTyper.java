package no.nav.dolly.domain.resultset;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum SystemTyper {

    AAREG("Arbeidsregister (AAREG)"),
    INST2("Institusjonsopphold (INST2)"),
    KRRSTUB("Digital kontaktinformasjon (DKIF)"),
    SIGRUNSTUB("Skatteinntekt grunnlag (SIGRUN)"),
    SIGRUN_LIGNET("Lignet skatteinntekt (Sigrunstub)"),
    SIGRUN_PENSJONSGIVENDE("Pensjonsgivende inntekt (Sigrunstub)"),
    ARENA("Arena fagsystem"),
    ARENA_BRUKER("Arena bruker"),
    ARENA_AAP("Arena AAP ytelse"),
    ARENA_AAP115("Arena AAP115 rettighet"),
    ARENA_DAGP("Arena dagpenger"),
    UDISTUB("Utlendingsdirektoratet (UDI)"),
    INNTK("Inntektskomponenten (INNTK)"),
    PEN_FORVALTER("Pensjon persondata (PEN)"),
    PEN_INNTEKT("Pensjonsopptjening (POPP)"),
    TP_FORVALTER("Tjenestepensjon (TP)"),
    PEN_AP("Alderspensjon (AP)"),
    PEN_UT("Uføretrygd (UT)"),
    PEN_SAMBOER("Pensjon samboerregister"),
    INNTKMELD("Inntektsmelding (ALTINN/JOARK)"),
    BRREGSTUB("Brønnøysundregistrene (BRREGSTUB)"),
    DOKARKIV("Dokumentarkiv (JOARK)"),
    MEDL("Medlemskap (MEDL)"),
    HISTARK("Saksmappearkiv (HISTARK)"),
    TPS_MESSAGING("Meldinger til TPS"),
    SYKEMELDING("NAV sykemelding"),
    PDLIMPORT("Import av identer (TESTNORGE)"),
    SKJERMINGSREGISTER("Skjermingsregisteret"),
    ORGANISASJON_FORVALTER("Enhetsregisteret (EREG)"),
    PDL_FORVALTER("Persondetaljer"),
    PDL_ORDRE("Ordre til PDL"),
    KONTOREGISTER("Bankkontoregister"),
    PDL_PERSONSTATUS("Person finnes i PDL"),
    TPS_STATUS("Person finnes i TPS"),
    ANNEN_FEIL("Annen Feil"),
    ARBEIDSPLASSENCV("Arbeidsplassen CV");

    private String beskrivelse;

    SystemTyper(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
