package no.nav.dolly.domain.resultset.aareg;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsOrganisasjon extends RsAktoer {

    @ApiModelProperty(
            value = "Organisasjonsnummer, må finnes i EREG",
            required = true,
            position = 1
    )
    private String orgnummer;
}
