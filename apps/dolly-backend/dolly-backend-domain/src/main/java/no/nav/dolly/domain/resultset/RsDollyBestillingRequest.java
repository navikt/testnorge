package no.nav.dolly.domain.resultset;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyBestillingRequest extends RsDollyUtvidetBestilling {

    @Schema(
            required = true,
            description = "Antall testpersoner som bestilles"
    )
    private int antall;
}