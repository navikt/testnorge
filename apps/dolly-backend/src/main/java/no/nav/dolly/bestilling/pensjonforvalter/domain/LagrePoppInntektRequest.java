package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LagrePoppInntektRequest {
    private String fnr;
    private Integer fomAar;
    private Integer tomAar;
    private Integer belop;
    private Boolean redusertMedGrunnbelop;
}
