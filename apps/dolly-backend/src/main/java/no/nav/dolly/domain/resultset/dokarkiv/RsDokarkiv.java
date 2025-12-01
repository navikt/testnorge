package no.nav.dolly.domain.resultset.dokarkiv;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsDokarkiv {

    @Schema(description = "Tittel som beskriver forsendelsen samlet, feks \"Søknad om dagpenger ved permittering\"")
    private String tittel;
    private JournalPostType journalpostType;
    @Schema(description = "Temaet som forsendelsen tilhører, for eksempel “DAG” (Dagpenger)")
    private String tema;
    @Schema(description = "Behandlingstema for forsendelsen, for eksempel ab0001 (Ordinære dagpenger).")
    private String behandlingstema;
    @Schema(description = "Kanalen som ble brukt ved innsending eller distribusjon. F.eks. NAV_NO, ALTINN eller EESSI.")
    private String kanal;
    @Schema(description = "NAV-enheten som har journalført, eventuelt skal journalføre, forsendelsen. "
            + "Ved automatisk journalføring uten mennesker involvert skal enhet settes til \"9999\".\n"
            + "Konsument må sette journalfoerendeEnhet dersom tjenesten skal ferdigstille journalføringen.")
    private String journalfoerendeEnhet;
    @Schema(description = "AvsenderMottaker")
    private AvsenderMottaker avsenderMottaker;
    @Schema(description = "Første dokument blir tilknyttet som hoveddokument på journalposten. "
            + "Øvrige dokumenter tilknyttes som vedlegg. Rekkefølgen på vedlegg beholdes ikke ved uthenting av journalpost.")
    private List<Dokument> dokumenter;
    @Schema(description = "Forsøker å ferdigstille dokument etter innsending")
    private Boolean ferdigstill;
    @Schema(description = "Saken i PSAK eller GSAK som dokumentene skal journalføres mot.")
    private Sak sak;

    public List<Dokument> getDokumenter() {
        if (isNull(dokumenter)) {
            dokumenter = new ArrayList<>();
        }
        return dokumenter;
    }

    public enum JournalPostType {

        INNGAAENDE, UTGAAENDE, NOTAT
    }

    public enum IdType {

        FNR, ORGNR, HPRNR, UTL_ORG
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class AvsenderMottaker {

        @Schema(description = "Identifikatoren til avsender/mottaker. "
                + "Dette er normalt et fødselsnummer eller organisasjonsnummer, men valideres ikke. "
                + "Dersom det ønskes å nullstille denne verdien, kan den settes til en tom string.")
        private String id;

        @Schema(description = "Angir hvilken type identifikator som er benyttet i AvsenderMottaker.id.\n" +
                "Påkrevd dersom id er satt. * FNR")
        private IdType idType;

        @Schema(description = "Navnet til avsender/mottaker.\n" +
                "Navn på personbrukere skal lagres på formatet etternavn, fornavn mellomnavn")
        private String navn;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Dokument {

        @Schema(description = """
                Dokumentets tittel, f.eks. "Søknad om dagpenger ved permittering".
                Dokumentets tittel blir synlig i brukers journal på nav.no, samt i Gosys.""")
        private String tittel;

        @Schema(description = """
                Typen dokument. Brevkoden sier noe om dokumentets innhold og oppbygning.
                "For inngående dokumenter kan brevkoden være en NAV-skjemaID f.eks. “NAV 04-01.04” eller en SED-id.
                "Brevkode skal ikke settes for ustrukturert, uklassifisert dokumentasjon, f.eks. brukeropplastede vedlegg.""")
        private String brevkode;

        @Schema(description = "DokumentVariant")
        private List<DokumentVariant> dokumentvarianter;

        public List<DokumentVariant> getDokumentvarianter() {
            if (isNull(dokumentvarianter)) {
                dokumentvarianter = new ArrayList<>();
            }
            return dokumentvarianter;
        }

        @Override
        public String toString() {
            return String.format("Dokument{tittel='%s', brevkode='%s', dokumentvariantListe=%s}",
                    tittel,
                    brevkode,
                    dokumentvarianter.stream().map(DokumentVariant::toString)
                            .toList());
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class DokumentVariant {

            @Schema(description = "Filtypen til filen som følger, feks PDFA, JSON eller XML.")
            private String filtype;

            @Schema(description = "Selve dokumentet. Hvis filtype er PDF/XML, ved fysisk dokument brukes bytearray.")
            private String fysiskDokument;

            @Schema(description = "Referanse til dokumentet")
            private Long dokumentReferanse;

            @Schema(description = """
                    ARKIV brukes for dokumentvarianter i menneskelesbart format (for eksempel PDFA).
                    Gosys og nav.no henter arkivvariant og viser denne til bruker.
                    ORIGINAL skal brukes for dokumentvariant i maskinlesbart format
                    (for eksempel XML og JSON) som brukes for automatisk saksbehandling
                    Alle dokumenter må ha én variant med variantFormat ARKIV.""")
            private String variantformat;

            @Override
            public String toString() {
                return "DokumentVariant{" +
                        "filtype='" + filtype + '\'' +
                        ", fysiskDok='" + (isNotBlank(fysiskDokument) ? fysiskDokument.substring(0, 10) + "..." + '\'' : null) +
                        ", dokumentReferanse=" + dokumentReferanse +
                        ", variantformat='" + variantformat + '\'' +
                        '}';
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sak {

        @Schema(example = "10695768", description = "Iden til fagsaken i fagsystemet. Skal kun settes dersom sakstype = FAGSAK.")
        private String fagsakId;

        @Schema(description = "Fagsystemet som saken behandles i.")
        private Fagsaksystem fagsaksystem;

        @Schema(description = "FAGSAK vil si at dokumentene tilhører en sak i et fagsystem. Dersom FAGSAK velges, må fagsakid og fagsaksystem oppgis.")
        private Sakstype sakstype;
    }
}
