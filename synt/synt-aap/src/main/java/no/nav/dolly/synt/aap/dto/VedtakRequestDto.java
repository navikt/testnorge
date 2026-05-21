package no.nav.dolly.synt.aap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VedtakRequestDto {

    private String fraDato;
    private String tilDato;
    private String utfall;
    private String vedtakDato;
    private String vedtakTypeKode;

}

