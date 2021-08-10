package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatrikkeladresseDTO implements Serializable {

    private String adressetilleggsnavn;
    private String bruksenhetsnummer;
    private Integer bruksnummer;
    private Integer gaardsnummer;
    private String kommunenummer;
    private String postnummer;
}
