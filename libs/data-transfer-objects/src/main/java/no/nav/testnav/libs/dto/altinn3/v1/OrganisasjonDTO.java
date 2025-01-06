package no.nav.testnav.libs.dto.altinn3.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganisasjonDTO {

        private String navn;
        private String organisasjonsnummer;
        private String organisasjonsform;
}