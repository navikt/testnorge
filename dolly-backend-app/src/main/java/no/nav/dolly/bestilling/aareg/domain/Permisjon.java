package no.nav.dolly.bestilling.aareg.domain;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsPeriode;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permisjon {

    private String permisjonsId;

    private RsPeriode permisjonsPeriode;

    private BigDecimal permisjonsprosent;

    private String permisjonOgPermittering;
}
