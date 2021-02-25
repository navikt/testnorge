package no.nav.registre.testnorge.person.consumer.dto.pdl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FoedselDTO {
    Integer foedselsaar;
    LocalDate foedselsdato;
    String kilde;
    String master;
}
