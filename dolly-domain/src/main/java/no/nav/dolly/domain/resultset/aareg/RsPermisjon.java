package no.nav.dolly.domain.resultset.aareg;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsPermisjon {

    private String permisjonsId;
    private RsPeriode permisjonsPeriode;
    private BigDecimal permisjonsprosent;
    private String permisjonOgPermittering;
}
