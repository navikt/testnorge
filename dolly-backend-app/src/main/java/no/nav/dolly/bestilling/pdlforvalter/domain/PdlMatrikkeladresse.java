package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlMatrikkeladresse extends PdlOpplysning {

        private String tilleggsnavn;
        private String bruksenhetsnummer;
        private Integer bruksnummer;
        private Integer festenummer;
        private Integer gaardsnummer;
        private String kommunenummer;
        private String postnummer;
        private Integer undernummer;
}
