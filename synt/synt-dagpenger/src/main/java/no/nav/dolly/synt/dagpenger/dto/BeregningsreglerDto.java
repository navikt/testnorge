package no.nav.dolly.synt.dagpenger.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BeregningsreglerDto {

    private String oppfyllerKravTilFangstOgFiske;
    private String harAvtjentVernepliktSiste3Av12Mnd;
}

