package no.nav.dolly.bestilling.histark.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "En semikolon-separarert liste med data som beskriver PDF filen som importeres. Formatet er som følger:" +
        "<antall sider>;<enhetsnummer>;<enhetsnavn>;<temakoder>;<brukerident>;<startår>;<sluttår>;<skanningstidspunkt>;<skanner>;<skanne-sted>;<filnavn>;<klage>;<sjekksum>")
public class HistarkRequest {

    private List<HistarkDokument> histarkDokumenter;

    public List<HistarkDokument> getHistarkDokumenter() {
        if (isNull(histarkDokumenter)) {
            histarkDokumenter = new ArrayList<>();
        }
        return histarkDokumenter;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class HistarkDokument {
        @Schema(description = "PDF dokument som skal importeres")
        private String file;

        @Schema(description = "Metadata tilhørende filen som sendes")
        private HistarkMetadata metadata;

        @Override
        public String toString() {
            return "HistarkDokument{file='%s...', metadata=%s}".formatted(file.substring(0, 10), metadata);
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public static class HistarkMetadata {


            @Schema(description = "Antall sider i PDF dokumentet")
            private String antallSider;
            @Schema(description = "Nummeret på enheten som saksmappen tilhører")
            private String enhetsnummer;

            @Schema(description = "Navnet på enheten som saksmappen tilhører")
            private String enhetsnavn;

            @Schema(description = "En komma-separert liste med temakoder")
            private String temakoder;

            @Schema(description = "Fødselsnummer eller D-nummer til bruker")
            private String brukerident;
            @Schema(description = "Startåret for saksmappen")
            private String startAar;

            @Schema(description = "Sluttåret for saksmappen")
            private String sluttAar;

            @Schema(description = "Når saksmappen ble skannet")
            private String skanningstidspunkt;

            @Schema(description = "Hvilen skanner som ble brukt")
            private String skanner;

            @Schema(description = "Hvor saksmappen ble skannet")
            private String skannested;

            @Schema(description = "Filnavnet på PDF fila")
            private String filnavn;

            @Schema(description = "IKKE I BRUK, kan stå tomt")
            private String klage;

            @Schema(description = "SHA256 sjekksum av PDF fila")
            private String sjekksum;

            @Override
            public String toString() {
                return String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s",
                        antallSider,
                        enhetsnummer,
                        enhetsnavn,
                        temakoder,
                        brukerident,
                        startAar,
                        sluttAar,
                        skanningstidspunkt,
                        skanner,
                        skannested,
                        filnavn,
                        klage,
                        sjekksum);
            }
        }
    }
}