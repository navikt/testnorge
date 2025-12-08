package no.nav.testnav.libs.dto.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatrikkeladresseDTO implements Serializable {

    private String kommunenummer;
    private Integer gaardsnummer;
    private Integer bruksnummer;
    private String postnummer;
    private String bruksenhetsnummer;
    private String tilleggsnavn;
}
