package no.nav.dolly.domain.resultset.aareg;

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
public class RsOrganisasjon extends RsAktoer {

    @Schema(description = "Organisasjonsnummer, må finnes i EREG",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String orgnummer;

    @Override
    public String getAktoertype() {
        return "ORG";
    }
}
