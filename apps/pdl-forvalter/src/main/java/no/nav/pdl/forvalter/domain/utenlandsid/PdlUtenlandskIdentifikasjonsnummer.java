package no.nav.pdl.forvalter.domain.utenlandsid;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.pdl.forvalter.domain.PdlDbVersjon;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlUtenlandskIdentifikasjonsnummer extends PdlDbVersjon {

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
