package no.nav.registre.orkestratoren.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonDTO {
    String ident;
    LocalDateTime foedselsdato;
}
