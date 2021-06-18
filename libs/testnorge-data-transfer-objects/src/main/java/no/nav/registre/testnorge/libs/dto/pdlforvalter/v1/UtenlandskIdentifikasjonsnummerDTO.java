package no.nav.registre.testnorge.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtenlandskIdentifikasjonsnummerDTO extends DbVersjonDTO {

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
