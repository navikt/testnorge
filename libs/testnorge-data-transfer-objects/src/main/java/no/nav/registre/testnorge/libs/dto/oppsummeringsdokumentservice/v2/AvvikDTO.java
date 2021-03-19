package no.nav.registre.testnorge.libs.dto.oppsummeringsdokumentservice.v2;

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
