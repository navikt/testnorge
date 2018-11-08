package no.nav.registre.hodejegeren.provider.rs.requests;

import java.util.Map;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenereringsOrdreRequest {
    
    @NotNull
    private Long gruppeId;
    @NotNull
    private String miljoe;
    @ApiModelProperty(value = "{\"endringskode1\":antallSkdmeldinger,\n\"endringskode2\":antallSkdmeldinger2 osv.}", required = true)
    @NotNull
    private Map<String, Integer> antallMeldingerPerEndringskode;
}
