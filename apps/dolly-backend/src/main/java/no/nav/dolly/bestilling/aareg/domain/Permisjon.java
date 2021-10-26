package no.nav.dolly.bestilling.aareg.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsPeriodeAareg;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permisjon {

    private String permisjonsId;

    private RsPeriodeAareg permisjonsPeriode;

    private BigDecimal permisjonsprosent;

    private String permisjonOgPermittering;
}
