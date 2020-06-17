package no.nav.dolly.domain.resultset.dokarkiv;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class RsDokarkiv {

    public enum JournalPostType {

        INNGAAENDE, UTGAAENDE, NOTAT
    }

    public enum IdType {

        FNR, ORGNR, HPRNR, UTL_ORG
    }
    @ApiModelProperty(
            position = 1,
            value = "Tittel som beskriver forsendelsen samlet, feks \"Søknad om dagpenger ved permittering\""
    )
    private String tittel;

    @ApiModelProperty(
            position = 2
    )
    private JournalPostType journalPost;

    @ApiModelProperty(
            position = 3,
            value = "Temaet som forsendelsen tilhører, for eksempel “DAG” (Dagpenger)"
    )
    private String tema;

    @ApiModelProperty(
            position = 4,
            value = "Behandlingstema for forsendelsen, for eksempel ab0001 (Ordinære dagpenger)."
    )
    private String behandlingstema;

    @ApiModelProperty(
            position = 5,
            value = "Kanalen som ble brukt ved innsending eller distribusjon. F.eks. NAV_NO, ALTINN eller EESSI."
    )
    private String kanal;

    @ApiModelProperty(
            position = 6,
            value = "NAV-enheten som har journalført, eventuelt skal journalføre, forsendelsen. Ved automatisk journalføring uten mennesker involvert skal enhet settes til \"9999\".\n" +
                    "Konsument må sette journalfoerendeEnhet dersom tjenesten skal ferdigstille journalføringen."
    )
    private String journalfoerendeEnhet;

    @ApiModelProperty(
            position = 7,
            value = "AvsenderMottaker"
    )
    private AvsenderMottaker avsenderMottaker;

    @ApiModelProperty(
            position = 8,
            value = "Første dokument blir tilknyttet som hoveddokument på journalposten. Øvrige dokumenter tilknyttes som vedlegg. Rekkefølgen på vedlegg beholdes ikke ved uthenting av journalpost."
    )
    private Dokument dokumenter;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class AvsenderMottaker {

        @ApiModelProperty(
                position = 1,
                value = "Identifikatoren til avsender/mottaker. Dette er normalt et fødselsnummer eller organisasjonsnummer, men valideres ikke. Dersom det ønskes å nullstille denne verdien, kan den settes til en tom string."
        )
        private String id;

        @ApiModelProperty(
                position = 2,
                value = "Angir hvilken type identifikator som er benyttet i AvsenderMottaker.id.\n" +
                        "Påkrevd dersom id er satt. * FNR"
        )
        private IdType idType;

        @ApiModelProperty(
                position = 3,
                value = "Navnet til avsender/mottaker.\n" +
                        "Navn på personbrukere skal lagres på formatet etternavn, fornavn mellomnavn"
        )
        private String navn;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class Dokument {

        @ApiModelProperty(
                position = 1,
                value = "Dokumentets tittel, f.eks. \"Søknad om dagpenger ved permittering\".\n" +
                        "Dokumentets tittel blir synlig i brukers journal på nav.no, samt i Gosys."
        )
        private String tittel;

        @ApiModelProperty(
                position = 2,
                value = "Typen dokument. Brevkoden sier noe om dokumentets innhold og oppbygning.\n" +
                        "For inngående dokumenter kan brevkoden være en NAV-skjemaID f.eks. “NAV 04-01.04” eller en SED-id.\n" +
                        "Brevkode skal ikke settes for ustrukturert, uklassifisert dokumentasjon, f.eks. brukeropplastede vedlegg."
        )
        private String brevkode;

        @ApiModelProperty(
                position = 3,
                value = "DokumentVariant"
        )
        private DokumentVariant dokumentVarianter;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(value = JsonInclude.Include.NON_EMPTY)
    public static class DokumentVariant {

        @ApiModelProperty(
                position = 1,
                value = "Filtypen til filen som følger, feks PDFA, JSON eller XML."
        )
        private String filtype;

        @ApiModelProperty(
                position = 2,
                value = "Selve dokumentet. Hvis filtype er PDF/XML, ved fysisk dokument brukes bytearray."
        )
        private String fysiskDokument;

        @ApiModelProperty(
                position = 3,
                value = "ARKIV brukes for dokumentvarianter i menneskelesbart format (for eksempel PDFA). Gosys og nav.no henter arkivvariant og viser denne til bruker.\n" +
                        "ORIGINAL skal brukes for dokumentvariant i maskinlesbart format (for eksempel XML og JSON) som brukes for automatisk saksbehandling\n" +
                        "Alle dokumenter må ha én variant med variantFormat ARKIV."
        )
        private String variantformat;
    }
}
