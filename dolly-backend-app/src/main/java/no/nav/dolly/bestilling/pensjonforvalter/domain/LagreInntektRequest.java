package no.nav.dolly.bestilling.pensjonforvalter.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LagreInntektRequest {

    private List<String> miljoer;

    private String fnr;
    private Integer tomAar;
    private Integer fomAar;
    private Integer belop;
    private Boolean redusertMedGrunnbelop;
}
