package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Sporingsinformasjon{

    private final String opprettetTidspunkt;
    private final String opprettetAv;
    private final String opprettetKilde;
    private final String opprettetKildereferense;
    private final String endretTidspunkt;
    private final String endretAv;
    private final String endretKilde;
    private final String endretKildereferanse;

}