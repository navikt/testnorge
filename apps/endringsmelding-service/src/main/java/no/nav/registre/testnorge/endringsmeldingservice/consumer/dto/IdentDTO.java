package no.nav.registre.testnorge.endringsmeldingservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class IdentDTO {
    String ident;
    Set<String> env;
}
