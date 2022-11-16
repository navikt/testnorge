package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
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
