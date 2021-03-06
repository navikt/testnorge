package no.nav.testnav.libs.dto.generernavnservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class NavnDTO {
    String adjektiv;
    String adverb;
    String substantiv;
}
