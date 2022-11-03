package no.nav.testnav.apps.syntaaregservice.domain.synt;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsSyntetiskPermisjon {

    private String permisjonOgPermittering;
    private String permisjonsId;
    private RsSyntetiskPeriode permisjonsPeriode;
    private Double permisjonsprosent;
}
