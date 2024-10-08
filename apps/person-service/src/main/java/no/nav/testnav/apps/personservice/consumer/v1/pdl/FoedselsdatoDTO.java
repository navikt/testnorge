package no.nav.testnav.apps.personservice.consumer.v1.pdl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FoedselsdatoDTO {
    Integer foedselsaar;
    LocalDate foedselsdato;
    String kilde;
    String master;
}
