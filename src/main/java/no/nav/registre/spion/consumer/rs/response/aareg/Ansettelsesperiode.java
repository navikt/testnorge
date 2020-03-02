package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.NoArgsConstructor;
import lombok.Value;


@Value
@NoArgsConstructor(force = true)
public class Ansettelsesperiode {

    private final Periode periode;
    private final Periode bruksperiode;
    private final Sporingsinformasjon sporingsinformasjon;

}
