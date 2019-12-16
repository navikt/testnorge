package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsPdlKontaktinformasjonForDoedsbo {

    @ApiModelProperty(
            position = 1,
            required = true,
            value= "Dødsboets adresse, adresselinje 1"
    )
    private String adresselinje1;

    @ApiModelProperty(
            position = 2,
            value= "Dødsboets adresse, adresselinje 2"
    )
    private String adresselinje2;

    @ApiModelProperty(
            position = 3,
            required = true,
            value = "Postnummer i hht kodeverk 'Postnummer'"
    )
    private String postnummer;

    @ApiModelProperty(
            position = 4,
            required = true,
            value = "Poststed i hht kodeverk 'Postnummer'"
    )
    private String poststedsnavn;

    @ApiModelProperty(
            position = 5,
            value = "Landkode i hht. kodeverk 'Landkoder'"
    )
    private String landkode;

    @ApiModelProperty(
            position = 6,
            required = true,
            value = "Dødsboets skifteform"
    )
    private PdlSkifteform skifteform;

    @ApiModelProperty(
            position = 7,
            required = true
    )
    private PdlSomAdressat adressat;

    @ApiModelProperty(
            position = 8,
            required = true,
            dataType = "LocalDateTime",
            value = "Dato for utstedelse"
    )
    private LocalDateTime utstedtDato;
}
