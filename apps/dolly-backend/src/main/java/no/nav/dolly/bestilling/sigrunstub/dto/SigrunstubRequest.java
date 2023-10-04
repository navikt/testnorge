package no.nav.dolly.bestilling.sigrunstub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SigrunstubRequest {

    private String inntektsaar;
    private String testdataEier;
}
