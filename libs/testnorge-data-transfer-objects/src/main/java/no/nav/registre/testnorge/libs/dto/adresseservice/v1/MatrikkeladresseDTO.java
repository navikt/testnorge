package no.nav.registre.testnorge.libs.dto.adresseservice.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatrikkeladresseDTO {

    private String matrikkelId;
    private String kommunenummer;
    private String gaardsnummer;
    private String bruksnummer;
    private String postnummer;
    private String poststed;
    private String tilleggsnavn;
}
