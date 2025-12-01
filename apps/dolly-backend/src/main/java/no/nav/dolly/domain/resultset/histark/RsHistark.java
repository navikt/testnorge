package no.nav.dolly.domain.resultset.histark;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsHistark {


    @Schema(description = "Dokumenter som skal sendes for identen til histark")
    private List<RsHistarkDokument> dokumenter;

    public List<RsHistarkDokument> getDokumenter() {

        if (isNull(dokumenter)) {
            dokumenter = new ArrayList<>();
        }
        return dokumenter;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class RsHistarkDokument {

        @Schema(description = """
                Dokumentets tittel, f.eks. "Søknad om dagpenger ved permittering".
                Dokumentets tittel blir synlig i brukers journal på nav.no, samt i Gosys.""")
        private String tittel;

        @Schema(description = "Antall sider i dokumentet")
        private Integer antallSider;

        @Schema(description = "Hvilken skanner som ble brukt")
        private String skanner;

        @Schema(description = "Hvor saksmappen ble scannet")
        private String skannested;

        @Schema(description = "Når saksmappen ble skannet")
        private LocalDateTime skanningsTidspunkt;
        @Schema(description = "Temakoder som forsendelsen tilhører, for eksempel “DAG” (Dagpenger)")
        private List<String> temakoder;
        @Schema(description = "Kanalen som ble brukt ved innsending eller distribusjon. F.eks. NAV_NO, ALTINN eller EESSI.")
        private String enhetsnavn;
        @Schema(description = """
                NAV-enheten som har journalført, eventuelt skal journalføre, forsendelsen. Ved automatisk journalføring uten mennesker involvert skal enhet settes til "9999".
                Konsument må sette journalfoerendeEnhet dersom tjenesten skal ferdigstille journalføringen.""")
        private String enhetsnummer;

        @Schema(description = "Startåret for saksmappen, for eksempel 2010")
        private Integer startYear;

        @Schema(description = "Sluttåret for saksmappen, for eksempel 2019")
        private Integer endYear;

        @Schema(description = "Selve PDF dokumentet. Ved fysisk dokument brukes bytearray.")
        private String fysiskDokument;

        @Schema(description = "Referanse til dokumentet")
        private Long dokumentReferanse;
    }
}
