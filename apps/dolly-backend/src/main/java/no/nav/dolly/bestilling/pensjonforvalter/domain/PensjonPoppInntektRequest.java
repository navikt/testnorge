package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PensjonPoppInntektRequest {

    private String fnr;
    private Integer tomAar;
    private Integer fomAar;
    private Integer belop;
    private Boolean redusertMedGrunnbelop;
}
