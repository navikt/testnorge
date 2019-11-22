package no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid;

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
public class PdlUtenlandskIdentifikasjonsnummer {

    @ApiModelProperty(
            position = 1,
            required = true,
            value = "Utenlandsk identifikasjonsnummer knyttet til personen"
    )
    private String identifikasjonsnummer;

    @ApiModelProperty(
            position = 2,
            required = true,
            value = "Dataens opprinnelse"
    )
    private String kilde;

    @ApiModelProperty(
            position = 3,
            required = true,
            value = "Er det utenlandske identifikasjonsnummeret opphørt?"
    )
    private Boolean opphoert;

    @ApiModelProperty(
            position = 4,
            required = true,
            value = "Land i hht kodeverk 'Landkoder'"
    )
    private String utstederland;
}
