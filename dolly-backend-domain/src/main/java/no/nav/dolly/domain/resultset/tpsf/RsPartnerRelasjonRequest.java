package no.nav.dolly.domain.resultset.tpsf;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsPartnerRelasjonRequest {

    @ApiModelProperty(
            position = 1,
            required = true,
            value = "Entydig identifisering av partner"
    )
    private String ident;

    @ApiModelProperty(
            position = 2,
            value = "Liste av sivilstander beskriver forhold mellom hovedperson og partner"
    )
    private List<RsSivilstandRequest> sivilstander;

    @ApiModelProperty(
            position = 3,
            value = "Når true (eller blankt) får partner samme adresse som hovedperson. False innebærer ulik boadresse."
    )
    private Boolean harFellesAdresse;
}