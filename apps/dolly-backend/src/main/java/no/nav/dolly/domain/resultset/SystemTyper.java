package no.nav.dolly.domain.resultset;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum SystemTyper {

    AAREG("Arbeidsregister (AAREG)"),
    ANNEN_FEIL("Annen Feil"),
    ARBEIDSPLASSENCV("Arbeidsplassen CV"),
    ARBEIDSSOEKERREGISTERET("Arbeidssøkerregisteret"),
    ARENA("Arena fagsystem"),
    ARENA_AAP("Arena AAP ytelse"),
    ARENA_AAP115("Arena AAP115 rettighet"),
    ARENA_BRUKER("Arena bruker"),
    ARENA_DAGP("Arena dagpenger"),
    BRREGSTUB("Brønnøysundregistrene (BRREGSTUB)"),
    DOKARKIV("Dokumentarkiv (JOARK)"),
    ETTERLATTE("Etterlatte (Gjenny)"),
    FULLMAKT("Fullmakt (Representasjon)"),
    HISTARK("Saksmappearkiv (HISTARK)"),
    INNTK("Inntektskomponenten (INNTK)"),
    INNTKMELD("Inntektsmelding (ALTINN/JOARK)"),
    INST2("Institusjonsopphold (INST2)"),
    KONTOREGISTER("Bankkontoregister"),
    KRRSTUB("Digital kontaktinformasjon (DKIF)"),
    MEDL("Medlemskap (MEDL)"),
    ORGANISASJON_FORVALTER("Enhetsregisteret (EREG)"),
    PDLIMPORT("Import av personer (TESTNORGE)"),
    PDL_FORVALTER("Opprett persondetaljer"),
    PDL_ORDRE("Ordre til PDL"),
    PDL_PERSONSTATUS("Person finnes i PDL"),
    PEN_AFP_OFFENTLIG("AFP offentlig (PEN)"),
    PEN_ANNET("Pensjon (PEN)"),
    PEN_AP("Alderspensjon (AP)"),
    PEN_AP_REVURDERING("Revurdering alderspensjon (AP)"),
    PEN_FORVALTER("Pensjon persondata (PEN)"),
    PEN_INNTEKT("Pensjonsopptjening (POPP)"),
    PEN_PENSJONSAVTALE("Pensjonsavtale (PEN)"),
    PEN_SAMBOER("Pensjon samboerregister"),
    PEN_UT("Uføretrygd (UT)"),
    SIGRUNSTUB("Skatteinntekt grunnlag (SIGRUN)"),
    SIGRUN_LIGNET("Lignet skatteinntekt (Sigrunstub)"),
    SIGRUN_PENSJONSGIVENDE("Pensjonsgivende inntekt (Sigrunstub)"),
    SIGRUN_SUMMERT("Summert skattegrunnlag (Sigrunstub)"),
    SKATTEKORT("Skattekort (SOKOS)"),
    SKJERMINGSREGISTER("Skjermingsregisteret"),
    SYKEMELDING("NAV sykemelding"),
    TPS_MESSAGING("Meldinger til TPS"),
    TP_FORVALTER("Tjenestepensjon (TP)"),
    UDISTUB("Utlendingsdirektoratet (UDI)"),
    YRKESSKADE("Yrkesskade");

    private String beskrivelse;

    SystemTyper(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
