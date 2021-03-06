package no.nav.testnav.libs.dto.syntrest.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AvvikDTO {
    String id;
    String navn;
    String alvorlighetsgrad;
}
