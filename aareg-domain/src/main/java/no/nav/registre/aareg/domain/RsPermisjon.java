package no.nav.registre.aareg.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsPermisjon {

    private String permisjonOgPermittering;
    private String permisjonsId;
    private RsPeriode permisjonsPeriode;
    private Double permisjonsprosent;
}
