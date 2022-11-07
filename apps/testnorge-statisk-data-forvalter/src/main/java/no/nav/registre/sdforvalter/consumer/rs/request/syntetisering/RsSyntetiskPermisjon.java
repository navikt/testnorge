package no.nav.registre.sdforvalter.consumer.rs.request.syntetisering;

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
