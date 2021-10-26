package no.nav.dolly.domain.resultset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyImportFraPdlRequest extends RsDollyBestilling {

    @Schema(required = true,
            description = "Liste av identer som skal importeres")
    private List<String> identer;
}
