package no.nav.registre.spion.consumer.rs.response.aareg;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Sporingsinformasjon{

    String opprettetTidspunkt;
    String opprettetAv;
    String opprettetKilde;
    String opprettetKildereferense;
    String endretTidspunkt;
    String endretAv;
    String endretKilde;
    String endretKildereferanse;

}