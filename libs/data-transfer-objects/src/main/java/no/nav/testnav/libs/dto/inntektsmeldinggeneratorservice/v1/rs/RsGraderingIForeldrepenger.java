package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsGraderingIForeldrepenger {

    private RsPeriode periode;
    private Integer arbeidstidprosent;
}
