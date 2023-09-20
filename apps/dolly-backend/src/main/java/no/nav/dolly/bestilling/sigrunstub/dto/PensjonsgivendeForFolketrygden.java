package no.nav.dolly.bestilling.sigrunstub.dto;

import com.fasterxml.jackson.databind.JsonNode;
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
    private JsonNode pensjonsgivendeInntekt;
    private String personidentifikator;
    private String testdataEier;
}
