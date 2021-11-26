package no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.dolly.domain.resultset.pdlforvalter.PdlOpplysning;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlUtenlandskIdentifikasjonsnummer extends PdlOpplysning {

    @Schema(required = true,
            description = "Utenlandsk identifikasjonsnummer knyttet til personen")
    private String identifikasjonsnummer;

    @Schema(required = true,
            description = "Er det utenlandske identifikasjonsnummeret opphørt?")
    private Boolean opphoert;

    @Schema(required = true,
            description = "Land i hht kodeverk 'Landkoder'")
    private String utstederland;
}
