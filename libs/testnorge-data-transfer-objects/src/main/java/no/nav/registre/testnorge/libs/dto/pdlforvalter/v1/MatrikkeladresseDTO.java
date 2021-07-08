package no.nav.registre.testnorge.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
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
