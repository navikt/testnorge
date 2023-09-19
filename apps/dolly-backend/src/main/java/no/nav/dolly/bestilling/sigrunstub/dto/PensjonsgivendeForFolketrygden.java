package no.nav.dolly.bestilling.sigrunstub.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensjonsgivendeForFolketrygden {

    private String inntektsaar;
    @Schema(description = "Liste av map som beskrevet i /api/v1/pensjonsgivendeinntektforfolketrygden/kodeverk")
    private JsonNode pensjonsgivendeInntekt;
    private String personidentifikator;
    private String testdataEier;
}
