package no.nav.dolly.bestilling.arenaforvalter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "NB! miljø og personident er påkrevd. Hvis ikke vedtaktype er angitt, tolkes det som O (ny rettighet")
public class AapRequest {

    @Schema(description = "Brukerens fødselsnummer")
    private String personident;

    @Schema(description = "Hvilket miljø endringen skal gjøres i")
    private String miljoe;

    @Schema(description = "AAP-rettigheter for brukeren")
    private Aap nyeAap;
}
