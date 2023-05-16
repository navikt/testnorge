package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public enum SystemTyper {

    AAREG("Arbeidsregister (AAREG)"),
    INST2("Institusjonsopphold (INST2)"),
    KRRSTUB("Digital kontaktinformasjon (DKIF)"),
    SIGRUNSTUB("Skatteinntekt grunnlag (SIGRUN)"),
    ARENA("Arena fagsystem"),
    UDISTUB("Utlendingsdirektoratet (UDI)"),
    INNTK("Inntektskomponenten (INNTK)"),
    PEN_FORVALTER("Pensjon persondata (PEN)"),
    PEN_INNTEKT("Pensjonsopptjening (POPP)"),
    TP_FORVALTER("Tjenestepensjon (TP)"),
    PEN_AP("Alderspensjon (AP)"),
    INNTKMELD("Inntektsmelding (ALTINN/JOARK)"),
    BRREGSTUB("Brønnøysundregistrene (BRREGSTUB)"),
    DOKARKIV("Dokumentarkiv (JOARK)"),
    MEDL("Medlemskapsperioder (MEDL)"),
    HISTARK("Saksmappearkiv (HISTARK)"),
    TPS_MESSAGING("Meldinger til TPS"),
    SYKEMELDING("NAV Sykemelding"),
    PDLIMPORT("Import av identer (TESTNORGE)"),
    SKJERMINGSREGISTER("Skjermingsregisteret"),
    SAKOGARKIV("Sak og arkivfasade (SAF)"),
    ORGANISASJON_FORVALTER("Enhetsregisteret (EREG)"),
    PDL_FORVALTER("Opprettet person"),
    PDL_ORDRE("Ordre til PDL"),
    KONTOREGISTER("Bankkontoregister"),
    PDL_PERSONSTATUS("Person finnes i PDL"),
    ANNEN_FEIL("Annen Feil"),
    ARBEIDSPLASSENCV("Arbeidsplassen CV");

    private String beskrivelse;

    SystemTyper(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemBeskrivelse {

        private String system;
        private String beskrivelse;
    }
}
