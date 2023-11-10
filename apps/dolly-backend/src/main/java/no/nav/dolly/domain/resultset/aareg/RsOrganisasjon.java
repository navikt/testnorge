package no.nav.dolly.domain.resultset.aareg;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
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
