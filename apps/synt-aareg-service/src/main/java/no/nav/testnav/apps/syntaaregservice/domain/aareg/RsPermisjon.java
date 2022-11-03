package no.nav.testnav.apps.syntaaregservice.domain.aareg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RsPermisjon {

    private String permisjonOgPermittering;
    private String permisjonId;
    private String permisjon;
    private RsPeriode permisjonsPeriode;
    private Double permisjonsprosent;
}
