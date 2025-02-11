package no.nav.dolly.bestilling.histark.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "En semikolon-separarert liste med data som beskriver PDF filen som importeres. Formatet er som følger:" +
        "<antall sider>;<enhetsnummer>;<enhetsnavn>;<temakoder>;<brukerident>;<startår>;<sluttår>;<skanningstidspunkt>;<skanner>;<skanne-sted>;<filnavn>;<klage>;<sjekksum>")
public class HistarkRequest {

    @Schema(description = "PDF dokument som skal importeres")
    private ByteArrayResource file;

    @Schema(description = "Metadata tilhørende filen som sendes")
    private HistarkMetadata metadata;

    @Override
    public String toString() {
        try {
            var contents = file.getContentAsString(StandardCharsets.UTF_8);
            return "HistarkDokument{file='%s...', metadata=%s}".formatted(contents.substring(contents.length() - 21, contents.length() - 1), metadata);
        } catch (IOException e) {
            log.error("Kunne ikke hente filinnhold", e);
            return "HistarkDokument{file='kunne ikke hente filinnhold', metadata=%s}".formatted(metadata);
        }
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