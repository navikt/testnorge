package no.nav.dolly.bestilling.pensjon.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LagreInntektRequest {

    private String fnr;
    private Integer tomAar;
    private Integer fomAar;
    private Integer belop;
    private Boolean redusertMedGrunnbelop;
}
