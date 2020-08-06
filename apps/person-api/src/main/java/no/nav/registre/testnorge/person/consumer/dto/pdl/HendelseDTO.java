package no.nav.registre.testnorge.person.consumer.dto.pdl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HendelseDTO {
    String hendelseId;
}
