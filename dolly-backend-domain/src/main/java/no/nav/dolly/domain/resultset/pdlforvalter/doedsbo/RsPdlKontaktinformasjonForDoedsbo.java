package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsPdlKontaktinformasjonForDoedsbo {

    @Schema(required = true,
            description = "Dødsboets adresse, adresselinje 1")
    private String adresselinje1;

    @Schema(description = "Dødsboets adresse, adresselinje 2")
    private String adresselinje2;

    @Schema(required = true,
            description = "Postnummer i hht kodeverk 'Postnummer'")
    private String postnummer;

    @Schema(required = true,
            description = "Poststed i hht kodeverk 'Postnummer'")
    private String poststedsnavn;

    @Schema(description = "Landkode i hht. kodeverk 'Landkoder'")
    private String landkode;

    @Schema(required = true,
            description = "Dødsboets skifteform")
    private PdlSkifteform skifteform;

    @Schema(required = true)
    private RsPdlAdressat adressat;

    @Schema(required = true,
            type = "LocalDateTime",
            description = "Dato for utstedelse")
    private LocalDateTime utstedtDato;
}
