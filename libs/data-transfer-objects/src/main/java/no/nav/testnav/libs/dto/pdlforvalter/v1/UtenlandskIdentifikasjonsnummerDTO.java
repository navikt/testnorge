package no.nav.testnav.libs.dto.pdlforvalter.v1;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class UtenlandskIdentifikasjonsnummerDTO extends DbVersjonDTO {

    @Schema(
            description = "Utenlandsk identifikasjonsnummer knyttet til personen")
    private String identifikasjonsnummer;

    @Schema(
            description = "Er det utenlandske identifikasjonsnummeret opphørt?")
    private Boolean opphoert;

    @Schema(
            description = "Land i hht kodeverk 'Landkoder'")
    private String utstederland;

}
