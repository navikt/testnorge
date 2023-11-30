package no.nav.registre.testnorge.batchbestillingservice.request;

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
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Antall testpersoner som bestilles"
    )
    private int antall;
}