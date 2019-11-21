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
            value = "Dataens opprinnelse. Default verdi er 'Dolly'"
    )
    private String kilde;

    @ApiModelProperty(
            position = 1,
            required = true,
            value = "Er det utenlandske identifikasjonsnummeret opphørt?"
    )
    private Boolean opphoert;

    @ApiModelProperty(
            position = 1,
            required = true,
            value = "Land i hht kodeverk 'Landkoder'"
    )
    private String utstederland;
}
