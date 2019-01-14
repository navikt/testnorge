package no.nav.registre.hodejegeren.provider.rs.requests;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenereringsOrdreRequest {

    @NotNull
    @ApiModelProperty(value = "Angir avspillergruppen i TPSF SKD-ENDRINGSMELDINGER som meldingene skal lagres i", required = true)
    private Long avspillergruppeId;

    @NotNull
    @ApiModelProperty(value = "Status på eksisterende identer i avspillergruppen blir hentet fra dette miljøet", required = true)
    private String miljoe;

    @NotNull
    @ApiModelProperty(value = "{\"endringskode1\":antallSkdmeldinger,\n\"endringskode2\":antallSkdmeldinger2 osv.}", required = true)
    private Map<String, Integer> antallMeldingerPerEndringskode;
}
