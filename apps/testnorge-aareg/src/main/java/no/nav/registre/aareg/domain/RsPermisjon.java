package no.nav.registre.aareg.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
