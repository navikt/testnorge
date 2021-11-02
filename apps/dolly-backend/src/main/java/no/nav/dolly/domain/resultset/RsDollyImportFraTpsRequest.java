package no.nav.dolly.domain.resultset;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyImportFraTpsRequest extends RsDollyBestilling {

    @Schema(required = true,
            description = "Liste av identer som skal importeres")
    private List<String> identer;

    @Schema(required = true,
            description = "Miljø som det skal leses fra")
    private String kildeMiljoe;
}
