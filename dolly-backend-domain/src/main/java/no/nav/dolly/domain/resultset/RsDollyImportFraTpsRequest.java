package no.nav.dolly.domain.resultset;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyImportFraTpsRequest extends RsDollyBestilling {

    @ApiModelProperty(
            value = "Liste av identer som skal importeres",
            required = true
    )
    private List<String> identer;

    @ApiModelProperty(
            value = "Miljø som det skal leses fra",
            required = true
    )
    private String kildeMiljoe;
}
