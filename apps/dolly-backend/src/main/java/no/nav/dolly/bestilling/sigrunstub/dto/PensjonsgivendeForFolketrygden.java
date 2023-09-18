package no.nav.dolly.bestilling.sigrunstub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensjonsgivendeForFolketrygden {

    private String inntektsaar;
    @Schema(description = "Liste av map som beskrevet i /api/v1/pensjonsgivendeinntektforfolketrygden/kodeverk")
    private List<Map<String, Object>> pensjonsgivendeInntekt;
    private String personidentifikator;
    private String testdataEier;
}
