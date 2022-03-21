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
    TPSF("Tjenestebasert personsystem (TPS)"),
    INST2("Institusjonsopphold (INST2)"),
    KRRSTUB("Digital kontaktinformasjon (DKIF)"),
    NOM("NAV Organisasjonsmaster (NOM)"),
    SIGRUNSTUB("Skatteinntekt grunnlag (SIGRUN)"),
    ARENA("Arena fagsystem"),
    PDL_FORVALTER("Persondataløsningen (PDL)"),
    PDL_FALSKID("Falsk identitet (PDL)"),
    PDL_DODSBO("Kontaktinformasjon dødsbo (PDL)"),
    PDL_UTENLANDSID("Utenlandsk id (PDL)"),
    UDISTUB("Utlendingsdirektoratet (UDI)"),
    INNTK("Inntektskomponenten (INNTK)"),
    PEN_FORVALTER("Pensjon (PEN)"),
    PEN_INNTEKT("Pensjonsopptjening (POPP)"),
    INNTKMELD("Inntektsmelding (ALTINN/JOARK)"),
    BRREGSTUB("Brønnøysundregistrene (BRREGSTUB)"),
    DOKARKIV("Dokumentarkiv (JOARK)"),
    TPS_MESSAGING("Meldinger til TPS"),
    SYKEMELDING("Testnorge Sykemelding"),
    TPSIMPORT("Import av identer (MINI-NORGE)"),
    PDLIMPORT("Import av identer (TESTNORGE)"),
    SKJERMINGSREGISTER("Skjermingsregisteret"),
    SAKOGARKIV("Sak og arkivfasade (SAF)"),
    ORGANISASJON_FORVALTER("Enhetsregisteret (EREG)"),
    PDL_DATA("Persondataløsning (PDL) ny");

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
