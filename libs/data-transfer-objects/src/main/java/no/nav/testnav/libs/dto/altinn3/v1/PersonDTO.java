package no.nav.testnav.libs.dto.altinn3.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {

    private String ident;
    private List<OrganisasjonDTO> organisasjoner;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganisasjonDTO {

        private String navn;
        private String organisasjonsnummer;
        private String organisasjonsform;
        private Boolean hasAltinnDollyTilgang;
        private Boolean hasDollyOrganisasjonTilgang;
        private String melding;
    }
}
