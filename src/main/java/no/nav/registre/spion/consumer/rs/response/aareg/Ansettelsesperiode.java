package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.NoArgsConstructor;
import lombok.Value;


@Value
@NoArgsConstructor(force = true)
public class Ansettelsesperiode {

    Periode periode;
    Periode bruksperiode;
    Sporingsinformasjon sporingsinformasjon;

}
